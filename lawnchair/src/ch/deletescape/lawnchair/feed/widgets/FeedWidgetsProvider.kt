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
import android.os.Bundle
import android.view.ViewGroup
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.feed.Card
import ch.deletescape.lawnchair.feed.FeedProvider
import com.android.launcher3.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class FeedWidgetsProvider(c: Context) : FeedProvider(c) {
    val hostViewCache = mutableMapOf<Int, AppWidgetHostView>()
    val inflateExecutor = Executors.newFixedThreadPool(5)
    val appWidgetManager by lazy {
        context.getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager
    }

    override fun onFeedShown() {
    }

    override fun onFeedHidden() {
    }

    override fun onCreate() {
    }

    override fun onDestroy() {
    }

    override fun getCards(): List<Card> {
        return WidgetDatabase.getInstance(context).dao().all
                .filter { appWidgetManager.getAppWidgetInfo(it.id) != null }.map { widget ->
                    val it = widget.id
                    val metadata = widget
                    Card(if (!metadata.showCardTitle) null else appWidgetManager.getAppWidgetInfo(
                            it).loadIcon(context, context.resources.displayMetrics.densityDpi),
                            if (!metadata.showCardTitle) null else metadata.customCardTitle
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
                                                        (metadata
                                                                ?: Widget.DEFAULT).height
                                                                ?: appWidgetInfo.minHeight)
                                        updateAppWidgetOptions(Bundle().apply {
                                            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,
                                                    width)
                                            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,
                                                    width)
                                            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,
                                                    (metadata ?: Widget.DEFAULT).height
                                                            ?: height)
                                            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
                                                    (metadata ?: Widget.DEFAULT).height
                                                            ?: height)
                                        })
                                        invalidate()
                                    }.also { it2 ->
                                        hostViewCache += it to it2; it2.invalidate()
                                    }
                                }
                            }, if ((metadata ?: Widget.DEFAULT).raiseCard) if (!(metadata
                                    ?: Widget.DEFAULT).showCardTitle) Card.NO_HEADER or Card.RAISE
                    else Card.RAISE
                    else if (!(metadata
                                    ?: Widget.DEFAULT).showCardTitle) Card.NO_HEADER
                    else Card.DEFAULT, if (metadata?.sortable == true) "" else "nosort, top",
                            it shl 2)
                }
    }

    override fun getActions(exclusive: Boolean): List<Action> {
        return listOf(Action((if (exclusive) R.drawable.ic_add.fromDrawableRes(
                context) else R.drawable.ic_widget.fromDrawableRes(context)).tint(
                if (useWhiteText(backgroundColor, context)) R.color.textColorPrimary.fromColorRes(
                        context) else R.color.textColorPrimaryInverse.fromColorRes(context)),
                R.string.title_feed_toolbar_add_widget.fromStringRes(context), Runnable {
            feed?.pickWidget {
                if (it != -1) {
                    GlobalScope.launch {
                        WidgetDatabase.getInstance(context)
                                .dao()
                                .addWidget(Widget(it,
                                        WidgetDatabase.getInstance(
                                                context)
                                                .dao().all.size))
                    }.invokeOnCompletion {
                        feed?.refresh(0, 0, true)
                    }
                }
            }
        }))
    }
}