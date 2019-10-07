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

package ch.deletescape.lawnchair.feed.go

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.deletescape.lawnchair.fromColorRes
import ch.deletescape.lawnchair.getColorEngineAccent
import ch.deletescape.lawnchair.runOnMainThread
import com.android.launcher3.R
import com.google.android.libraries.launcherclient.ILauncherOverlay
import com.google.android.libraries.launcherclient.ILauncherOverlayCallback
import kotlinx.android.synthetic.main.go_feed.view.*
import kotlin.math.sign

class GoFeed(val originalContext: Context) : ILauncherOverlay.Stub() {
    private val windowService
        get() = originalContext.getSystemService(Context.WINDOW_SERVICE)
                as WindowManager
    private var attached = false
        set(value) {
            field = value
            if (value) {
                if (context.isDark) {
                    controller.systemUiVisibility =
                            View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                windowService.addView(controller, layoutParams)
            } else {
                controller.systemUiVisibility = 0
                windowService.removeViewImmediate(controller)
            }
        }
    private val swipeLayout
        get() = controller.feed_content
    private val recycler
        get() = controller.go_feed_recycler

    lateinit var context: Context
    lateinit var controller: GoSlidingView
    lateinit var layoutParams: WindowManager.LayoutParams
    lateinit var callbacks: ILauncherOverlayCallback
    lateinit var adapter: GoAdapter

    init {
        init()
    }

    @SuppressLint("InflateParams")
    fun init() {
        context = ContextThemeWrapper(originalContext,
                if (originalContext.isDark) R.style.FeedTheme_Dark else R.style.FeedTheme_Light)
        controller = LayoutInflater.from(context).inflate(R.layout.go_feed, null, false)
                as GoSlidingView
        controller.mLauncherFeed = this
        controller.background = ColorDrawable(
                (if (!context.isDark) R.color.qsb_background_hotseat_white else R.color.qsb_background_hotseat_dark).fromColorRes(
                        context))
        controller.setOnApplyWindowInsetsListener { v, insets ->
            swipeLayout.apply {
                clipToPadding = false
                setPadding(insets.stableInsetLeft, insets.stableInsetTop, insets.stableInsetRight,
                        insets.stableInsetBottom)
            }
            recycler.apply {
                clipToPadding = false
                setPadding(insets.stableInsetLeft, insets.stableInsetTop, insets.stableInsetRight,
                        insets.stableInsetBottom)
            }
            insets
        }

        adapter = GoAdapter(emptyList())
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context)

        swipeLayout.setColorSchemeColors(context.getColorEngineAccent(),
                Color.parseColor("#DB4437"), Color.parseColor("#F4B400"),
                Color.parseColor("#0F9D58"))

        swipeLayout.setOnRefreshListener {
            recycler.isLayoutFrozen = true
            (recycler.adapter as RecyclerView.Adapter).notifyDataSetChanged()
            recycler.isLayoutFrozen = false
            swipeLayout.isRefreshing = false
        }
    }

    override fun startScroll() {
        runOnMainThread {
            if (!attached) {
                attached = true
            }
            controller.startScroll()
        }
    }

    override fun onScroll(progress: Float) {
        runOnMainThread {
            controller.onScroll(progress)
        }
    }

    override fun endScroll() {
        runOnMainThread {
            controller.endScroll()
        }
    }

    override fun windowAttached(lp: WindowManager.LayoutParams, cb: ILauncherOverlayCallback,
                                flags: Int) {
        runOnMainThread {
            if (attached) {
                attached = false
            }
            layoutParams = lp
            callbacks = cb
        }
    }

    override fun windowDetached(isChangingConfigurations: Boolean) {
        attached = false
    }

    override fun closeOverlay(flags: Int) {
        runOnMainThread {
            controller.closeOverlay((flags and 1 != 0), (flags shl 2))
        }
    }

    override fun onPause() {
    }

    override fun onResume() {
    }

    override fun openOverlay(flags: Int) {
        runOnMainThread {
            attached = true
            controller.openOverlay((flags and 1 != 0), (flags shl 2));
        }
    }

    override fun requestVoiceDetection(start: Boolean) {
    }

    override fun getVoiceSearchLanguage() = "en"
    override fun isVoiceDetectionRunning() = false
    override fun hasOverlayContent() = true
    override fun windowAttached2(bundle: Bundle, cb: ILauncherOverlayCallback) {
        runOnMainThread {
            if (attached) {
                attached = false
            }
        }
        synchronized(this) {
            layoutParams = bundle.getParcelable("params")!!
            callbacks = cb
        }
    }

    override fun unusedMethod() {
    }

    override fun setActivityState(flags: Int) {
    }

    override fun startSearch(data: ByteArray, bundle: Bundle): Boolean = false


    fun cbScroll(progress: Float, dragging: Boolean) {
        callbacks.overlayScrollChanged(progress)
        if (sign(progress).compareTo(sign(0f)) == 0 && !dragging) {
            attached = false
        }
    }
}