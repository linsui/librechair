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
 *     but WITHOUT ANY WARRANTY without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.cp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import ch.deletescape.lawnchair.LawnchairApp
import ch.deletescape.lawnchair.appWidgetManager
import ch.deletescape.lawnchair.lawnchairApp
import com.android.launcher3.R
import java.util.*

object NonoverlayCallbacks {
    private val widgetRequests = mutableListOf<OverlayCallbacks.WidgetRequest>()

    fun postWidgetRequest(context: Context, callback: (id: Int) -> Unit) {
        val id = UUID.randomUUID().hashCode()
        widgetRequests.add(OverlayCallbacks.WidgetRequest(id) {
            callback(it)
        })
        context.startActivity(Intent(context, WidgetRequestActivity::class.java).apply {
            putExtra("request_id", id)
            flags = flags or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    @SuppressLint("Registered")
    class WidgetRequestActivity : Activity() {
        val id by lazy { intent.extras!!["request_id"] as Int }
        val request by lazy { widgetRequests.first { it.id == id } }
        private var configurable = false

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short)
            val id = (applicationContext as LawnchairApp).overlayWidgetHost.allocateAppWidgetId()
            overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short)
            startActivityForResult(Intent(this, CustomWidgetPicker::class.java).also {
                it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            }, id)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (resultCode == RESULT_OK) {
                val widgetInfo = appWidgetManager.getAppWidgetInfo(requestCode)
                if (widgetInfo.configure != null && !request.configured) {
                    configurable = true
                    request.configured = true
                    startActivityForResult(Intent().setComponent(widgetInfo.configure), requestCode)
                    return
                } else {
                    request.callback(requestCode)
                    finish()
                    return
                }
            }
            if (request.configured && resultCode == RESULT_OK) {
                request.callback(requestCode)
                finish()
            } else {
                try {
                    request.callback(-1)
                    lawnchairApp.overlayWidgetHost.deleteAppWidgetId(requestCode)
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                }
                finish()
            }
        }

        class CustomWidgetPicker : Activity() {
            val id by lazy { intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) }

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                val widgetManager = appWidgetManager
                val providers = widgetManager.installedProviders

                val dialog = AlertDialog.Builder(this)
                dialog.setItems(providers.map {
                    it.loadLabel(packageManager)
                }.toTypedArray()) { _, which: Int ->
                    if (widgetManager.bindAppWidgetIdIfAllowed(id, providers[which].provider)) {
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND)
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER,
                                providers[which].provider)
                        startActivityForResult(intent, 11) // Launcher.REQUEST_BIND_APPWIDGET...
                    }
                }.setOnDismissListener {
                    setResult(RESULT_CANCELED)
                    finish()
                }.show()
            }

            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                super.onActivityResult(requestCode, resultCode, data)
                setResult(resultCode, data)
                finish()
            }
        }
    }
}