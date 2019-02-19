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
import androidx.lifecycle.Observer
import androidx.preference.ListPreference
import androidx.preference.Preference
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.features.settings.project.viewmodel.ProjectViewModel
import me.raatiniemi.worker.features.settings.view.BasePreferenceFragment
import me.raatiniemi.worker.util.*
import me.raatiniemi.worker.util.NullUtil.isNull
import me.raatiniemi.worker.util.NullUtil.nonNull
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class ProjectFragment : BasePreferenceFragment(), Preference.OnPreferenceChangeListener {
    private val keyValueStore: KeyValueStore by inject()

    private val vm: ProjectViewModel by viewModel()

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

        populateCheckBoxPreference(CONFIRM_CLOCK_OUT_KEY, vm.confirmClockOut)

        try {
            val startingPointForTimeSummary = keyValueStore.int(
                    AppKeys.TIME_SUMMARY.rawValue,
                    TimeIntervalStartingPoint.MONTH.rawValue
            )

            val timeSummary = findPreference(TIME_SUMMARY_KEY) as ListPreference
            timeSummary.value = startingPointForTimeSummary.toString()
            timeSummary.onPreferenceChangeListener = this
        } catch (e: ClassCastException) {
            Timber.w(e, "Unable to set listener for 'time_summary'")
        }

        try {
            val timeReportSummaryFormatValue = keyValueStore.int(
                    AppKeys.TIME_REPORT_SUMMARY_FORMAT.rawValue,
                    TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK
            )

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.viewActions.observeAndConsume(this, Observer {
            it.action(requireActivity())
        })
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

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return when (preference?.key) {
            CONFIRM_CLOCK_OUT_KEY -> {
                PreferenceUtil.readCheckBoxPreference(preference) {
                    vm.confirmClockOut = it
                }
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
        vm.changeTimeSummaryStartingPoint(startingPoint)
    }

    private fun changeTimeReportSummaryFormat(newValue: Any) {
        val newFormat = Integer.parseInt(newValue as String)
        vm.changeTimeReportSummaryFormat(newFormat)
    }

    companion object {
        private const val CONFIRM_CLOCK_OUT_KEY = "settings_project_confirm_clock_out"
        private const val TIME_SUMMARY_KEY = "settings_project_time_summary"
        private const val TIME_REPORT_SUMMARY_FORMAT_KEY = "settings_project_time_report_summary_format"
        private const val ONGOING_NOTIFICATION_ENABLE_KEY = "settings_project_ongoing_notification_enable"
        private const val ONGOING_NOTIFICATION_CHRONOMETER_KEY = "settings_project_ongoing_notification_chronometer"
    }
}
