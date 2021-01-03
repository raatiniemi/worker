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

package me.raatiniemi.worker.feature.shared.datetime.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialogfragment_date_time_picker.*
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.time.hoursMinutes
import me.raatiniemi.worker.domain.time.yearsMonthsDays
import me.raatiniemi.worker.feature.shared.datetime.model.DateTimeConfiguration
import me.raatiniemi.worker.feature.shared.datetime.model.DateTimeViewActions
import me.raatiniemi.worker.feature.shared.datetime.viewmodel.DateTimeViewModel
import me.raatiniemi.worker.feature.shared.view.*
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class DateTimePickerDialogFragment : DialogFragment() {
    private val usageAnalytics: UsageAnalytics by inject()
    private val vm: DateTimeViewModel by viewModel()

    private var configuration: DateTimeConfiguration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        configureUserInterface()
        return inflater.inflate(R.layout.dialogfragment_date_time_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val configuration = requireNotNull(configuration) {
                "No configuration is available for view"
            }
            vm.configure(configuration)

            configureUserInterface(configuration)
            bindUserInterfaceToViewModel()
        } catch (e: IllegalArgumentException) {
            Timber.w(e, "Unable to show date time picker dialog")
            dismiss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()

        usageAnalytics.setCurrentScreen(this)
    }

    private fun configureUserInterface() {
        dialog?.also {
            it.setCanceledOnTouchOutside(false)
        }
    }

    private fun configureUserInterface(configuration: DateTimeConfiguration) {
        update(dpDate, yearsMonthsDays(configuration.date))
        update(tpTime, hoursMinutes(configuration.date))

        tpTime.setIs24HourView(true)
    }

    private fun bindUserInterfaceToViewModel() {
        change(dpDate, vm::chooseDate)
        change(tpTime, vm::chooseTime)

        click(tvDate) {
            vm.chooseDate()
        }
        click(tvTime) {
            vm.chooseTime()
        }
        click(btnOk) {
            vm.choose()
        }
        click(btnCancel) {
            vm.dismiss()
        }
    }

    private fun observeViewModel() {
        observe(vm.minDate) {
            dpDate.minDate = it
        }
        observe(vm.maxDate) {
            dpDate.maxDate = it
        }
        observe(vm.date) {
            tvDate.text = it
        }
        observe(vm.time) {
            tvTime.text = it
        }
        observeAndConsume(vm.viewActions) { viewAction ->
            when (viewAction) {
                is DateTimeViewActions.ChooseDate -> viewAction(view)
                is DateTimeViewActions.ChooseTime -> viewAction(view)
                is DateTimeViewActions.DateTimeIsOutsideOfAllowedInterval -> {
                    viewAction(requireContext())
                }
                is DateTimeViewActions.Choose -> {
                    val configuration = requireNotNull(configuration)
                    viewAction(this, configuration.choose)
                }
                is DateTimeViewActions.Dismiss -> viewAction(this)
            }
        }
    }

    companion object {
        internal fun newInstance(configure: (DateTimeConfiguration) -> Unit): DateTimePickerDialogFragment {
            val configuration = DateTimeConfiguration()
            configure(configuration)

            return DateTimePickerDialogFragment()
                .also { it.configuration = configuration }
        }
    }
}
