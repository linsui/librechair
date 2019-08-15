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

import android.animation.Animator
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.design.widget.TabLayout
import android.support.v4.graphics.ColorUtils
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Toolbar
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.feed.FeedAdapter
import ch.deletescape.lawnchair.feed.getFeedController
import ch.deletescape.lawnchair.feed.tabs.TabController
import ch.deletescape.lawnchair.feed.widgets.WidgetSelectionService
import ch.deletescape.lawnchair.theme.ThemeManager
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import com.android.launcher3.config.FeatureFlags
import com.github.difflib.DiffUtils
import com.github.difflib.patch.DeltaType
import com.google.android.libraries.launcherclient.ILauncherOverlay
import com.google.android.libraries.launcherclient.ILauncherOverlayCallback
import java.util.concurrent.Executors
import kotlin.math.sign

class LauncherFeed(contex2t: Context) : ILauncherOverlay.Stub() {
    private val dark: Boolean = ThemeManager.getInstance(
            contex2t.applicationContext).isDark || contex2t.lawnchairPrefs.feedCardBlur
    private val context = ContextThemeWrapper(contex2t,
                                              if (dark) R.style.SettingsTheme_V2_Dark else R.style.SettingsTheme_V2)
    private var backgroundColor: Int = ColorUtils
            .setAlphaComponent(if (dark) Color.DKGRAY else Color.WHITE,
                               LawnchairPreferences.getInstance(
                                       context).feedBackgroundOpacity.toInt() * (255 / 100)).also {}
    private val adapter = FeedAdapter(getFeedController(context).getProviders(),
                                      ThemeManager.getInstance(context), backgroundColor,
                                      context.applicationContext, this)
    private val handler = Handler(Looper.getMainLooper())
    private val windowService = context.getSystemService(WindowManager::class.java)
    private val feedController = (LayoutInflater.from(context).inflate(R.layout.overlay_feed, null,
                                                                       false) as FeedController)
            .also {
                it.setLauncherFeed(this)
                it.setBackgroundColor(backgroundColor)
                adapter.backgroundColor = backgroundColor
            }
    private val tabController: TabController =
            TabController.inflate(context.lawnchairPrefs.feedTabController, context)
    private val useTabbedMode = tabController.allTabs.isNotEmpty()
    private val tabbedProviders = tabController.sortFeedProviders(adapter.providers)
    private val tabs = tabController.allTabs
    private val tabView = feedController.findViewById(R.id.feed_tabs) as TabLayout
    private val recyclerView = (feedController.findViewById(R.id.feed_recycler) as RecyclerView)
    private val toolbar = (feedController.findViewById(R.id.feed_title_bar) as Toolbar)
    private val content = (feedController.findViewById(R.id.feed_content) as ViewGroup)
    private val googleColours = arrayOf(Color.parseColor("#4285F4"), Color.parseColor("#DB4437"),
                                        Color.parseColor("#F4B400"), Color.parseColor("#0F9D58"))


