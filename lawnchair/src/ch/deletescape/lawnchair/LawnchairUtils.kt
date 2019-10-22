/*
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

package ch.deletescape.lawnchair

import android.app.Activity
import android.app.ActivityManager
import android.appwidget.AppWidgetManager
import android.content.*
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherActivityInfo
import android.content.pm.PackageInfo.REQUESTED_PERMISSION_GRANTED
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.*
import android.graphics.Color.alpha
import android.graphics.drawable.*
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.AttributeSet
import android.util.Property
import android.util.TypedValue
import android.view.*
import android.view.animation.Interpolator
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.preference.Preference
import androidx.preference.PreferenceGroup
import ch.deletescape.lawnchair.blur.BlurWallpaperProvider
import ch.deletescape.lawnchair.colors.ColorEngine
import ch.deletescape.lawnchair.feed.FeedAdapter
import ch.deletescape.lawnchair.feed.FeedProvider
import ch.deletescape.lawnchair.feed.FeedScope
import ch.deletescape.lawnchair.feed.i18n.UnitLocale
import ch.deletescape.lawnchair.feed.maps.MapProvider
import ch.deletescape.lawnchair.feed.maps.MapScreen
import ch.deletescape.lawnchair.feed.maps.TextOverlay
import ch.deletescape.lawnchair.feed.maps.locationsearch.LocationSearchManager
import ch.deletescape.lawnchair.font.CustomFontManager
import ch.deletescape.lawnchair.persistence.feedPrefs
import ch.deletescape.lawnchair.persistence.generalPrefs
import ch.deletescape.lawnchair.smartspace.weather.forecast.ForecastProvider
import ch.deletescape.lawnchair.theme.ThemeManager
import ch.deletescape.lawnchair.util.JSONMap
import ch.deletescape.lawnchair.util.extensions.d
import ch.deletescape.lawnchair.util.hasFlag
import com.android.launcher3.*
import com.android.launcher3.compat.LauncherAppsCompat
import com.android.launcher3.compat.UserManagerCompat
import com.android.launcher3.model.BgDataModel
import com.android.launcher3.shortcuts.DeepShortcutManager
import com.android.launcher3.util.ComponentKey
import com.android.launcher3.util.LooperExecutor
import com.android.launcher3.util.PackageUserKey
import com.android.launcher3.util.Themes
import com.android.launcher3.views.OptionsPopupView
import com.android.systemui.shared.recents.model.TaskStack
import com.google.android.apps.nexuslauncher.CustomAppPredictor
import com.google.android.apps.nexuslauncher.CustomIconUtils
import com.google.android.material.tabs.TabLayout
import com.hoko.blur.HokoBlur
import com.hoko.blur.processor.BlurProcessor
import com.rometools.rome.feed.synd.SyndEntry
import kotlinx.android.synthetic.main.calendar_event.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.xmlpull.v1.XmlPullParser
import java.io.IOException
import java.lang.reflect.Field
import java.security.MessageDigest
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

/*
 * Copyright (C) 2018 paphonb@xda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

private val forecastProviders = mutableMapOf<KClass<out ForecastProvider>, ForecastProvider>()

val Context.launcherAppState get() = LauncherAppState.getInstance(this)
val Context.lawnchairPrefs get() = Utilities.getLawnchairPrefs(this)

val Context.hasStoragePermission
    get() = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this,
                                                                                   android.Manifest.permission.READ_EXTERNAL_STORAGE)
val Context.forecastProvider: ForecastProvider
    get() = forecastProviders[Class.forName(
            generalPrefs.weatherProvider).kotlin as KClass<out ForecastProvider>]
            ?: ForecastProvider.Controller
                    .inflateForecastProvider(this, this.generalPrefs.weatherProvider).also {
                        forecastProviders += Class.forName(
                                generalPrefs.weatherProvider).kotlin as KClass<out ForecastProvider> to it
                    }

fun nothing() {

}

fun tomorrow(current: Date = Date()): Date {
    val date = current.clone() as Date
    val oneDayMillis = 1000 * 60 * 60 * 24
    date.time += oneDayMillis
    date.time = date.time - (date.time % oneDayMillis)
    return date
}

fun tomorrowL(current: Long = System.currentTimeMillis()): Long {
    val oneDayMillis = TimeUnit.DAYS.toMillis(1)
    val tp = current + oneDayMillis
    return tp - (tp % oneDayMillis)
}

@ColorInt fun Context.getColorEngineAccent(): Int {
    return ColorEngine.getInstance(this).accent
}

@ColorInt fun Context.getColorAccent(): Int {
    return getColorAttr(android.R.attr.colorAccent)
}

@ColorInt fun Context.getDisabled(inputColor: Int): Int {
    return applyAlphaAttr(android.R.attr.disabledAlpha, inputColor)
}

@ColorInt fun Context.applyAlphaAttr(attr: Int, inputColor: Int): Int {
    val ta = obtainStyledAttributes(intArrayOf(attr))
    val alpha = ta.getFloat(0, 0f)
    ta.recycle()
    return applyAlpha(alpha, inputColor)
}

@ColorInt fun applyAlpha(a: Float, inputColor: Int): Int {
    var alpha = a
    alpha *= alpha(inputColor)
    return Color.argb(alpha.toInt(), Color.red(inputColor), Color.green(inputColor),
                      Color.blue(inputColor))
}

@ColorInt fun Context.getColorAttr(attr: Int): Int {
    val ta = obtainStyledAttributes(intArrayOf(attr))
    @ColorInt val colorAccent = ta.getColor(0, 0)
    ta.recycle()
    return colorAccent
}

fun Context.getThemeAttr(attr: Int): Int {
    val ta = obtainStyledAttributes(intArrayOf(attr))
    val theme = ta.getResourceId(0, 0)
    ta.recycle()
    return theme
}

fun Context.getDrawableAttr(attr: Int): Drawable? {
    val ta = obtainStyledAttributes(intArrayOf(attr))
    val drawable = ta.getDrawable(0)
    ta.recycle()
    return drawable
}

fun Context.getDrawableAttrNullable(attr: Int): Drawable? {
    return try {
        getDrawableAttr(attr)
    } catch (e: Resources.NotFoundException) {
        null
    }
}

fun Context.getDimenAttr(attr: Int): Int {
    val ta = obtainStyledAttributes(intArrayOf(attr))
    val size = ta.getDimensionPixelSize(0, 0)
    ta.recycle()
    return size
}

fun Context.getBooleanAttr(attr: Int): Boolean {
    val ta = obtainStyledAttributes(intArrayOf(attr))
    val value = ta.getBoolean(0, false)
    ta.recycle()
    return value
}

fun Context.getIntAttr(attr: Int): Int {
    val ta = obtainStyledAttributes(intArrayOf(attr))
    val value = ta.getInt(0, 0)
    ta.recycle()
    return value
}

inline fun ViewGroup.forEachChildIndexed(action: (View, Int) -> Unit) {
    val count = childCount
    for (i in (0 until count)) {
        action(getChildAt(i), i)
    }
}

inline fun ViewGroup.forEachChild(action: (View) -> Unit) {
    forEachChildIndexed { view, _ -> action(view) }
}

inline fun ViewGroup.forEachChildReversedIndexed(action: (View, Int) -> Unit) {
    val count = childCount
    for (i in (0 until count).reversed()) {
        action(getChildAt(i), i)
    }
}

inline fun ViewGroup.forEachChildReversed(action: (View) -> Unit) {
    forEachChildReversedIndexed { view, _ -> action(view) }
}

fun ComponentKey.getLauncherActivityInfo(context: Context): LauncherActivityInfo? {
    return LauncherAppsCompat.getInstance(context).getActivityList(componentName.packageName, user)
            .firstOrNull { it.componentName == componentName }
}

@Suppress("UNCHECKED_CAST") class JavaField<T>(private val targetObject: Any, fieldName: String,
                                               targetClass: Class<*> = targetObject::class.java) {

    private val field: Field = targetClass.getDeclaredField(fieldName).apply { isAccessible = true }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return field.get(targetObject) as T
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        field.set(targetObject, value)
    }
}

class KFloatPropertyCompat(private val property: KMutableProperty0<Float>, name: String) :
        FloatPropertyCompat<Any>(name) {

    override fun getValue(`object`: Any) = property.get()

    override fun setValue(`object`: Any, value: Float) {
        property.set(value)
    }
}

class KFloatProperty(private val property: KMutableProperty0<Float>, name: String) :
        Property<Any, Float>(Float::class.java, name) {

    override fun get(`object`: Any) = property.get()

    override fun set(`object`: Any, value: Float) {
        property.set(value)
    }
}

val SCALE_XY: Property<View, Float> = object : Property<View, Float>(Float::class.java, "scaleXY") {
    override fun set(view: View, value: Float) {
        view.scaleX = value
        view.scaleY = value
    }

    override fun get(view: View): Float {
        return view.scaleX
    }
}

fun Float.clamp(min: Float, max: Float): Float {
    if (this <= min) return min
    if (this >= max) return max
    return this
}

fun isPrivilegedApp(ai: ApplicationInfo): Boolean {
    /* val method = ApplicationInfo::class.java.getDeclaredMethod("isPrivilegedApp")
    return method.invoke(ai) as Boolean */
    return false
}

