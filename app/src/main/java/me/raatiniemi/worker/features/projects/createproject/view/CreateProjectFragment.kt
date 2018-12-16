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
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_create_project.*
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.projects.createproject.model.CreateProjectEvent
import me.raatiniemi.worker.features.projects.createproject.viewmodel.CreateProjectViewModel
import me.raatiniemi.worker.features.shared.view.*
import me.raatiniemi.worker.util.Keyboard
import me.raatiniemi.worker.util.NullUtil.isNull
import org.greenrobot.eventbus.EventBus
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class CreateProjectFragment : CoroutineScopedDialogFragment(), DialogInterface.OnShowListener {
    private val eventBus = EventBus.getDefault()
    private val vm: CreateProjectViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_create_project, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog.setTitle(R.string.fragment_create_project_title)
        dialog.setOnShowListener(this)

        observeViewModel()
        bindUserInterfaceToViewModel()
    }

    private fun observeViewModel() {
        vm.isCreateEnabled.observe(this, Observer {
            btnCreate.isEnabled = it
        })

        vm.viewActions.observeAndConsume(this, Observer {
            it.action(requireContext(), etProjectName)
        })

        // TODO: replace use of event bus with interface.
        vm.project.observe(this, Observer {
            eventBus.post(CreateProjectEvent(it))

            dismiss()
        })
    }

    private fun bindUserInterfaceToViewModel() {
        etProjectName.onChange { vm.projectName = it }
        etProjectName.on(EditorAction.DONE) {
            vm.createProject()
        }

        btnCreate.onClick { vm.createProject() }
        btnDismiss.setOnClickListener { dismiss() }
    }

    override fun onShow(dialog: DialogInterface?) {
        // We might have dismissed the dialog, we have to make sure that the
        // dialog and activity are still available before we can continue.
        if (isNull(dialog) || isNull(activity)) {
            Timber.d("No dialog/activity available, exiting...")
            return
        }

        // Force the keyboard to show when the dialog is showing.
        Keyboard.show(requireContext())
    }

    companion object {
        @JvmStatic
        fun newInstance() = CreateProjectFragment()
    }
}
