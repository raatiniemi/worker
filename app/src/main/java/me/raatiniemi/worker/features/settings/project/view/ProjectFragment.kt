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

package me.raatiniemi.worker.features.settings.project.view

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.preference.ListPreference
import androidx.preference.Preference
import com.google.android.material.snackbar.Snackbar
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.settings.project.presenter.ProjectPresenter
import me.raatiniemi.worker.features.settings.view.BasePreferenceFragment
import me.raatiniemi.worker.util.KeyValueStore
import me.raatiniemi.worker.util.Notifications
import me.raatiniemi.worker.util.NullUtil.isNull
import me.raatiniemi.worker.util.NullUtil.nonNull
import me.raatiniemi.worker.util.PreferenceUtil
import me.raatiniemi.worker.util.PresenterUtil.detachViewIfNotNull
import org.koin.android.ext.android.inject
import timber.log.Timber

class ProjectFragment : BasePreferenceFragment(), ProjectView, Preference.OnPreferenceChangeListener {
    private val keyValueStore: KeyValueStore by inject()

    private val presenter: ProjectPresenter by inject()
    private val isOngoingChannelEnabled: Boolean
        @RequiresApi(api = Build.VERSION_CODES.O)
        get() {
            try {
                val nm = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                return !Notifications.isOngoingChannelDisabled(nm)
            } catch (e: ClassCastException) {
                return true
            }
        }

    override val title = R.string.activity_settings_project

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter.attachView(this)

        populateCheckBoxPreference(CONFIRM_CLOCK_OUT_KEY, keyValueStore.confirmClockOut())

        try {
            val startingPointForTimeSummary = keyValueStore.startingPointForTimeSummary()

            val timeSummary = findPreference(TIME_SUMMARY_KEY) as ListPreference
            timeSummary.value = startingPointForTimeSummary.toString()
            timeSummary.onPreferenceChangeListener = this
        } catch (e: ClassCastException) {
            Timber.w(e, "Unable to set listener for 'time_summary'")
        }

        try {
            val timeReportSummaryFormatValue = keyValueStore.timeReportSummaryFormat()

            val timeReportSummaryFormat = findPreference(TIME_REPORT_SUMMARY_FORMAT_KEY) as ListPreference
            timeReportSummaryFormat.value = timeReportSummaryFormatValue.toString()
            timeReportSummaryFormat.onPreferenceChangeListener = this
        } catch (e: ClassCastException) {
            Timber.w(e, "Unable to set listener for 'settings_project_time_report_summary_format'")
        }

        populateCheckBoxPreference(ONGOING_NOTIFICATION_ENABLE_KEY, keyValueStore.ongoingNotification())
        val preference = findPreference(ONGOING_NOTIFICATION_ENABLE_KEY)
        if (nonNull(preference)) {
            preference.setSummary(R.string.activity_settings_project_ongoing_notification_enable_summary)
        }

