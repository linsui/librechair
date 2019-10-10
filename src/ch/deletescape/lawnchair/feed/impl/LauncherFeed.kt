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

@file:Suppress("UNCHECKED_CAST")

package ch.deletescape.lawnchair.feed.impl

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.appwidget.AppWidgetHostView
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Binder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.colors.ColorEngine
import ch.deletescape.lawnchair.cp.OverlayCallbacks
import ch.deletescape.lawnchair.feed.*
import ch.deletescape.lawnchair.feed.chips.ChipAdapter
import ch.deletescape.lawnchair.feed.chips.ChipController
import ch.deletescape.lawnchair.feed.chips.ChipDatabase
import ch.deletescape.lawnchair.feed.images.screen.ImageDataScreen
import ch.deletescape.lawnchair.feed.tabs.TabController
import ch.deletescape.lawnchair.feed.tabs.colors.ColorProvider
import ch.deletescape.lawnchair.feed.tabs.indicator.TabIndicatorProvider
import ch.deletescape.lawnchair.feed.tabs.indicator.inflate
import ch.deletescape.lawnchair.feed.widgets.OverlayWidgetHost
import ch.deletescape.lawnchair.font.CustomFontManager
import ch.deletescape.lawnchair.persistence.chipPrefs
import ch.deletescape.lawnchair.persistence.feedPrefs
import com.android.launcher3.R
import com.android.launcher3.Utilities
import com.android.launcher3.config.FeatureFlags
import com.android.overlayclient.ActivityState
import com.android.overlayclient.ServiceState
import com.github.difflib.DiffUtils
import com.github.difflib.patch.DeltaType
import com.google.android.libraries.launcherclient.ILauncherOverlay
import com.google.android.libraries.launcherclient.ILauncherOverlayCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.overlay_feed.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import kotlin.math.*
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.jvm.isAccessible

