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
import me.raatiniemi.worker.features.project.view.ProjectActivity
import me.raatiniemi.worker.features.projects.adapter.ProjectsAdapter
import me.raatiniemi.worker.features.projects.createproject.model.CreateProjectEvent
import me.raatiniemi.worker.features.projects.model.ProjectsAction
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.features.projects.model.ProjectsViewActions
import me.raatiniemi.worker.features.projects.viewmodel.ProjectsViewModel
import me.raatiniemi.worker.features.settings.project.model.TimeSummaryStartingPointChangeEvent
import me.raatiniemi.worker.features.shared.model.ActivityViewAction
import me.raatiniemi.worker.features.shared.model.OngoingNotificationActionEvent
import me.raatiniemi.worker.features.shared.view.ConfirmAction
import me.raatiniemi.worker.features.shared.view.CoroutineScopedFragment
import me.raatiniemi.worker.features.shared.view.visibleIf
import me.raatiniemi.worker.util.HintedImageButtonListener
import me.raatiniemi.worker.util.KeyValueStore
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*
import kotlin.concurrent.schedule

class ProjectsFragment : CoroutineScopedFragment() {
    private val eventBus = EventBus.getDefault()

    private val projectsViewModel: ProjectsViewModel by viewModel()

    private val keyValueStore: KeyValueStore by inject()

    private var refreshActiveProjectsTimer: Timer? = null

    private lateinit var projectsAdapter: ProjectsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventBus.register(this)

        projectsAdapter = ProjectsAdapter(
                object : ProjectsActionConsumer {
                    override fun accept(action: ProjectsAction) = when (action) {
                        is ProjectsAction.Open -> onItemClick(action.item)
                        is ProjectsAction.Toggle -> onClockActivityToggle(action.item)
                        is ProjectsAction.At -> onClockActivityAt(action.item)
                        is ProjectsAction.Remove -> onDelete(action.item)
                    }
                },
                HintedImageButtonListener(requireActivity())
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureView()
        observeViewModel()
    }

    private fun configureView() {
        rvProjects.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = projectsAdapter
        }
    }

    private fun observeViewModel() {
        projectsViewModel.projects.observe(this, Observer {
            projectsAdapter.submitList(it)

            tvEmptyProjects.visibleIf { it.isEmpty() }
        })

        projectsViewModel.viewActions.observeAndConsume(this, Observer {
            processViewAction(it)
        })
    }

    private fun processViewAction(viewAction: ProjectsViewActions) {
        when (viewAction) {
            is ProjectsViewActions.RefreshProjects -> viewAction.action(projectsAdapter)
            is ActivityViewAction -> viewAction.action(requireActivity())
            else -> Timber.w("Unable to handle view action ${viewAction.javaClass.simpleName}")
        }
    }

    override fun onResume() {
        super.onResume()

        startRefreshTimer()
    }

    private fun startRefreshTimer() {
        cancelRefreshTimer()

        refreshActiveProjectsTimer = Timer()
        refreshActiveProjectsTimer?.schedule(Date(), 60_000) {
            launch {
                val projects = projectsAdapter.currentList ?: return@launch

                projectsViewModel.refreshActiveProjects(projects)
            }
        }
    }

    override fun onPause() {
        super.onPause()

        cancelRefreshTimer()
    }

    private fun cancelRefreshTimer() {
        refreshActiveProjectsTimer?.cancel()
        refreshActiveProjectsTimer = null
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: CreateProjectEvent) {
        projectsViewModel.reloadProjects()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: OngoingNotificationActionEvent) {
        projectsViewModel.reloadProjects()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: TimeSummaryStartingPointChangeEvent) {
        projectsViewModel.reloadProjects()
    }

    private fun onItemClick(item: ProjectsItem) {
        val (id) = item.asProject()

        val intent = ProjectActivity.newIntent(requireActivity(), id)
        startActivity(intent)
    }

    private fun onClockActivityToggle(item: ProjectsItem) {
        launch {
            if (!item.isActive) {
                projectsViewModel.clockIn(item, Date())
                return@launch
            }

            if (!keyValueStore.confirmClockOut()) {
                projectsViewModel.clockOut(item, Date())
                return@launch
            }

            val confirmAction = ConfirmClockOutDialog.show(requireContext())
            if (ConfirmAction.YES == confirmAction) {
                projectsViewModel.clockOut(item, Date())
            }
        }
    }

    private fun onClockActivityAt(item: ProjectsItem) {
        val fragment = ClockActivityAtFragment.newInstance(
                item
        ) { calendar ->
            launch {
                if (item.isActive) {
                    projectsViewModel.clockOut(item, calendar.time)
                    return@launch
                }

                projectsViewModel.clockIn(item, calendar.time)
            }
        }

        childFragmentManager.beginTransaction()
                .add(fragment, FRAGMENT_CLOCK_ACTIVITY_AT_TAG)
                .commit()
    }

    private fun onDelete(item: ProjectsItem) {
        launch {
            val confirmAction = RemoveProjectDialog.show(requireContext())
            if (ConfirmAction.YES == confirmAction) {
                projectsViewModel.remove(item)
            }
        }
    }

    companion object {
        private const val FRAGMENT_CLOCK_ACTIVITY_AT_TAG = "clock activity at"
    }
}
