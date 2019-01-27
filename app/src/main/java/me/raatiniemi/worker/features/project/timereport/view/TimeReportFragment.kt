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

package me.raatiniemi.worker.features.project.timereport.view

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager
import kotlinx.android.synthetic.main.fragment_time_report.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.util.DigitalHoursMinutesIntervalFormat
import me.raatiniemi.worker.domain.util.FractionIntervalFormat
import me.raatiniemi.worker.domain.util.HoursMinutesFormat
import me.raatiniemi.worker.features.project.timereport.adapter.TimeReportAdapter
import me.raatiniemi.worker.features.project.timereport.model.TimeReportViewActions
import me.raatiniemi.worker.features.project.timereport.viewmodel.TimeReportViewModel
import me.raatiniemi.worker.features.project.view.ProjectActivity
import me.raatiniemi.worker.features.shared.model.OngoingNotificationActionEvent
import me.raatiniemi.worker.features.shared.model.ViewAction
import me.raatiniemi.worker.features.shared.view.ConfirmAction
import me.raatiniemi.worker.features.shared.view.CoroutineScopedFragment
import me.raatiniemi.worker.util.KeyValueStore
import me.raatiniemi.worker.util.NullUtil.isNull
import me.raatiniemi.worker.util.SelectionListener
import me.raatiniemi.worker.util.TIME_REPORT_SUMMARY_FORMAT_FRACTION
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class TimeReportFragment : CoroutineScopedFragment(), SelectionListener {
    private val keyValueStore: KeyValueStore by inject()

    private val vm: TimeReportViewModel by viewModel()

    private val eventBus = EventBus.getDefault()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var timeReportAdapter: TimeReportAdapter
    private var actionMode: ActionMode? = null

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean {
            actionMode.setTitle(R.string.menu_title_actions)
            actionMode.menuInflater.inflate(R.menu.actions_project_time_report, menu)
            return true
        }

        override fun onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(actionMode: ActionMode, item: MenuItem) = when (item.itemId) {
            R.id.actions_project_time_report_delete -> {
                confirmRemoveSelectedItems(actionMode)
                false
            }

            R.id.actions_project_time_report_register -> {
                toggleRegisterSelectedItems(actionMode)
                true
            }

            else -> {
                Timber.w("Undefined action: %d", item.itemId)
                false
            }
        }

        override fun onDestroyActionMode(actionMode: ActionMode) {
            timeReportAdapter.deselectItems()

            this@TimeReportFragment.actionMode = null
        }

        private fun confirmRemoveSelectedItems(actionMode: ActionMode) {
            launch {
                val confirmAction = ConfirmDeleteTimeIntervalDialog.show(requireContext())
                if (ConfirmAction.YES == confirmAction) {
                    vm.remove(timeReportAdapter.selectedItems)
                }

                withContext(Dispatchers.Main) {
                    actionMode.finish()
                }
            }
        }

        private fun toggleRegisterSelectedItems(actionMode: ActionMode) = launch {
            vm.register(timeReportAdapter.selectedItems)

            withContext(Dispatchers.Main) {
                actionMode.finish()
            }
        }
    }

    private var loading = false

    private val projectId: Long
        get() = arguments?.getLong(ProjectActivity.MESSAGE_PROJECT_ID, -1) ?: -1

    private val hoursMinutesFormat: HoursMinutesFormat
        get() {
            val format = keyValueStore.timeReportSummaryFormat()
            return if (TIME_REPORT_SUMMARY_FORMAT_FRACTION == format) {
                FractionIntervalFormat()
            } else DigitalHoursMinutesIntervalFormat()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventBus.register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_time_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timeReportAdapter = TimeReportAdapter(hoursMinutesFormat, this)
        linearLayoutManager = LinearLayoutManager(requireActivity())

        val recyclerViewExpandableItemManager = RecyclerViewExpandableItemManager(savedInstanceState)

        rvTimeReport.apply {
            setHasFixedSize(false)
            layoutManager = linearLayoutManager
            adapter = recyclerViewExpandableItemManager.createWrappedAdapter(timeReportAdapter)
            addItemDecoration(
                    SimpleListDividerDecorator(
                            resources.getDrawable(
                                    R.drawable.list_item_divider,
                                    requireContext().theme
                            ),
                            true
                    )
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    // Make sure we're not loading data before checking the position.
                    if (!loading) {
                        // Retrieve positional data, needed for the calculation on whether
                        // we are close to the end of the list or not.
                        val visibleItems = linearLayoutManager.childCount
                        val firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition()

                        // Retrieve the total number of items within the recycler view,
                        // this will include both the group and the children.
                        val totalItems = linearLayoutManager.itemCount

                        // Check if the last row in the list is visible.
                        if (visibleItems + firstVisiblePosition >= totalItems) {
                            // We are about to start loading data, and thus we need
                            // to block additional loading requests.
                            loading = true

                            // Retrieve the total number of groups within the view, we need to
                            // exclude the children otherwise the offset will be wrong.
                            val offset = timeReportAdapter.groupCount

                            // Retrieve additional timesheet items with offset.
                            loadTimeReportViaViewModel(offset)
                        }
                    }
                }
            })
        }
        recyclerViewExpandableItemManager.attachRecyclerView(rvTimeReport)

        observeViewModel()
        loadTimeReportViaViewModel(offset = 0)
    }

    private fun observeViewModel() {
        vm.timeReport.observe(this, Observer {
            timeReportAdapter.add(it)

            // TODO: Call `finishLoading` when all items in buffer have been added.
            // The call to `finishLoading` will be called for each of the added
            // groups, i.e. there's a window in where we can load the same segment
            // multiple times due to the disconnect between finish loading and the
            // user attempts scroll (causing another load to happen). However, this
            // seems to be fairly theoretical, at least now, but should be improved.
            finishLoading()
        })

        vm.viewActions.observeAndConsume(this, Observer {
            when (it) {
                is TimeReportViewActions.UpdateRegistered -> {
                    if (keyValueStore.hideRegisteredTime()) {
                        timeReportAdapter.remove(it.results)
                        return@Observer
                    }

                    timeReportAdapter.set(it.results)
                }
                is TimeReportViewActions.RemoveRegistered -> {
                    timeReportAdapter.remove(it.results)
                }
                is ViewAction -> it.action(requireActivity())
            }
        })
    }

    private fun loadTimeReportViaViewModel(offset: Int) = launch {
        vm.fetch(projectId, offset)
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }

    private fun finishLoading() {
        loading = false
    }

    fun refresh() {
        // Clear the items from the list and start loading from the beginning...
        timeReportAdapter.clear()
        loadTimeReportViaViewModel(offset = 0)
    }

    override fun onSelect() {
        if (isNull(actionMode)) {
            actionMode = requireActivity().startActionMode(actionModeCallback)
        }
    }

    override fun onDeselect() {
        if (isNull(actionMode)) {
            return
        }

        if (timeReportAdapter.haveSelectedItems()) {
            return
        }

        actionMode?.finish()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: OngoingNotificationActionEvent) {
        if (event.projectId == projectId) {
            refresh()
            return
        }

        Timber.d("No need to refresh, event is related to another project")
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle): TimeReportFragment {
            val fragment = TimeReportFragment()
            fragment.arguments = bundle

            return fragment
        }
    }
}
