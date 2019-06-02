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

package me.raatiniemi.worker.features.shared.view.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import me.raatiniemi.worker.R
import timber.log.Timber
import java.util.*

internal open class DateTimePickerFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private val calendar = Calendar.getInstance()

    private var onDateTimeSetListener: OnDateTimeSetListener? = null

    private var minDate: Calendar? = null
    private var maxDate: Calendar? = null

    private var datePicker: DatePickerFragment? = null
    private var timePicker: TimePickerFragment? = null

    protected open val isStateInvalid: Boolean
        @CallSuper
        get() {
            if (onDateTimeSetListener == null) {
                Timber.w("No `OnDateTimeSetListener` have been supplied")
                return true
            }

            return false
        }

    /**
     * Set the minimum date for the date picker.
     *
     * @param minDate Minimum date.
     */
    protected fun setMinDate(minDate: Calendar) {
        if (minDate > maxDate) {
            throw IllegalArgumentException("Minimum date occurs after maximum date")
        }

        this.minDate = minDate
    }

    /**
     * Set the maximum date for the date picker.
     *
     * @param maxDate Maximum date.
     */
    protected fun setMaxDate(maxDate: Calendar) {
        if (maxDate < minDate) {
            throw IllegalArgumentException("Maximum date occurs before minimum date")
        }

        this.maxDate = maxDate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isStateInvalid) {
            dismissDialogWithInvalidState()
            return
        }

        datePicker = DatePickerFragment.newInstance(this)
            .also { fragment ->
                fragment.setOnCancelListener { dismiss() }

                minDate?.also { fragment.setMinDate(it) }
                maxDate?.also { fragment.setMaxDate(it) }
            }
            .also {
                showFragmentWithTag(it, FRAGMENT_DATE_PICKER_TAG)
            }
    }

    private fun dismissDialogWithInvalidState() {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            R.string.projects_create_unknown_error_message,
            Snackbar.LENGTH_SHORT
        ).show()

        dismiss()
    }

    /**
     * Dismiss the DateTimePickerFragment.
     *
     *
     * Triggers the onDestroy-method for additional clean up.
     */
    private fun dismiss() {
        fragmentManager?.also {
            it.beginTransaction()
                .remove(this)
                .commit()
        }
    }

    private fun showFragmentWithTag(fragment: Fragment, tag: String) {
        fragmentManager?.also {
            it.beginTransaction()
                .add(fragment, tag)
                .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        datePicker?.also { fragment ->
            fragmentManager?.also {
                it.beginTransaction()
                    .remove(fragment)
                    .commit()
            }
        }
        datePicker = null

        timePicker?.also { fragment ->
            fragmentManager?.also {
                it.beginTransaction()
                    .remove(fragment)
                    .commit()
            }
        }
        timePicker = null
    }

    override fun onDateSet(datePicker: DatePicker, year: Int, month: Int, day: Int) {
        // Relay the selected year, month, and day to the stored calendar.
        calendar.set(year, month, day)

        timePicker = TimePickerFragment.newInstance(this)
            .also {
                // The timer picker only needs to listen to the "onDismiss"-event since
                // it will run for both set time and cancel.
                //
                // And, in either case we'd want to clean up the fragment.
                //
                // Also, I was unable to get the TimePickerDialog to trigger the
                // "onCancel"-event to the DialogFragment.
                it.setOnDismissListener { dismiss() }
                showFragmentWithTag(it, FRAGMENT_TIME_PICKER_TAG)
            }
    }

    override fun onTimeSet(timePicker: TimePicker, hour: Int, minute: Int) {
        onDateTimeSetListener?.apply {
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            onDateTimeSet(calendar)
        }
    }

    /**
     * Set the "OnDateTimeSetListener".
     *
     * @param onDateTimeSetListener Listener for "OnDateTimeSetListener".
     */
    protected fun setOnDateTimeSetListener(onDateTimeSetListener: OnDateTimeSetListener) {
        this.onDateTimeSetListener = onDateTimeSetListener
    }

    @FunctionalInterface
    interface OnDateTimeSetListener {
        /**
         * Listen for the selected date and time.
         *
         * @param calendar Selected date and time.
         */
        fun onDateTimeSet(calendar: Calendar)
    }

    companion object {
        private const val FRAGMENT_DATE_PICKER_TAG = "date picker"

        private const val FRAGMENT_TIME_PICKER_TAG = "time picker"
    }
}
