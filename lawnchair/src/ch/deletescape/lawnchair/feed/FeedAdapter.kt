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

package ch.deletescape.lawnchair.feed

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Vibrator
import android.util.TypedValue
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.colors.ColorEngine.Resolvers.Companion.FEED_CARD
import ch.deletescape.lawnchair.feed.impl.Interpolators
import ch.deletescape.lawnchair.feed.impl.LauncherFeed
import ch.deletescape.lawnchair.reflection.ReflectionUtils
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import com.github.mmin18.widget.RealtimeBlurView
import kotlin.math.roundToInt

open class FeedAdapter(var providers: List<FeedProvider>, backgroundColor: Int,
                  private val context: Context, private val feed: LauncherFeed?) :
        androidx.recyclerview.widget.RecyclerView.Adapter<CardViewHolder>() {
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    var backgroundColor: Int = 0
        set(value) {
            d("init: backgroundColor is now ${value}")
            field = value
        }

    init {
        d("init: backgroundColor is ${backgroundColor}")
        this.backgroundColor = backgroundColor
        if (!context.lawnchairPrefs.feedOnboardingShown) {
            providers += OnboardingProvider(context)
            context.lawnchairPrefs.feedOnboardingShown = true
        } else {
            if (providers.any { it.javaClass == OnboardingProvider::class.java }) {
                providers = providers.filter { it.javaClass != OnboardingProvider::class.java }
            }
        }
    }

    protected val cards = ArrayList<Card>()
    val immutableCards
        get() = cards.clone() as List<Card>

    init {
        providers.forEach {
            it.onAttachedToAdapter(this)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addItemDecoration(Decoration(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        recyclerView.context.lawnchairPrefs.cardDecorationMarginVertical,
                        recyclerView.context.resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        recyclerView.context.lawnchairPrefs.cardDecorationMarginHorizontal,
                        recyclerView.context.resources.displayMetrics).toInt()))
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(parent, viewType, backgroundColor)
    }

    override fun getItemViewType(position: Int): Int {
        return cards[position].type
    }

    open fun refresh(): Int {
        cards.clear()
        val toSort: MutableList<List<Card>> = ArrayList()
        providers.iterator().forEach {
            if (it.feed == null && feed != null) {
                it.feed = feed
            }
            d("refresh: loading cards for provider ${it::class.qualifiedName}")
            toSort += mutableListOf(it.cards)
        }
        val algorithm = ReflectionUtils.inflateSortingAlgorithm(
                LawnchairPreferences.getInstanceNoCreate().feedPresenterAlgorithm)
        d("refresh: sorting algorithm is $algorithm")
        cards += algorithm.sort(* toSort.toTypedArray())
                .filter { !context.lawnchairPrefs.feedDisabledCards.contains(it.identifier) }
        d("refresh: cards are: $cards")
        return cards.size
    }

    @SuppressLint("MissingPermission")
    override fun getItemCount(): Int {
        return cards.size;
    }

    override fun onDetachedFromRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        providers.iterator().forEachRemaining {
            it.onDestroy()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        var isDeleteActive = false
        holder.itemView.animate().scaleX(1f).scaleY(1f)

        if (cards[holder.adapterPosition].canHide) {
            val hasAction =
                    cards[holder.adapterPosition].actionListener != null && cards[holder.adapterPosition].actionName != null
            holder.itemView.setOnLongClickListener {
                if (isDeleteActive) {
                    return@setOnLongClickListener false
                }
                isDeleteActive = true
                if (!hasAction) {
                    holder.itemView.animate().scaleX(0.7f).scaleY(0.7f).duration = 100
                } else {
                    holder.itemView.animate().scaleX(0.9f).scaleY(0.9f).duration = 100
                }
                (LayoutInflater.from(holder.itemView.context).inflate(R.layout.card_remove_hint,
                        holder.itemView as ViewGroup,
                        false) as ViewGroup).apply {
                    background = ColorDrawable(Color.BLACK)
                    alpha = 0f
                    if (hasAction) {
                        try {
                            findViewsByClass(TextView::class.java, true)
                                    .forEach { it.visibility = GONE }
                            findViewById<Button>(R.id.delete_item_action).apply {
                                text = cards[holder.adapterPosition].actionName
                                setOnClickListener {
                                    cards[holder.adapterPosition].actionListener?.invoke(it.context)
                                    isDeleteActive = false
                                    holder.itemView.animate().scaleX(1f).scaleY(1f).duration = 100
                                    holder.itemView.removeView(holder.itemView.findViewById<View>(
                                            R.id.card_removal_hint));
                                }
                            }
                            findViewById<Button>(R.id.remove_action).setOnClickListener {
                                holder.itemView.animate().scaleX(0f).scaleY(0f)
                                        .setInterpolator(Interpolators.ACCEL).duration = 500
                                (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
                                        .vibrate(20)
                                val backupCards = cards.clone() as List<Card>
                                holder.itemView.context.lawnchairPrefs.feedDisabledCards
                                        .add(cards[holder.adapterPosition].identifier)
                                if (backupCards[holder.adapterPosition].onRemoveListener != null) {
                                    backupCards[holder.adapterPosition].onRemoveListener!!()
                                }
                                runOnNewThread {
                                    cards.removeAt(holder.adapterPosition)
                                    holder.itemView.post {
                                        holder.itemView.removeView(
                                                holder.itemView.findViewById<View>(
                                                        R.id.card_removal_hint));
                                        notifyItemRemoved(holder.adapterPosition)
                                        /* Snackbar.make(holder.itemView, R.string.item_removed,
                                                      Snackbar.LENGTH_SHORT)
                                                .setAction(R.string.undo) {
                                                    runOnNewThread {
                                                        holder.itemView.context.lawnchairPrefs
                                                                .feedDisabledCards
                                                                .remove(cards[holder.adapterPosition].identifier)
                                                        cards.clear()
                                                        cards.addAll(backupCards)
                                                        holder.itemView.post {
                                                            notifyItemInserted(
                                                                    holder.adapterPosition)
                                                            recyclerView.scrollToPosition(
                                                                    holder.adapterPosition)
                                                        }
                                                    }
                                                }.show() */
                                    }
                                }
                            }
                        } catch (e: IndexOutOfBoundsException) {
                            e.printStackTrace()
                        }
                    } else {
                        findViewById<Button>(R.id.delete_item_action).visibility = GONE
                        findViewById<Button>(R.id.remove_action).visibility = GONE
                    }
                    animate().alpha(.7f).duration = 500
                }.also { (holder.itemView).addView(it) }
                true
            }

            holder.itemView.setOnTouchListener { view: View, motionEvent: MotionEvent ->
                if (isDeleteActive && motionEvent.action == MotionEvent.ACTION_UP && !hasAction) {
                    holder.itemView.animate().scaleX(0f).scaleY(0f)
                            .setInterpolator(Interpolators.ACCEL).duration = 500
                    (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(20)
                    val backupCards = cards.clone() as List<Card>
                    holder.itemView.context.lawnchairPrefs.feedDisabledCards
                            .add(cards[holder.adapterPosition].identifier)
                    if (backupCards[holder.adapterPosition].onRemoveListener != null) {
                        backupCards[holder.adapterPosition].onRemoveListener!!()
                    }
                    runOnNewThread {
                        cards.removeAt(holder.adapterPosition)
                        holder.itemView.post {
                            (holder.itemView as ViewGroup).removeView(
                                    holder.itemView.findViewById<View>(R.id.card_removal_hint));
                            notifyItemRemoved(holder.adapterPosition)
                            /*
                            Snackbar.make(holder.itemView, R.string.item_removed,
                                          Snackbar.LENGTH_SHORT).setAction(R.string.undo) {
                                runOnNewThread {
                                    holder.itemView.context.lawnchairPrefs.feedDisabledCards
                                            .remove(cards[holder.adapterPosition].identifier)
                                    cards.clear()
                                    cards.addAll(backupCards)
                                    holder.itemView.post {
                                        notifyItemInserted(holder.adapterPosition)
                                        recyclerView.scrollToPosition(holder.adapterPosition)
                                    }
                                }
                            }.show()
                             */
                        }
                    }
                    return@setOnTouchListener true
                } else if (isDeleteActive && motionEvent.action == MotionEvent.ACTION_CANCEL) {
                    isDeleteActive = false
                    holder.itemView.animate().scaleX(1f).scaleY(1f).duration = 100
                    (holder.itemView as ViewGroup)
                            .removeView(holder.itemView.findViewById<View>(R.id.card_removal_hint));
                }

                return@setOnTouchListener false
            }
        } else {
            holder.itemView.setOnLongClickListener(null)
            holder.itemView.setOnTouchListener(null)
        }
        if (holder.itemViewType and Card.NO_HEADER != 1) {
            holder.description?.text = cards[holder.adapterPosition].title
            holder.icon?.setImageDrawable(cards[holder.adapterPosition].icon)
        }
        holder.viewHolder.removeAllViewsInLayout()
        try {
            holder.viewHolder.addView(cards[holder.adapterPosition].inflateHelper.inflate(
                    holder.viewHolder).also { (it.parent as ViewGroup?)?.removeView(it) })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (holder.itemView is androidx.cardview.widget.CardView) {
            if (!context.lawnchairPrefs.feedCardBlur) {
                holder.itemView.setCardBackgroundColor(
                        context.colorEngine.getResolver(FEED_CARD).resolveColor().setAlpha(
                                context.lawnchairPrefs.feedCardOpacity.roundToInt()))
            }
            if (context.lawnchairPrefs.feedCardOpacity.roundToInt() != 255 || context.lawnchairPrefs.feedCardBlur) {
                holder.itemView.cardElevation = 0f
            } else {
                holder.itemView.cardElevation = TypedValue
                        .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                context.lawnchairPrefs.feedCardElevation,
                                context.resources.displayMetrics)
            }
            holder.itemView.radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    LawnchairPreferences.getInstance(
                            holder.itemView.context).feedCornerRounding,
                    holder.itemView.context.resources.displayMetrics)
        }
    }

    companion object {
        fun getOverrideColor(c: Context, currentColor: Int): Int {
            return if (currentColor == R.color.colorAccent.fromColorRes(c)
                    || currentColor == R.color.colorAccentDark.fromColorRes(c)) {
                c.getColorAttr(R.attr.colorAccent)
            } else {
                currentColor
            }
        }

        fun getOverrideColor(c: Context): Int {
            return getOverrideColor(c, c.getColorEngineAccent())
        }
    }
}

class CardViewHolder : androidx.recyclerview.widget.RecyclerView.ViewHolder {
    val icon by lazy {
        itemView.findViewById(R.id.card_provider_small_icon) as ImageView?
    }
    val description by lazy { itemView.findViewById(R.id.card_title) as TextView? }
    val viewHolder: LinearLayout by lazy {
        itemView.findViewById(R.id.card_view_holder) as LinearLayout
    }
    private val backgroundBlurView by lazy {
        itemView.findViewById(R.id.card_blur_view) as RealtimeBlurView?
    }

    constructor(parent: ViewGroup, type: Int, backgroundColor: Int) : super(LayoutInflater.from(
            if (type and Card.RAISE != 0) ContextThemeWrapper(parent.context, if (useWhiteText(
                            parent.context.colorEngine.getResolver(FEED_CARD).resolveColor(),
                            parent.context) || parent.context.lawnchairPrefs.feedCardBlur) R.style.SettingsTheme_V2_Dark else R.style.SettingsTheme_V2) else parent.context).inflate(
            when (type) {
                Card.DEFAULT -> R.layout.card_default
                Card.DEFAULT or Card.NARROW -> R.layout.card_narrow
                Card.DEFAULT or Card.RAISE -> R.layout.card_raised
                Card.DEFAULT or Card.RAISE or Card.NARROW -> R.layout.card_raised_narrow
                Card.DEFAULT or Card.TEXT_ONLY -> R.layout.card_text_only
                Card.DEFAULT or Card.RAISE or Card.TEXT_ONLY -> R.layout.card_raised_text_only
                Card.DEFAULT or Card.NO_HEADER -> R.layout.card_default_no_header
                Card.DEFAULT or Card.RAISE or Card.NO_HEADER -> R.layout.card_raised_no_header
                else -> error("invalid bitmask")
            }, parent, false)) {
        if (type and Card.TEXT_ONLY == 1) {
            viewHolder.visibility = GONE
        }

        d("constructor: luminace for background ${backgroundColor} is ${ColorUtils.calculateLuminance(
                backgroundColor)}")

        if (type and Card.RAISE == 0 && description != null && useWhiteText(backgroundColor,
                        viewHolder.context)) {
            description!!.setTextColor(description!!.context.getColor(R.color.textColorPrimary))
        } else if (type and Card.RAISE == 0) {
            description?.setTextColor(
                    description?.context?.getColor(R.color.textColorPrimaryInverse) ?: 0)
        }

        if (viewHolder.context.lawnchairPrefs.feedCardBlur && type and Card.RAISE != 0) {
            (itemView as androidx.cardview.widget.CardView).setCardBackgroundColor(Color.TRANSPARENT)
            itemView.background.setTint(Color.argb(64, 255, 255, 255))
            backgroundBlurView!!.visibility = VISIBLE
            backgroundBlurView!!.setBlurRadius(
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25f,
                            itemView.context.resources.displayMetrics))
        }
    }
}

private class Decoration(private val spaceHeightVertical: Int,
                         private val spaceHeightHorizontal: Int) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView,
                                state: androidx.recyclerview.widget.RecyclerView.State) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = spaceHeightVertical
            }
            left = spaceHeightHorizontal
            right = spaceHeightHorizontal
            bottom = spaceHeightVertical
        }
    }
}