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

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.ongoing.service.DismissOngoingNotificationsService
import me.raatiniemi.worker.features.ongoing.service.ReloadNotificationService
import me.raatiniemi.worker.features.settings.project.viewmodel.ProjectViewModel
import me.raatiniemi.worker.features.shared.view.configurePreference
import me.raatiniemi.worker.features.shared.view.isOngoingChannelDisabled
import me.raatiniemi.worker.features.shared.view.observeAndConsume
import me.raatiniemi.worker.features.shared.view.onCheckChange
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class ProjectFragment : PreferenceFragmentCompat() {
    private val usageAnalytics: UsageAnalytics by inject()
    private val vm: ProjectViewModel by viewModel()

    private val isOngoingChannelEnabled: Boolean by lazy {
        !isOngoingChannelDisabled(requireContext())
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_project)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureView()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()

        usageAnalytics.setCurrentScreen(this)
    }

    private fun configureView() {
        configureTimeSummaryStartingPoint()
        configureOngoingNotification()
    }

    private fun configureTimeSummaryStartingPoint() {
        configurePreference<ListPreference>(TIME_SUMMARY_KEY) {
            value = vm.timeSummary.toString()

            setOnPreferenceChangeListener { _, newValue ->
                val startingPoint = Integer.parseInt(newValue as String)
                vm.changeTimeSummaryStartingPoint(startingPoint)

                true
            }
        }
    }

    private fun configureOngoingNotification() {
        configurePreference<CheckBoxPreference>(ONGOING_NOTIFICATION_ENABLE_KEY) {
            isEnabled = isOngoingChannelEnabled
            isChecked = vm.ongoingNotificationEnabled
            setSummary(R.string.settings_project_ongoing_notification_enable_summary)

            onCheckChange {
                vm.ongoingNotificationEnabled = it
                if (vm.ongoingNotificationEnabled) {
                    reloadOngoingNotifications()
                } else {
                    dismissOngoingNotifications()
                }

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
                    reloadOngoingNotifications()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun reloadOngoingNotifications() {
        Intent(requireContext(), ReloadNotificationService::class.java)
            .let { requireContext().startService(it) }
    }

    private fun dismissOngoingNotifications() {
        Intent(requireContext(), DismissOngoingNotificationsService::class.java)
            .let { requireContext().startService(it) }
    }

    private fun observeViewModel() {
        observeAndConsume(vm.viewActions) {
            it.action(requireActivity())
        }
    }

    companion object {
        private const val TIME_SUMMARY_KEY = "settings_project_time_summary"
        private const val ONGOING_NOTIFICATION_ENABLE_KEY =
            "settings_project_ongoing_notification_enable"
        private const val ONGOING_NOTIFICATION_CHRONOMETER_KEY =
            "settings_project_ongoing_notification_chronometer"
    }
}
