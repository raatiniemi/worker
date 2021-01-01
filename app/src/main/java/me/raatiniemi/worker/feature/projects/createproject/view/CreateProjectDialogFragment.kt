/*
 * Copyright (C) 2020 Tobias Raatiniemi
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
import me.raatiniemi.worker.feature.projects.createproject.model.CreateProjectViewActions
import me.raatiniemi.worker.feature.projects.createproject.viewmodel.CreateProjectViewModel
import me.raatiniemi.worker.feature.shared.view.*
import me.raatiniemi.worker.monitor.analytics.UsageAnalytics
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class CreateProjectDialogFragment : DialogFragment() {
    private val usageAnalytics: UsageAnalytics by inject()
    private val vm: CreateProjectViewModel by viewModel()

    private lateinit var onCreateProject: () -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialogfragment_create_project, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setTitle(R.string.projects_create_title)

        bindUserInterfaceToViewModel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()

        showKeyboard(etProjectName)
        usageAnalytics.setCurrentScreen(this)
    }

    private fun observeViewModel() {
        observe(vm.isCreateEnabled) {
            btnCreate.isEnabled = it
        }

        observeAndConsume(vm.viewActions) { viewAction ->
            when (viewAction) {
                is CreateProjectViewActions.CreatedProject -> {
                    onCreateProject()
                    viewAction(this)
                }
                is CreateProjectViewActions.InvalidProjectNameErrorMessage -> {
                    viewAction(etProjectName)
                }
                is CreateProjectViewActions.DuplicateNameErrorMessage -> viewAction(etProjectName)
                is CreateProjectViewActions.UnknownErrorMessage -> viewAction(etProjectName)
            }
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
        click(btnDismiss) {
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        internal fun newInstance(onCreateProject: () -> Unit): CreateProjectDialogFragment {
            return CreateProjectDialogFragment()
                .also { it.onCreateProject = onCreateProject }
        }
    }
}
