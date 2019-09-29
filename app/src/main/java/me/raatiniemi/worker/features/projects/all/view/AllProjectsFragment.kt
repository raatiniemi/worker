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

package me.raatiniemi.worker.features.projects.all.view

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_all_projects.*
import kotlinx.coroutines.launch
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.projects.all.adapter.AllProjectsAdapter
import me.raatiniemi.worker.features.projects.all.model.AllProjectsViewActions
import me.raatiniemi.worker.features.projects.all.viewmodel.AllProjectsViewModel
import me.raatiniemi.worker.features.settings.project.model.TimeSummaryStartingPointChangeEvent
import me.raatiniemi.worker.features.shared.model.ActivityViewAction
import me.raatiniemi.worker.features.shared.model.ContextViewAction
import me.raatiniemi.worker.features.shared.model.OngoingNotificationActionEvent
import me.raatiniemi.worker.features.shared.view.*
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*
import kotlin.concurrent.schedule

class AllProjectsFragment : CoroutineScopedFragment() {
    private val eventBus = EventBus.getDefault()

    private val usageAnalytics: UsageAnalytics by inject()
    private val vm: AllProjectsViewModel by viewModel()
    private val allProjectsAdapter: AllProjectsAdapter by lazy {
        AllProjectsAdapter(vm, HintedImageButtonListener())
    }

    private var refreshActiveProjectsTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventBus.register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_projects, container, false)
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

    private fun configureView() {
        setHasOptionsMenu(true)

        rvProjects.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = allProjectsAdapter
        }
    }

    private fun observeViewModel() {
        observe(vm.projects) {
            allProjectsAdapter.submitList(it)

            tvEmptyProjects.visibleIf { it.isEmpty() }
        }

        observeAndConsume(vm.viewActions) {
            processViewAction(it)
        }
    }

    private fun processViewAction(viewAction: AllProjectsViewActions) {
        when (viewAction) {
            is AllProjectsViewActions.CreateProject -> viewAction.action(this) {
                vm.projectCreated()
            }
            is AllProjectsViewActions.ProjectCreated -> viewAction.action(requireActivity())
            is AllProjectsViewActions.RefreshProjects -> viewAction.action(allProjectsAdapter)
            is AllProjectsViewActions.OpenProject -> viewAction.action(this)
            is AllProjectsViewActions.ShowConfirmClockOutMessage -> showConfirmClockOutMessage(
                viewAction
            )
            is AllProjectsViewActions.ShowChooseTimeForClockActivity -> showChooseTimeForClockActivity(
                viewAction
            )
            is AllProjectsViewActions.ShowConfirmRemoveProjectMessage -> showConfirmRemoveProjectMessage(
                viewAction
            )
            is ActivityViewAction -> viewAction.action(requireActivity())
            is ContextViewAction -> viewAction.action(requireContext())
            else -> Timber.w("Unable to handle view action ${viewAction.javaClass.simpleName}")
        }
    }

    private fun showConfirmClockOutMessage(viewAction: AllProjectsViewActions.ShowConfirmClockOutMessage) =
        launch {
            val confirmAction = ConfirmClockOutDialog.show(requireContext())
            if (ConfirmAction.YES == confirmAction) {
                vm.clockOutAt(viewAction.item.asProject(), viewAction.date)
            }
        }

    private fun showChooseTimeForClockActivity(viewAction: AllProjectsViewActions.ShowChooseTimeForClockActivity) {
        viewAction.action(childFragmentManager) { projectsItem, date ->
            launch {
                if (projectsItem.isActive) {
                    vm.clockOutAt(projectsItem.asProject(), date)
                    return@launch
                }

                vm.clockInAt(projectsItem.asProject(), date)
            }
        }
    }

    private fun showConfirmRemoveProjectMessage(
        viewAction: AllProjectsViewActions.ShowConfirmRemoveProjectMessage
    ) = launch {
        val confirmAction = RemoveProjectDialog.show(requireContext())
        if (ConfirmAction.YES == confirmAction) {
            vm.remove(viewAction.item.asProject())
        }
    }

    private fun startRefreshTimer() {
        cancelRefreshTimer()

        refreshActiveProjectsTimer = Timer()
        refreshActiveProjectsTimer?.schedule(Date(), 60_000) {
            launch {
                val projects = allProjectsAdapter.currentList ?: return@launch

                vm.refreshActiveProjects(projects)
            }
        }
    }

    private fun cancelRefreshTimer() {
        refreshActiveProjectsTimer?.cancel()
        refreshActiveProjectsTimer = null
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
