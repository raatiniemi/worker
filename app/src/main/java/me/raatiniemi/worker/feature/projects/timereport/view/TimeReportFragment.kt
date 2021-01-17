/*
 * Copyright (C) 2021 Tobias Raatiniemi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.raatiniemi.worker.feature.projects.timereport.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import me.raatiniemi.worker.R
import me.raatiniemi.worker.databinding.FragmentProjectTimeReportBinding
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.model.ProjectId
import me.raatiniemi.worker.domain.project.model.projectName
import me.raatiniemi.worker.feature.projects.model.ProjectHolder
import me.raatiniemi.worker.feature.projects.timereport.model.TimeReportAction
import me.raatiniemi.worker.feature.projects.timereport.model.TimeReportViewActions
import me.raatiniemi.worker.feature.projects.timereport.viewmodel.TimeReportViewModel
import me.raatiniemi.worker.feature.shared.model.ActivityViewAction
import me.raatiniemi.worker.feature.shared.model.ContextViewAction
import me.raatiniemi.worker.feature.shared.model.OngoingNotificationActionEvent
import me.raatiniemi.worker.feature.shared.view.*
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class TimeReportFragment : Fragment() {
    private val eventBus = EventBus.getDefault()
    private val arguments: TimeReportFragmentArgs by navArgs()
    private val projectHolder: ProjectHolder by inject()

    private val usageAnalytics: UsageAnalytics by inject()
    private val vm: TimeReportViewModel by viewModel()
    private val timeReportAdapter: TimeReportAdapter by lazy {
        TimeReportAdapter(vm, get())
    }

    private val refreshActiveWeeks = RefreshTimeIntervalLifecycleObserver {
        timeReportAdapter.currentList?.let { weeks ->
            vm.refreshActiveTimeReportWeek(weeks)
        }
    }

    private var actionMode: ActionMode? = null

    private var _binding: FragmentProjectTimeReportBinding? = null
    private val binding: FragmentProjectTimeReportBinding
        get() = requireNotNull(_binding) { "Unable to configure binding for view" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventBus.register(this)
        projectHolder += Project(
            id = ProjectId(arguments.projectId),
            name = projectName(arguments.projectName)
        )
        lifecycle.addObserver(refreshActiveWeeks)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectTimeReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observe(projectHolder.observable) {
            setTitle(it.name.value)
        }
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()

        usageAnalytics.setCurrentScreen(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(refreshActiveWeeks)
        eventBus.unregister(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_project, menu)
        menu.findItem(R.id.actions_project_hide_registered)?.let {
            it.isChecked = vm.shouldHideRegisteredTime
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.actions_project_hide_registered == item.itemId) {
            item.isChecked = !item.isChecked
            vm.shouldHideRegisteredTime = item.isChecked
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun configureView() {
        setHasOptionsMenu(true)

        binding.rvTimeReport.apply {
            adapter = timeReportAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
        }
    }

    private fun observeViewModel() {
        observe(vm.isSelectionActivated) { shouldShowActionMode ->
            if (shouldShowActionMode) {
                showActionMode()
            } else {
                dismissActionMode()
            }
        }

        observe(vm.weeks) {
            timeReportAdapter.submitList(it)
        }

        observeAndConsume(vm.viewActions) {
            when (it) {
                is TimeReportViewActions.RefreshTimeReportWeek -> it.action(timeReportAdapter)
                is ActivityViewAction -> it(requireActivity())
                is ContextViewAction -> it(requireContext())
                else -> Timber.w("No observation for ${it.javaClass.simpleName}")
            }
        }
    }

    private fun showActionMode() {
        if (actionMode != null) {
            return
        }

        val callback = TimeReportActionModeCallback(object : TimeReportActionConsumer {
            override fun consume(action: TimeReportAction) {
                when (action) {
                    TimeReportAction.TOGGLE_REGISTERED -> toggleRegisteredStateForSelectedItems()
                    TimeReportAction.REMOVE -> confirmRemoveSelectedItems()
                    TimeReportAction.DISMISS -> clearSelection()
                }
            }
        })
        actionMode = requireActivity().startActionMode(callback)
    }

    private fun dismissActionMode() {
        actionMode?.finish()
        actionMode = null
    }

    private fun toggleRegisteredStateForSelectedItems() {
        lifecycleScope.launch {
            vm.toggleRegisteredStateForSelectedItems()
        }
    }

    private fun confirmRemoveSelectedItems() {
        lifecycleScope.launch {
            val confirmAction = ConfirmDeleteTimeIntervalDialog.show(requireContext())
            if (confirmAction == ConfirmAction.NO) {
                return@launch
            }

            vm.removeSelectedItems()
        }
    }

    private fun clearSelection() {
        vm.clearSelection()

        timeReportAdapter.notifyDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    internal fun onEventMainThread(event: OngoingNotificationActionEvent) {
        if (projectHolder.value == event.project) {
            vm.reloadTimeReport()
            return
        }

        Timber.d("No need to refresh, event is related to another project")
    }
}
