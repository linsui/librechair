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

package ch.deletescape.lawnchair.preferences

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.preference.DialogPreference
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.TextView
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.feed.widgets.WidgetMetadata
import ch.deletescape.lawnchair.theme.ThemeManager
import ch.deletescape.lawnchair.theme.ThemeOverride
import ch.deletescape.lawnchair.util.extensions.d
import ch.deletescape.lawnchair.views.VerticalResizeView
import com.android.launcher3.Launcher
import com.android.launcher3.R
import java.util.*


class FeedWidgetsListPreference(context: Context, attrs: AttributeSet) :
        DialogPreference(context, attrs) {

    override fun getNegativeButtonText(): CharSequence? {
        return null
    }

    override fun getDialogLayoutResource() = R.layout.dialog_preference_recyclerview


    class ViewHolder(itemView: View,
                     val title: TextView = itemView.findViewById(android.R.id.title),
                     val summary: TextView = itemView.findViewById(android.R.id.summary),
                     val dragHandle: View = itemView.findViewById(R.id.drag_handle)) :
            RecyclerView.ViewHolder(itemView)

    class Fragment : PreferenceDialogFragmentCompat() {
        val preference by lazy { context!!.lawnchairPrefs.feedWidgetList }

        override fun onDialogClosed(positiveResult: Boolean) {

        }

        override fun onBindDialogView(view: View) {
            super.onBindDialogView(view)
            d("onBindDialogView: bound to dialog view $view")
            val recyclerView = view.findViewById<RecyclerView>(R.id.list)
            d("onBindDialogView: found recycler $recyclerView")
            recyclerView.layoutManager = LinearLayoutManager(context)
            d("onBindDialogView: set layoutManager for RecyclerView $recyclerView")
            recyclerView.adapter = FeedWidgetsPreferenceAdapter(preference)
            d("onBindDialogView: set adapter for RecyclerView ${recyclerView.adapter}")
        }

        class FeedWidgetsPreferenceAdapter(
                val preference: LawnchairPreferences.MutableListPref<Int>) :
                RecyclerView.Adapter<ViewHolder>() {
            val prefList = preference.getList().filter {
                Launcher.getInstance().appWidgetManager.getAppWidgetInfo(it) != null
            }.toMutableList()
            var itemTouchHelper: ItemTouchHelper? = null


            fun synchronizeWithPreference() {
                preference.setAll(prefList)
            }

            override fun onCreateViewHolder(parent: ViewGroup,
                                            viewType: Int): ViewHolder = ViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.event_provider_dialog_item,
                                                                parent, false))

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                d("onBindViewHolder: retrieving widget information for widget ${preference.getAll()[holder.adapterPosition]}")
                val appWidgetInfo = holder.itemView.context.appWidgetManager
                        .getAppWidgetInfo(preference.getAll().filter {
                            Launcher.getInstance().appWidgetManager.getAppWidgetInfo(it) != null
                        }[holder.adapterPosition])
                val appWidgetId = preference.getAll().filter {
                    Launcher.getInstance().appWidgetManager.getAppWidgetInfo(it) != null
                }[holder.adapterPosition]
                holder.dragHandle.setOnTouchListener { view: View, motionEvent: MotionEvent ->
                    if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                        itemTouchHelper?.startDrag(holder)
                        true
                    }
                    false
                }
                holder.dragHandle.visibility = View.VISIBLE

                holder.title.text = appWidgetInfo.loadLabel(holder.itemView.context.packageManager)
                holder.itemView.setOnClickListener {
                    val builder = object : AlertDialog(it.context,
                                                       ThemeOverride.AlertDialog().getTheme(
                                                               ThemeManager(
                                                                       it.getContext()).getCurrentFlags())) {}
                    builder.setTitle(R.string.title_preference_resize_widget)
                    val dialogView: View = LayoutInflater.from(builder.context)
                            .inflate(R.layout.dialog_widget_resize, null, false)
                    builder.setView(dialogView)
                    builder.show()
                    val resizedAppWidgetInfo = holder.itemView.context.appWidgetManager
                            .getAppWidgetInfo(preference.getAll().filter {
                                Launcher.getInstance().appWidgetManager.getAppWidgetInfo(it) != null
                            }[holder.adapterPosition]).apply {
                                minWidth = (dialogView.parent as View).width
                            }
                    val resizeView = dialogView.findViewById<VerticalResizeView>(R.id.resize_view)
                    val widgetView =
                            (it.context.applicationContext as LawnchairApp).overlayWidgetHost
                                    .createView(LawnchairLauncher.getLauncher(it.context),
                                                appWidgetId, resizedAppWidgetInfo)
                    dialogView.findViewById<FrameLayout>(R.id.resize_view_container)
                            .addView(widgetView)
                    widgetView.layoutParams = FrameLayout.LayoutParams(4600,
                                                                       (it.context.lawnchairPrefs.feedWidgetMetadata.getAll().firstOrNull { it2 -> it2.first == appWidgetId }?.second
                                                                        ?: WidgetMetadata.DEFAULT).height
                                                                       ?: resizedAppWidgetInfo.minHeight)
                    val widgetBackgroundCheckbox =
                            dialogView.findViewById<CheckBox>(R.id.add_widget_background)
                    val cardTitleCheckbox =
                            dialogView.findViewById<CheckBox>(R.id.display_card_title)
                    widgetBackgroundCheckbox.isChecked =
                            it.context.lawnchairPrefs.feedWidgetMetadata.getAll().firstOrNull { it2 -> it2.first == appWidgetId }?.second?.raiseCard
                            ?: false
                    widgetBackgroundCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
                        it.context.lawnchairPrefs.feedWidgetMetadata
                                .customAdder(appWidgetId to WidgetMetadata().apply {
                                    height = it.context.lawnchairPrefs.feedWidgetMetadata.getAll()
                                            .firstOrNull { it2 -> it2.first == appWidgetId }?.second
                                            ?.height
                                    raiseCard = isChecked

                                    showCardTitle =
                                            it.context.lawnchairPrefs.feedWidgetMetadata.getAll().firstOrNull { it2 -> it2.first == appWidgetId }?.second?.showCardTitle
                                            ?: false
                                })
                    }
                    cardTitleCheckbox.isChecked =
                            it.context.lawnchairPrefs.feedWidgetMetadata.getAll().firstOrNull { it2 -> it2.first == appWidgetId }?.second?.showCardTitle
                            ?: false
                    cardTitleCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
                        it.context.lawnchairPrefs.feedWidgetMetadata
                                .customAdder(appWidgetId to WidgetMetadata().apply {
                                    showCardTitle = isChecked
                                    height = it.context.lawnchairPrefs.feedWidgetMetadata.getAll()
                                            .firstOrNull { it2 -> it2.first == appWidgetId }?.second
                                            ?.height
                                    raiseCard =
                                            it.context.lawnchairPrefs.feedWidgetMetadata.getAll().firstOrNull { it2 -> it2.first == appWidgetId }?.second?.raiseCard
                                            ?: false
                                })
                    }
                    val originalSize = resizedAppWidgetInfo.minHeight
                    widgetView.apply {
                        widgetView.updateAppWidgetOptions(Bundle().apply {
                            Bundle().apply {
                                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, width)
                                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, width)
                                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,
                                       (context.lawnchairPrefs.feedWidgetMetadata.getAll().firstOrNull { it2 -> it2.first == appWidgetId }?.second
                                        ?: WidgetMetadata.DEFAULT).height
                                       ?: resizedAppWidgetInfo.minHeight)
                                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
                                       (context.lawnchairPrefs.feedWidgetMetadata.getAll().firstOrNull { it2 -> it2.first == appWidgetId }?.second
                                        ?: WidgetMetadata.DEFAULT).height
                                       ?: resizedAppWidgetInfo.minHeight)
                            }
                        })
                    }
                    resizeView.setOnResizeCallback { difference ->
                        d("onBindViewHolder: resizing widget by $difference")
                        if (widgetView.height + difference > originalSize) {
                            val toSize = alter(widgetView.height + difference < 0, null,
                                               (widgetView.height + difference).toInt())
                            it.context.lawnchairPrefs.feedWidgetMetadata
                                    .customAdder(appWidgetId to WidgetMetadata().apply {
                                        height = toSize
                                        raiseCard =
                                                it.context.lawnchairPrefs.feedWidgetMetadata.getAll().firstOrNull { it2 -> it2.first == appWidgetId }?.second?.raiseCard
                                                ?: false
                                        showCardTitle =
                                                it.context.lawnchairPrefs.feedWidgetMetadata.getAll().firstOrNull { it2 -> it2.first == appWidgetId }?.second?.showCardTitle
                                                ?: false
                                    })
                            widgetView.layoutParams =
                                    FrameLayout.LayoutParams(widgetView.width, toSize ?: -1)
                            widgetView.apply {
                                widgetView.updateAppWidgetOptions(Bundle().apply {
                                    Bundle().apply {
                                        putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, width)
                                        putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, width)
                                        putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,
                                               (context.lawnchairPrefs.feedWidgetMetadata.getAll().firstOrNull { it2 -> it2.first == appWidgetId }?.second
                                                ?: WidgetMetadata.DEFAULT).height
                                               ?: resizedAppWidgetInfo.minHeight)
                                        putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
                                               (context.lawnchairPrefs.feedWidgetMetadata.getAll().firstOrNull { it2 -> it2.first == appWidgetId }?.second
                                                ?: WidgetMetadata.DEFAULT).height
                                               ?: resizedAppWidgetInfo.minHeight)
                                    }
                                })
                            }
                        } else {
                            it.context.lawnchairPrefs.feedWidgetMetadata
                                    .customAdder(appWidgetId to WidgetMetadata().apply {
                                        height = null
                                        raiseCard =
                                                it.context.lawnchairPrefs.feedWidgetMetadata.getAll().firstOrNull { it2 -> it2.first == appWidgetId }?.second?.raiseCard
                                                ?: false
                                        showCardTitle =
                                                it.context.lawnchairPrefs.feedWidgetMetadata.getAll().firstOrNull { it2 -> it2.first == appWidgetId }?.second?.showCardTitle
                                                ?: false
                                    })
                            widgetView.layoutParams = FrameLayout.LayoutParams(widgetView.width, -1)
                            widgetView.apply {
                                widgetView.updateAppWidgetOptions(Bundle().apply {
                                    Bundle().apply {
                                        putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, width)
                                        putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, width)
                                        putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,
                                               (context.lawnchairPrefs.feedWidgetMetadata.getAll().firstOrNull { it2 -> it2.first == appWidgetId }?.second
                                                ?: WidgetMetadata.DEFAULT).height
                                               ?: resizedAppWidgetInfo.minHeight)
                                        putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
                                               (context.lawnchairPrefs.feedWidgetMetadata.getAll().firstOrNull { it2 -> it2.first == appWidgetId }?.second
                                                ?: WidgetMetadata.DEFAULT).height
                                               ?: resizedAppWidgetInfo.minHeight)
                                    }
                                })
                            }
                        }
                        null
                    }
                }
            }

            override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                super.onAttachedToRecyclerView(recyclerView)
                ItemTouchHelper(object : ItemTouchHelper.Callback() {
                    override fun getMovementFlags(recyclerView: RecyclerView,
                                                  viewHolder: RecyclerView.ViewHolder): Int {
                        return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                                                 ItemTouchHelper.START)
                    }

                    override fun onMove(recyclerView: RecyclerView,
                                        viewHolder: RecyclerView.ViewHolder,
                                        target: RecyclerView.ViewHolder): Boolean {
                        val fromPosition = target.adapterPosition
                        val toPosition = viewHolder.adapterPosition

                        if (fromPosition < toPosition) {
                            for (i in fromPosition until toPosition) {
                                Collections.swap(prefList, i, i + 1)
                            }
                        } else {
                            for (i in fromPosition downTo toPosition + 1) {
                                Collections.swap(prefList, i, i - 1)
                            }
                        }

                        notifyItemMoved(fromPosition, toPosition)
                        synchronizeWithPreference()
                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        (viewHolder.itemView.context.applicationContext as LawnchairApp)
                                .overlayWidgetHost
                                .deleteAppWidgetId(prefList[viewHolder.adapterPosition])
                        prefList.removeAt(viewHolder.adapterPosition)
                        synchronizeWithPreference()
                        notifyItemRemoved(viewHolder.adapterPosition)
                    }

                    override fun isItemViewSwipeEnabled() = true
                    override fun isLongPressDragEnabled() = true
                }).apply {
                    attachToRecyclerView(recyclerView)
                }.also { itemTouchHelper = it }
            }

            override fun getItemCount(): Int {
                return prefList.size
            }
        }

        companion object {
            fun newInstance(): Fragment {
                return Fragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_KEY, "pref_feed_widgets")
                    }
                }
            }
        }
    }
}