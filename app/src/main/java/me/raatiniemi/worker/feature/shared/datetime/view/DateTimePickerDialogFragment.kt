/*
 * Copyright (C) 2022 Tobias Raatiniemi
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
import me.raatiniemi.worker.databinding.DialogfragmentDateTimePickerBinding
import me.raatiniemi.worker.domain.time.hoursMinutes
import me.raatiniemi.worker.domain.time.yearsMonthsDays
import me.raatiniemi.worker.feature.shared.datetime.model.DateTimeConfiguration
import me.raatiniemi.worker.feature.shared.datetime.model.DateTimeViewActions
import me.raatiniemi.worker.feature.shared.datetime.viewmodel.DateTimeViewModel
import me.raatiniemi.worker.feature.shared.view.*
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class DateTimePickerDialogFragment : DialogFragment() {
    private val usageAnalytics: UsageAnalytics by inject()
    private val vm: DateTimeViewModel by viewModel()

    private var configuration: DateTimeConfiguration? = null

    private var _binding: DialogfragmentDateTimePickerBinding? = null
    private val binding: DialogfragmentDateTimePickerBinding
        get() = requireNotNull(_binding) { "Unable to configure binding for view" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        configureUserInterface()

        _binding = DialogfragmentDateTimePickerBinding.inflate(inflater, container, false)
        return binding.root
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
            observeViewModel()
        } catch (e: IllegalArgumentException) {
            Timber.w(e, "Unable to show date time picker dialog")
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()

        usageAnalytics.setCurrentScreen(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun configureUserInterface() {
        dialog?.also {
            it.setCanceledOnTouchOutside(false)
        }
    }

    private fun configureUserInterface(configuration: DateTimeConfiguration) {
        update(binding.dpDate, yearsMonthsDays(configuration.date))
        update(binding.tpTime, hoursMinutes(configuration.date))

        binding.tpTime.setIs24HourView(true)
    }

    private fun bindUserInterfaceToViewModel() {
        change(binding.dpDate, vm::chooseDate)
        change(binding.tpTime, vm::chooseTime)

        click(binding.tvDate) {
            vm.chooseDate()
        }
        click(binding.tvTime) {
            vm.chooseTime()
        }
        click(binding.btnOk) {
            vm.choose()
        }
        click(binding.btnCancel) {
            vm.dismiss()
        }
    }

    private fun observeViewModel() {
        observe(vm.minDate) {
            binding.dpDate.minDate = it
        }
        observe(vm.maxDate) {
            binding.dpDate.maxDate = it
        }
        observe(vm.date) {
            binding.tvDate.text = it
        }
        observe(vm.time) {
            binding.tvTime.text = it
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
                is DateTimeViewActions.Dismiss -> {
                    val configuration = requireNotNull(configuration)
                    viewAction(this, configuration.choose)
                }
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