class LauncherFeed(val originalContext: Context,
                   backgroundSetupListener: ((backgroundCallback: (bkg: Bitmap) -> Unit) -> Unit)? = null) :
        ILauncherOverlay.Stub() {
    private var backgroundColor: Int = ColorUtils.setAlphaComponent(
            ColorEngine.getInstance(originalContext).feedBackground.value.resolveColor(),
            (LawnchairPreferences.getInstance(
                    originalContext).feedBackgroundOpacity * (255f / 100f)).roundToInt())
    private var dark: Boolean = useWhiteText(backgroundColor.setAlpha(255), originalContext)
    private val accessingPackages = mutableSetOf<String>()
    private val activityState = ActivityState()
    private lateinit var currentTab: TabController.Item

    private var lastScroll = 0f

    private var internalActions = mutableMapOf<Int, FeedProvider.Action>()

    private var context = ContextThemeWrapper(originalContext,
            if (dark) R.style.FeedTheme_Dark else R.style.FeedTheme_Light)

    var chipAdapter: ChipAdapter = ChipAdapter(context, this)
    private var lastOrientation = context.resources.configuration.orientation
    private var adapter = FeedAdapter(getFeedController(context).getProviders(), backgroundColor,
            context.applicationContext, this)
    private val handler = Handler(Looper.getMainLooper())
    private val windowService = context.getSystemService(WindowManager::class.java)
    private var verticalBackground: Drawable? = null
    private var horizontalBackground: Drawable? = null
    val screenActions = mutableMapOf<ProviderScreen, List<FeedProvider.Action>>()

    var readMoreUrl: String? = null
    var feedController = (LayoutInflater.from(context).inflate(R.layout.overlay_feed, null,
            false) as FeedController)
            .also {
                it.setLauncherFeed(this)
                it.viewTreeObserver.addOnGlobalLayoutListener {
                    if (horizontalBackground == null || verticalBackground == null) {
                        verticalBackground = ColorDrawable(backgroundColor)
                        horizontalBackground = ColorDrawable(backgroundColor)
                    }
                    lastOrientation = context.resources.configuration.orientation
                    it.background =
                            if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) verticalBackground!! else horizontalBackground!!
                }
            }
    private var tabChanged = false
    private val tabController: TabController =
            TabController.inflate(context.lawnchairPrefs.feedTabController, context)
    private val useTabbedMode = tabController.allTabs.isNotEmpty()
    private val tabbedProviders = tabController.sortFeedProviders(adapter.providers).toMutableMap()
    private val tabs = tabController.allTabs.toMutableList()
    private var tabView = (feedController.findViewById(R.id.feed_tabs) as TabLayout).also {
        it.viewTreeObserver.addOnGlobalLayoutListener { ->
            (it.getChildAt(0) as ViewGroup).childs.forEach {
                val textView = it::class.java.getDeclaredField(
                        "textView").also { it.isAccessible = true }.get(it)
                        as? TextView // TODO figure out Kotlin reflection is being so slow
                if (textView != null) {
                    CustomFontManager.getInstance(context)
                            .loadFont(CustomFontManager.FONT_CATEGORY_TITLE,
                                    textView.typeface.style) {
                                textView.typeface = it
                            }
                }
            }
        }
    }
        set(value) = run {
            field = value.also {
                it.viewTreeObserver.addOnGlobalLayoutListener { ->
                    (it.getChildAt(0) as ViewGroup).childs.forEach {
                        val textView = it::class.java.getDeclaredField(
                                "textView").also { it.isAccessible = true }.get(it)
                                as? TextView // TODO figure out Kotlin reflection is being so slow
                        if (textView != null) {
                            CustomFontManager.getInstance(context)
                                    .loadFont(CustomFontManager.FONT_CATEGORY_TITLE,
                                            textView.typeface.style) {
                                        textView.typeface = it
                                    }
                        }
                    }
                }
            }
        }
    private var recyclerView = (feedController.findViewById(
            R.id.feed_recycler) as RecyclerView)
    private var toolbar = (feedController.findViewById(R.id.feed_title_bar) as Toolbar)
    private val toolbarParent
        get() = feedController.feed_title_parent
    private var content = (feedController.findViewById(R.id.feed_content) as ViewGroup)
    private var frame = (feedController.findViewById(R.id.feed_main_frame) as FrameLayout)
    private var upButton =
            (feedController.findViewById(R.id.feed_back_to_top) as FloatingActionButton)
    private var tabColours = ColorProvider.Companion.inflate(
            Class.forName(context.lawnchairPrefs.feedColorProvider) as Class<out ColorProvider>)
            .getColors(context).toMutableList().also {
                if (it.isEmpty()) {
                    it += 0
                }
            }
    private lateinit var oldIconTint: ColorStateList
    private var oldIndicatorTint: Int = -1
    private lateinit var oldTextColor: ColorStateList
    private val tabsOnBottom = originalContext.lawnchairPrefs.feedTabsOnBottom
    private val providerScreens: MutableList<Pair<ProviderScreen, ScreenData>> = mutableListOf()
    private var searchWidgetView: AppWidgetHostView? = null
        set(value) {
            field = value
            if (value is OverlayWidgetHost.OverlayWidgetView) {
                value.dark = dark
                value.shouldForceStyle = context.lawnchairPrefs.feedToolbarWidgetForceStyle
                if (value.shouldForceStyle) {
                    value.forceStyle()
                }
            }
        }
    var infobox = feedController.findViewById(R.id.info_box_text) as TextView
    private var reapplyInsetFlag = false
    private var conservativeRefreshTimes =
            mutableMapOf(* tabs.map { it to 0L }.toTypedArray())
    private var lastRefresh = 0L
    var statusBarHeight: Int? = null
    var navigationBarHeight: Int? = null

    var chips: RecyclerView = feedController.findViewById(R.id.chip_container)

    val swipeRefreshLayout
        get() = feedController.findViewById<SwipeRefreshLayout>(R.id.feed_refresh_indicator)

    init {
        reinitState()
        if (backgroundSetupListener != null) {
            backgroundSetupListener {
                runBlocking { delay(2) }
                reinitState(it, true)
            }
        }
    }

    fun initBitmapInfo(url: String, desc: String, bkg: Bitmap) {
        if (context.lawnchairPrefs.feedShowInfobox) {
            readMoreUrl = url
            infobox.text = desc.take(60)
            if (infobox.text.length == 60) {
                infobox.text = infobox.text.toString() + "..."
            }
            infobox.setOnClickListener {
                val screen = ImageDataScreen(context, bkg, desc,
                        if (readMoreUrl != null)
                            ({
                                Utilities.openURLinBrowser(context, readMoreUrl)
                            } as () -> Unit) else null)
                screen.display(this, it.getPostionOnScreen().first, it.getPostionOnScreen().second)
            }
            runOnMainThread {
                (infobox.parent as View).visibility =
                        if (infobox.text.length > 1 && context.lawnchairPrefs.feedShowInfobox) View.VISIBLE else View.GONE
            }
        }
    }

    @SuppressLint("RestrictedApi")
    fun reinitState(backgroundToProcess: Bitmap? = null, reinit: Boolean = false) = handler.post {
        if (context.appWidgetManager
                        .getAppWidgetInfo(
                                context.lawnchairPrefs.feedToolbarWidget) == null &&
                context.lawnchairPrefs.feedToolbarWidget != OverlayWidgetHost.SPECIAL_SMARTSPACE) {
            context.lawnchairPrefs.feedToolbarWidget = -1
        }
        if (searchWidgetView != null && reinit &&
                searchWidgetView?.parent == toolbar) {
            toolbar.findViewById<LinearLayout>(R.id.feed_widget_layout).removeView(searchWidgetView)
        }
        if (reinit) {
            val background =
                    if (!context.lawnchairPrefs.feedBlur) backgroundToProcess else backgroundToProcess?.blur(
                            originalContext)
            providerScreens.iterator().let {
                it.forEach { screen ->
                    frame.removeView(screen.second.view)
                    it.remove()
                }
            }

            internalActions.clear()

            backgroundColor = if (background == null) ColorUtils.setAlphaComponent(
                    ColorEngine.getInstance(originalContext).feedBackground.value.resolveColor(),
                    (LawnchairPreferences.getInstance(
                            originalContext).feedBackgroundOpacity * (255f / 100f)).roundToInt()) else androidx.palette.graphics.Palette.from(
                    backgroundToProcess!!).generate().getDominantColor(0).setAlpha(255)

            dark = useWhiteText(backgroundColor.setAlpha(255), originalContext)
            context = ContextThemeWrapper(originalContext,
                    if (dark) R.style.FeedTheme_Dark else R.style.FeedTheme_Light)
            feedAttached = false
            closeOverlay(0)
            verticalBackground = null
            horizontalBackground = null
            feedController = (LayoutInflater.from(context).inflate(R.layout.overlay_feed, null,
                    false) as FeedController).also {
                it.setLauncherFeed(this)
                it.viewTreeObserver.addOnGlobalLayoutListener {
                    if (horizontalBackground == null || verticalBackground == null) {
                        if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                            verticalBackground = if (background == null) ColorDrawable(
                                    backgroundColor) else BitmapDrawable(context.resources,
                                    Utilities.scaleToSize(
                                            background,
                                            it.measuredHeight,
                                            it.measuredWidth))
                            horizontalBackground = if (background == null) ColorDrawable(
                                    backgroundColor) else BitmapDrawable(context.resources,
                                    Utilities.scaleToSize(
                                            background,
                                            it.measuredWidth,
                                            it.measuredHeight))
                        } else {
                            horizontalBackground = if (background == null) ColorDrawable(
                                    backgroundColor) else BitmapDrawable(context.resources,
                                    Utilities.scaleToSize(
                                            background,
                                            it.measuredHeight,
                                            it.measuredWidth))
                            verticalBackground = if (background == null) ColorDrawable(
                                    backgroundColor) else BitmapDrawable(context.resources,
                                    Utilities.scaleToSize(
                                            background,
                                            it.measuredWidth,
                                            it.measuredHeight))
                        }
                    }
                    lastOrientation = context.resources.configuration.orientation
                    it.background =
                            if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) verticalBackground!! else horizontalBackground!!
                }
            }
            tabView = feedController.findViewById(R.id.feed_tabs) as TabLayout
            recyclerView = (feedController.findViewById(
                    R.id.feed_recycler) as RecyclerView)
            adapter = FeedAdapter(getFeedController(context).getProviders(), backgroundColor,
                    context.applicationContext, this)
            toolbar = (feedController.findViewById(R.id.feed_title_bar) as Toolbar)
            content = (feedController.findViewById(R.id.feed_content) as ViewGroup)
            frame = (feedController.findViewById(R.id.feed_main_frame) as FrameLayout)
            upButton = (feedController.findViewById(R.id.feed_back_to_top) as FloatingActionButton)

            chips = feedController.findViewById(R.id.chip_container) as RecyclerView
            chipAdapter = ChipAdapter(context, this)
        }

        if (ChipDatabase.Holder.getInstance(context).dao().all.size == 0) {
            chips.visibility = View.GONE
        } else {
            chips.visibility = View.VISIBLE
        }

        chips.adapter = chipAdapter
        chips.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        chipAdapter.setController(feedController)

        if (context.chipPrefs.chipsOnTop) {
            val cP = chips.parent as ViewGroup
            cP.removeView(chips)
            cP.addView(chips, 0)
        }

        if (!context.lawnchairPrefs.feedShowInfobox) {
            (infobox.parent as View).visibility = View.GONE
        }

        chips.isNestedScrollingEnabled = true
        chips.setOnTouchListener { v, event ->
            feedController.disallowInterceptCurrentTouchEvent = true
            false
        }
        if (context.lawnchairPrefs.feedHighContrastToolbar) {
            toolbar.setBackgroundColor(backgroundColor.setAlpha(175))
        }

        var oldToolbarPaddingVertical: Pair<Int, Int>? = null
        var oldToolbarPaddingHorizontal: Pair<Int, Int>? = null
        var oldRecyclerViewPaddingHorizontal: Pair<Int, Int>? = null
        if (context.lawnchairPrefs.feedToolbarWidget != -1) {
            val widgetContainer = toolbar.findViewById<LinearLayout>(R.id.feed_widget_layout)
            var deleting = false
            searchWidgetView = (context.applicationContext as LawnchairApp)
                    .overlayWidgetHost
                    .createView(context, context.lawnchairPrefs.feedToolbarWidget,
                            context.appWidgetManager
                                    .getAppWidgetInfo(context.lawnchairPrefs.feedToolbarWidget))
            searchWidgetView!!.layoutParams =
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            context.appWidgetManager
                                    .getAppWidgetInfo(
                                            context.lawnchairPrefs.feedToolbarWidget)?.minHeight
                                    ?: 45f.applyAsDip(context).toInt())
                            .apply {
                                marginStart = 8f.applyAsDip(context)
                                        .toInt()
                                marginEnd = 8f.applyAsDip(context)
                                        .toInt()
                                topMargin = 8f.applyAsDip(context)
                                        .toInt()
                                bottomMargin = 4f.applyAsDip(context)
                                        .toInt()
                            }
            searchWidgetView!!.setOnLongClickListener {
                searchWidgetView!!.animate()
                        .scaleX(0.7f)
                        .scaleY(0.7f)
                        .setInterpolator(Interpolators.ACCEL_1_5).duration = 500
                deleting = true
                true
            }
            searchWidgetView!!.setOnTouchListener { v, event ->
                if (deleting && event.action == MotionEvent.ACTION_UP) {
                    searchWidgetView!!.animate()
                            .scaleX(0f)
                            .scaleY(0f)
                            .setDuration(500)
                            .setListener(object : Animator.AnimatorListener {
                                override fun onAnimationRepeat(animation: Animator?) {

                                }

                                override fun onAnimationEnd(animation: Animator?) {
                                    context.lawnchairPrefs.feedToolbarWidget = -1
                                    reapplyInsetFlag = true
                                    widgetContainer.removeView(searchWidgetView)
                                    searchWidgetView = null
                                }

                                override fun onAnimationCancel(animation: Animator?) {

                                }

                                override fun onAnimationStart(animation: Animator?) {

                                }

                            })
                } else if (deleting && event.action == MotionEvent.ACTION_CANCEL) {
                    deleting = false
                    searchWidgetView!!.animate().scaleX(1f).scaleY(1f).setDuration(250)
                }
                true
            }
            widgetContainer.addView(searchWidgetView, 0)
        }
        swipeRefreshLayout.isEnabled = context.feedPrefs.pullDownToRefresh
        feedController
                .addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                    if (lastOrientation != context.resources.configuration.orientation) {
                        lastOrientation = context.resources.configuration.orientation
                        if (verticalBackground != null && horizontalBackground != null) {
                            feedController.background =
                                    if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) verticalBackground!! else horizontalBackground!!
                        }
                    }
                    if (reapplyInsetFlag) {
                        feedController.requestApplyInsets()
                    }
                }
        upButton.visibility = if (context.lawnchairPrefs.feedBackToTop) View.VISIBLE else View.GONE
        swipeRefreshLayout.setOnRefreshListener {
            FeedScope.launch {
                refresh(100, 0, true, true)
            }
        }
        feedController.setOnApplyWindowInsetsListener { v, insets ->
            statusBarHeight = insets.stableInsetTop
            navigationBarHeight = insets.stableInsetBottom
            toolbarParent.apply {
                if (oldToolbarPaddingVertical == null) {
                    oldToolbarPaddingVertical = paddingTop to paddingBottom
                }
                if (oldToolbarPaddingHorizontal == null) {
                    oldToolbarPaddingHorizontal = paddingLeft to paddingRight
                }
                setPadding(oldToolbarPaddingHorizontal!!.first + insets.stableInsetLeft,
                        if (!tabsOnBottom) oldToolbarPaddingVertical!!.first + statusBarHeight!! else paddingTop,
                        oldToolbarPaddingHorizontal!!.second + insets.stableInsetRight,
                        if (tabsOnBottom) oldToolbarPaddingVertical!!.second + navigationBarHeight!! else paddingBottom)
            }
            (upButton.layoutParams as ViewGroup.MarginLayoutParams).apply {
                marginEnd =
                        if (upButton.layoutDirection == ViewGroup.LAYOUT_DIRECTION_LTR) insets.stableInsetRight + 16 else insets.stableInsetLeft + 16
                bottomMargin = insets.stableInsetBottom + 16f.applyAsDip(context).toInt()
            }
            upButton.animate().translationY(
                    (upButton.measuredHeight + (upButton.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin).toFloat())
                    .duration = 500
            toolbarParent.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                val height = abs(bottom - top)
                val rvPaddingTop = height + 8f.applyAsDip(context).toInt()
                recyclerView.apply {
                    if (oldRecyclerViewPaddingHorizontal == null) {
                        oldRecyclerViewPaddingHorizontal = paddingLeft to paddingRight
                    }
                    setPadding(oldRecyclerViewPaddingHorizontal!!.first + insets.stableInsetLeft,
                            if (tabsOnBottom) 8f.applyAsDip(
                                    context).toInt() + insets.stableInsetTop else rvPaddingTop + statusBarHeight!!,
                            oldRecyclerViewPaddingHorizontal!!.second + insets.stableInsetRight,
                            if (!tabsOnBottom) 8f.applyAsDip(
                                    context).toInt() + insets.stableInsetBottom else rvPaddingTop + navigationBarHeight!!)
                }
                swipeRefreshLayout.apply {
                    if (!tabsOnBottom) {
                        swipeRefreshLayout.setProgressViewOffset(false, 0,
                                rvPaddingTop + statusBarHeight!!)
                    }
                }
            }
            insets
        }
        feedController.mOpenedCallback = {
            runOnNewThread {
                refresh(100)
            }
        }
        upButton.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
            toolbarParent.animate().translationY(0f)
            upButton.animate().translationY(
                    (upButton.measuredHeight + (upButton.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin).toFloat())
        }
        tabView.tabMode = TabLayout.MODE_SCROLLABLE
        tabView.tabGravity = TabLayout.GRAVITY_FILL
        tabView.setOnTouchListener { view, _ ->
            feedController.disallowInterceptCurrentTouchEvent = true
            true
        }
        if (tabsOnBottom) {
            (toolbarParent.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.BOTTOM
            toolbar.parent.requestLayout()
            recyclerView.apply {
                setPadding(paddingLeft, 0, paddingRight, paddingTop)
            }
        }
        if (!useWhiteText(backgroundColor, context) &&
                backgroundColor.alpha > 35) {
            tabView.tabIconTint =
                    ColorStateList.valueOf(R.color.textColorPrimaryInverse.fromColorRes(context))
            tabView.tabSelectedIndicator!!
                    .setTint(R.color.textColorPrimaryInverse.fromColorRes(context))
            tabView.tabTextColors = ColorStateList(
                    arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(), intArrayOf()),
                    arrayOf(tabColours[0],
                            R.color.textColorPrimaryInverse.fromColorRes(context)).toIntArray())
        } else if (!useWhiteText(backgroundColor, context)) {
            tabView.tabIconTint =
                    ColorStateList.valueOf(R.color.textColorPrimaryInverse.fromColorRes(context))
            tabView.tabSelectedIndicator!!
                    .setTint(R.color.textColorPrimaryInverse.fromColorRes(context))
            tabView.tabTextColors =
                    ColorStateList.valueOf(R.color.textColorPrimaryInverse.fromColorRes(context))
        } else if (backgroundColor.alpha < 35 &&
                useWhiteText(backgroundColor, context)) {
            tabView.tabIconTint =
                    ColorStateList.valueOf(R.color.textColorPrimary.fromColorRes(context))
            tabView.tabSelectedIndicator!!
                    .setTint(R.color.textColorPrimary.fromColorRes(context))
            tabView.tabTextColors =
                    ColorStateList.valueOf(R.color.textColorPrimary.fromColorRes(context))
        } else {
            tabView.tabIconTint =
                    ColorStateList.valueOf(R.color.textColorPrimary.fromColorRes(context))
            tabView.tabSelectedIndicator!!
                    .setTint(R.color.textColorPrimary.fromColorRes(context))
            tabView.tabTextColors = ColorStateList(
                    arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(), intArrayOf()),
                    arrayOf(tabColours[0],
                            R.color.textColorPrimary.fromColorRes(context)).toIntArray())
        }
        if (!useTabbedMode) {
            if (tabbedProviders.keys != setOf(null)) {
                error("tabbing inconsistency detected: no tabs were defined but providers are sorted by tabs")
            } else if (tabbedProviders.keys.isEmpty()) {
                error("tabbing inconsistency detected: no tabs were defined but there is no null key in provider map")
            }
            tabView.visibility = View.GONE
        } else {
            processTabs()
            mutableMapOf(* tabs.map { it to 0L }.toTypedArray())
            tabView.setSelectedTabIndicator(TabIndicatorProvider.inflate(
                    Class.forName(
                            context.lawnchairPrefs.feedIndicatorProvider).kotlin as KClass<out TabIndicatorProvider>,
                    context).drawable)
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
                tabView.setOnTouchListener(null)
            }
            currentTab = tabs[0]
            tabView.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                }

                override fun onTabSelected(tab: TabLayout.Tab) {
                    tabChanged = true
                    currentTab = tabs[tab.position]!!
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
                    if (context.lawnchairPrefs.feedHideTabText) {
                        for (i in 0 until (tabView.getChildAt(0) as ViewGroup).childCount) {
                            val tab = (tabView.getChildAt(0) as ViewGroup).getChildAt(i)
                            val title =
                                    tab::class.declaredMembers.first { it.name == "textView" }.apply {
                                        isAccessible = true
                                    }.call(tab) as TextView
                            title.visibility = View.GONE
                        }
                    }
                    (tabView.getChildAt(0) as ViewGroup).childs.forEach {
                        val textView = it::class.java.getDeclaredField(
                                "textView").also { it.isAccessible = true }.get(it)
                                as? TextView // TODO figure out Kotlin reflection is being so slow
                        if (textView != null) {
                            CustomFontManager.getInstance(context)
                                    .loadFont(CustomFontManager.FONT_CATEGORY_TITLE,
                                            textView.typeface.style) {
                                        textView.typeface = it
                                    }
                        }
                    }
                    updateActions()
                    runOnNewThread { refresh(0) }
                }
            })

            adapter.providers = tabbedProviders[tabs.first()]!!
            if (backgroundColor.alpha > 35) {
                tabView.tabTextColors = ColorStateList(
                        arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(), intArrayOf()),
                        arrayOf(tabColours[0],
                                tabView.tabTextColors!!.defaultColor).toIntArray())
                tabView.setSelectedTabIndicatorColor(tabColours[0])
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
            updateActions()
        }
        if (context.lawnchairPrefs.feedAutoHideToolbar) {
            recyclerView.addOnScrollListener(object :
                    RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView,
                                        dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        toolbarParent.animate().translationY(
                                if (!tabsOnBottom) -toolbarParent.measuredHeight.toFloat() else toolbarParent.measuredHeight.toFloat())
                    } else if (dy < 0) {
                        toolbarParent.animate().translationY(0f)
                    }
                }
            })
        }
        if (context.lawnchairPrefs.feedBackToTop) {
            recyclerView.addOnScrollListener(object :
                    RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView,
                                        dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        upButton.animate().translationY(0f).duration = 500
                    } else if (dy < 0) {
                        upButton.animate().translationY(
                                (upButton.measuredHeight + (upButton.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin).toFloat())
                                .duration = 500
                    }
                }
            })
        }
        adapter.registerAdapterDataObserver(object :
                RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                runOnMainThread {
                    if (adapter.itemCount == 0) {
                        toolbar.setTitleTextColor(if (useWhiteText(backgroundColor,
                                        context)) Color.WHITE else R.color.textColorPrimaryInverse.fromColorRes(
                                context))
                    } else {
                        toolbar.title = ""
                    }
                }
            }
        })
        if (adapter.itemCount == 0) {
            toolbar.setTitleTextColor(
                    if (useWhiteText(backgroundColor,
                                    context)) Color.WHITE else R.color.textColorPrimaryInverse.fromColorRes(
                            context))
        } else {
            toolbar.title = ""
        }
        if (!FeatureFlags.GO_DISABLE_WIDGETS) {
            toolbar.setOnLongClickListener {
                OverlayCallbacks.postWidgetRequest(context) {
                    context.lawnchairPrefs.feedToolbarWidget = it
                    if (context.lawnchairPrefs.feedToolbarWidget != -1) {
                        val widgetContainer =
                                toolbar.findViewById<LinearLayout>(
                                        R.id.feed_widget_layout)
                        if (searchWidgetView != null) {
                            widgetContainer.removeView(searchWidgetView)
                        }
                        var deleting = false
                        searchWidgetView =
                                (context.applicationContext as LawnchairApp)
                                        .overlayWidgetHost
                                        .createView(context,
                                                context.lawnchairPrefs.feedToolbarWidget,
                                                context.appWidgetManager
                                                        .getAppWidgetInfo(
                                                                context.lawnchairPrefs.feedToolbarWidget))
                        searchWidgetView!!.layoutParams =
                                LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        context.appWidgetManager
                                                .getAppWidgetInfo(
                                                        context.lawnchairPrefs.feedToolbarWidget)?.minHeight
                                                ?: 32f.applyAsDip(
                                                        context).toInt())
                                        .apply {
                                            marginStart = 8f.applyAsDip(
                                                    context)
                                                    .toInt()
                                            marginEnd = 8f.applyAsDip(
                                                    context)
                                                    .toInt()
                                            topMargin = 8f.applyAsDip(
                                                    context)
                                                    .toInt()
                                            bottomMargin =
                                                    4f.applyAsDip(
                                                            context)
                                                            .toInt()
                                        }
                        searchWidgetView!!.setOnLongClickListener {
                            searchWidgetView!!.animate()
                                    .scaleX(0.7f)
                                    .scaleY(0.7f)
                                    .setInterpolator(
                                            Interpolators.ACCEL_1_5)
                                    .duration = 500
                            deleting = true
                            true
                        }
                        searchWidgetView!!.setOnTouchListener { v, event ->
                            if (deleting && event.action == MotionEvent.ACTION_UP) {
                                searchWidgetView!!.animate()
                                        .scaleX(0f)
                                        .scaleY(0f)
                                        .setDuration(500)
                                        .setListener(object :
                                                Animator.AnimatorListener {
                                            override fun onAnimationRepeat(
                                                    animation: Animator?) {

                                            }

                                            override fun onAnimationEnd(
                                                    animation: Animator?) {
                                                context.lawnchairPrefs.feedToolbarWidget =
                                                        -1
                                                reapplyInsetFlag = true
                                                widgetContainer.removeView(
                                                        searchWidgetView)
                                                searchWidgetView = null
                                            }

                                            override fun onAnimationCancel(
                                                    animation: Animator?) {

                                            }

                                            override fun onAnimationStart(
                                                    animation: Animator?) {

                                            }

                                        })
                            } else if (deleting && event.action == MotionEvent.ACTION_CANCEL) {
                                deleting = false
                                searchWidgetView!!.animate().scaleX(1f)
                                        .scaleY(1f).setDuration(250)
                            }
                            true
                        }
                        reapplyInsetFlag = true
                        widgetContainer.addView(searchWidgetView, 0)
                    }
                }
                true
            }

            upButton.supportImageTintList =
                    ColorStateList.valueOf(FeedAdapter.getOverrideColor(context))
        }
        if (reinit && lastScroll != 0f) {
            startScroll()
            feedController.onScroll(lastScroll)
            callback?.overlayScrollChanged(lastScroll)
            feedAttached = true
        }
        tabView.isInlineLabel = context.lawnchairPrefs.feedHorizontalTabs
        if (context.lawnchairPrefs.feedHideTabText) {
            for (i in 0 until (tabView.getChildAt(0) as ViewGroup).childCount) {
                val tab = (tabView.getChildAt(0) as ViewGroup).getChildAt(i)
                val title = tab::class.declaredMembers.first { it.name == "textView" }.apply {
                    isAccessible = true
                }.call(tab) as TextView
                title.visibility = View.GONE
            }
        }
    }

    fun displayProviderScreen(screen: ProviderScreen, x: Float, y: Float,
                              inflater: (parent: ViewGroup) -> View) {
        var view: View? = null
        displayView({
            inflater(it).also { view = it }
        }, x, y)
        providerScreens.add(screen to ScreenData(x, y, view!!))
        synchronized(internalActions) {
            if (!internalActions.containsKey(R.id.cancel)) {
                internalActions.put(R.id.cancel, FeedProvider.Action(
                        R.drawable.ic_close.fromDrawableRes(context), context.getString(
                        R.string.title_action_back), Runnable {
                    if (providerScreens.isEmpty() || providerScreens.size == 1) {
                        internalActions.remove(R.id.cancel)
                        updateActions()
                    }
                    popScreens()
                }))
            }
            updateActions()
        }
        feedController.isFocusableInTouchMode = true
        feedController.requestFocus()
        feedController.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP &&
                    keyCode == KeyEvent.KEYCODE_BACK && !providerScreens.isEmpty()) {
                if (providerScreens.last().first.onBackPressed()) {
                    return@setOnKeyListener true
                }
                popScreens()
                return@setOnKeyListener true
            } else if (event.action == KeyEvent.ACTION_UP
                    && keyCode == KeyEvent.KEYCODE_BACK){
                feedController.closeOverlay(true, 0)
                true
            } else {
                false
            }
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun popScreens() {
        removeDisplayedView(providerScreens.last().second.view, providerScreens.last().second.x,
                providerScreens.last().second.y)
        screenActions.remove(providerScreens.last().first)
        providerScreens.last().first.onDestroy()
        providerScreens.remove(providerScreens.last())
        updateActions()
    }

    private fun displayView(inflater: (parent: ViewGroup) -> View, x: Float, y: Float) {
        if (useTabbedMode) {
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
                tabView.tabSelectedIndicator!!
                        .setTint(getColorForIndex(tabView.selectedTabPosition))
                tabView.tabTextColors = ColorStateList(
                        arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(), intArrayOf()),
                        arrayOf(getColorForIndex(tabView.selectedTabPosition),
                                R.color.textColorPrimaryInverse.fromColorRes(context)).toIntArray())
            } else if (!useWhiteText(backgroundColor, context) && dark) {
                tabView.tabIconTint = ColorStateList(
                        arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(), intArrayOf()),
                        arrayOf(getColorForIndex(tabView.selectedTabPosition),
                                R.color.textColorPrimary.fromColorRes(context)).toIntArray())
                tabView.tabSelectedIndicator!!
                        .setTint(getColorForIndex(tabView.selectedTabPosition))
                tabView.tabTextColors = ColorStateList(
                        arrayOf(arrayOf(android.R.attr.state_selected).toIntArray(), intArrayOf()),
                        arrayOf(getColorForIndex(tabView.selectedTabPosition),
                                R.color.textColorPrimary.fromColorRes(context)).toIntArray())
            }
        }
        frame.addView(inflater(frame).apply {
            background = ColorDrawable(backgroundColor.setAlpha(max(200, backgroundColor.alpha)))
            visibility = View.INVISIBLE
            fitsSystemWindows = false
            var originalPaddingHorizontal: Pair<Int, Int>? = null
            var originalPaddingVertical: Pair<Int, Int>? = null
            setOnApplyWindowInsetsListener { _, windowInsets ->
                if (originalPaddingHorizontal == null || originalPaddingVertical == null) {
                    originalPaddingHorizontal = paddingStart to paddingEnd
                    originalPaddingVertical = paddingTop to paddingBottom
                }
                if (this is ViewGroup) {
                    setPadding(
                            originalPaddingHorizontal!!.first + if (!rtl) windowInsets.stableInsetLeft else windowInsets.stableInsetRight,
                            originalPaddingVertical!!.first + if (!tabsOnBottom) toolbarParent.measuredHeight + R.dimen.overlay_view_margin.fromDimenRes(
                                    context).toInt() + statusBarHeight!! else statusBarHeight!!,
                            originalPaddingHorizontal!!.second + if (!rtl) windowInsets.stableInsetRight else windowInsets.stableInsetLeft,
                            originalPaddingVertical!!.second + if (tabsOnBottom) toolbarParent.measuredHeight + R.dimen.overlay_view_margin.fromDimenRes(
                                    context).toInt() + statusBarHeight!! else statusBarHeight!!)
                }
                if (context.feedPrefs.useBackgroundImageAsScreenBackground) {
                    background =
                            if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) horizontalBackground!! else verticalBackground!!
                } else {
                    background =
                            ColorDrawable(backgroundColor.setAlpha(max(200, backgroundColor.alpha)))
                }
                windowInsets
            }
            viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    val (height, width) = measuredHeight to measuredWidth
                    viewTreeObserver.removeOnPreDrawListener(this)
                    val radius = hypot(height.toDouble(), width.toDouble())
                    val animator = ViewAnimationUtils
                            .createCircularReveal(this@apply, x.toInt(), y.toInt(), 0f,
                                    radius.toFloat())
                    animate().setDuration(0)
                            .translationZ(4f.applyAsDip(context) * (providerScreens.size - 1));
                    visibility = View.VISIBLE
                    animator.apply {
                        duration = 300
                        start()
                    }
                    recyclerView.isLayoutFrozen = true
                    toolbarParent.animate().translationY(0f)
                    return true;
                }
            })
            viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    requestApplyInsets()
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        })
        for (i in 0 until toolbar.menu.size()) {
            toolbar.menu.getItem(i).icon?.setTint(tabView.tabTextColors?.defaultColor ?: 0);
        }
    }

    fun removeDisplayedView(v: View, x: Float, y: Float) {
        if (useTabbedMode) {
            tabView.tabIconTint = oldIconTint
            tabView.tabTextColors = oldTextColor
            tabView.tabSelectedIndicator?.setTint(oldIndicatorTint)
            for (i in 0 until toolbar.menu.size()) {
                toolbar.menu.getItem(i).icon?.setTint(oldTextColor.defaultColor)
            }
        }
        tabView.tabsEnabled = true
        recyclerView.isLayoutFrozen = false
        v.apply {
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
                        frame.removeView(v)
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }
                })
                start()
                animate().setDuration(300).translationZ(0f)
            }
            recyclerView.isLayoutFrozen = false
        }
    }

    fun processTabs() = if (context.lawnchairPrefs.feedHideUnusedTabs) {
        val iter = tabs.iterator()
        iter.forEach {
            if (tabbedProviders[it]?.isEmpty() == true) {
                iter.remove()
                tabbedProviders.remove(it)
            }
        }
    } else Unit

    private var callback: ILauncherOverlayCallback? = null
    private lateinit var layoutParams: WindowManager.LayoutParams
    private var feedAttached = false
        set(value) = synchronized(this) {
            if (field != value) {
                field = value
                if (field) {
                    if (recyclerView.adapter == null) {
                        recyclerView.adapter = this.adapter
                        recyclerView.layoutManager =
                                object : androidx.recyclerview.widget.LinearLayoutManager(context) {
                                    override fun onLayoutChildren(
                                            recycler: RecyclerView.Recycler?,
                                            state: RecyclerView.State?) {
                                        try {
                                            super.onLayoutChildren(recycler, state)
                                        } catch (e: RuntimeException) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                    }
                    callback?.overlayStatusChanged(
                            ServiceState.FLAG_ATTACHED or ServiceState.FLAG_SEARCH_ATTACHED)

                    if (lastOrientation != context.resources.configuration.orientation) {
                        if (horizontalBackground != null && verticalBackground != null) {
                            feedController.background =
                                    if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) verticalBackground!! else horizontalBackground!!
                        }
                        lastOrientation = context.resources.configuration.orientation
                    }
                    feedController.visibility = View.VISIBLE
                    windowService.addView(feedController, layoutParams)
                    providerScreens.forEach { it.first.onResume() }
                } else {
                    feedController.visibility = View.INVISIBLE
                    feedController.alpha = 0f
                    try {
                        windowService.removeViewImmediate(feedController)
                    } catch (e: RuntimeException) {
                        windowService.removeView(feedController)
                    }
                    providerScreens.forEach { it.first.onPause() }
                }
            }
        }

    override fun startScroll() {
        if (lastOrientation != context.resources.configuration.orientation) {
            lastOrientation = context.resources.configuration.orientation
            if (verticalBackground != null && horizontalBackground != null) {
                feedController.background =
                        if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) verticalBackground!! else horizontalBackground!!
            }
        }
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
        handler.post {
            lastScroll = progress
            feedController.onScroll(progress)
        }
    }

    override fun endScroll() {
        handler.post { synchronized(this@LauncherFeed) { feedController.endScroll() } }
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
        }
    }

    override fun closeOverlay(flags: Int) {
        if (feedAttached) {
            handler.post { feedController.closeOverlay((flags and 1) != 0, flags shr 2) }
        }
    }

    override fun onPause() {
        activityState.onPause()
    }

    override fun onResume() {
        activityState.onResume()
    }

    override fun openOverlay(flags: Int) {
        handler.post {
            feedAttached = true
            feedController.openOverlay((flags and 1) != 0, flags shr 2)
        }
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
        activityState.importMask(flags)
    }

    override fun startSearch(data: ByteArray?, bundle: Bundle?): Boolean {
        return if (searchWidgetView == null) false else run {
            searchWidgetView?.performClick()
            true
        }
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
        if (index < tabColours.size) {
            return tabColours[index]
        } else {
            val first = tabColours[index % tabColours.size]
            val second = tabColours[tabColours.size - 1 - index % tabColours.size]
            return {
                val result =
                        if (index % 2 > 0) (first and 0x00F0FF) or (second and 0xFF0F00) else (first and 0xFF00F0) or (second and 0x00FF0F)
                val opacity = first shr 24
                opacity shl 24 or (result and 0xFFFFFF)
            }()
        }
    }

    fun refresh(sleep: Long, count: Int = 0, quick: Boolean = true,
                clearCache: Boolean = false): Unit = synchronized(this) {
        Thread.sleep(sleep + 150)
        swipeRefreshLayout.post {
            swipeRefreshLayout.isEnabled = context.feedPrefs.pullDownToRefresh
            swipeRefreshLayout.setColorSchemeColors(* tabColours.toIntArray())
            swipeRefreshLayout.isRefreshing = true
        }
        runOnMainThread {
            updateActions()
        }
        recyclerView.apply {
            post {
                recyclerView.isLayoutFrozen = true
            }
        }
        FeedScope.launch {
            ChipController.getInstance(context, this@LauncherFeed).refresh()
        }
        runOnMainThread {
            if (!context.lawnchairPrefs.feedShowInfobox) {
                toolbarParent.removeView(infobox.parent as View)
            }
            if (!context.chipPrefs.chipsOnTop) {
                val cP = chips.parent as ViewGroup
                cP.removeView(chips)
                cP.addView(chips, cP.childCount)
            } else {
                if (!context.chipPrefs.chipsOnTop) {
                    val cP = chips.parent as ViewGroup
                    cP.removeView(chips)
                    cP.addView(chips, 0)
                }
            }
            if (context.lawnchairPrefs.feedShowInfobox) {
                toolbarParent.addView(infobox.parent as View)
            }
            if (ChipDatabase.Holder.getInstance(context).dao().all.size == 0) {
                chips.visibility = View.GONE
            } else {
                chips.visibility = View.VISIBLE
            }
            chipAdapter.notifyDataSetChanged()
        }
        val oldCards = adapter.immutableCards
        FeedScope.launch {
            if (!quick) {
                if (clearCache || !context.feedPrefs.conservativeRefreshes ||
                        ((!useTabbedMode && System.currentTimeMillis() - lastRefresh > TimeUnit.MINUTES.toMillis(
                                5)) ||
                                (useTabbedMode && System.currentTimeMillis() - conservativeRefreshTimes[currentTab]!! > TimeUnit.MINUTES.toMillis(
                                        5)))) {
                    adapter.refresh()
                    if (!adapter.cards.isEmpty()) {
                        lastRefresh = System.currentTimeMillis()
                        if (::currentTab.isInitialized) {
                            conservativeRefreshTimes[currentTab] = System.currentTimeMillis()
                        }
                    }
                } else {
                    adapter.refreshVolatile()
                }
            }
            val cards = adapter.immutableCards
            if (quick) {
                if (!(clearCache || !context.feedPrefs.conservativeRefreshes ||
                                ((!useTabbedMode && System.currentTimeMillis() - lastRefresh > TimeUnit.MINUTES.toMillis(
                                        5)) ||
                                        (useTabbedMode && System.currentTimeMillis() - conservativeRefreshTimes[currentTab]!! > TimeUnit.MINUTES.toMillis(
                                                5)))) && adapter.providers.none { it.isVolatile } && !tabChanged) {
                    recyclerView.post {
                        recyclerView.isLayoutFrozen = false
                        recyclerView.visibility = View.VISIBLE
                    }
                    return@launch
                }
                tabChanged = false
                runOnMainThread {
                    var flag = false
                    recyclerView.animate().setDuration(250).alpha(0f)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    if (!flag) {
                                        flag = true
                                        FeedScope.launch {
                                            if (clearCache || !context.feedPrefs.conservativeRefreshes ||
                                                    ((!useTabbedMode && System.currentTimeMillis() - lastRefresh > TimeUnit.MINUTES.toMillis(
                                                            5)) ||
                                                            (useTabbedMode && System.currentTimeMillis() - conservativeRefreshTimes[currentTab]!! > TimeUnit.MINUTES.toMillis(
                                                                    5)))) {
                                                adapter.refresh()
                                                if (!adapter.cards.isEmpty()) {
                                                    lastRefresh = System.currentTimeMillis()
                                                    if (::currentTab.isInitialized) {
                                                        conservativeRefreshTimes[currentTab] =
                                                                System.currentTimeMillis()
                                                    }
                                                }
                                            } else {
                                                adapter.refreshVolatile()
                                            }
                                            recyclerView.post {
                                                adapter.notifyDataSetChanged()
                                                recyclerView.isLayoutFrozen = false
                                                recyclerView.visibility = View.VISIBLE
                                                feedController.findViewById<View>(R.id.empty_view)
                                                        .visibility =
                                                        if (adapter.itemCount >= 1) View.GONE else View.VISIBLE
                                                recyclerView.animate().setUpdateListener {
                                                    if (it.animatedFraction == 1f) {
                                                        swipeRefreshLayout.isRefreshing = false
                                                    }
                                                }.alpha(1f).duration = 250
                                            }
                                        }
                                    }
                                }
                            })
                }
            } else if (oldCards.isEmpty() && count == 0) {
                adapter.notifyItemRangeInserted(0, adapter.itemCount)
            } else {
                val patch = DiffUtils.diff(oldCards, cards)

                runOnMainThread {
                    if (adapter.cards.isNotEmpty()) {
                        patch.deltas.forEach {
                            when (it.type!!) {
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
                    } else {
                        recyclerView.isLayoutFrozen = false
                        adapter.notifyItemRangeRemoved(0, oldCards.size)
                    }
                    recyclerView.isLayoutFrozen = false
                    recyclerView.visibility = View.VISIBLE
                    feedController.findViewById<View>(R.id.empty_view).visibility =
                            if (cards.isNotEmpty()) View.GONE else View.VISIBLE
                    swipeRefreshLayout.post {
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }
    }

    fun pickWidget(callback: (id: Int) -> Unit) {
        handler.post {
            OverlayCallbacks.postWidgetRequest(context, callback)
        }
    }

    fun updateActions() {
        toolbar.menu.clear()
        if (adapter.providers.size == 1) {
            (adapter.providers[0].getActions(true)).sortedBy { it.name }
                    .forEach {
                        toolbar.menu.add(Menu.NONE, it.onClick.hashCode(), Menu.NONE, it.name)
                                .apply {
                                    if (!context.feedPrefs.displayActionsAsMenu) {
                                        setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                                    }
                                    icon = it.icon.tint(
                                            if (dark) Color.WHITE else R.color.textColorPrimaryInverse.fromColorRes(
                                                    context))
                                    setOnMenuItemClickListener { _ ->
                                        it.onClick.run()
                                        true
                                    }
                                }
                    }
        } else {
            adapter.providers.forEach {
                (it.getActions(
                        false)).sortedBy { it.name }.forEach {
                    toolbar.menu.add(Menu.NONE, it.onClick.hashCode(), Menu.NONE, it.name).apply {
                        if (!context.feedPrefs.displayActionsAsMenu) {
                            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                        }
                        icon = it.icon.tint(
                                if (dark) Color.WHITE else R.color.textColorPrimaryInverse.fromColorRes(
                                        context))
                        setOnMenuItemClickListener { _ ->
                            it.onClick.run()
                            true
                        }
                    }
                }
            }
        }
        if (providerScreens.isNotEmpty() && screenActions.containsKey(
                        providerScreens.last().first)) {
            (screenActions[providerScreens.last().first]!! + internalActions.values).forEach {
                toolbar.menu.add(Menu.NONE, it.onClick.hashCode(), Menu.NONE, it.name)
                        .apply {
                            if (!context.feedPrefs.displayActionsAsMenu) {
                                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                            }
                            icon = it.icon.tint(
                                    if (dark) Color.WHITE else R.color.textColorPrimaryInverse.fromColorRes(
                                            context))
                            setOnMenuItemClickListener { _ ->
                                it.onClick.run()
                                true
                            }
                        }
            }
        } else {
            internalActions.values.forEach {
                toolbar.menu.add(Menu.NONE, it.onClick.hashCode(), Menu.NONE, it.name)
                        .apply {
                            if (!context.feedPrefs.displayActionsAsMenu) {
                                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                            }
                            icon = it.icon.tint(
                                    if (dark) Color.WHITE else R.color.textColorPrimaryInverse.fromColorRes(
                                            context))
                            setOnMenuItemClickListener { _ ->
                                it.onClick.run()
                                true
                            }
                        }
            }
        }
    }

    fun onBackPressed() = false

    private data class ScreenData(val x: Float, val y: Float, val view: View)
}
