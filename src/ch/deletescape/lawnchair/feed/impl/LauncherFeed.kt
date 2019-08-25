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
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.support.design.widget.TabLayout
import android.support.v4.graphics.ColorUtils
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toolbar
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.colors.ColorEngine
import ch.deletescape.lawnchair.feed.FeedAdapter
import ch.deletescape.lawnchair.feed.getFeedController
import ch.deletescape.lawnchair.feed.tabs.TabController
import ch.deletescape.lawnchair.feed.widgets.WidgetSelectionService
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import com.android.launcher3.config.FeatureFlags
import com.github.difflib.DiffUtils
import com.github.difflib.patch.DeltaType
import com.google.android.libraries.launcherclient.ILauncherOverlay
import com.google.android.libraries.launcherclient.ILauncherOverlayCallback
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sign

class LauncherFeed(contex2t: Context) : ILauncherOverlay.Stub() {
    private var backgroundColor: Int = ColorUtils.setAlphaComponent(
            ColorEngine.getInstance(contex2t).feedBackground.value.resolveColor(),
            (LawnchairPreferences.getInstance(
                    contex2t).feedBackgroundOpacity * (255f / 100f)).roundToInt())
    private val dark: Boolean = useWhiteText(backgroundColor.setAlpha(255), contex2t)
    private val accessingPackages = mutableSetOf<String>()

    init {
        d("init: dark ${dark}")
    }

