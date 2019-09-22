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

package ch.deletescape.lawnchair.cp

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import ch.deletescape.lawnchair.LawnchairApp
import ch.deletescape.lawnchair.appWidgetManager
import java.util.*

object CallbackManager {
    private val widgetRequests = mutableListOf<WidgetRequest>()

    fun postWidgetRequest(context: Context, callback: (id: Int) -> Unit) {
        val id = UUID.randomUUID().hashCode();
        widgetRequests += WidgetRequest(id, callback)
        context.startActivity(Intent(context, WidgetRequestActivity::class.java).apply {
            putExtra("request_id", id)
            flags = flags or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    data class WidgetRequest(val id: Int, val callback: (arg: Int) -> Unit) {
        var configured = false
    }

    class WidgetRequestActivity : Activity() {
        val id by lazy { intent.extras!!["request_id"] as Int }
        val request by lazy { widgetRequests.first { it.id == id } }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val id = (applicationContext as LawnchairApp).overlayWidgetHost.allocateAppWidgetId()
            startActivityForResult(Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).also {
                it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            }, id)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (resultCode == RESULT_OK) {
                val widgetInfo = appWidgetManager.getAppWidgetInfo(requestCode)
                if (widgetInfo?.configure != null && !request.configured) {
                    request.configured = true
                    startActivityForResult(Intent().setComponent(widgetInfo.configure), requestCode);
                } else {
                    request.callback(requestCode)
                    finish()
                }
            } else {
                request.callback(-1)
                finish()
            }
        }
    }
}