/*
 * Copyright (C) 2019 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.shared.datetime.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialogfragment_date_time_picker.*
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.shared.datetime.model.DateTimeConfiguration
import me.raatiniemi.worker.features.shared.datetime.model.DateTimeViewActions
import me.raatiniemi.worker.features.shared.datetime.viewmodel.DateTimeViewModel
import me.raatiniemi.worker.features.shared.view.click
import me.raatiniemi.worker.features.shared.view.observe
import me.raatiniemi.worker.features.shared.view.observeAndConsume
import org.koin.android.viewmodel.ext.android.viewModel

class DateTimePickerDialogFragment : DialogFragment() {
    private lateinit var configuration: DateTimeConfiguration
    private val vm: DateTimeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialogfragment_date_time_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.configure(configuration)

        configureUserInterface()
        bindUserInterfaceToViewModel()
        observeViewModel()
    }

    private fun configureUserInterface() {
        tpTime.setIs24HourView(true)
    }

    private fun bindUserInterfaceToViewModel() {
        click(tvDate) {
            vm.chooseDate()
        }
        click(tvTime) {
            vm.chooseTime()
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
