/*
 * Copyright (c) 2019 oldosfan.
 * Copyright (c) 2019 the Lawnchair developers
 *
 *     This file is part of Librechair.
 *
 *     Librechair is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Librechair is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

////////////////////////////////////////////////////////////////////////////////
//
//  Location - An Android location app.
//
//  Copyright (C) 2015	Bill Farmer
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Bill Farmer	 william j farmer [at] yahoo [dot] co [dot] uk.
//
///////////////////////////////////////////////////////////////////////////////


package ch.deletescape.lawnchair.feed.maps;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;

import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;


public class TextOverlay extends Overlay {
    private Paint paint;
    private int xOffset = 10;
    private int yOffset = 10;
    private boolean alignBottom = true;
    private boolean alignRight = false;
    private final DisplayMetrics dm;
    private String text;
    // Constructor

    public TextOverlay(Context context) {
        super();

        // Get the string
        Resources resources = context.getResources();

        // Get the display metrics
        dm = resources.getDisplayMetrics();

        // Get paint
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(dm.density * 12);
    }

    public void setTextSize(int fontSize) {
        paint.setTextSize(dm.density * fontSize);
    }

    public void setTextColor(int color) {
        paint.setColor(color);
    }
    // Set alignBottom

    public void setAlignBottom(boolean alignBottom) {
        this.alignBottom = alignBottom;
    }

    // Set alignRight

    public void setAlignRight(boolean alignRight) {
        this.alignRight = alignRight;
    }

    /**
     * Sets the screen offset. Values are in real pixels, not dip
     *
     * @param x horizontal screen offset, if aligh right is set, the offset is from the right, otherwise lift
     * @param y vertical screen offset, if align bottom is set, the offset is pixels from the bottom (not the top)
     */
    public void setOffset(final int x, final int y) {
        xOffset = x;
        yOffset = y;
    }

    @Override
    public void draw(Canvas canvas, MapView map, boolean shadow) {
        draw(canvas, map.getProjection());
    }

    /**
     * @since 6.1.0
     */
    @Override
    public void draw(final Canvas canvas, final Projection pProjection) {
        if (text == null || text.length() == 0)
            return;

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        float x;
        float y;

        if (alignRight) {
            x = width - xOffset;
            paint.setTextAlign(Paint.Align.RIGHT);
        } else {
            x = xOffset;
            paint.setTextAlign(Paint.Align.LEFT);
        }

        if (alignBottom)
            y = height - yOffset;
        else
            y = paint.getTextSize() + yOffset;

        // Draw the text
        pProjection.save(canvas, false, false);
        canvas.drawText(text, x, y, paint);
        pProjection.restore(canvas, false);
    }

    /**
     * @since 6.1.0
     */
    public void setText(final String pText) {
        text = pText;
    }
}