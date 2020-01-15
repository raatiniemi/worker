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

package me.raatiniemi.worker.feature.shared.view.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable

internal class LetterDrawable(private val character: Char) : ShapeDrawable() {
    private val textPaint = Paint()
    private val width
        get() = bounds.width()

    private val height
        get() = bounds.height()

    private val textSize
        get() = Math.min(width, height).toFloat() / 2

    private val text by lazy {
        character.toString()
    }

    init {
        textPaint.color = Color.WHITE
        textPaint.isAntiAlias = true
        textPaint.isFakeBoldText = true
        textPaint.textAlign = Paint.Align.CENTER

        paint.color = BACKGROUND_COLOR
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        textPaint.textSize = textSize

        val (x, y) = adjustVerticalCenterPointForTextSize(
            calculateTextPosition(width, height),
            calculateBaselineOffset(textPaint)
        )
        canvas.drawText(text, x, y, textPaint)
    }

    override fun getIntrinsicWidth() = width

    override fun getIntrinsicHeight() = height

    companion object {
        private const val BACKGROUND_COLOR = -0x6b868687
    }
}

internal fun letterDrawable(character: Char): LetterDrawable {
    return LetterDrawable(character)
}

private typealias Position = Pair<Float, Float>

private fun calculateCenterPoint(s: Int) = s.toFloat() / 2

private fun calculateTextPosition(width: Int, height: Int): Position =
    calculateCenterPoint(width) to calculateCenterPoint(height)

private fun calculateBaselineOffset(textPaint: Paint) =
    (textPaint.ascent() + textPaint.descent()) / 2

private fun adjustVerticalCenterPointForTextSize(position: Position, baselineOffset: Float) =
    position.let { (x, y) ->
        x to (y - baselineOffset)
    }
