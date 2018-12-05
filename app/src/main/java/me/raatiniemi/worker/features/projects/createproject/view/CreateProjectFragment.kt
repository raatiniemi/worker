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

package me.raatiniemi.worker.features.projects.createproject.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.fragment_create_project.*
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.features.projects.createproject.model.CreateProjectEvent
import me.raatiniemi.worker.features.projects.createproject.viewmodel.CreateProjectViewModel
import me.raatiniemi.worker.features.shared.view.fragment.RxDialogFragment
import me.raatiniemi.worker.features.shared.view.onChange
import me.raatiniemi.worker.util.Keyboard
import me.raatiniemi.worker.util.NullUtil.isNull
import me.raatiniemi.worker.util.RxUtil.applySchedulers
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import timber.log.Timber

class CreateProjectFragment : RxDialogFragment(), DialogInterface.OnShowListener {
    private val eventBus = EventBus.getDefault()
    private val vm: CreateProjectViewModel.ViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm.output.createProjectSuccess
                .compose(bindToLifecycle())
                .compose(applySchedulers())
                .subscribe { success(it) }

        vm.error.invalidProjectNameError
                .compose(bindToLifecycle())
                .subscribe { it.action(context, etProjectName) }

        vm.error.duplicateProjectNameError
                .compose(bindToLifecycle())
                .subscribe { it.action(context, etProjectName) }

        vm.error.createProjectError
                .compose(bindToLifecycle())
                .subscribe { it.action(context, etProjectName) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_create_project, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog.setTitle(R.string.fragment_create_project_title)
        dialog.setOnShowListener(this)

        etProjectName.onChange { vm.input.projectName(it) }
        etProjectName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                vm.createProject()
                return@setOnEditorActionListener true
            }
            false
        }

        btnCreate.setOnClickListener { vm.input.createProject() }
        btnDismiss.setOnClickListener { dismiss() }

        vm.output.isProjectNameValid
                .compose(bindToLifecycle())
                .subscribe { btnCreate.isEnabled = it }
    }

    override fun onShow(dialog: DialogInterface?) {
        // We might have dismissed the dialog, we have to make sure that the
        // dialog and activity are still available before we can continue.
        if (isNull(dialog) || isNull(activity)) {
            Timber.d("No dialog/activity available, exiting...")
            return
        }

        // Force the keyboard to show when the dialog is showing.
        Keyboard.show(activity)
    }

    private fun success(project: Project) {
        eventBus.post(CreateProjectEvent(project))

        dismiss()
    }

    companion object {
        @JvmStatic
        fun newInstance() = CreateProjectFragment()
    }
}
