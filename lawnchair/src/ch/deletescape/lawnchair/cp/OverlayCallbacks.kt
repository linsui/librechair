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

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import ch.deletescape.lawnchair.LawnchairApp
import ch.deletescape.lawnchair.appWidgetManager
import ch.deletescape.lawnchair.feed.images.ImageStore
import ch.deletescape.lawnchair.lawnchairApp
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import java.util.*

object OverlayCallbacks {
    private val widgetRequests = mutableListOf<WidgetRequest>()
    private val imageRequests = mutableListOf<ImageRequest>()

    fun postWidgetRequest(context: Context, callback: (id: Int) -> Unit) {
        val id = UUID.randomUUID().hashCode()
        widgetRequests.add(WidgetRequest(id) {
            d("postWidgetRequest: retrieved widget $it")
            callback(it)
        })
        context.startActivity(Intent(context, WidgetRequestActivity::class.java).apply {
            putExtra("request_id", id)
            flags = flags or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        })
    }

    fun postImageRequest(context: Context, view: View? = null, callback: (storeUUID: String?) -> Unit) {
        val id = UUID.randomUUID().hashCode()
        imageRequests += ImageRequest(id, callback)
        context.startActivity(Intent(context, ImageRequestActivity::class.java).apply {
            putExtra("request_id", id)
            if (view != null) {
                putExtra("activity_transition_options",
                        ActivityOptionsCompat.makeClipRevealAnimation(view, 0, 0,
                                view.measuredWidth, view.measuredHeight).toBundle())
            }
            flags = flags or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        })
    }


    data class WidgetRequest(val id: Int, val callback: (arg: Int) -> Unit) {
        var configured = false
    }

    data class ImageRequest(val id: Int, val callback: (storeUUID: String?) -> Unit)

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

    @SuppressLint("Registered")
    class ImageRequestActivity : Activity() {
        val id by lazy { intent.extras!!["request_id"] as Int }
        private val startOpt by lazy { intent.extras!!["activity_transition_options"] as Bundle? }
        val request by lazy { imageRequests.first { it.id == id } }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val id = (applicationContext as LawnchairApp).overlayWidgetHost.allocateAppWidgetId()
            startActivityForResult(Intent(this, ImageStore.ImageStoreActivity::class.java), id,
                    startOpt)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (resultCode == RESULT_OK) {
                request.callback(data!!.getStringExtra(ImageStore.ImageStoreActivity.IMAGE_UUID)!!)
                finish()
                overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short)
            } else {
                try {
                    request.callback(null)
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                }
                finish()
                overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short)
            }
        }
    }
}