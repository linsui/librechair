/*
 *     Copyright (c) 2017-2019 the Lawnchair team
 *     Copyright (c)  2019 oldosfan (would)
 *     This file is part of Lawnchair Launcher.
 *
 *     Lawnchair Launcher is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Lawnchair Launcher is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*

class VectorEditorView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    var lastTouchEvent: Pair<Float, Float>? = null
    val vector: Vector<Pair<Float, Float>> = Vector()
    val vHeight: Int = 256
    val vWidth: Int = 256
    val paint = Paint()
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP) {
            return false
        } else if (lastTouchEvent == null) {
            lastTouchEvent = event.x to event.y
            return true
        } else {
            vector += lastTouchEvent
            vector += event.x to event.y
            lastTouchEvent = event.x to event.y
            postInvalidate()
            return true
        }
    }

    private fun updateCanvas(canvas: Canvas) {
        for (i in 0 until vector.size) {
            if (i == vector.size - 1) {
                return
            } else {
                val (x, y) = vector[i]
                val (endX, endY) = vector[i + 1]
                canvas.drawLine(x, y, endX, endY, paint)
            }
        }
    }

    @SuppressLint("CanvasSize")
    override fun onDraw(canvas: Canvas) {
        updateCanvas(canvas)
    }
}