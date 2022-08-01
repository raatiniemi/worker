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

package me.raatiniemi.worker.feature.settings.view

import android.os.Bundle
import android.view.View
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import me.raatiniemi.worker.BuildConfig
import me.raatiniemi.worker.R
import me.raatiniemi.worker.feature.settings.viewmodel.SettingsViewModel
import me.raatiniemi.worker.feature.shared.view.configurePreference
import me.raatiniemi.worker.feature.shared.view.observeAndConsume
import me.raatiniemi.worker.feature.shared.view.onCheckChange
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val CONFIRM_CLOCK_OUT_KEY = "confirm_clock_out"
private const val TIME_SUMMARY_KEY = "time_summary"

private const val VERSION_KEY = "version"

class SettingsFragment : PreferenceFragmentCompat() {
    private val vm: SettingsViewModel by viewModel()
    private val usageAnalytics: UsageAnalytics by inject()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureUserInterface()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()

        usageAnalytics.setCurrentScreen(this)
    }

    private fun configureUserInterface() {
        configureConfirmClockOut()
        configureTimeSummary()
        configureVersion()
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

    private fun configureTimeSummary() {
        configurePreference<ListPreference>(TIME_SUMMARY_KEY) {
            value = vm.timeSummary.toString()

            setOnPreferenceChangeListener { _, newValue ->
                val startingPoint = Integer.parseInt(newValue as String)
                vm.changeTimeSummaryStartingPoint(startingPoint)

                true
            }
        }
    }

    private fun configureVersion() {
        configurePreference<Preference>(VERSION_KEY) {
            isSelectable = false
            summary = getString(
                R.string.settings_version_summary,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE
            )
        }
    }

    private fun observeViewModel() {
        observeAndConsume(vm.viewActions) {
            it(requireActivity())
        }
    }
}
