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
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import me.raatiniemi.worker.R
import me.raatiniemi.worker.data.service.ongoing.ProjectNotificationService
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.features.project.view.ProjectActivity
import me.raatiniemi.worker.features.projects.createproject.model.CreateProjectEvent
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult
import me.raatiniemi.worker.features.projects.viewmodel.ClockActivityViewModel
import me.raatiniemi.worker.features.projects.viewmodel.ProjectsViewModel
import me.raatiniemi.worker.features.projects.viewmodel.RefreshActiveProjectsViewModel
import me.raatiniemi.worker.features.projects.viewmodel.RemoveProjectViewModel
import me.raatiniemi.worker.features.settings.project.model.TimeSummaryStartingPointChangeEvent
import me.raatiniemi.worker.features.shared.model.OngoingNotificationActionEvent
import me.raatiniemi.worker.features.shared.view.adapter.SimpleListAdapter
import me.raatiniemi.worker.features.shared.view.dialog.RxAlertDialog
import me.raatiniemi.worker.features.shared.view.fragment.RxFragment
import me.raatiniemi.worker.util.HintedImageButtonListener
import me.raatiniemi.worker.util.KeyValueStore
import me.raatiniemi.worker.util.RxUtil.applySchedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.*
import kotlin.concurrent.schedule

class ProjectsFragment : RxFragment(), OnProjectActionListener, SimpleListAdapter.OnItemClickListener {
    private val eventBus = EventBus.getDefault()

    private val refreshViewModel: RefreshActiveProjectsViewModel by viewModel()

    private val projectsViewModel: ProjectsViewModel by inject()
    private val clockActivityViewModel: ClockActivityViewModel.ViewModel by inject()
    private val removeProjectViewModel: RemoveProjectViewModel.ViewModel by inject()
    private val keyValueStore: KeyValueStore by inject()

    private var refreshActiveProjectsTimer: Timer? = null

    private lateinit var adapter: ProjectsAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventBus.register(this)