fun Float.round() = roundToInt().toFloat()

fun Float.ceilToInt() = ceil(this).toInt()

fun Double.ceilToInt() = ceil(this).toInt()

class PropertyDelegate<T>(private val property: KMutableProperty0<T>) {

    operator fun getValue(thisRef: Any?, prop: KProperty<*>): T {
        return property.get()
    }

    operator fun setValue(thisRef: Any?, prop: KProperty<*>, value: T) {
        property.set(value)
    }
}

val mainHandler by lazy { Handler(Looper.getMainLooper()) }
val uiWorkerHandler by lazy { Handler(LauncherModel.getUiWorkerLooper()) }
val iconPackUiHandler by lazy { Handler(LauncherModel.getIconPackUiLooper()) }
val workerHandler by lazy { Handler(LauncherModel.getWorkerLooper()) }

fun runOnUiWorkerThread(r: () -> Unit) {
    runOnThread(uiWorkerHandler, r)
}

fun runOnNewThread(r: () -> Unit) {
    Executors.newSingleThreadExecutor().submit {
        r()
    }
}

fun isChromiumCompatiblePackage(pakage: String, context: Context): Boolean {
    return Intent().setComponent(ComponentName(pakage, "org.chromium.chrome.browser.searchwidget.SearchActivity")).resolveActivityInfo(context.packageManager, 0) != null
}

fun runOnMainThread(r: () -> Unit) {
    runOnThread(mainHandler, r)
}

