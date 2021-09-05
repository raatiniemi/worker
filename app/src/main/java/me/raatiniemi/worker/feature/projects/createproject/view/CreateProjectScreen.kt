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

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.raatiniemi.worker.R
import me.raatiniemi.worker.WorkerTheme
import me.raatiniemi.worker.feature.projects.createproject.model.CreateProjectError
import me.raatiniemi.worker.feature.projects.createproject.model.CreateProjectState
import me.raatiniemi.worker.feature.projects.createproject.model.emptyCreateProjectState
import me.raatiniemi.worker.feature.projects.createproject.viewmodel.CreateProjectViewModel
import me.raatiniemi.worker.feature.shared.model.Error

@Composable
internal fun CreateProjectScreen(vm: CreateProjectViewModel) {
    val name: String by vm.name.observeAsState("")
    val error: Error? by vm.error.observeAsState()

    val scope = rememberCoroutineScope()
    CreateProjectContent(
        state = CreateProjectState(name, error),
        onNameChange = {
            scope.launch {
                vm.onNameChange(it)
            }
        },
        onDismiss = vm::dismiss,
        onCreate = {
            scope.launch {
                vm.createProject(name)
            }
        }
    )
}

@Composable
private fun CreateProjectContent(
    state: CreateProjectState = emptyCreateProjectState(),
    onNameChange: (String) -> Unit = { },
    onDismiss: () -> Unit = { },
    onCreate: (String) -> Unit = { }
) {
    CreateProjectCard {
        CreateProjectTitle()
        CreateProjectDescription()
        CreateProjectTextField(state, onNameChange, onCreate)
        CreateProjectButtonGroup {
            CreateProjectDismissButton(onDismiss)
            CreateProjectSubmitButton(state, onCreate)
        }
    }
}

@Composable
private fun CreateProjectCard(content: @Composable ColumnScope.() -> Unit) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp, 20.dp),
            content = content
        )
    }
}

@Composable
private fun CreateProjectTitle() {
    Text(
        text = stringResource(id = R.string.projects_create_title).uppercase(),
        style = typography.h2,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun CreateProjectDescription() {
    Text(
        text = stringResource(id = R.string.projects_create_description),
        style = typography.body1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun CreateProjectTextField(
    state: CreateProjectState,
    onNameChange: (String) -> Unit,
    onCreate: (String) -> Unit
) {
    OutlinedTextField(
        value = state.name,
        onValueChange = onNameChange,
        label = {
            Text(text = stringResource(id = R.string.projects_create_name_hint))
        },
        isError = state.error != null,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                onCreate(state.name)
            }
        )
    )
    state.error?.let {
        CreateProjectErrorMessage(it)
    }
}

@Composable
private fun CreateProjectErrorMessage(error: Error) {
    Text(
        text = message(error),
        style = typography.body2
            .copy(color = colors.error),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

@Composable
private fun message(error: Error) = when (error) {
    CreateProjectError.InvalidName -> stringResource(id = R.string.projects_create_missing_name_error_message)
    CreateProjectError.ProjectAlreadyExists -> stringResource(id = R.string.projects_create_project_already_exists_error_message)
    else -> stringResource(id = R.string.projects_create_unknown_error_message)
}

@Composable
private fun CreateProjectButtonGroup(content: @Composable RowScope.() -> Unit) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp),
        content = content
    )
}

@Composable
private fun CreateProjectDismissButton(onDismiss: () -> Unit) {
    TextButton(
        onClick = onDismiss,
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.projects_create_dismiss).uppercase()
        )
    }
}

@Composable
private fun CreateProjectSubmitButton(state: CreateProjectState, onCreate: (String) -> Unit) {
    TextButton(
        onClick = { onCreate(state.name) },
        enabled = canCreateProject(state)
    ) {
        Text(
            text = stringResource(id = R.string.projects_create_submit).uppercase()
        )
    }
}

private fun canCreateProject(state: CreateProjectState): Boolean {
    return state.error == null && state.name.isNotBlank()
}

@Preview
@Composable
fun CreateProjectContentPreview() {
    WorkerTheme {
        CreateProjectContent()
    }
}

@Preview
@Composable
fun CreateProjectContentWithContentPreview() {
    WorkerTheme {
        CreateProjectContent(
            state = CreateProjectState(
                name = "Worker"
            )
        )
    }
}

@Preview
@Composable
fun CreateProjectContentWithErrorPreview() {
    WorkerTheme {
        CreateProjectContent(
            state = CreateProjectState(
                error = CreateProjectError.ProjectAlreadyExists
            )
        )
    }
}
