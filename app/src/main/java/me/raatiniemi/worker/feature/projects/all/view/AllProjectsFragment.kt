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

package me.raatiniemi.worker.feature.projects.all.view

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import me.raatiniemi.worker.R
import me.raatiniemi.worker.databinding.FragmentAllProjectsBinding
import me.raatiniemi.worker.feature.projects.all.model.AllProjectsActions
import me.raatiniemi.worker.feature.projects.all.model.AllProjectsViewActions
import me.raatiniemi.worker.feature.projects.all.viewmodel.AllProjectsViewModel
import me.raatiniemi.worker.feature.settings.model.TimeSummaryStartingPointChangeEvent
import me.raatiniemi.worker.feature.shared.model.ActivityViewAction
import me.raatiniemi.worker.feature.shared.model.ContextViewAction
import me.raatiniemi.worker.feature.shared.model.OngoingNotificationActionEvent
import me.raatiniemi.worker.feature.shared.view.ConfirmAction
import me.raatiniemi.worker.feature.shared.view.RefreshTimeIntervalLifecycleObserver
import me.raatiniemi.worker.feature.shared.view.launch
import me.raatiniemi.worker.feature.shared.view.observeAndConsume
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class AllProjectsFragment : Fragment() {
    private val eventBus = EventBus.getDefault()

    private val usageAnalytics: UsageAnalytics by inject()
    private val vm: AllProjectsViewModel by viewModel()
    private val allProjectsAdapter: AllProjectsAdapter by lazy {
        AllProjectsAdapter { action ->
            when (action) {
                is AllProjectsActions.Open -> vm.open(action.item)
                is AllProjectsActions.Toggle -> {
                    launch {
                        vm.toggle(action.item, action.date)
                    }
                }
                is AllProjectsActions.At -> vm.at(action.item)
                is AllProjectsActions.Remove -> vm.remove(action.item)
            }
        }
    }

    private val refreshActiveProjects = RefreshTimeIntervalLifecycleObserver {
        launch {
            vm.refreshActiveProjects(allProjectsAdapter.snapshot())
        }
    }

    private var _binding: FragmentAllProjectsBinding? = null
    private val binding: FragmentAllProjectsBinding
        get() = requireNotNull(_binding) { "Unable to configure binding for view" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventBus.register(this)
        lifecycle.addObserver(refreshActiveProjects)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllProjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureUserInterface()
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

        lifecycle.removeObserver(refreshActiveProjects)
        eventBus.unregister(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_projects, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.actions_main_create_project -> {
            vm.createProject()
            true
        }
        else -> super.onOptionsItemSelected(menuItem)
    }

    private fun configureUserInterface() {
        setHasOptionsMenu(true)

        binding.rvProjects.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = allProjectsAdapter
        }

        launch {
            allProjectsAdapter.loadStateFlow.collectLatest {
                with(binding) {
                    if (it.refresh is LoadState.Error) {
                        rvProjects.isVisible = false
                        tvEmptyProjects.isVisible = true
                        tvEmptyProjects.text = getString(R.string.projects_all_error_text)
                    } else {
                        val isEmpty = allProjectsAdapter.itemCount < 1
                        rvProjects.isVisible = !isEmpty
                        tvEmptyProjects.isVisible = isEmpty
                        tvEmptyProjects.text = getString(R.string.projects_all_empty_text)
                    }
                }
            }
        }
    }

    private fun observeViewModel() {
        launch {
            vm.projects.collectLatest {
                allProjectsAdapter.submitData(it)
            }
        }
        observeAndConsume(vm.viewActions) {
            processViewAction(it)
        }
    }

    private fun processViewAction(viewAction: AllProjectsViewActions) {
        when (viewAction) {
            is AllProjectsViewActions.ReloadProjects -> allProjectsAdapter.refresh()
            is AllProjectsViewActions.CreateProject -> {
                launch {
                    val project = viewAction.apply(childFragmentManager)
                    project?.also { vm.projectCreated() }
                }
            }
            is AllProjectsViewActions.ProjectCreated -> viewAction(requireActivity())
            is AllProjectsViewActions.RefreshProjects -> viewAction.action(allProjectsAdapter)
            is AllProjectsViewActions.OpenProject -> viewAction(this)
            is AllProjectsViewActions.ShowConfirmClockOutMessage -> showConfirmClockOutMessage(
                viewAction
            )
            is AllProjectsViewActions.ChooseDateAndTimeForClockIn -> {
                launch {
                    viewAction.apply(childFragmentManager)
                        ?.let { (project, date) -> vm.clockInAt(project, date) }
                }
            }
            is AllProjectsViewActions.ChooseDateAndTimeForClockOut -> {
                launch {
                    viewAction.apply(childFragmentManager)
                        ?.let { (project, date) -> vm.clockOutAt(project, date) }
                }
            }
            is AllProjectsViewActions.ShowConfirmRemoveProjectMessage -> showConfirmRemoveProjectMessage(
                viewAction
            )
            is ActivityViewAction -> viewAction(requireActivity())
            is ContextViewAction -> viewAction(requireContext())
            else -> Timber.w("Unable to handle view action ${viewAction.javaClass.simpleName}")
        }
    }

    private fun showConfirmClockOutMessage(viewAction: AllProjectsViewActions.ShowConfirmClockOutMessage) {
        launch {
            val confirmAction = ConfirmClockOutDialog.show(requireContext())
            if (ConfirmAction.YES == confirmAction) {
                vm.clockOutAt(viewAction.item.asProject(), viewAction.date)
            }
        }
    }

    private fun showConfirmRemoveProjectMessage(
        viewAction: AllProjectsViewActions.ShowConfirmRemoveProjectMessage
    ) {
        launch {
            val confirmAction = RemoveProjectDialog.show(requireContext())
            if (ConfirmAction.YES == confirmAction) {
                vm.remove(viewAction.item.asProject())
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    internal fun onEventMainThread(event: OngoingNotificationActionEvent) {
        vm.reloadProjects()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: TimeSummaryStartingPointChangeEvent) {
        vm.reloadProjects()
    }
}
