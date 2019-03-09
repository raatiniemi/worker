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

package me.raatiniemi.worker.features.projects.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_projects.*
import kotlinx.coroutines.launch
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.projects.adapter.ProjectsAdapter
import me.raatiniemi.worker.features.projects.createproject.model.CreateProjectEvent
import me.raatiniemi.worker.features.projects.model.ProjectsViewActions
import me.raatiniemi.worker.features.projects.viewmodel.ProjectsViewModel
import me.raatiniemi.worker.features.settings.project.model.TimeSummaryStartingPointChangeEvent
import me.raatiniemi.worker.features.shared.model.ActivityViewAction
import me.raatiniemi.worker.features.shared.model.ContextViewAction
import me.raatiniemi.worker.features.shared.model.OngoingNotificationActionEvent
import me.raatiniemi.worker.features.shared.view.ConfirmAction
import me.raatiniemi.worker.features.shared.view.CoroutineScopedFragment
import me.raatiniemi.worker.features.shared.view.visibleIf
import me.raatiniemi.worker.util.HintedImageButtonListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*
import kotlin.concurrent.schedule

class ProjectsFragment : CoroutineScopedFragment() {
    private val eventBus = EventBus.getDefault()

    private val vm: ProjectsViewModel by viewModel()
    private val projectsAdapter: ProjectsAdapter by lazy {
        ProjectsAdapter(vm, HintedImageButtonListener(requireActivity()))
    }

    private var refreshActiveProjectsTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventBus.register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureView()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()

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

    private fun configureView() {
        rvProjects.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = projectsAdapter
        }
    }

    private fun observeViewModel() {
        vm.projects.observe(this, Observer {
            projectsAdapter.submitList(it)

            tvEmptyProjects.visibleIf { it.isEmpty() }
        })

        vm.viewActions.observeAndConsume(this, Observer {
            processViewAction(it)
        })
    }

    private fun processViewAction(viewAction: ProjectsViewActions) {
        when (viewAction) {
            is ProjectsViewActions.RefreshProjects -> viewAction.action(projectsAdapter)
            is ProjectsViewActions.ShowConfirmClockOutMessage -> launch {
                val confirmAction = ConfirmClockOutDialog.show(requireContext())
                if (ConfirmAction.YES == confirmAction) {
                    vm.clockOut(viewAction.item, viewAction.date)
                }
            }
            is ProjectsViewActions.ShowChooseTimeForClockActivity -> {
                viewAction.action(childFragmentManager) { projectsItem, date ->
                    launch {
                        if (projectsItem.isActive) {
                            vm.clockOut(projectsItem, date)
                            return@launch
                        }

                        vm.clockIn(projectsItem, date)
                    }
                }
            }
            is ProjectsViewActions.ShowConfirmRemoveProjectMessage -> launch {
                val confirmAction = RemoveProjectDialog.show(requireContext())
                if (ConfirmAction.YES == confirmAction) {
                    vm.remove(viewAction.item)
                }
            }
            is ActivityViewAction -> viewAction.action(requireActivity())
            is ContextViewAction -> viewAction.action(requireContext())
            else -> Timber.w("Unable to handle view action ${viewAction.javaClass.simpleName}")
        }
    }

    private fun startRefreshTimer() {
        cancelRefreshTimer()

        refreshActiveProjectsTimer = Timer()
        refreshActiveProjectsTimer?.schedule(Date(), 60_000) {
            launch {
                val projects = projectsAdapter.currentList ?: return@launch

                vm.refreshActiveProjects(projects)
            }
        }
    }

    private fun cancelRefreshTimer() {
        refreshActiveProjectsTimer?.cancel()
        refreshActiveProjectsTimer = null
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: CreateProjectEvent) {
        vm.reloadProjects()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: OngoingNotificationActionEvent) {
        vm.reloadProjects()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: TimeSummaryStartingPointChangeEvent) {
        vm.reloadProjects()
    }
}