    init {
        tabView.tabMode = TabLayout.MODE_SCROLLABLE
        tabView.tabGravity = TabLayout.GRAVITY_FILL
        tabView.setOnTouchListener { view, _ ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            true
        }
        if (!useWhiteText(backgroundColor, context)) {
            tabView.tabTextColors =
                    ColorStateList.valueOf(R.color.textColorPrimaryInverse.fromColorRes(context))
            tabView.tabIconTint =
                    ColorStateList.valueOf(R.color.textColorPrimaryInverse.fromColorRes(context))
            tabView.tabSelectedIndicator!!
                    .setTint(R.color.textColorPrimaryInverse.fromColorRes(context))
            tabView.tabTextColors = ColorStateList(
                    arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(),
                            arrayOf<Int>().toIntArray()),
                    arrayOf(googleColours[0], tabView.tabIconTint!!.defaultColor).toIntArray())
        }
        if (!useTabbedMode) {
            if (tabbedProviders.keys != setOf(null)) {
                error("tabbing inconsistency detected: no tabs were defined but providers are sorted by tabs")
            } else if (tabbedProviders.keys.isEmpty()) {
                error("tabbing inconsistency detected: no tabs were defined but there is no null key in provider map")
            }
            tabView.visibility = View.GONE
        } else {
            tabs.forEach {
                tabView.addTab(tabView.newTab().apply {
                    text = it.title
                    icon = it.icon
                })
            }
            val pxWidth = context.resources.displayMetrics.widthPixels
            val tlWidth = tabView
                    .apply { measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED) }
                    .measuredWidth
            if (pxWidth < tlWidth) {
                tabView.tabMode = TabLayout.MODE_SCROLLABLE
            } else {
                tabView.tabMode = TabLayout.MODE_FIXED
            }
            tabView.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                }

                override fun onTabSelected(tab: TabLayout.Tab) {
                    if (backgroundColor.alpha > 35) {
                        tabView.setSelectedTabIndicatorColor(getColorForIndex(tab.position))
                        tabView.tabTextColors = ColorStateList(
                                arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(),
                                        arrayOf<Int>().toIntArray()),
                                arrayOf(getColorForIndex(tab.position),
                                        tabView.tabIconTint!!.defaultColor).toIntArray())
                        tabView.tabIconTint = ColorStateList(
                                arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(),
                                        arrayOf<Int>().toIntArray()),
                                arrayOf(getColorForIndex(tab.position),
                                        tabView.tabIconTint!!.defaultColor).toIntArray())
                    }
                    adapter.providers = tabbedProviders[tabs.first { it.title == tab.text }]!!
                    Executors.newSingleThreadExecutor().submit {
                        recyclerView.post {
                            recyclerView.isLayoutFrozen = true
                            recyclerView.layoutManager?.scrollToPosition(0)
                        }
                        val oldCards = adapter.immutableCards
                        adapter.refresh()
                        val cards = adapter.immutableCards
                        val patch = DiffUtils.diff(oldCards, cards)

                        handler.post {
                            patch.deltas.forEach {
                                when (it.type) {
                                    DeltaType.CHANGE -> adapter.notifyItemRangeChanged(
                                            it.source.position, it.source.lines.size)
                                    DeltaType.INSERT -> adapter.notifyItemRangeInserted(
                                            it.source.position, it.source.lines.size)
                                    DeltaType.DELETE -> adapter.notifyItemRangeRemoved(
                                            it.source.position, it.source.lines.size)
                                    DeltaType.EQUAL -> {
                                    }
                                }
                            }
                            recyclerView.post {
                                recyclerView.isLayoutFrozen = false
                                if (adapter.itemCount == 0) {
                                    toolbar.setTitleTextColor(if (useWhiteText(backgroundColor,
                                                                               context)) Color.WHITE else Color.DKGRAY)
                                } else {
                                    toolbar.title = ""
                                }
                            }
                        }
                    }
                }
            })
            d("init: tabbed providers are $tabbedProviders and tabs are $tabs")
            adapter.providers = tabbedProviders[tabs.first()]!!
            if (backgroundColor.alpha > 35) {
                tabView.setSelectedTabIndicatorColor(googleColours[0])
                tabView.tabIconTint = ColorStateList(
                        arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(),
                                arrayOf<Int>().toIntArray()),
                        arrayOf(googleColours[0], tabView.tabIconTint!!.defaultColor).toIntArray())
            }
        }
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                runOnMainThread {
                    if (adapter.itemCount == 0) {
                        toolbar.setTitleTextColor(if (useWhiteText(backgroundColor,
                                                                   context)) Color.WHITE else Color.DKGRAY)
                    } else {
                        toolbar.title = ""
                    }
                }
            }
        })
        if (adapter.itemCount == 0) {
            toolbar.setTitleTextColor(
                    if (useWhiteText(backgroundColor, context)) Color.WHITE else Color.DKGRAY)
        } else {
            toolbar.title = ""
        }
        if (!FeatureFlags.GO_DISABLE_WIDGETS) {
            toolbar.menu.add(context.getString(R.string.title_feed_toolbar_add_widget))
            toolbar.menu.getItem(0).icon = R.drawable.ic_widget.fromDrawableRes(context)
                    .duplicateAndSetColour(if (useWhiteText(backgroundColor,
                                                            context)) R.color.textColorPrimary.fromColorRes(
                            context)
                                           else R.color.textColorPrimaryInverse.fromColorRes(
                            context))
            toolbar.menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            toolbar.menu.getItem(0).setOnMenuItemClickListener {
                context.bindService(Intent(context, WidgetSelectionService::class.java),
                                    object : ServiceConnection {
                                        override fun onServiceDisconnected(name: ComponentName?) {
                                        }

                                        override fun onServiceConnected(name: ComponentName?,
                                                                        service: IBinder?) {
                                            IWidgetSelector.Stub.asInterface(service)
                                                    .pickWidget(object :
                                                                        WidgetSelectionCallback.Stub() {
                                                        override fun onWidgetSelected(i: Int) {
                                                            context.lawnchairPrefs.feedWidgetList
                                                                    .customAdder(i)
                                                            context.bindService(Intent(context,
                                                                                       PreferenceSynchronizerService::class.java),
                                                                                object :
                                                                                        ServiceConnection {
                                                                                    override fun onServiceDisconnected(
                                                                                            name: ComponentName?) {
                                                                                    }

                                                                                    override fun onServiceConnected(
                                                                                            name: ComponentName?,
                                                                                            service: IBinder?) {
                                                                                        PreferenceSynchronizer
                                                                                                .Stub
                                                                                                .asInterface(
                                                                                                        service)
                                                                                                .requestSynchronization()
                                                                                    }
                                                                                },
                                                                                Context.BIND_AUTO_CREATE)

                                                            Executors.newSingleThreadExecutor()
                                                                    .submit {
                                                                        recyclerView.post {
                                                                            recyclerView
                                                                                    .isLayoutFrozen =
                                                                                    true
                                                                        }
                                                                        val oldCards = adapter
                                                                                .immutableCards
                                                                        adapter.refresh()
                                                                        val cards = adapter
                                                                                .immutableCards
                                                                        val patch = DiffUtils
                                                                                .diff(oldCards,
                                                                                      cards)

                                                                        handler.post {
                                                                            patch.deltas.forEach {
                                                                                when (it.type) {
                                                                                    DeltaType.CHANGE -> adapter.notifyItemRangeChanged(
                                                                                            it.source.position,
                                                                                            it.source.lines.size)
                                                                                    DeltaType.INSERT -> adapter.notifyItemRangeInserted(
                                                                                            it.source.position,
                                                                                            it.source.lines.size)
                                                                                    DeltaType.DELETE -> adapter.notifyItemRangeRemoved(
                                                                                            it.source.position,
                                                                                            it.source.lines.size)
                                                                                    DeltaType.EQUAL -> {
                                                                                    }
                                                                                }
                                                                            }
                                                                            recyclerView.post {
                                                                                recyclerView
                                                                                        .isLayoutFrozen =
                                                                                        false
                                                                            }
                                                                        }
                                                                    }
                                                        }
                                                    })
                                        }
                                    }, Context.BIND_IMPORTANT or Context.BIND_AUTO_CREATE)
            }
        }
    }

    fun displayView(inflater: (parent: ViewGroup) -> View) {
        content.findViewById<View>(R.id.feed_overlay_view)?.also { content.removeView(it) }
        val anim: Animator
        content.addView(inflater(content).apply {
            id = R.id.feed_overlay_view
            alpha = 0f
        })
        content.findViewById<View>(R.id.feed_overlay_view).apply {
            animate().alpha(255f).duration = 2000
        }
        toolbar.menu.add(0, R.id.cancel, 0, android.R.string.cancel).apply {
            icon = R.drawable.ic_close.fromDrawableRes(context).duplicateAndSetColour(
                    if (useWhiteText(backgroundColor,
                                     context)) R.color.textColorPrimary.fromColorRes(
                            context) else R.color.textColorPrimaryInverse.fromColorRes(context))
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            setOnMenuItemClickListener {
                toolbar.menu.removeItem(R.id.cancel)
                removeDisplayedView()
                true
            }
        }
    }

    fun removeDisplayedView() {
        content.findViewById<View>(R.id.feed_overlay_view)?.also { content.removeView(it) }
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
                        recyclerView.layoutManager = object : LinearLayoutManager(context) {
                            override fun onLayoutChildren(recycler: RecyclerView.Recycler?,
                                                          state: RecyclerView.State?) {
                                try {
                                    super.onLayoutChildren(recycler, state)
                                } catch (e: RuntimeException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                    Executors.newSingleThreadExecutor().submit {
                        Thread.sleep(1000)
                        recyclerView.post { recyclerView.isLayoutFrozen = true }
                        val oldCards = adapter.immutableCards
                        adapter.refresh()
                        val cards = adapter.immutableCards
                        val patch = DiffUtils.diff(oldCards, cards)

                        handler.post {
                            patch.deltas.forEach {
                                when (it.type) {
                                    DeltaType.CHANGE -> adapter.notifyItemRangeChanged(
                                            it.source.position, it.source.lines.size)
                                    DeltaType.INSERT -> adapter.notifyItemRangeInserted(
                                            it.source.position, it.source.lines.size)
                                    DeltaType.DELETE -> adapter.notifyItemRangeRemoved(
                                            it.source.position, it.source.lines.size)
                                    DeltaType.EQUAL -> {
                                    }
                                }
                            }
                            recyclerView.isLayoutFrozen = false
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
            if (!useWhiteText(backgroundColor, context)) {
                feedController.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
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
    }

    override fun onResume() {
    }

    override fun openOverlay(flags: Int) {
    }

    override fun requestVoiceDetection(start: Boolean) {
    }

    override fun getVoiceSearchLanguage(): String {
        return "en"
    }

    override fun isVoiceDetectionRunning(): Boolean {
        return false
    }

    override fun hasOverlayContent(): Boolean {
        return true
    }

    override fun unusedMethod() {
    }

    override fun setActivityState(flags: Int) {
    }

    override fun startSearch(data: ByteArray?, bundle: Bundle?): Boolean {
        return false
    }

    fun onProgress(progress: Float, isDragging: Boolean) {
        callback?.overlayScrollChanged(progress)
        val touchable = Math.signum(progress).compareTo(sign(0f)) != 0
        if (!touchable && !isDragging) {
            feedController.systemUiVisibility = 0
            feedAttached = false
        }
    }

    fun getColorForIndex(index: Int): Int {
        if (index < googleColours.size) {
            return googleColours[index]
        } else {
            val first = googleColours[index % googleColours.size]
            val second = googleColours[googleColours.size - 1 - index % googleColours.size]
            return {
                val result = first or (second and 0xFF00FF)
                val opacity = first shr 24
                opacity shl 24 or (result and 0xFFFFFF)
            }()
        }
    }
}