        preference.isEnabled = isOngoingChannelEnabled
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_project)
    }

    private fun populateCheckBoxPreference(
            preferenceKey: String,
            shouldCheck: Boolean
    ) {
        val preference = findPreference(preferenceKey)
        if (isNull(preference)) {
            Timber.w("Unable to find preference with key: %s", preferenceKey)
            return
        }

        PreferenceUtil.populateCheckBoxPreference(preference, shouldCheck)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        detachViewIfNotNull(presenter)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return when (preference?.key) {
            CONFIRM_CLOCK_OUT_KEY -> {
                toggleConfirmClockOut(preference)
                true
            }
            TIME_SUMMARY_KEY, TIME_REPORT_SUMMARY_FORMAT_KEY -> true
            ONGOING_NOTIFICATION_ENABLE_KEY -> {
                toggleOngoingNotification(preference)
                true
            }
            ONGOING_NOTIFICATION_CHRONOMETER_KEY -> {
                toggleOngoingNotificationChronometer(preference)
                true
            }
            else -> super.onPreferenceTreeClick(preference)
        }
    }

    private fun toggleConfirmClockOut(preference: Preference) {
        PreferenceUtil.readCheckBoxPreference(preference) { keyValueStore.setConfirmClockOut(it) }
    }

    private fun toggleOngoingNotification(preference: Preference) {
        PreferenceUtil.readCheckBoxPreference(preference) { isChecked ->
            if (isChecked) {
                keyValueStore.enableOngoingNotification()
                return@readCheckBoxPreference
            }

            keyValueStore.disableOngoingNotification()
        }
    }

    private fun toggleOngoingNotificationChronometer(preference: Preference) {
        PreferenceUtil.readCheckBoxPreference(preference) { isChecked ->
            if (isChecked) {
                keyValueStore.enableOngoingNotificationChronometer()
                return@readCheckBoxPreference
            }

            keyValueStore.disableOngoingNotificationChronometer()
        }
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        return when (preference.key) {
            TIME_SUMMARY_KEY -> {
                changeTimeSummaryStartingPoint(newValue)
                true
            }
            TIME_REPORT_SUMMARY_FORMAT_KEY -> {
                changeTimeReportSummaryFormat(newValue)
                true
            }
            else -> false
        }
    }

    private fun changeTimeSummaryStartingPoint(newStartingPoint: Any) {
        val startingPoint = Integer.parseInt(newStartingPoint as String)
        presenter.changeTimeSummaryStartingPoint(startingPoint)
    }

    override fun showChangeTimeSummaryStartingPointToWeekSuccessMessage() {
        val contentView = requireActivity().findViewById<View>(android.R.id.content)
        if (isNull(contentView)) {
            return
        }

        val snackBar = Snackbar.make(
                contentView,
                R.string.message_change_time_summary_starting_point_week,
                Snackbar.LENGTH_LONG
        )
        snackBar.show()
    }

    override fun showChangeTimeSummaryStartingPointToMonthSuccessMessage() {
        val contentView = requireActivity().findViewById<View>(android.R.id.content)
        if (isNull(contentView)) {
            return
        }

        val snackBar = Snackbar.make(
                contentView,
                R.string.message_change_time_summary_starting_point_month,
                Snackbar.LENGTH_LONG
        )
        snackBar.show()
    }

    override fun showChangeTimeSummaryStartingPointErrorMessage() {
        val contentView = requireActivity().findViewById<View>(android.R.id.content)
        if (isNull(contentView)) {
            return
        }

        val snackBar = Snackbar.make(
                contentView,
                R.string.error_message_change_time_summary_starting_point,
                Snackbar.LENGTH_LONG
        )
        snackBar.show()
    }

    private fun changeTimeReportSummaryFormat(newValue: Any) {
        val newFormat = Integer.parseInt(newValue as String)
        presenter.changeTimeReportSummaryFormat(newFormat)
    }

    override fun showChangeTimeReportSummaryToFractionSuccessMessage() {
        val contentView = requireActivity().findViewById<View>(android.R.id.content)
        if (isNull(contentView)) {
            return
        }

        val snackBar = Snackbar.make(
                contentView,
                R.string.message_change_time_report_summary_format_fraction,
                Snackbar.LENGTH_LONG
        )
        snackBar.show()
    }

    override fun showChangeTimeReportSummaryToDigitalClockSuccessMessage() {
        val contentView = requireActivity().findViewById<View>(android.R.id.content)
        if (isNull(contentView)) {
            return
        }

        val snackBar = Snackbar.make(
                contentView,
                R.string.message_change_time_report_summary_format_digital_clock,
                Snackbar.LENGTH_LONG
        )
        snackBar.show()
    }

    override fun showChangeTimeReportSummaryFormatErrorMessage() {
        val contentView = requireActivity().findViewById<View>(android.R.id.content)
        if (isNull(contentView)) {
            return
        }

        val snackBar = Snackbar.make(
                contentView,
                R.string.error_message_change_time_report_summary_format,
                Snackbar.LENGTH_LONG
        )
        snackBar.show()
    }

    companion object {
        private const val CONFIRM_CLOCK_OUT_KEY = "settings_project_confirm_clock_out"
        private const val TIME_SUMMARY_KEY = "settings_project_time_summary"
        private const val TIME_REPORT_SUMMARY_FORMAT_KEY = "settings_project_time_report_summary_format"
        private const val ONGOING_NOTIFICATION_ENABLE_KEY = "settings_project_ongoing_notification_enable"
        private const val ONGOING_NOTIFICATION_CHRONOMETER_KEY = "settings_project_ongoing_notification_chronometer"
    }
}