        adapter = ProjectsAdapter(resources, this, HintedImageButtonListener(requireActivity()))
        adapter.setOnItemClickListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.fragment_projects)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        val startingPointForTimeSummary = keyValueStore.startingPointForTimeSummary()
        clockActivityViewModel.input().startingPointForTimeSummary(startingPointForTimeSummary)

        projectsViewModel.projects()
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe { adapter.add(it) }

        clockActivityViewModel.output().clockInSuccess()
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe { result ->
                    updateNotificationForProject(result.projectsItem)
                    updateProject(result)
                }

        clockActivityViewModel.error().clockInError()
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe { showClockInErrorMessage() }

        clockActivityViewModel.output().clockOutSuccess()
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe { result ->
                    updateNotificationForProject(result.projectsItem)
                    updateProject(result)
                }

        clockActivityViewModel.error().clockOutError()
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe { showClockOutErrorMessage() }

        removeProjectViewModel.output().removeProjectSuccess()
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe { (_, _) -> showDeleteProjectSuccessMessage() }

        removeProjectViewModel.error().removeProjectError()
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    restoreProjectAtPreviousPosition(result)
                    showDeleteProjectErrorMessage()
                }

        observeViewModel()
    }

    private fun observeViewModel() {
        projectsViewModel.viewActions.observeAndConsume(this, Observer {
            it.action(requireActivity())
        })

        refreshViewModel.activePositions.observe(this, Observer {
            refreshPositions(it)
        })
    }

    override fun onResume() {
        super.onResume()

        startRefreshTimer()
    }

    private fun startRefreshTimer() {
        cancelRefreshTimer()

        refreshActiveProjectsTimer = Timer()
        refreshActiveProjectsTimer?.schedule(Date(), 60_000) {
            refreshViewModel.projects(adapter.items)
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
        addCreatedProject(event.project)
    }

    private fun addCreatedProject(@NonNull project: Project) {
        val item = ProjectsItem.from(project, emptyList())
        val position = adapter.add(item)

        recyclerView.scrollToPosition(position)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: OngoingNotificationActionEvent) {
        reloadProjects()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: TimeSummaryStartingPointChangeEvent) {
        val startingPointForTimeSummary = keyValueStore.startingPointForTimeSummary()
        clockActivityViewModel.input().startingPointForTimeSummary(startingPointForTimeSummary)

        reloadProjects()
    }

    private fun reloadProjects() {
        adapter.clear()

        // TODO: Move to input event for view model.
        projectsViewModel.projects()
                .compose(bindToLifecycle())
                .subscribe { adapter.add(it) }
    }

    private fun refreshPositions(positions: List<Int>) {
        // Check that we have positions to refresh.
        if (positions.isEmpty()) {
            // We should never reach this code since there are supposed to be
            // checks for positions before the refreshPositions-method is called.
            Timber.w("No positions, skip refreshing projects")
            return
        }

        // Iterate and refresh every position.
        Timber.d("Refreshing %d projects", positions.size)
        for (position in positions) {
            adapter.notifyItemChanged(position)
        }
    }

    private fun updateNotificationForProject(project: ProjectsItem) {
        ProjectNotificationService.startServiceWithContext(
                requireActivity(),
                project.asProject()
        )
    }

    private fun updateProject(result: ProjectsItemAdapterResult) {
        adapter.set(result.position, result.projectsItem)
    }

    private fun showClockInErrorMessage() {
        Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                R.string.error_message_clock_in,
                Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showClockOutErrorMessage() {
        Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                R.string.error_message_clock_out,
                Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun deleteProjectAtPosition(position: Int) {
        adapter.remove(position)
    }

    private fun restoreProjectAtPreviousPosition(result: ProjectsItemAdapterResult) {
        adapter.add(result.position, result.projectsItem)

        recyclerView.scrollToPosition(result.position)
    }

    private fun showDeleteProjectSuccessMessage() {
        Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                R.string.message_project_deleted,
                Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showDeleteProjectErrorMessage() {
        Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                R.string.error_message_project_deleted,
                Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onItemClick(@NonNull view: View) {
        // Retrieve the position for the project from the RecyclerView.
        val position = recyclerView.getChildAdapterPosition(view)
        if (RecyclerView.NO_POSITION == position) {
            Timber.w("Unable to retrieve project position for onItemClick")
            return
        }

        try {
            // Retrieve the project from the retrieved position.
            val item = adapter.get(position)
            val (id) = item.asProject()

            val intent = ProjectActivity.newIntent(
                    requireActivity(),
                    id
            )
            startActivity(intent)
        } catch (e: IndexOutOfBoundsException) {
            Timber.w(e, "Unable to get project position")
            Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    R.string.error_message_unable_to_find_project,
                    Snackbar.LENGTH_SHORT
            ).show()
        }

    }

    override fun onClockActivityToggle(@NonNull result: ProjectsItemAdapterResult) {
        val projectsItem = result.projectsItem
        if (projectsItem.isActive) {
            // Check if clock out require confirmation.
            if (!keyValueStore.confirmClockOut()) {
                clockActivityViewModel.input().clockOut(result, Date())
                return
            }

            ConfirmClockOutDialog.show(requireActivity())
                    .filter { RxAlertDialog.isPositive(it) }
                    .subscribe(
                            { clockActivityViewModel.input().clockOut(result, Date()) },
                            { Timber.w(it) }
                    )
            return
        }

        clockActivityViewModel.input().clockIn(result, Date())
    }

    override fun onClockActivityAt(@NonNull result: ProjectsItemAdapterResult) {
        val projectsItem = result.projectsItem
        val fragment = ClockActivityAtFragment.newInstance(
                projectsItem
        ) { calendar ->
            if (projectsItem.isActive) {
                clockActivityViewModel.input().clockOut(result, calendar.time)
                return@newInstance
            }

            clockActivityViewModel.input().clockIn(result, calendar.time)
        }

        childFragmentManager.beginTransaction()
                .add(fragment, FRAGMENT_CLOCK_ACTIVITY_AT_TAG)
                .commit()
    }

    override fun onDelete(@NonNull result: ProjectsItemAdapterResult) {
        RemoveProjectDialog.show(requireActivity())
                .filter { RxAlertDialog.isPositive(it) }
                .subscribe(
                        {
                            deleteProjectAtPosition(result.position)

                            removeProjectViewModel.input().remove(result)
                        },
                        { Timber.w(it) }
                )
    }

    companion object {
        private const val FRAGMENT_CLOCK_ACTIVITY_AT_TAG = "clock activity at"
    }
}
