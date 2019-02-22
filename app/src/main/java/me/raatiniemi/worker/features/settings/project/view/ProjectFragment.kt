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
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.settings.project.viewmodel.ProjectViewModel
import me.raatiniemi.worker.features.settings.view.BasePreferenceFragment
import me.raatiniemi.worker.features.shared.view.configurePreference
import me.raatiniemi.worker.features.shared.view.onCheckChange
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.KeyValueStore
import me.raatiniemi.worker.util.Notifications
import me.raatiniemi.worker.util.TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class ProjectFragment : BasePreferenceFragment(), Preference.OnPreferenceChangeListener {
    private val keyValueStore: KeyValueStore by inject()

    private val vm: ProjectViewModel by viewModel()

    private val isOngoingChannelEnabled: Boolean by lazy {
        try {
            val nm = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            return@lazy !Notifications.isOngoingChannelDisabled(nm)
        } catch (e: ClassCastException) {
            return@lazy true
        }
    }

    override val title = R.string.activity_settings_project

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_project)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureView()
        observeViewModel()
    }

    private fun configureView() {
        configureConfirmClockOut()
        configureTimeSummaryStartingPoint()
        configureTimeReportSummaryFormat()
        configureOngoingNotification()
    }

    private fun configureConfirmClockOut() {
        configurePreference<CheckBoxPreference>(CONFIRM_CLOCK_OUT_KEY) {
            isChecked = vm.confirmClockOut

            onCheckChange {
                vm.confirmClockOut = it
                true
            }
        }
    }

    private fun configureTimeSummaryStartingPoint() {
        configurePreference<ListPreference>(TIME_SUMMARY_KEY) {
            value = vm.timeSummary.toString()
            onPreferenceChangeListener = this@ProjectFragment
        }
    }

    private fun configureTimeReportSummaryFormat() {
        configurePreference<ListPreference>(TIME_REPORT_SUMMARY_FORMAT_KEY) {
            val timeReportSummaryFormat = keyValueStore.int(
                    AppKeys.TIME_REPORT_SUMMARY_FORMAT.rawValue,
                    TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK
            )

            value = timeReportSummaryFormat.toString()
            onPreferenceChangeListener = this@ProjectFragment
        }
    }

    private fun configureOngoingNotification() {
        configurePreference<CheckBoxPreference>(ONGOING_NOTIFICATION_ENABLE_KEY) {
            isEnabled = isOngoingChannelEnabled
            isChecked = vm.ongoingNotificationEnabled
            setSummary(R.string.activity_settings_project_ongoing_notification_enable_summary)

            onCheckChange {
                vm.ongoingNotificationEnabled = it

                configurePreference<CheckBoxPreference>(ONGOING_NOTIFICATION_CHRONOMETER_KEY) {
                    isEnabled = vm.ongoingNotificationEnabled
                }
                true
            }
        }
        configurePreference<CheckBoxPreference>(ONGOING_NOTIFICATION_CHRONOMETER_KEY) {
            isEnabled = isOngoingChannelEnabled && vm.ongoingNotificationEnabled
            isChecked = vm.ongoingNotificationChronometerEnabled

            onCheckChange {
                if (vm.ongoingNotificationEnabled) {
                    vm.ongoingNotificationChronometerEnabled = it
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun observeViewModel() {
        vm.viewActions.observeAndConsume(this, Observer {
            it.action(requireActivity())
        })
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return when (preference?.key) {
            CONFIRM_CLOCK_OUT_KEY -> false
            TIME_SUMMARY_KEY, TIME_REPORT_SUMMARY_FORMAT_KEY -> true
            ONGOING_NOTIFICATION_ENABLE_KEY -> false
            ONGOING_NOTIFICATION_CHRONOMETER_KEY -> false
            else -> super.onPreferenceTreeClick(preference)
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
