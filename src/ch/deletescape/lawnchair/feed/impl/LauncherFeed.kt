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

package ch.deletescape.lawnchair.feed.impl

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.graphics.ColorUtils
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toolbar
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.feed.FeedAdapter
import ch.deletescape.lawnchair.feed.getFeedController
import ch.deletescape.lawnchair.theme.ThemeManager
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import com.google.android.libraries.launcherclient.ILauncherOverlay
import com.google.android.libraries.launcherclient.ILauncherOverlayCallback
import java.util.concurrent.Executors

class LauncherFeed(contex2t: Context) : ILauncherOverlay.Stub() {

    private val dark: Boolean = ThemeManager.getInstance(contex2t.applicationContext).isDark
    private val context = ContextThemeWrapper(contex2t,
                                              if (dark) R.style.SettingsTheme_V2_Dark else R.style.SettingsTheme_V2)
    private var backgroundColor: Int = ColorUtils
            .setAlphaComponent(if (dark) Color.DKGRAY else Color.WHITE,
                               LawnchairPreferences.getInstance(
                                       context).feedBackgroundOpacity.toInt() * (255 / 100)).also {
                d("backgroundColor: ${it}")
            }
    private val adapter by lazy {
        FeedAdapter(getFeedController(context.applicationContext).getProviders(),
                    ThemeManager.getInstance(context), backgroundColor, context.applicationContext)
    }
    private val handler = Handler(Looper.getMainLooper())
    private val windowService = context.getSystemService(WindowManager::class.java)
    private val feedController = (LayoutInflater.from(context).inflate(R.layout.overlay_feed, null,
                                                                       false) as FeedController)
            .also {
                it.setLauncherFeed(this)
                it.setBackgroundColor(backgroundColor)
                d("feedController: background color value: ${LawnchairPreferences.getInstance(
                        context).feedBackgroundOpacity.toInt()}")
                adapter.backgroundColor = backgroundColor
            }
    private val recyclerView = (feedController.findViewById(R.id.feed_recycler) as RecyclerView)
    private val toolbar = (feedController.findViewById(R.id.feed_title_bar) as Toolbar)

    init {
        toolbar.menu.add(context.getString(R.string.title_feed_toolbar_add_widget))
        toolbar.menu.getItem(0).icon = R.drawable.ic_widget.fromDrawableRes(context)
                .duplicateAndSetColour(if (useWhiteText(backgroundColor, context))
                                           R.color.textColorPrimary.fromColorRes(context)
                                       else
                                           R.color.textColorPrimaryInverse.fromColorRes(context))
        toolbar.menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    private var callback: ILauncherOverlayCallback? = null
    private lateinit var layoutParams: WindowManager.LayoutParams

    private var feedAttached = false
        set(value) {
            if (field != value) {
                field = value
                if (field) {
                    if (recyclerView.adapter == null) {
                        recyclerView.adapter = this.adapter
                        recyclerView.layoutManager = LinearLayoutManager(context)
                    }
                    Executors.newSingleThreadExecutor().submit {
                        Thread.sleep(1000);
                        d("refreshing adapter")
                        adapter.refresh()
                        d("adapter refreshed")
                        handler.post {
                            d("notifying adapter")
                            adapter.notifyDataSetChanged()
                            d("adapter notified")
                        }
                    }
                    windowService.addView(feedController, layoutParams)
                } else {
                    windowService.removeView(feedController)
                }
            }
        }

    override fun startScroll() {
        handler.post {
            feedAttached = true
            feedController.startScroll()
        }
    }

    override fun onScroll(progress: Float) {
        handler.post { feedController.onScroll(progress) }
    }

    override fun endScroll() {
        handler.post { feedController.endScroll() }
    }

    override fun windowAttached(lp: WindowManager.LayoutParams, cb: ILauncherOverlayCallback,
                                flags: Int) {
        callback = cb
        cb.overlayStatusChanged(1)
        layoutParams = lp
        //        layoutParams.flags = layoutParams.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        //        layoutParams.flags = layoutParams.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    }

    override fun windowAttached2(bundle: Bundle, cb: ILauncherOverlayCallback) {
        windowAttached(bundle.getParcelable("layout_params")!!, cb, 0 /* TODO: figure this out */)
    }

    override fun windowDetached(isChangingConfigurations: Boolean) {
        handler.post { windowService.removeView(feedController) }
    }

    override fun closeOverlay(flags: Int) {
        handler.post { feedController.closeOverlay((flags and 1) != 0, flags shr 2) }
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
    }

    override fun openOverlay(flags: Int) {
        Log.d(TAG, "openOverlay($flags)")
    }

    override fun requestVoiceDetection(start: Boolean) {
        Log.d(TAG, "requestVoiceDetection")
    }

    override fun getVoiceSearchLanguage(): String {
        Log.d(TAG, "getVoiceSearchLanguage")
        return "en"
    }

    override fun isVoiceDetectionRunning(): Boolean {
        Log.d(TAG, "isVoiceDetectionRunning")
        return false
    }

    override fun hasOverlayContent(): Boolean {
        Log.d(TAG, "hasOverlayContent")
        return true
    }

    override fun unusedMethod() {
        Log.d(TAG, "unusedMethod")
    }

    override fun setActivityState(flags: Int) {
        Log.d(TAG, "setActivityState($flags)")
    }

    override fun startSearch(data: ByteArray?, bundle: Bundle?): Boolean {
        Log.d(TAG, "startSearch")
        return false
    }

    fun onProgress(progress: Float, isDragging: Boolean) {
        callback?.overlayScrollChanged(progress)
        val touchable = Math.signum(progress).compareTo(Math.signum(0f)) != 0
        if (!touchable && !isDragging) {
            feedAttached = false
        }
    }

    companion object {

        private const val TAG = "LauncherFeed"
    }
}