fun drawableToBitmap(drawable: Drawable): Bitmap {
    var bitmap: Bitmap;


    if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
        bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    } else {
        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                                     Bitmap.Config.ARGB_8888)
    }

    var canvas = Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);
    return bitmap;
}

fun runOnThread(handler: Handler, r: () -> Unit) {
    if (handler.looper.thread.id == Looper.myLooper()?.thread?.id) {
        r()
    } else {
        handler.post(r)
    }
}

fun TextView.setCustomFont(type: Int) {
    CustomFontManager.getInstance(context).setCustomFont(this, type)
}

fun ViewGroup.getAllChilds() = ArrayList<View>().also { getAllChilds(it) }

fun ViewGroup.getAllChilds(list: MutableList<View>) {
    for (i in (0 until childCount)) {
        val child = getChildAt(i)
        if (child is ViewGroup) {
            child.getAllChilds(list)
        } else {
            list.add(child)
        }
    }
}

fun Activity.hookGoogleSansDialogTitle() {
    val activity = this
    layoutInflater.factory2 = object : LayoutInflater.Factory2 {
        override fun onCreateView(parent: View?, name: String, context: Context,
                                  attrs: AttributeSet): View? {
            if (name == "com.android.internal.widget.DialogTitle") {
                return (Class.forName(name).getConstructor(Context::class.java,
                                                           AttributeSet::class.java).newInstance(
                    context, attrs) as TextView)
                        .apply { setCustomFont(CustomFontManager.FONT_DIALOG_TITLE) }
            }
            return activity.onCreateView(parent, name, context, attrs)
        }

        override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
            return onCreateView(null, name, context, attrs)
        }

    }
}

fun openPopupMenu(view: View, rect: RectF?, vararg items: OptionsPopupView.OptionItem) {
    val launcher = Launcher.getLauncher(view.context)
    OptionsPopupView.show(launcher, rect ?: RectF(launcher.getViewBounds(view)), items.toList())
}

fun Context.getLauncherOrNull(): Launcher? {
    return try {
        Launcher.getLauncher(this)
    } catch (e: ClassCastException) {
        null
    } catch (e: IllegalArgumentException) {
        null
    }
}

fun Context.getBaseDraggingActivityOrNull(): BaseDraggingActivity? {
    return try {
        BaseDraggingActivity.fromContext(this)
    } catch (e: ClassCastException) {
        null
    } catch (e: IllegalArgumentException) {
        null
    }
}

var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

private val MAX_UNICODE = '\uFFFF'

/**
 * Returns true if {@param target} is a search result for {@param query}
 */
fun java.text.Collator.matches(query: String, target: String): Boolean {
    return when (this.compare(query, target)) {
        0 -> true
        -1 ->
            // The target string can contain a modifier which would make it larger than
            // the query string (even though the length is same). If the query becomes
            // larger after appending a unicode character, it was originally a prefix of
            // the target string and hence should match.
            this.compare(query + MAX_UNICODE, target) > -1 || target.contains(query,
                                                                              ignoreCase = true)
        else -> false
    }
}

fun String.toTitleCase(): String = splitToSequence(" ").map { it.capitalize() }.joinToString(" ")

fun reloadIcons(context: Context) {
    val userManagerCompat = UserManagerCompat.getInstance(context)
    val launcherApps = LauncherAppsCompat.getInstance(context)

    reloadIcons(context, userManagerCompat.userProfiles.flatMap { user ->
        launcherApps.getActivityList(null, user)
                .map { PackageUserKey(it.componentName.packageName, it.user) }
    })
}

fun reloadIconsFromComponents(context: Context, components: Collection<ComponentKey>) {
    reloadIcons(context, components.map { PackageUserKey(it.componentName.packageName, it.user) })
}

fun reloadIcons(context: Context, packages: Collection<PackageUserKey>) {
    LooperExecutor(LauncherModel.getIconPackLooper()).execute {
        val userManagerCompat = UserManagerCompat.getInstance(context)
        val las = LauncherAppState.getInstance(context)
        val model = las.model
        val launcher = las.launcher

        for (user in userManagerCompat.userProfiles) {
            model.onPackagesReload(user)
        }

        val shortcutManager = DeepShortcutManager.getInstance(context)
        packages.forEach {
            CustomIconUtils.reloadIcon(shortcutManager, model, it.mUser, it.mPackageName)
        }
        if (launcher != null) {
            runOnMainThread {
                (launcher.userEventDispatcher as CustomAppPredictor).uiManager
                        .onPredictionsUpdated()
            }
        }
    }
}

fun Context.getIcon(): Drawable = packageManager.getApplicationIcon(applicationInfo)

val TaskStack.mostRecentTask
    get() = this.tasks.getOrNull(this.taskCount - 1)

fun <T, A> ensureOnMainThread(creator: (A) -> T): (A) -> T {
    return { it ->
        if (Looper.myLooper() == Looper.getMainLooper()) {
            creator(it)
        } else {
            try {
                MainThreadExecutor().submit(Callable { creator(it) }).get()
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            } catch (e: ExecutionException) {
                throw RuntimeException(e)
            }

        }
    }
}

fun <T> useApplicationContext(creator: (Context) -> T): (Context) -> T {
    return { it -> creator(it.applicationContext) }
}

class ViewPagerAdapter(private val pages: List<Pair<String, View>>) : androidx.viewpager.widget.PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = pages[position].second
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun getCount() = pages.size

    override fun isViewFromObject(view: View, obj: Any) = (view === obj)

    override fun getPageTitle(position: Int) = pages[position].first
}

