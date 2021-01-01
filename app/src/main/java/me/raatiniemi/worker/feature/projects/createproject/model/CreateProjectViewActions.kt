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

package me.raatiniemi.worker.feature.projects.createproject.model

import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.DialogFragment
import me.raatiniemi.worker.R
import me.raatiniemi.worker.feature.shared.model.DialogFragmentViewAction
import me.raatiniemi.worker.feature.shared.model.EditTextViewAction

internal sealed class CreateProjectViewActions {
    object InvalidProjectNameErrorMessage : CreateProjectViewActions(), EditTextViewAction {
        override fun accept(t: AppCompatEditText) {
            t.error = t.context.getString(R.string.projects_create_missing_name_error_message)
        }
    }

    object DuplicateNameErrorMessage : CreateProjectViewActions(), EditTextViewAction {
        override fun accept(t: AppCompatEditText) {
            t.error = t.context.getString(
                R.string.projects_create_project_already_exists_error_message
            )
        }
    }

    object UnknownErrorMessage : CreateProjectViewActions(), EditTextViewAction {
        override fun accept(t: AppCompatEditText) {
            t.error = t.context.getString(R.string.projects_create_unknown_error_message)
        }
    }

    object CreatedProject : CreateProjectViewActions(), DialogFragmentViewAction {
        override fun accept(t: DialogFragment) {
            t.dismiss()
        }
    }
}
