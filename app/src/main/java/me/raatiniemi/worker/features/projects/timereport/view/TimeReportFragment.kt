/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.projects.timereport.view

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_project_time_report.*
import kotlinx.coroutines.launch
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.model.ProjectId
import me.raatiniemi.worker.domain.project.model.projectName
import me.raatiniemi.worker.domain.time.minutes
import me.raatiniemi.worker.features.projects.model.ProjectHolder
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportAction
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportViewActions
import me.raatiniemi.worker.features.projects.timereport.viewmodel.TimeReportViewModel
import me.raatiniemi.worker.features.shared.model.ActivityViewAction
import me.raatiniemi.worker.features.shared.model.ContextViewAction
import me.raatiniemi.worker.features.shared.model.OngoingNotificationActionEvent
import me.raatiniemi.worker.features.shared.view.*
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*
import kotlin.concurrent.schedule

class TimeReportFragment : CoroutineScopedFragment() {
    private val eventBus = EventBus.getDefault()
    private val arguments: TimeReportFragmentArgs by navArgs()
    private val projectHolder: ProjectHolder by inject()

    private val usageAnalytics: UsageAnalytics by inject()
    private val vm: TimeReportViewModel by viewModel()
    private val timeReportAdapter: TimeReportAdapter by lazy {
        TimeReportAdapter(get(), vm)
    }

    private var refreshActiveTimeIntervalsTimer: Timer? = null
    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventBus.register(this)
        projectHolder += Project(
            id = ProjectId(arguments.projectId),
            name = projectName(arguments.projectName)
        )
        observe(projectHolder.observable) {
            setTitle(it.name.value)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_project_time_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureView()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()

        usageAnalytics.setCurrentScreen(this)
        startRefreshTimer()
    }

    override fun onPause() {
        super.onPause()

        cancelRefreshTimer()
    }

    override fun onDestroy() {
        super.onDestroy()

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

        rvTimeReport.apply {
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

        observe(vm.timeReport) {
            timeReportAdapter.submitList(it)

            tvEmptyTimeReport.visibleIf { it.isEmpty() }
        }

        observeAndConsume(vm.viewActions) {
            when (it) {
                is TimeReportViewActions.RefreshTimeReportDays -> it.action(timeReportAdapter)
                is ActivityViewAction -> it.action(requireActivity())
                is ContextViewAction -> it.action(requireContext())
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

    private fun toggleRegisteredStateForSelectedItems() = launch {
        vm.toggleRegisteredStateForSelectedItems()
    }

    private fun confirmRemoveSelectedItems() = launch {
        val confirmAction = ConfirmDeleteTimeIntervalDialog.show(requireContext())
        if (confirmAction == ConfirmAction.NO) {
            return@launch
        }

        vm.removeSelectedItems()
    }

    private fun clearSelection() {
        vm.clearSelection()

        timeReportAdapter.notifyDataSetChanged()
    }

    private fun startRefreshTimer() {
        cancelRefreshTimer()

        refreshActiveTimeIntervalsTimer = Timer()
        refreshActiveTimeIntervalsTimer?.schedule(Date(), 1.minutes) {
            val timeReportDays = timeReportAdapter.currentList ?: return@schedule
            vm.refreshActiveTimeReportDay(timeReportDays)
        }
    }

    private fun cancelRefreshTimer() {
        refreshActiveTimeIntervalsTimer?.run {
            cancel()
            purge()
        }
        refreshActiveTimeIntervalsTimer = null
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
