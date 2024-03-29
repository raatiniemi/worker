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

package me.raatiniemi.worker.feature.projects.createproject.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import me.raatiniemi.worker.WorkerTheme
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.feature.projects.createproject.model.CreateProjectViewActions
import me.raatiniemi.worker.feature.projects.createproject.viewmodel.CreateProjectViewModel
import me.raatiniemi.worker.feature.shared.view.observeAndConsume
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

internal typealias OnCreateProject = (Project?) -> Unit

class CreateProjectDialogFragment : DialogFragment() {
    private val usageAnalytics: UsageAnalytics by inject()
    private val vm: CreateProjectViewModel by viewModel()

    private var onCreateProject: OnCreateProject? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        usageAnalytics.setCurrentScreen(this)
        return ComposeView(requireContext())
            .apply {
                setContent {
                    WorkerTheme {
                        CreateProjectScreen(vm)
                    }
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureUserInterface()
        observeViewModel()
    }

    private fun configureUserInterface() {
        dialog?.also {
            it.setCanceledOnTouchOutside(false)
        }
    }

    private fun observeViewModel() {
        observeAndConsume(vm.viewActions) { viewAction ->
            when (viewAction) {
                is CreateProjectViewActions.Created -> {
                    viewAction(this, requireNotNull(onCreateProject))
                }
                is CreateProjectViewActions.Dismiss -> {
                    viewAction(this, requireNotNull(onCreateProject))
                }
            }
        }
    }

    companion object {
        @JvmStatic
        internal fun newInstance(onCreateProject: OnCreateProject): CreateProjectDialogFragment {
            return CreateProjectDialogFragment()
                .also { it.onCreateProject = onCreateProject }
        }
    }
}