fun dpToPx(size: Float): Float {
    return TypedValue
            .applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, Resources.getSystem().displayMetrics)
}

fun pxToDp(size: Float): Float {
    return size / dpToPx(1f)
}

fun Drawable.toBitmap(forceCreate: Boolean = true, fallbackSize: Int = 0): Bitmap? {
    return Utilities.drawableToBitmap(this, forceCreate, fallbackSize)
}

fun AlertDialog.applyAccent() {
    val fontManager = CustomFontManager.getInstance(context)
    val color = ColorEngine.getInstance(context).accent

    getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
        fontManager.setCustomFont(this, CustomFontManager.FONT_BUTTON)
        setTextColor(color)
    }
    getButton(AlertDialog.BUTTON_NEUTRAL)?.apply {
        fontManager.setCustomFont(this, CustomFontManager.FONT_BUTTON)
        setTextColor(color)
    }
    getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
        fontManager.setCustomFont(this, CustomFontManager.FONT_BUTTON)
        setTextColor(color)
    }
}

fun android.app.AlertDialog.applyAccent() {
    val color = ColorEngine.getInstance(context).accent
    val buttons =
            listOf(getButton(AlertDialog.BUTTON_NEGATIVE), getButton(AlertDialog.BUTTON_NEUTRAL),
                   getButton(AlertDialog.BUTTON_POSITIVE))
    buttons.forEach {
        it.setTextColor(color)
        it.setCustomFont(CustomFontManager.FONT_DIALOG_TITLE)
    }
}

fun BgDataModel.workspaceContains(packageName: String): Boolean {
    return this.workspaceItems.any { it.targetComponent?.packageName == packageName }
}

fun findInViews(op: Workspace.ItemOperator, vararg views: ViewGroup?): View? {
    views.forEach { view ->
        if (view == null || view.width == 0 || view.height == 0) return@forEach
        view.forEachChild { item ->
            val info = item.tag as ItemInfo?
            if (op.evaluate(info, item)) {
                return item
            }
        }
    }
    return null
}

class ReverseOutputInterpolator(private val base: Interpolator) : Interpolator {

    override fun getInterpolation(input: Float): Float {
        return 1 - base.getInterpolation(input)
    }
}

class ReverseInputInterpolator(private val base: Interpolator) : Interpolator {

    override fun getInterpolation(input: Float): Float {
        return base.getInterpolation(1 - input)
    }
}

fun Switch.applyColor(color: Int) {
    val colorForeground = Themes.getAttrColor(context, android.R.attr.colorForeground)
    val alphaDisabled = Themes.getAlpha(context, android.R.attr.disabledAlpha)
    val switchThumbNormal = context.resources
            .getColor(androidx.preference.R.color.switch_thumb_normal_material_light)
    val switchThumbDisabled = context.resources
            .getColor(com.google.android.material.R.color.switch_thumb_disabled_material_light)
    val thstateList = ColorStateList(
        arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf(android.R.attr.state_checked),
                intArrayOf()), intArrayOf(switchThumbDisabled, color, switchThumbNormal))
    val trstateList = ColorStateList(
        arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf(android.R.attr.state_checked),
                intArrayOf()),
        intArrayOf(ColorUtils.setAlphaComponent(colorForeground, alphaDisabled), color,
                   colorForeground))
    DrawableCompat.setTintList(thumbDrawable, thstateList)
    DrawableCompat.setTintList(trackDrawable, trstateList)
}

fun Button.applyColor(color: Int) {
    val rippleColor = ColorStateList.valueOf(ColorUtils.setAlphaComponent(color, 31))
    (background as RippleDrawable).setColor(rippleColor)
    DrawableCompat.setTint(background, color)
    val tintList = ColorStateList.valueOf(color)
    if (this is RadioButton) {
        buttonTintList = tintList
    }
}

inline fun <T> Iterable<T>.safeForEach(action: (T) -> Unit) {
    val tmp = ArrayList<T>()
    tmp.addAll(this)
    for (element in tmp) action(element)
}

operator fun PreferenceGroup.get(index: Int): Preference = getPreference(index)
inline fun PreferenceGroup.forEachIndexed(action: (i: Int, pref: Preference) -> Unit) {
    for (i in 0 until preferenceCount) action(i, this[i])
}

operator fun XmlPullParser.get(index: Int): String? = getAttributeValue(index)
operator fun XmlPullParser.get(namespace: String?, key: String): String? = getAttributeValue(
    namespace, key)

operator fun XmlPullParser.get(key: String): String? = this[null, key]

val Configuration.usingNightMode get() = uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

fun <T, U : Comparable<U>> comparing(extractKey: (T) -> U): Comparator<T> {
    return Comparator { o1, o2 -> extractKey(o1).compareTo(extractKey(o2)) }
}

fun <T, U : Comparable<U>> Comparator<T>.then(extractKey: (T) -> U): Comparator<T> {
    return kotlin.Comparator { o1, o2 ->
        val res = compare(o1, o2)
        if (res != 0) res else extractKey(o1).compareTo(extractKey(o2))
    }
}

fun <E> MutableSet<E>.addOrRemove(obj: E, exists: Boolean): Boolean {
    if (contains(obj) != exists) {
        if (exists) add(obj)
        else remove(obj)
        return true
    }
    return false
}

