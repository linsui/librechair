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

package ch.deletescape.lawnchair.feed.widgets

import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.ViewGroup
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.cp.OverlayCallbacks
import ch.deletescape.lawnchair.feed.Card
import ch.deletescape.lawnchair.feed.DbScope
import ch.deletescape.lawnchair.feed.FeedProvider
import com.android.launcher3.R
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.Executors

class FeedWidgetsProvider(c: Context) : FeedProvider(c) {
    private val hostViewCache = mutableMapOf<Int, AppWidgetHostView>()
    private val inflateExecutor = Executors.newFixedThreadPool(5)!!
    private val appWidgetManager by lazy {
        context.getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager
    }
    override fun getCards(): List<Card> {
        return WidgetDatabase.getInstance(context).dao().all
                .filter { appWidgetManager.getAppWidgetInfo(it.id) != null }.map { widget ->
                    val it = widget.id
                    Card(if (!widget.showCardTitle) null else appWidgetManager.getAppWidgetInfo(
                            it).loadIcon(context, context.resources.displayMetrics.densityDpi),
                            if (!widget.showCardTitle) null else widget.customCardTitle
                                    ?: appWidgetManager.getAppWidgetInfo(
                                            it).loadLabel(
                                            context.packageManager),
                            { parent, _ ->
                                if (hostViewCache.containsKey(it)) {
                                    hostViewCache[it] ?: error("")
                                } else {
                                    (parent.context.applicationContext as LawnchairApp)
                                            .overlayWidgetHost.createView(parent.context, it,
                                            appWidgetManager.getAppWidgetInfo(
                                                    it).apply {
                                                minWidth = parent.width
                                            }).apply {
                                        setExecutor(inflateExecutor)
                                        layoutParams = ViewGroup
                                                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                        if (widget.height != -1) widget.height else appWidgetInfo.minHeight)
                                        updateAppWidgetOptions(Bundle().apply {
                                            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,
                                                    width)
                                            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,
                                                    width)
                                            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,
                                                    if (widget.height != -1) widget.height else appWidgetInfo.minHeight)
                                            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
                                                    if (widget.height != -1) widget.height else appWidgetInfo.minHeight)
                                        })
                                        invalidate()
                                    }.also { it2 ->
                                        hostViewCache += it to it2; it2.invalidate()
                                    }
                                }
                            }, if ((widget ?: Widget.DEFAULT).raiseCard) if (!(widget
                                    ?: Widget.DEFAULT).showCardTitle) Card.NO_HEADER or Card.RAISE
                    else Card.RAISE
                    else if (!(widget
                                    ?: Widget.DEFAULT).showCardTitle) Card.NO_HEADER
                    else Card.DEFAULT, if (widget?.sortable == true) "" else "nosort, top",
                            it shl 2 + UUID.randomUUID().hashCode()).apply {
                        canHide = true
                        onRemoveListener = {
                            DbScope.launch {
                                synchronized(WidgetDatabase.getInstance(context)) {
                                    val widgets = WidgetDatabase.getInstance(context).dao()
                                            .all.filter { it != widget }
                                    WidgetDatabase.getInstance(context).dao().removeAll()
                                    widgets.forEach {
                                        WidgetDatabase.getInstance(context).dao()
                                                .addWidget(it.apply {
                                                    entryOrder = widgets.indexOf(this)
                                                })
                                    }
                                }
                            }
                        }
                    }
                }
    }

    override fun isVolatile(): Boolean {
        return true
    }

    override fun getActions(exclusive: Boolean): List<Action> {
        return listOf(Action((if (exclusive) R.drawable.ic_add.fromDrawableRes(
                context) else R.drawable.ic_widget.fromDrawableRes(context)).tint(
                if (useWhiteText(backgroundColor, context)) R.color.textColorPrimary.fromColorRes(
                        context) else R.color.textColorPrimaryInverse.fromColorRes(context)),
                R.string.title_feed_toolbar_add_widget.fromStringRes(context), Runnable {
            OverlayCallbacks.postWidgetRequest(context) {
                if (it != -1) {
                    DbScope.launch {
                        try {
                            WidgetDatabase.getInstance(context)
                                    .dao()
                                    .addWidget(Widget(it,
                                            WidgetDatabase.getInstance(
                                                    context)
                                                    .dao().all.size))
                        } catch (e: SQLiteConstraintException) {
                            e.printStackTrace() // todo dirty hack
                        }
                    }.invokeOnCompletion {
                        runOnMainThread {
                            requestRefreshFeed()
                        }
                    }
                }
            }
        }))
    }
}