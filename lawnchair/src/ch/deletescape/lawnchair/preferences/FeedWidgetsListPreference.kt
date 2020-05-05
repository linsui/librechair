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
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.preference.DialogPreference
import androidx.preference.PreferenceDialogFragmentCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.room.InvalidationTracker
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.cp.NonoverlayCallbacks
import ch.deletescape.lawnchair.feed.FeedScope
import ch.deletescape.lawnchair.feed.widgets.Widget
import ch.deletescape.lawnchair.feed.widgets.WidgetDatabase
import ch.deletescape.lawnchair.theme.ThemeManager
import ch.deletescape.lawnchair.theme.ThemeOverride
import ch.deletescape.lawnchair.util.SingleUseHold
import ch.deletescape.lawnchair.views.FolderNameEditText
import ch.deletescape.lawnchair.views.VerticalResizeView
import com.android.launcher3.R
import kotlinx.coroutines.launch
import java.util.*

class FeedWidgetsListPreference(context: Context, attrs: AttributeSet) :
        DialogPreference(context, attrs) {

    override fun getNegativeButtonText(): CharSequence? {
        return null
    }

    override fun getDialogLayoutResource() = R.layout.dialog_preference_recyclerview
    fun updateSummary() {
        FeedScope.launch {
            val summary = WidgetDatabase.getInstance(context).dao().all.mapNotNull {
                it.customCardTitle ?: context.appWidgetManager.getAppWidgetInfo(it.id)?.loadLabel(
                        context.packageManager)
            }.joinToString()
            runOnMainThread {
                this@FeedWidgetsListPreference.summary = summary
            }
        }
    }

    init {
        updateSummary()
        WidgetDatabase.getInstance(context).invalidationTracker.addObserver(object :
                InvalidationTracker.Observer("widget") {
            override fun onInvalidated(tables: MutableSet<String>) {
                runOnMainThread {
                    updateSummary()
                }
            }
        })
    }

    override fun getPositiveButtonText(): CharSequence = R.string.add_new_tab.fromStringRes(
            context)

    class Fragment : PreferenceDialogFragmentCompat() {
        override fun onDialogClosed(positiveResult: Boolean) {
            if (positiveResult) {
                val ct = context!!
                NonoverlayCallbacks.postWidgetRequest(ct) {
                    FeedScope.launch {
                        WidgetDatabase.getInstance(ct).dao().addWidget(
                                Widget(it, WidgetDatabase.getInstance(ct).dao().all.size))
                    }
                }
            }
        }

        override fun onBindDialogView(view: View) {
            super.onBindDialogView(view)
            val recyclerView =
                    view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.list)
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            recyclerView.adapter = FeedWidgetsPreferenceAdapter(context!!)
        }

        class FeedWidgetsPreferenceAdapter(val c: Context) :
                androidx.recyclerview.widget.RecyclerView.Adapter<ProviderItemViewHolder>() {
            val prefList = mutableListOf<Widget>()
            val initHold = SingleUseHold()

            init {
                FeedScope.launch {
                    try {
                        prefList.clear()
                        prefList.addAll(WidgetDatabase.getInstance(c).dao().all.filter {
                            c.appWidgetManager.getAppWidgetInfo(it.id) != null
                        })
                    } catch (e: RuntimeException) {
                        e.printStackTrace()
                    }
                    initHold.trigger()
                    runOnMainThread {
                        notifyDataSetChanged()
                    }
                }
            }

            var itemTouchHelper: ItemTouchHelper? = null

            override fun onCreateViewHolder(parent: ViewGroup,
                                            viewType: Int): ProviderItemViewHolder = ProviderItemViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.event_provider_dialog_item,
                            parent, false))

            override fun onBindViewHolder(holder: ProviderItemViewHolder, position: Int) {
                val appWidgetInfo = holder.itemView.context.appWidgetManager
                        .getAppWidgetInfo(prefList[holder.adapterPosition].id)
                if (appWidgetInfo == null) {
                    holder.itemView.visibility = View.GONE
                } else {
                    holder.itemView.visibility = View.VISIBLE
                }
                val appWidgetId = prefList[holder.adapterPosition].id
                holder.dragHandle.setOnTouchListener { view: View, motionEvent: MotionEvent ->
                    if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                        itemTouchHelper?.startDrag(holder)
                        true
                    }
                    false
                }
                holder.dragHandle.visibility = View.VISIBLE

                holder.title.text =
                        appWidgetInfo?.loadLabel(holder.itemView.context.packageManager) ?: "!!!!"
                holder.itemView.setOnClickListener {
                    val widget = prefList[holder.adapterPosition]
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
                            .getAppWidgetInfo(widget.id)?.apply {
                                minWidth = (dialogView.parent as View).width
                            }
                    val resizeView = dialogView.findViewById<VerticalResizeView>(R.id.resize_view)
                    val widgetView =
                            (it.context.applicationContext as LawnchairApp).overlayWidgetHost
                                    .createView(LawnchairLauncher.getLauncher(it.context),
                                            appWidgetId, resizedAppWidgetInfo)
                    dialogView.findViewById<FrameLayout>(R.id.resize_view_container)
                            .addView(widgetView)
                    val widgetBackgroundCheckbox =
                            dialogView.findViewById<CheckBox>(R.id.add_widget_background)
                    val cardTitleCheckbox =
                            dialogView.findViewById<CheckBox>(R.id.display_card_title)
                    val sortCards = dialogView.findViewById<CheckBox>(R.id.sort_widget)
                    val customTitle =
                            dialogView.findViewById<FolderNameEditText>(R.id.custom_card_title)
                    customTitle.setText(
                            widget.customCardTitle)
                    customTitle.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {
                            widget.customCardTitle = s.toString()
                            FeedScope.launch {
                                WidgetDatabase.getInstance(c).dao()
                                        .setCardTitle(widget.id, s.toString())
                            }
                        }

                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int,
                                                       after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int,
                                                   count: Int) {
                        }
                    })
                    sortCards.isChecked =
                            widget.sortable
                    sortCards.setOnCheckedChangeListener { buttonView, isChecked ->
                        widget.sortable = isChecked
                        FeedScope.launch {
                            WidgetDatabase.getInstance(c).dao().setSortable(widget.id, isChecked)
                        }
                    }
                    widgetBackgroundCheckbox.isChecked =
                            widget.raiseCard
                    widgetBackgroundCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
                        widget.raiseCard = isChecked
                        FeedScope.launch {
                            WidgetDatabase.getInstance(c).dao().setRaised(widget.id, isChecked)
                        }
                    }
                    cardTitleCheckbox.isChecked = widget.showCardTitle
                    cardTitleCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
                        widget.showCardTitle = isChecked
                        FeedScope.launch {
                            WidgetDatabase.getInstance(c).dao()
                                    .setShowCardTitle(widget.id, isChecked)
                        }
                    }
                    val originalSize = resizedAppWidgetInfo?.minHeight ?: -1
                    widgetView.apply {
                        viewTreeObserver.addOnGlobalLayoutListener {
                            layoutParams.height =
                                    if (widget.height != Widget.DEFAULT_HEIGHT) widget.height else originalSize
                        }
                        updateAppWidgetOptions(Bundle().apply {
                            if (widget.height != -1) {
                                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,
                                        width)
                                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,
                                        width)
                                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,
                                        widget.height)
                                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
                                        widget.height)
                            } else {
                                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,
                                        width)
                                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,
                                        width)
                                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,
                                        resizedAppWidgetInfo?.minHeight ?: -1)
                                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
                                        resizedAppWidgetInfo?.minHeight ?: -1)
                            }
                        })
                    }
                    resizeView.setOnResizeCallback { difference ->
                        if (widgetView.height + difference > originalSize) {
                            val toSize = alter(widgetView.height + difference < 0, -1,
                                    (widgetView.height + difference).toInt())
                            widget.height = toSize
                            FeedScope.launch {
                                WidgetDatabase.getInstance(c).dao().setHeight(widget.id, toSize)
                            }
                            widgetView.layoutParams =
                                    FrameLayout.LayoutParams(widgetView.width, toSize ?: -1)
                            widgetView.apply {
                                widgetView.updateAppWidgetOptions(Bundle().apply {
                                    if (toSize != -1) {
                                        putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,
                                                width)
                                        putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,
                                                width)
                                        putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,
                                                widget.height)
                                        putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
                                                widget.height)
                                    } else {
                                        putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,
                                                width)
                                        putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,
                                                width)
                                        putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,
                                                resizedAppWidgetInfo?.minHeight ?: -1)
                                        putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
                                                resizedAppWidgetInfo?.minHeight ?: -1)
                                    }
                                })
                            }
                        } else {
                            val toSize = -1
                            widget.height = toSize
                            FeedScope.launch {
                                WidgetDatabase.getInstance(c).dao().setHeight(widget.id, toSize)
                            }
                            widgetView.layoutParams =
                                    FrameLayout.LayoutParams(widgetView.width, originalSize)
                            widgetView.apply {
                                widgetView.updateAppWidgetOptions(Bundle().apply {
                                    Bundle().apply {
                                        if (toSize != -1) {
                                            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,
                                                    width)
                                            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,
                                                    width)
                                            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,
                                                    widget.height)
                                            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
                                                    widget.height)
                                        } else {
                                            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,
                                                    width)
                                            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,
                                                    width)
                                            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,
                                                    resizedAppWidgetInfo?.minHeight ?: -1)
                                            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
                                                    resizedAppWidgetInfo?.minHeight ?: -1)
                                        }
                                    }
                                })
                            }
                        }
                        null
                    }
                    builder.applyAccent()
                }
            }

            override fun onAttachedToRecyclerView(
                    recyclerView: androidx.recyclerview.widget.RecyclerView) {
                super.onAttachedToRecyclerView(recyclerView)
                ItemTouchHelper(object : ItemTouchHelper.Callback() {
                    override fun getMovementFlags(
                            recyclerView: androidx.recyclerview.widget.RecyclerView,
                            viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder): Int {
                        return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                                ItemTouchHelper.START)
                    }

                    override fun onMove(recyclerView: androidx.recyclerview.widget.RecyclerView,
                                        viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                                        target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
                        val fromPosition = target.adapterPosition
                        val toPosition = viewHolder.adapterPosition

                        FeedScope.launch {
                            synchronized(this@FeedWidgetsPreferenceAdapter) {
                                for (i in 0 until prefList.size) {
                                    prefList[i].entryOrder = i
                                    WidgetDatabase.getInstance(c).dao()
                                            .setOrder(prefList[i].id, i)
                                }
                            }
                        }

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
                        return true
                    }

                    override fun onSwiped(
                            viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                            direction: Int) {
                        (viewHolder.itemView.context.applicationContext as LawnchairApp)
                                .overlayWidgetHost
                                .deleteAppWidgetId(prefList[viewHolder.adapterPosition].id)
                        FeedScope.launch {
                            try {
                                WidgetDatabase.getInstance(c).dao()
                                        .removeWidget(prefList[viewHolder.adapterPosition].id)
                            } catch (e: IndexOutOfBoundsException) {
                                e.printStackTrace()
                            }
                        }
                        prefList.removeAt(viewHolder.adapterPosition)
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