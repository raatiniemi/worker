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

package me.raatiniemi.worker.feature.projects.createproject.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialogfragment_create_project.*
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.feature.projects.createproject.model.CreateProjectViewActions
import me.raatiniemi.worker.feature.projects.createproject.viewmodel.CreateProjectViewModel
import me.raatiniemi.worker.feature.shared.view.*
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

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
        configureUserInterface()
        return inflater.inflate(R.layout.dialogfragment_create_project, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindUserInterfaceToViewModel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()

        try {
            requireNotNull(onCreateProject) {
                "No `OnCreateProject` closure is available"
            }

            showKeyboard(etProjectName)
            usageAnalytics.setCurrentScreen(this)
        } catch (e: IllegalArgumentException) {
            Timber.w(e, "Unable to show create project dialog")
            dismiss()
        }
    }

    private fun configureUserInterface() {
        dialog?.also {
            it.setTitle(R.string.projects_create_title)
            it.setCanceledOnTouchOutside(false)
        }
    }

    private fun bindUserInterfaceToViewModel() {
        doOnTextChange(etProjectName) { vm.name = it }
        done(etProjectName) {
            vm.createProject()
        }

        click(btnCreate) {
            vm.createProject()
        }
        click(btnDismiss) { vm.dismiss() }
    }

    private fun observeViewModel() {
        observe(vm.isCreateEnabled) {
            btnCreate.isEnabled = it
        }

        observeAndConsume(vm.viewActions) { viewAction ->
            when (viewAction) {
                is CreateProjectViewActions.InvalidProjectNameErrorMessage -> {
                    viewAction(etProjectName)
                }
                is CreateProjectViewActions.DuplicateNameErrorMessage -> viewAction(etProjectName)
                is CreateProjectViewActions.UnknownErrorMessage -> viewAction(etProjectName)
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