fun CheckedTextView.applyAccent() {
    val tintList = ColorStateList.valueOf(ColorEngine.getInstance(context).accent)
    if (Utilities.ATLEAST_MARSHMALLOW) {
        compoundDrawableTintList = tintList
    }
    backgroundTintList = tintList
}

fun ViewGroup.isChild(view: View): Boolean = indexOfChild(view) != -1

fun ImageView.tintDrawable(color: Int) {
    val drawable = drawable.mutate()
    drawable.setTint(color)
    setImageDrawable(drawable)
}

fun View.runOnAttached(runnable: Runnable) {
    if (isAttachedToWindow) {
        runnable.run()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {

            override fun onViewAttachedToWindow(v: View?) {
                runnable.run()
                removeOnAttachStateChangeListener(this)
            }

            override fun onViewDetachedFromWindow(v: View?) {
                removeOnAttachStateChangeListener(this)
            }
        })

    }
}

@Suppress("UNCHECKED_CAST") fun <T> JSONArray.toArrayList(): ArrayList<T> {
    val arrayList = ArrayList<T>()
    for (i in (0 until length())) {
        arrayList.add(get(i) as T)
    }
    return arrayList
}

fun Collection<String>.toJsonStringArray(): JSONArray {
    val array = JSONArray()
    forEach { array.put(it) }
    return array
}

fun Context.resourcesForApplication(packageName: String): Resources? {
    return try {
        packageManager.getResourcesForApplication(packageName)
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}

fun ViewGroup.setCustomFont(type: Int, allCaps: Boolean? = null) {
    forEachChild {
        if (it is ViewGroup) {
            it.setCustomFont(type, allCaps)
        } else if (it is TextView) {
            it.setCustomFont(type)
            if (allCaps != null) {
                it.isAllCaps = allCaps
            }
        }
    }
}

fun getTabRipple(context: Context, accent: Int): ColorStateList {
    return ColorStateList(arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf()),
                          intArrayOf(ColorUtils.setAlphaComponent(accent, 31),
                                     context.getColorAttr(android.R.attr.colorControlHighlight)))
}

fun JSONObject.getNullable(key: String): Any? {
    return opt(key)
}

fun JSONObject.asMap() = JSONMap(this)

fun String.asNonEmpty(): String? {
    if (TextUtils.isEmpty(this)) return null
    return this
}

fun createRipple(foreground: Int, background: Int): RippleDrawable {
    val rippleColor = ColorStateList.valueOf(ColorUtils.setAlphaComponent(foreground, 31))
    return RippleDrawable(rippleColor, ShapeDrawable().apply { paint.color = background },
                          ShapeDrawable())
}

fun Context.createColoredButtonBackground(color: Int): Drawable {
    val shape = getDrawable(R.drawable.colored_button_shape)!!
    shape.setTintList(
        ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf()),
                       intArrayOf(getDisabled(getColorAttr(R.attr.colorButtonNormal)), color)))
    val highlight = getColorAttr(R.attr.colorControlHighlight)
    val ripple = RippleDrawable(ColorStateList.valueOf(highlight), shape, null)
    val insetHorizontal =
            resources.getDimensionPixelSize(R.dimen.abc_button_inset_horizontal_material)
    val insetVertical = resources.getDimensionPixelSize(R.dimen.abc_button_inset_vertical_material)
    return InsetDrawable(ripple, insetHorizontal, insetVertical, insetHorizontal, insetVertical)
}

fun Context.createDisabledColor(color: Int): ColorStateList {
    return ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf()),
                          intArrayOf(getDisabled(getColorAttr(android.R.attr.colorForeground)),
                                     color))
}

class ViewGroupChildIterator(private val viewGroup: ViewGroup, private var current: Int) :
        ListIterator<View> {

    override fun hasNext() = current < viewGroup.childCount

    override fun next() = viewGroup.getChildAt(current++)!!

    override fun nextIndex() = current

    override fun hasPrevious() = current > 0

    override fun previous() = viewGroup.getChildAt(current--)!!

    override fun previousIndex() = current - 1
}

class ViewGroupChildList(private val viewGroup: ViewGroup) : List<View> {

    override val size get() = viewGroup.childCount

    override fun isEmpty() = size == 0

    override fun contains(element: View): Boolean {
        return any { it === element }
    }

    override fun containsAll(elements: Collection<View>): Boolean {
        return elements.all { contains(it) }
    }

    override fun get(index: Int) = viewGroup.getChildAt(index)!!

    override fun indexOf(element: View) = indexOfFirst { it === element }

    override fun lastIndexOf(element: View) = indexOfLast { it === element }

    override fun iterator() = listIterator()

    override fun listIterator() = listIterator(0)

    override fun listIterator(index: Int) = ViewGroupChildIterator(viewGroup, index)

    override fun subList(fromIndex: Int, toIndex: Int) = ArrayList(this).subList(fromIndex, toIndex)
}

val ViewGroup.childs get() = ViewGroupChildList(this)

fun ContentResolver.getDisplayName(uri: Uri): String? {
    query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
    }
    return null
}

inline fun avg(vararg of: Float) = of.average()
inline fun avg(vararg of: Int) = of.average()
inline fun avg(vararg of: Long) = of.average()
inline fun avg(vararg of: Double) = of.average()

fun Context.checkLocationAccess(): Boolean {
    return Utilities.hasPermission(
        this, android.Manifest.permission.ACCESS_FINE_LOCATION)
}

