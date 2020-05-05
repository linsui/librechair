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

package ch.deletescape.lawnchair.preferences

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import ch.deletescape.lawnchair.feed.FeedProviderContainer
import ch.deletescape.lawnchair.feed.METADATA_CONTROLLER_PACKAGE
import ch.deletescape.lawnchair.feed.MainFeedController
import ch.deletescape.lawnchair.getColorEngineAccent
import ch.deletescape.lawnchair.isVisible
import ch.deletescape.lawnchair.persistence.feedPrefs
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R

class FeedProvidersAdapter(private val context: Context)
    : androidx.recyclerview.widget.RecyclerView.Adapter<FeedProvidersAdapter.Holder>() {
    private val allProviders = ArrayList<ProviderItem>()
    private val handler = Handler()

    private var dividerIndex = 0

    private val adapterItems = ArrayList<Item>()
    private val currentSpecs = ArrayList<FeedProviderContainer>()
    private val otherItems = ArrayList<ProviderItem>()
    private val divider = DividerItem()
    private var isDragging = false

    var itemTouchHelper: ItemTouchHelper? = null

    init {
        allProviders.addAll(MainFeedController.getFeedProviders(context).map {
            ProviderItem(ProviderInfo(it))
        })
        currentSpecs.addAll(context.feedPrefs.feedProviders)

        fillItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return when (viewType) {
            TYPE_HEADER -> createHolder(parent, R.layout.event_provider_text_item, ::HeaderHolder)
            TYPE_ITEM -> createHolder(parent, R.layout.event_provider_dialog_item, ::ProviderHolder)
            TYPE_DIVIDER -> createHolder(parent, R.layout.event_providers_divider_item, ::DividerHolder)
            else -> throw IllegalArgumentException("type must be either TYPE_TEXT, " +
                                                   "TYPE_PROVIDER or TYPE_DIVIDER")
        }
    }

    override fun getItemCount() = adapterItems.count()

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(adapterItems[position])
    }

    override fun getItemViewType(position: Int): Int {
        return adapterItems[position].type
    }

    fun saveSpecs(): ArrayList<FeedProviderContainer> {
        val newSpecs = ArrayList<FeedProviderContainer>()
        val iterator = adapterItems.iterator()

        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item is ProviderItem) {
                newSpecs.add(item.info.name)
                d("adding item ${item.info.name}")
            }
            if (item is DividerItem) break
        }
        return newSpecs
    }

    private fun fillItems(){
        otherItems.clear()
        otherItems.addAll(allProviders)

        adapterItems.clear()
        adapterItems.add(HeaderItem())
        currentSpecs.forEach {
            val item = getAndRemoveOther(it)
            if (item != null) {
                adapterItems.add(item)
            }
        }
        dividerIndex = adapterItems.count()
        adapterItems.add(divider)
        adapterItems.addAll(otherItems)
    }

    private fun getAndRemoveOther(s: FeedProviderContainer): ProviderItem? {
        val iterator = otherItems.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.info.name == s) {
                iterator.remove()
                return item
            }
        }
        return null
    }

    private fun move(from: Int, to: Int): Boolean {
        if (to == from) return true
        move(from, to, adapterItems)
        dividerIndex = adapterItems.indexOf(divider)
        return true
    }

    private fun <T> move(from: Int, to: Int, list: MutableList<T>) {
        list.add(to, list.removeAt(from))
        notifyItemMoved(from, to)
    }

    private inline fun createHolder(parent: ViewGroup, resource: Int, creator: (View) -> Holder): Holder {
        return creator(LayoutInflater.from(parent.context).inflate(resource, parent, false))
    }

    abstract class Item {

        abstract val isStatic: Boolean
        abstract val type: Int
    }

    class HeaderItem : Item() {

        override val isStatic = true
        override val type = TYPE_HEADER
    }

    open class ProviderItem(val info: ProviderInfo) : Item() {

        override val isStatic = false
        override val type = TYPE_ITEM
    }

    class DividerItem : Item() {

        override val isStatic = true
        override val type = TYPE_DIVIDER
    }

    abstract class Holder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        open fun bind(item: Item) {

        }
    }

    inner class ProviderInfo(val name: FeedProviderContainer) {

        val displayName = MainFeedController.getDisplayName(name, context);
    }

    class HeaderHolder(itemView: View) : Holder(itemView) {

        init {
            itemView.findViewById<TextView>(android.R.id.text1).apply {
                setText(R.string.enabled_icon_packs)
                setTextColor(context.getColorEngineAccent())
            }
        }
    }

    open inner class ProviderHolder(itemView: View) : Holder(itemView), View.OnClickListener, View.OnTouchListener {

        val title: TextView = itemView.findViewById(android.R.id.title)
        val summary: TextView = itemView.findViewById(android.R.id.summary)
        private val dragHandle: View = itemView.findViewById(R.id.drag_handle)
        private val packItem get() = adapterItems[adapterPosition] as? ProviderItem
                                     ?: throw IllegalArgumentException("item must be ProviderItem")

        init {
            itemView.setOnClickListener(this)
            dragHandle.setOnTouchListener(this)
        }

        override fun bind(item: Item) {
            val packItem = item as? ProviderItem
                           ?: throw IllegalArgumentException("item must be ProviderItem")
            title.text = packItem.info.displayName
            itemView.isClickable = !packItem.isStatic
            dragHandle.isVisible = !packItem.isStatic
            summary.isVisible =
                    packItem.info.name.arguments.get(METADATA_CONTROLLER_PACKAGE) != null
            if (summary.isVisible) {
                summary.text = context.packageManager.getApplicationLabel(
                        context.packageManager.getApplicationInfo(
                                packItem.info.name.arguments.get(METADATA_CONTROLLER_PACKAGE), 0))
            }
        }

        override fun onClick(v: View) {
            val item = packItem
            if (adapterPosition > dividerIndex) {
                adapterItems.removeAt(adapterPosition)
                adapterItems.add(1, item)
                notifyItemMoved(adapterPosition, 1)
                dividerIndex++
            } else {
                adapterItems.removeAt(adapterPosition)
                adapterItems.add(dividerIndex, item)
                notifyItemMoved(adapterPosition, dividerIndex)
                dividerIndex--
            }
            notifyItemChanged(dividerIndex)
        }

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            if (v == dragHandle && event.actionMasked == MotionEvent.ACTION_DOWN) {
                itemTouchHelper?.startDrag(this)
            }
            return false
        }
    }

    inner class DividerHolder(itemView: View) : Holder(itemView) {

        val text: TextView = itemView.findViewById(android.R.id.text1)

        init {
            text.setTextColor(text.context.getColorEngineAccent())
        }

        override fun bind(item: Item) {
            super.bind(item)
            if (isDragging || dividerIndex == adapterItems.size - 1) {
                text.setText(R.string.drag_to_disable_packs)
            } else {
                text.setText(R.string.drag_to_enable_packs)
            }
        }
    }

    inner class TouchHelperCallback : ItemTouchHelper.Callback() {

        override fun onSelectedChanged(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            isDragging = actionState == ItemTouchHelper.ACTION_STATE_DRAG
            handler.post { notifyItemChanged(dividerIndex) }
        }

        override fun canDropOver(recyclerView: androidx.recyclerview.widget.RecyclerView, current: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
            return target.adapterPosition in 1..dividerIndex
        }

        override fun getMovementFlags(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder): Int {
            val item = adapterItems[viewHolder.adapterPosition]
            val dragFlags = if (item.isStatic) 0 else ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragFlags, 0)
        }

        override fun onMove(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
            return move(viewHolder.adapterPosition, target.adapterPosition)
        }

        override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {

        }
    }

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
        const val TYPE_DIVIDER = 2
    }
}
