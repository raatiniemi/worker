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

package me.raatiniemi.worker

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
internal fun WorkerTheme(content: @Composable () -> Unit) {
    val typography = Typography(
        h2 = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            letterSpacing = 1.sp
        ),
        body1 = TextStyle(
            fontSize = 16.sp
        )
    )
    val colors = lightColors(
        primary = Color(0xff03a9f4),
        primaryVariant = Color(0xff0288d1),
        onPrimary = Color.White,
        secondary = Color(0xffff4081),
        onSecondary = Color.White,
        background = Color(0xfffafafa),
        onBackground = Color(0xff757575),
        surface = Color.White,
        onSurface = Color(0xff757575),
        error = Color.Red,
        onError = Color.White
    )

    MaterialTheme(
        colors = colors,
        typography = typography,
        content = content
    )
}