val Int.foregroundColor
    get() = androidx.palette.graphics.Palette.Swatch(ColorUtils.setAlphaComponent(this, 0xFF), 1).bodyTextColor

val Int.luminance get() = ColorUtils.calculateLuminance(this)

val Int.isDark get() = luminance < 0.5f

inline fun <E> createWeakSet(): MutableSet<E> = Collections.newSetFromMap(WeakHashMap<E, Boolean>())

inline fun <T> listWhileNotNull(generator: () -> T?): List<T> = mutableListOf<T>().apply {
    while (true) {
        add(generator() ?: break)
    }
}

inline infix fun Int.hasFlag(flag: Int) = (this and flag) != 0

fun String.hash(type: String): String {
    val chars = "0123456789abcdef"
    val bytes = MessageDigest.getInstance(type).digest(toByteArray())
    val result = StringBuilder(bytes.size * 2)

    bytes.forEach {
        val i = it.toInt()
        result.append(chars[i shr 4 and 0x0f])
        result.append(chars[i and 0x0f])
    }

    return result.toString()
}

val Context.locale: Locale
    get() {
        return if (Utilities.ATLEAST_NOUGAT) {
            this.resources.configuration.locales[0] ?: this.resources.configuration.locale
        } else {
            this.resources.configuration.locale
        }
    }

fun createRipplePill(context: Context, color: Int, radius: Float): Drawable {
    return RippleDrawable(ContextCompat.getColorStateList(context, R.color.focused_background)!!,
                          createPill(color, radius), createPill(color, radius))
}

fun createPill(color: Int, radius: Float): Drawable {
    return GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(color)
        cornerRadius = radius
    }
}

val Long.Companion.random get() = Random.nextLong()

fun StatusBarNotification.loadSmallIcon(context: Context): Drawable? {
    return if (Utilities.ATLEAST_MARSHMALLOW) {
        notification.smallIcon?.loadDrawable(context)
    } else {
        context.resourcesForApplication(packageName)?.getDrawable(notification.icon)
    }
}

fun Context.checkPackagePermission(packageName: String, permissionName: String): Boolean {
    try {
        val info = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        info.requestedPermissions.forEachIndexed { index, s ->
            if (s == permissionName) {
                return info.requestedPermissionsFlags[index].hasFlag(REQUESTED_PERMISSION_GRANTED)
            }
        }
    } catch (e: PackageManager.NameNotFoundException) {
    }
    return false
}

fun <T> Sequence<T>.isEmpty() = !iterator().hasNext()

fun formatTime(dateTime: Date, context: Context? = null): String {
    return when (context) {
        null -> String.format("%d:%02d", dateTime.hours, dateTime.minutes)
        else -> if (DateFormat.is24HourFormat(context)) String.format("%02d:%02d", dateTime.hours,
                                                                      dateTime.minutes) else String.format(
            "%d:%02d %s", if (dateTime.hours % 12 == 0) 12 else dateTime.hours % 12, dateTime.minutes,
            if (dateTime.hours < 12) "am" else "pm")
    }
}

fun formatTime(calendar: Calendar, context: Context? = null): String {
    return when (context) {
        null -> String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY),
                              calendar.get(Calendar.HOUR_OF_DAY))
        else -> if (DateFormat.is24HourFormat(context)) String.format("%02d:%02d", calendar.get(
            Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)) else String.format("%02d:%02d %s",
                                                                                     if (calendar.get(
                                                                                         Calendar.HOUR_OF_DAY) % 12 == 0) 12 else calendar.get(
                                                                                         Calendar.HOUR_OF_DAY) % 12,
                                                                                     calendar.get(
                                                                                         Calendar.MINUTE),
                                                                                     if (calendar.get(
                                                                                                 Calendar.HOUR_OF_DAY) < 12) "AM" else "PM")
    }
}

fun formatTime(zonedDateTime: ZonedDateTime, context: Context? = null): String {
    return when (context) {
        null -> String.format("%d:%02d", zonedDateTime.hour, zonedDateTime.minute)
        else -> if (DateFormat.is24HourFormat(context)) String.format("%02d:%02d",
                                                                      zonedDateTime.hour,
                                                                      zonedDateTime.minute) else String.format(
            "%d:%02d %s", if (zonedDateTime.hour % 12 == 0) 12 else zonedDateTime.hour % 12, zonedDateTime.minute,
            if (zonedDateTime.hour < 12) "AM" else "PM")
    }
}

