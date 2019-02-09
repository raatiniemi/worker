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
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_time_report.*
import kotlinx.coroutines.launch
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.project.model.ProjectHolder
import me.raatiniemi.worker.features.project.timereport.adapter.TimeReportAdapter
import me.raatiniemi.worker.features.project.timereport.model.TimeReportAction
import me.raatiniemi.worker.features.project.timereport.viewmodel.TimeReportViewModel
import me.raatiniemi.worker.features.shared.model.OngoingNotificationActionEvent
import me.raatiniemi.worker.features.shared.view.ConfirmAction
import me.raatiniemi.worker.features.shared.view.CoroutineScopedFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class TimeReportFragment : CoroutineScopedFragment() {
    private val projectHolder: ProjectHolder by inject()

    private val vm: TimeReportViewModel by viewModel()

    private val eventBus = EventBus.getDefault()

    private val timeReportAdapter: TimeReportAdapter by lazy {
        TimeReportAdapter(get(), vm)
    }

    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventBus.register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_time_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvTimeReport.apply {
            adapter = timeReportAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        vm.timeReport.observe(this, Observer {
            timeReportAdapter.submitList(it)
        })

        vm.viewActions.observeAndConsume(this, Observer {
            it.action(requireActivity())
        })

        vm.isSelectionActivated.observe(this, Observer {
            if (it) {
                showActionMode()
                return@Observer
            }

            dismissActionMode()
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }

    fun reloadTimeReport() {
        vm.reloadTimeReport()
    }

    private fun showActionMode() {
        if (actionMode != null) {
            return
        }

        val callback = TimeReportActionModeCallback(object : TimeReportActionConsumer {
            override fun consume(action: TimeReportAction) {
                when (action) {
                    TimeReportAction.TOGGLE_REGISTERED -> toggleRegisterSelectedItems()
                    TimeReportAction.REMOVE -> confirmRemoveSelectedItems()
                }
            }
        })
        actionMode = requireActivity().startActionMode(callback)
    }

    private fun dismissActionMode() {
        actionMode?.finish()
        actionMode = null
    }

    private fun toggleRegisterSelectedItems() = launch {
        vm.registerSelectedItems()
    }

    private fun confirmRemoveSelectedItems() = launch {
        val confirmAction = ConfirmDeleteTimeIntervalDialog.show(requireContext())
        if (confirmAction == ConfirmAction.NO) {
            return@launch
        }

        vm.removeSelectedItems()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: OngoingNotificationActionEvent) {
        if (event.projectId == projectHolder.project) {
            vm.reloadTimeReport()
            return
        }

        Timber.d("No need to refresh, event is related to another project")
    }

    companion object {
        @JvmStatic
        fun newInstance() = TimeReportFragment()
    }
}
