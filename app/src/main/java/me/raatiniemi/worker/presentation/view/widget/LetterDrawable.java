/*
 * Copyright (C) 2017 Worker Project
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

package me.raatiniemi.worker.presentation.view.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;

public class LetterDrawable extends ShapeDrawable {
    private static final int BACKGROUND_COLOR = 0x94797979;

    private final Paint textPaint = new Paint();
    private final String text;

    private LetterDrawable(String text) {
        super();

        this.text = text;

        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint paint = getPaint();
        paint.setColor(BACKGROUND_COLOR);
    }

    public static LetterDrawable build(String text) {
        return new LetterDrawable(text);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        textPaint.setTextSize(getFontSize());

        Point textPosition = calculateTextPosition();
        canvas.drawText(text, textPosition.x, textPosition.y, textPaint);
    }

    private int getFontSize() {
        return Math.min(getWidth(), getHeight()) / 2;
    }

    private Point calculateTextPosition() {
        return new Point(
                calculateHorizontalTextPosition(),
                calculateVerticalTextPosition()
        );
    }

    private int calculateHorizontalTextPosition() {
        return getWidth() / 2;
    }

    private int calculateVerticalTextPosition() {
        int verticalCenterPosition = getHeight() / 2;

        return adjustCalculationForFont(verticalCenterPosition);
    }

    private int adjustCalculationForFont(int verticalCenterPosition) {
        int calculatedFontAdjustment = (int) (textPaint.ascent() + textPaint.descent()) / 2;

        return verticalCenterPosition - calculatedFontAdjustment;
    }

    private int getWidth() {
        return getBounds().width();
    }

    private int getHeight() {
        return getBounds().height();
    }

    @Override
    public int getIntrinsicWidth() {
        return getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return getHeight();
    }
}