fun getCalendarFeedView(descriptionNullable: String?, addressNullable: String?, context: Context,
                        parentView: ViewGroup, provider: FeedProvider): View {
    val v = LayoutInflater.from(parentView.context).inflate(R.layout.calendar_event, parentView, false)
    val description = v.findViewById(R.id.calendar_event_description) as TextView
    val address = v.findViewById(R.id.calendar_event_address) as TextView
    val directions = v.findViewById(R.id.calendar_event_directions) as TextView
    val maps = v.findViewById<MapView>(R.id.maps_view)

    directions.setTextColor(FeedAdapter.getOverrideColor(context))

    v.findViewById<Button>(R.id.maps_more_btn).setBackgroundColor(FeedAdapter.getOverrideColor(context))
    v.findViewById<Button>(R.id.maps_more_btn).setTextColor(FeedAdapter.getOverrideColor(context))
    if (addressNullable == null || addressNullable.trim().isEmpty()) {
        address.visibility = View.GONE
        directions.visibility = View.GONE
        (maps.parent as View).visibility = View.GONE
        v.findViewById<View>(R.id.maps_more_btn).visibility = View.GONE
    } else {
        address.text = addressNullable
        @Suppress("UNCHECKED_CAST")
        maps.tileProvider.tileSource = MapProvider
                .inflate(Class.forName(context.feedPrefs.mapProvider) as Class<out MapProvider>).tileSource
        maps.onResume()
        maps.visibility = View.INVISIBLE
        maps.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        FeedScope.launch(Dispatchers.IO) {
            try {
                val loc = LocationSearchManager.getInstance(context).get(addressNullable!!)
                if (loc == null) {
                    maps.post {
                        maps.visibility = View.GONE
                        v.findViewById<View>(R.id.maps_more_btn).visibility = View.GONE
                    }
                } else {
                    val (lat, lon) = loc.first to loc.second
                    maps.post {

                        val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                                if (ch.deletescape.lawnchair.location.LocationManager.location != null) {
                                    MapScreen(context, provider.feed, lat, lon, 15.0, lat, lon, GeoPoint(ch.deletescape.lawnchair.location.LocationManager.location!!.first,
                                            ch.deletescape.lawnchair.location.LocationManager.location!!.second), GeoPoint(lat, lon))
                                            .display(provider,
                                                    v.maps_more_btn.getPostionOnScreen().first + e.x.roundToInt(),
                                                    v.maps_more_btn.getPostionOnScreen().second + e.y.roundToInt())
                                } else {
                                    MapScreen(context, provider.feed, lat, lon, 15.0, lat, lon)
                                            .display(provider,
                                                    v.maps_more_btn.getPostionOnScreen().first + e.x.roundToInt(),
                                                    v.maps_more_btn.getPostionOnScreen().second + e.y.roundToInt())
                                }
                                return true;
                            }
                        })
                        v.maps_more_btn.setOnTouchListener { v, event ->
                            gestureDetector.onTouchEvent(event)
                        }
                        maps.apply {
                            controller.apply {
                                setZoom(13.0)
                                alpha = 0f
                                visibility = View.VISIBLE
                                animate().alpha(1f).duration = 250
                                if (ch.deletescape.lawnchair.location.LocationManager.location != null) {
                                    overlayManager.add(TextOverlay(context).apply {
                                        val here = ch.deletescape.lawnchair.location.LocationManager.location.let {
                                            Location("").apply {
                                                latitude = it!!.first
                                                longitude = it.second
                                            }
                                        }
                                        val there = Location("").apply {
                                            latitude = lat
                                            longitude = lon
                                        }
                                        setText(UnitLocale.getDefault().formatDistanceKm((there.distanceTo(here) / 1000).roundToLong()))
                                    })
                                }
                                setCenter(GeoPoint(lat, lon))
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                maps.post { maps.visibility = View.GONE }
            }
        }
        directions.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$addressNullable"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                parentView.context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
    }
    if (descriptionNullable == null || descriptionNullable.trim().isEmpty()) {
        description.visibility = View.GONE
    } else {
        description.text = descriptionNullable
    }
    return v;
}

fun getCurrentProcessName(context: Context): String {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    manager.runningAppProcesses.iterator().forEach {
        if (it.pid == android.os.Process.myPid()) {
            return it.processName;
        }
    }
    error("This function may only be called within Librechair")
}

fun <T> newList(): MutableList<T> {
    return ArrayList()
}

fun useWhiteText(color: Int, c: Context): Boolean {
    if (ColorUtils.calculateLuminance(color) < 0.5) {
        return !ThemeManager.getInstance(c).supportsDarkText
    } else {
        return alpha(color) < 50
    }
}

inline val Calendar.minuteOfHour get() = get(Calendar.MINUTE)
inline val Calendar.hourOfDay get() = get(Calendar.HOUR_OF_DAY)
inline val Calendar.dayOfYear get() = get(Calendar.DAY_OF_YEAR)

inline val Int.red get() = Color.red(this)
inline val Int.green get() = Color.green(this)
inline val Int.blue get() = Color.blue(this)
inline val Int.alpha get() = alpha(this)
fun Int.setAlpha(alpha: Int): Int {
    return ColorUtils.setAlphaComponent(this, alpha)
}

fun Int.fromStringRes(c: Context, vararg formatObjects: Any): String {
    return c.getString(this, formatObjects)
}

fun Int.fromPluralRes(c: Context, item: Int, vararg formatObjects: String = arrayOf(item)
        .map { it.toString() }
        .toTypedArray()): String {
    return String.format(c.resources.getQuantityString(this, item), * formatObjects)
}

inline val Int.stringRes
    get() = Launcher.getInstance().getString(this)
inline val Int.dimenRes
    get() = Launcher.getInstance().resources.getDimension(this)
inline val Int.colourRes
    get() = Launcher.getInstance().getColor(this)
inline val Context.appWidgetManager
    get() = getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager


fun Int.fromColorRes(c: Context): Int {
    return c.getColor(this)
}

fun Int.fromDrawableRes(c: Context): Drawable {
    return c.getDrawable(this)!!
}

fun Drawable.tint(colour: Int): Drawable {
    return constantState?.newDrawable()?.mutate()?.also { it.setTint(colour) } ?:
    this.also { setTint(colour) }
}

fun Int.fromDimenRes(c: Context): Float {
    return c.resources.getDimension(this)
}

fun <T> alter(condition: Boolean, pos: T, neg: T): T {
    return if (condition) pos else neg
}

fun <T> alter(condition: Boolean, pos: () -> Unit, neg: () -> Unit) {
    if (condition) {
        pos()
    } else {
        neg()
    }
}

inline val SyndEntry.thumbnailURL: String?
    get() = run {
        if (foreignMarkup.isEmpty()) {
            return@run null
        }
        for (element in foreignMarkup) {
            d("get: parsing foreign markup element ${element.namespacePrefix} ${element.name} ${element.attributes} ${element.getAttribute(
                    "url")} for image url")
            if (element.getAttributeValue("url")?.contains(
                            Regex("(img|image|png|jpeg|jpg|bmp)")) == true || element.getAttributeValue(
                            "url")?.matches(
                            Regex("https://.+\\.googleusercontent.com/proxy/.+")) == true) {
                d("get: attribute \"url\" is a valid image URL. using that!")
                return@run element.getAttributeValue("url")
            } else if (element.getAttributeValue("url") != null) {
                return element.getAttributeValue("url")
            }
        }
        return@run null
    }.also { d("get: found thumbnail $it") }