    private val context = ContextThemeWrapper(contex2t,
                                              if (dark) R.style.SettingsTheme_V2_Dark else R.style.SettingsTheme_V2)
    private val adapter = FeedAdapter(getFeedController(context).getProviders(), backgroundColor,
                                      context.applicationContext, this)
    private val handler = Handler(Looper.getMainLooper())
    private val windowService = context.getSystemService(WindowManager::class.java)
    private val feedController = (LayoutInflater.from(context).inflate(R.layout.overlay_feed, null,
                                                                       false) as FeedController)
            .also {
                it.setLauncherFeed(this)
                it.background = ColorDrawable(backgroundColor)
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
    private val frame = (feedController.findViewById(R.id.feed_main_frame) as FrameLayout)
    private val googleColours = arrayOf(Color.parseColor("#4285F4"), Color.parseColor("#DB4437"),
                                        Color.parseColor("#F4B400"), Color.parseColor("#0F9D58"))
    private lateinit var oldIconTint: ColorStateList
    private var oldIndicatorTint: Int = -1
    private lateinit var oldTextColor: ColorStateList
    private val tabsOnBottom = contex2t.lawnchairPrefs.feedTabsOnBottom
    private val hasWidgetTab = tabs.any { it.isWidgetTab }
    var statusBarHeight: Int? = null
    var navigationBarHeight: Int? = null

    init {
        if (!useTabbedMode) {
            recyclerView.apply {
                setPadding(paddingStart, toolbar.also {
                    it.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                }.height + context.resources.getDimension(
                        R.dimen.feed_app_bar_bottom_padding).toInt(), paddingRight, paddingBottom);
            }
        }
        var oldToolbarPadding: Pair<Int, Int>? = null
        var oldRecyclerViewPadding: Pair<Int, Int>? = null
        if (backgroundColor.alpha == 255) {
            toolbar.setBackgroundColor(backgroundColor.setAlpha(175))
        }
        feedController.setOnApplyWindowInsetsListener { v, insets ->
            statusBarHeight = insets.stableInsetTop
            navigationBarHeight = insets.stableInsetBottom
            recyclerView.apply {
                if (oldRecyclerViewPadding == null) {
                    oldRecyclerViewPadding = paddingTop to paddingBottom
                }
                setPadding(paddingLeft, oldRecyclerViewPadding!!.first + statusBarHeight!!,
                           paddingRight, oldRecyclerViewPadding!!.second + navigationBarHeight!!)
            }
            toolbar.apply {
                if (oldToolbarPadding == null) {
                    oldToolbarPadding = paddingTop to paddingBottom
                }
                setPadding(paddingLeft,
                           if (!tabsOnBottom) oldToolbarPadding!!.first + statusBarHeight!! else paddingTop,
                           paddingRight,
                           if (tabsOnBottom) oldToolbarPadding!!.second + navigationBarHeight!! else paddingBottom)
            }
            insets
        }
        feedController.mOpenedCallback = {
            runOnNewThread {
                refresh(100)
            }
        }
        tabView.tabMode = TabLayout.MODE_SCROLLABLE
        tabView.tabGravity = TabLayout.GRAVITY_FILL
        tabView.setOnTouchListener { view, _ ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            true
        }
        if (tabsOnBottom) {
            (toolbar.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.BOTTOM
            toolbar.parent.requestLayout()
            recyclerView.apply {
                setPadding(paddingLeft, 0, paddingRight, paddingTop)
            }
        }
        if (!useWhiteText(backgroundColor, context)) {
            tabView.tabIconTint =
                    ColorStateList.valueOf(R.color.textColorPrimaryInverse.fromColorRes(context))
            tabView.tabSelectedIndicator!!
                    .setTint(R.color.textColorPrimaryInverse.fromColorRes(context))
            tabView.tabTextColors = ColorStateList(
                    arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(), intArrayOf()),
                    arrayOf(googleColours[0],
                            R.color.textColorPrimaryInverse.fromColorRes(context)).toIntArray())
        }
        if (!useTabbedMode) {
            if (tabbedProviders.keys != setOf(null)) {
                error("tabbing inconsistency detected: no tabs were defined but providers are sorted by tabs")
            } else if (tabbedProviders.keys.isEmpty()) {
                error("tabbing inconsistency detected: no tabs were defined but there is no null key in provider map")
            }
            tabView.visibility = View.GONE
        } else {
            if (context.lawnchairPrefs.feedAutoHideToolbar) {
                recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (dy > 0) {
                            toolbar.animate().translationY(
                                    if (!tabsOnBottom) -toolbar.measuredHeight.toFloat() else toolbar.measuredHeight.toFloat())
                        } else if (dy < 0) {
                            toolbar.animate().translationY(0f)
                        }
                    }
                })
            }
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
                        tabView.tabRippleColor = ColorStateList(
                                arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(),
                                        arrayOf<Int>().toIntArray()),
                                arrayOf(getColorForIndex(tab.position).setAlpha(50),
                                        tabView.tabRippleColor!!.defaultColor.setAlpha(
                                                50)).toIntArray())
                    }
                    adapter.providers = tabbedProviders[tabs[tab.position]]!!
                    if (hasWidgetTab) {
                        toolbar.menu.getItem(0).isVisible = tabs[tab.position]!!.isWidgetTab
                        toolbar.menu.getItem(0).icon = R.drawable.ic_add.fromDrawableRes(context)
                                .duplicateAndSetColour(if (useWhiteText(backgroundColor,
                                                                        context)) R.color.textColorPrimary.fromColorRes(
                                        context)
                                                       else R.color.textColorPrimaryInverse.fromColorRes(
                                        context))
                    }
                    runOnNewThread { refresh(0) }
                }
            })
            d("init: tabbed providers are $tabbedProviders and tabs are $tabs")
            adapter.providers = tabbedProviders[tabs.first()]!!
            if (backgroundColor.alpha > 35) {
                tabView.tabTextColors = ColorStateList(
                        arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(), intArrayOf()),
                        arrayOf(googleColours[0],
                                tabView.tabTextColors!!.defaultColor).toIntArray())
                tabView.setSelectedTabIndicatorColor(googleColours[0])
                tabView.tabIconTint = ColorStateList(
                        arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(),
                                arrayOf<Int>().toIntArray()), arrayOf(getColorForIndex(0),
                                                                      tabView.tabIconTint!!.defaultColor).toIntArray())
                tabView.tabRippleColor = ColorStateList(
                        arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(),
                                arrayOf<Int>().toIntArray()),
                        arrayOf(getColorForIndex(0).setAlpha(50),
                                tabView.tabIconTint!!.defaultColor.setAlpha(50)).toIntArray())
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

                                                            runOnNewThread {
                                                                refresh(0)
                                                            }
                                                        }
                                                    })
                                        }
                                    }, Context.BIND_IMPORTANT or Context.BIND_AUTO_CREATE)
            }
            if (hasWidgetTab) {
                toolbar.menu.getItem(0).isVisible = tabs[0]!!.isWidgetTab
            }
        }
    }

    fun displayView(inflater: (parent: ViewGroup) -> View, x: Float, y: Float) {
        tabView.tabsEnabled = false
        oldIconTint = tabView.tabIconTint!!
        oldIndicatorTint = if (backgroundColor.alpha > 35) getColorForIndex(
                tabView.selectedTabPosition) else if (useWhiteText(backgroundColor,
                                                                   context)) R.color.textColorPrimary.fromColorRes(
                context) else R.color.textColorPrimaryInverse.fromColorRes(context)
        oldTextColor = tabView.tabTextColors!!
        if (useWhiteText(backgroundColor, context) && !dark) {
            tabView.tabIconTint = ColorStateList(
                    arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(), intArrayOf()),
                    arrayOf(getColorForIndex(tabView.selectedTabPosition),
                            R.color.textColorPrimaryInverse.fromColorRes(context)).toIntArray())
            tabView.tabSelectedIndicator!!.setTint(getColorForIndex(tabView.selectedTabPosition))
            tabView.tabTextColors = ColorStateList(
                    arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(), intArrayOf()),
                    arrayOf(getColorForIndex(tabView.selectedTabPosition),
                            R.color.textColorPrimaryInverse.fromColorRes(context)).toIntArray())
        } else if (!useWhiteText(backgroundColor, context) && dark) {
            tabView.tabIconTint = ColorStateList(
                    arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(), intArrayOf()),
                    arrayOf(getColorForIndex(tabView.selectedTabPosition),
                            R.color.textColorPrimary.fromColorRes(context)).toIntArray())
            tabView.tabSelectedIndicator!!.setTint(getColorForIndex(tabView.selectedTabPosition))
            tabView.tabTextColors = ColorStateList(
                    arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(), intArrayOf()),
                    arrayOf(getColorForIndex(tabView.selectedTabPosition),
                            R.color.textColorPrimary.fromColorRes(context)).toIntArray())
        }
        frame.findViewById<View>(R.id.feed_overlay_view)?.also { content.removeView(it) }
        frame.addView(inflater(frame).apply {
            id = R.id.feed_overlay_view
            visibility = View.INVISIBLE
            fitsSystemWindows = false
            if (useTabbedMode) {
                setPadding(paddingLeft,
                           if (!tabsOnBottom) R.dimen.feed_app_bar_height_material.fromDimenRes(
                                   context).toInt() + R.dimen.overlay_view_margin.fromDimenRes(
                                   context).toInt() + statusBarHeight!! else paddingTop + statusBarHeight!!,
                           paddingRight,
                           if (tabsOnBottom) R.dimen.feed_app_bar_height_material.fromDimenRes(
                                   context).toInt() + R.dimen.overlay_view_margin.fromDimenRes(
                                   context).toInt() + statusBarHeight!! else paddingBottom + statusBarHeight!!)
            } else {
                setPadding(paddingLeft,
                           if (!tabsOnBottom) R.dimen.feed_app_bar_height_material.fromDimenRes(
                                   context).toInt() / 2 + R.dimen.overlay_view_margin.fromDimenRes(
                                   context).toInt() + statusBarHeight!! else paddingTop + statusBarHeight!!,
                           paddingRight,
                           if (tabsOnBottom) R.dimen.feed_app_bar_height_material.fromDimenRes(
                                   context).toInt() / 2 + R.dimen.overlay_view_margin.fromDimenRes(
                                   context).toInt() + statusBarHeight!! else paddingBottom + statusBarHeight!!)
            }
            viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    val (height, width) = measuredHeight to measuredWidth
                    viewTreeObserver.removeOnPreDrawListener(this)
                    val radius = hypot(height.toDouble(), width.toDouble())
                    val animator = ViewAnimationUtils
                            .createCircularReveal(this@apply, x.toInt(), y.toInt(), 0f,
                                                  radius.toFloat())
                    visibility = View.VISIBLE
                    animator.apply {
                        duration = 300
                        start()
                    }
                    recyclerView.isLayoutFrozen = true
                    toolbar.animate().translationY(0f)
                    return true;
                }
            })
        })
        toolbar.menu.add(0, R.id.cancel, 0, android.R.string.cancel).apply {
            icon = R.drawable.ic_close.fromDrawableRes(context).duplicateAndSetColour(
                    if (useWhiteText(backgroundColor,
                                     context)) R.color.textColorPrimary.fromColorRes(
                            context) else R.color.textColorPrimaryInverse.fromColorRes(context))
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            setOnMenuItemClickListener {
                toolbar.menu.removeItem(R.id.cancel)
                removeDisplayedView(x, y)
                true
            }
        }
        for (i in 0 until toolbar.menu.size()) {
            toolbar.menu.getItem(i).icon?.setTint(tabView.tabTextColors?.defaultColor ?: 0);
        }
    }

    fun removeDisplayedView(x: Float, y: Float) {
        tabView.tabIconTint = oldIconTint
        tabView.tabTextColors = oldTextColor
        tabView.tabSelectedIndicator?.setTint(oldIndicatorTint)
        for (i in 0 until toolbar.menu.size()) {
            toolbar.menu.getItem(i).icon?.setTint(oldTextColor.defaultColor)
        }

        tabView.tabsEnabled = true
        recyclerView.isLayoutFrozen = false
        frame.findViewById<View>(R.id.feed_overlay_view)?.apply {
            val view = this
            val (height, width) = measuredHeight to measuredWidth
            val radius = hypot(height.toDouble(), width.toDouble())
            val animator = ViewAnimationUtils
                    .createCircularReveal(this@apply, x.toInt(), y.toInt(), radius.toFloat(), 0f)
            visibility = View.VISIBLE
            animator.apply {
                duration = 300
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        frame.removeView(view)
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }
                })
                start()
            }
            recyclerView.isLayoutFrozen = false
        }
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
                    windowService.addView(feedController, layoutParams)
                } else {
                    feedController.alpha = 0f
                    windowService.removeView(feedController)
                }
            }
        }

    override fun startScroll() {
        accessingPackages += context.packageManager.getPackagesForUid(
                Binder.getCallingUid())?.toSet() ?: emptySet()
        handler.post {
            feedAttached = true
            if (accessingPackages.size > 1) {
                displayView({
                                return@displayView FrameLayout(it.context).apply {
                                    layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT
                                    layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT
                                    addView(TextView(it.context).apply {
                                        layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT
                                        layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT
                                        gravity = Gravity.CENTER
                                        text = R.string.message_feed_multiple_processes
                                                .fromStringRes(context)
                                    })
                                }
                            }, 0f, 0f)
            }
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
        handler.post {
            feedAttached = false
            windowService.removeView(feedController)
        }
    }

    override fun closeOverlay(flags: Int) {
        if (feedAttached) {
            handler.post { feedController.closeOverlay((flags and 1) != 0, flags shr 2) }
        }
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
                val result =
                        if (index % 2 > 0) (first and 0x00F0FF) or (second and 0xFF0F00) else (first and 0xFF00F0) or (second and 0x00FF0F)
                val opacity = first shr 24
                opacity shl 24 or (result and 0xFFFFFF)
            }()
        }
    }

    fun refresh(sleep: Long, count: Int = 0): Unit = synchronized(this) {
        Thread.sleep(sleep + 150)
        recyclerView.apply {
            post {
                recyclerView.isLayoutFrozen = true
            }
        }
        val oldCards = adapter.immutableCards
        adapter.refresh()
        val cards = adapter.immutableCards
        if (oldCards.isEmpty() && count == 0) {
            this.refresh(150, 1)
        } else {
            val patch = DiffUtils.diff(oldCards, cards)

            runOnMainThread {
                patch.deltas.forEach {
                    when (it.type!!) {
                        DeltaType.CHANGE -> adapter.notifyItemRangeChanged(it.source.position,
                                                                           it.source.lines.size)
                        DeltaType.INSERT -> adapter.notifyItemRangeInserted(it.source.position,
                                                                            it.source.lines.size)
                        DeltaType.DELETE -> adapter.notifyItemRangeRemoved(it.source.position,
                                                                           it.source.lines.size)
                        DeltaType.EQUAL -> {
                        }
                    }
                }
                recyclerView.isLayoutFrozen = false
            }
        }
    }
}