fun Resources.Theme.getColorAttrib(attrib: Int): Int {
    val value = TypedValue()
    resolveAttribute(R.attr.colorAccent, value, true)
    return value.data
}

fun ViewGroup.findViewsByClass(clazz: Class<out View>, exact: Boolean): List<View> {
    val results = mutableListOf<View>()
    for (view in getAllChilds()) {
        if (view.javaClass == clazz || (!exact && clazz.isInstance(view))) {
            results += view
        }
        if (view is ViewGroup) {
            results += view.findViewsByClass(clazz, exact)
        }
    }
    return results
}

fun atMost(number: Int, max: Int): Int {
    return if (number < max) number else max
}

fun <T> also(t: T, runnable: (t: T) -> Unit): T {
    return t.also { runnable(it) }
}

fun Bitmap.withContrastAndBrightness(contrast: Float, brightness: Float): Bitmap {
    val matrix = ColorMatrix(
            floatArrayOf(contrast, 0f, 0f, 0f, brightness, 0f, contrast, 0f, 0f, brightness, 0f, 0f,
                         contrast, 0f, brightness, 0f, 0f, 0f, 1f,
                         0f))
    val duplicate = Bitmap.createBitmap(width, height, config)
    val canvas = Canvas(duplicate)
    val paint = Paint()
    paint.setColorFilter(ColorMatrixColorFilter(matrix))
    canvas.drawBitmap(this, 0f, 0f, paint)
    return duplicate
}

fun Bitmap?.toDrawable(c: Context? = null) = if (this == null) null else if (c == null) BitmapDrawable(this) else BitmapDrawable(
        c.resources, this)

fun View.getPostionOnScreen(): Pair<Int, Int> {
    val array = intArrayOf(0, 0)
    getLocationOnScreen(array)
    return array[0] to array[1]
}

val Context.locationManager
    get() = getSystemService(Context.LOCATION_SERVICE) as LocationManager
val LocationManager.lastKnownPosition: Location?
    get() = getLastKnownLocation(getBestProvider(Criteria(), true))

inline var TabLayout.tabsEnabled: Boolean
    set(value) = {
        for (child in (childs[0] as ViewGroup).childs) {
            child.isClickable = value
        }
    }()
    get() = run {
        for (child in (childs[0] as ViewGroup).childs) {
            if (!child.isClickable) {
                return@run false
            }
        }
        true
    }
val Locale.twoLetterCountryCode: String
    get() = run {
        val countries: MutableMap<String, String> = mutableMapOf()
        for (iso in Locale.getISOCountries()) {
            val l = Locale("", iso);
            countries.put(l.isO3Country, iso);
        }
        return@run countries[isO3Country]!!
    }
val Context.lawnchairLocationManager: ch.deletescape.lawnchair.location.LocationManager
    get() = ch.deletescape.lawnchair.location.LocationManager.also {
        if (it.context == null) {
            it.context = this
        }
    }
val Context.colorEngine
    get() = ColorEngine.getInstance(this)

fun Float.applyAsDip(c: Context): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, c.resources.displayMetrics)
}

fun Float.applyAsSip(c: Context): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, c.resources.displayMetrics)
}

fun Bitmap.blur(c: Context): Bitmap = BlurProcessor.Builder(c)
            .mode(if (c.feedPrefs.useBoxBackgroundBlur)
                HokoBlur.MODE_BOX else HokoBlur.MODE_STACK)
            .scheme(HokoBlur.SCHEME_OPENGL)
            .context(c)
            .radius(c.lawnchairPrefs.feedBlurStrength.roundToInt() / (BlurWallpaperProvider.DOWNSAMPLE_FACTOR / 2))
            .processor()
            .blur(this)

fun ViewGroup.inflate(@LayoutRes res: Int): View = LayoutInflater.from(context).inflate(res, this, false)

val ViewGroup.allChildren: List<View>
    get() = run {
    val children = mutableListOf<View>()
    for (i in 0 until childCount) {
        val view = getChildAt(i);
        children.add(view)
        if (view is ViewGroup) {
            children.addAll(view.allChildren)
        }
    }
    children
}

val ViewGroup.rtl
    get() = layoutDirection == ViewGroup.LAYOUT_DIRECTION_RTL

val Context.eightF
    get() = 8f.applyAsDip(this)
val Context.eightI
    get() = eightF.roundToInt()