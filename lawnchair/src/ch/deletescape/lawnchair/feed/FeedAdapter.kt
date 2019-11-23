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
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Vibrator
import android.util.TypedValue
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.colors.ColorEngine.Resolvers.Companion.FEED_CARD
import ch.deletescape.lawnchair.colors.resolvers.FeedBackgroundResolver
import ch.deletescape.lawnchair.feed.impl.Interpolators
import ch.deletescape.lawnchair.feed.impl.LauncherFeed
import ch.deletescape.lawnchair.feed.shape.CardStyleRegistry
import ch.deletescape.lawnchair.font.CustomFontManager
import ch.deletescape.lawnchair.persistence.feedPrefs
import ch.deletescape.lawnchair.preferences.TitleAlignmentPreference
import ch.deletescape.lawnchair.reflection.ReflectionUtils
import ch.deletescape.lawnchair.theme.ThemeManager
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import com.github.mmin18.widget.RealtimeBlurView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

open class FeedAdapter(var providers: List<FeedProvider>, backgroundColor: Int,
                       var context: Context, val feed: LauncherFeed?) :
        RecyclerView.Adapter<CardViewHolder>() {
    private lateinit var recyclerView: RecyclerView
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

    open val cardCache = mutableMapOf<FeedProvider, List<Card>>()
    open val cards
        get() = run {
            val algorithm = ReflectionUtils.inflateSortingAlgorithm(
                    context.lawnchairPrefs.feedPresenterAlgorithm)
            algorithm.sort(* providers.map { cardCache[it] ?: emptyList() }.toTypedArray())
        }
    val fcache
        get() = cardCache.filter { providers.contains(it.key) }
    val immutableCards
        get() = ArrayList(cards)

    init {
        providers.forEach {
            it.onAttachedToAdapter(this)
        }
    }

    // TODO kt-utils2.el context+semantics-aware variable name refactor
    //    * lifted invalid syntax
    //    * enabled aggresive lexer syntax conversion table
    override fun onAttachedToRecyclerView(rv: RecyclerView) {
        super.onAttachedToRecyclerView(rv)
        if (rv.itemDecorationCount == 0) {
            rv.addItemDecoration(Decoration(
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            rv.context.lawnchairPrefs.cardDecorationMarginVertical,
                            rv.context.resources.displayMetrics).toInt(),
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            rv.context.lawnchairPrefs.cardDecorationMarginHorizontal,
                            rv.context.resources.displayMetrics).toInt()))
        }
        this.recyclerView = rv
        ThemeManager.getInstance(context)
                .changeCallbacks += {
            if (::recyclerView.isInitialized) {
                recyclerView.adapter = null
                recyclerView.adapter = this
                feed?.refresh(0);
                d("onAttachedToRecyclerView: theme changed")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(parent, viewType, backgroundColor)
    }

    override fun getItemViewType(position: Int): Int {
        return cards[position].type
    }

    open suspend fun refresh(): List<Pair<Int, FeedProvider>> {
        val coroutines = mutableListOf<Job>()
        val changed = Vector<Pair<Int, FeedProvider>>()
        providers.forEach {
            d("refresh: refreshing $it")
            if (it.context != context) {
                it.context = context
            }
            changed += (cardCache[it]?.size ?: 0) to it
            coroutines += FeedScope.launch {
                it.feed = feed
                cardCache[it] = it.cards.toImmutableList()
            }
        }
        coroutines.forEach { it.join() }
        return changed
    }

    open suspend fun refreshVolatile(): List<Pair<Int, FeedProvider>> {
        val coroutines = mutableListOf<Job>()
        val changed = Vector<Pair<Int, FeedProvider>>()
        providers.filter { it.isVolatile }.forEach {
            if (it.context != context) {
                it.context = context
            }
            changed += (cardCache[it]?.size ?: 0) to it
            coroutines += FeedScope.launch {
                it.feed = feed
                cardCache[it] = it.cards.toImmutableList()
            }
        }
        coroutines.forEach { it.join() }
        return changed
    }

    @SuppressLint("MissingPermission")
    override fun getItemCount(): Int {
        return cards.size;
    }

    override fun onDetachedFromRecyclerView(
            recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
    }

    @SuppressLint("MissingPermission", "RestrictedApi")
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        var isDeleteActive = false
        holder.itemView.animate().scaleX(1f).scaleY(1f)

        holder.description?.setTag("font_ignore")

        if (holder.description != null) {
            CustomFontManager.getInstance(context)
                    .setCustomFont(holder.description!!, CustomFontManager.FONT_FEED_TITLES,
                            holder.description!!.typeface.style)
        }

        if (cards[position].hasGlobalClickListener()) {
            holder.itemView.setOnClickListener(cards[position].globalClickListener)
            if (holder.itemView is MaterialCardView) {
                holder.itemView.rippleColor = ColorStateList.valueOf(
                        MaterialColors.getColor(holder.itemView,
                                com.google.android.material.R.attr.colorControlHighlight))
            }
        } else {
            holder.itemView.isClickable = false
            if (holder.itemView is MaterialCardView) {
                holder.itemView.rippleColor = ColorStateList.valueOf(0)
            }
            holder.itemView.setOnClickListener(null)
        }

        if (holder.itemViewType and Card.TEXT_ONLY == 0
                && holder.itemViewType and Card.NO_HEADER != 1 && holder.icon != null) {
            val constraintLayout = holder.icon!!.parent as ConstraintLayout
            val constraintSet = ConstraintSet().apply { clone(constraintLayout) }
            constraintSet.clear(holder.description!!.id, ConstraintSet.START)
            constraintSet.clear(holder.description!!.id, ConstraintSet.END)
            constraintSet.clear(holder.description!!.id, ConstraintSet.LEFT)
            constraintSet.clear(holder.description!!.id, ConstraintSet.RIGHT)


            when (context.lawnchairPrefs.feedRaisedCardTitleAlignment) {
                TitleAlignmentPreference.ALIGNMENT_CENTER -> {
                    constraintSet.connect(holder.description!!.id, ConstraintSet.START,
                            holder.icon!!.id, ConstraintSet.END)
                    constraintSet.connect(holder.description!!.id, ConstraintSet.END,
                            ConstraintSet.PARENT_ID, ConstraintSet.END)
                }
                TitleAlignmentPreference.ALIGNMENT_END -> {
                    constraintSet.connect(holder.description!!.id, ConstraintSet.END,
                            ConstraintSet.PARENT_ID, ConstraintSet.END)
                }
                TitleAlignmentPreference.ALIGNMENT_START -> {
                    constraintSet.connect(holder.description!!.id, ConstraintSet.START,
                            holder.icon!!.id, ConstraintSet.END)
                }
            }
            constraintSet.applyTo(constraintLayout)
        } else if (holder.itemViewType and Card.NO_HEADER != 1 && holder.icon != null) {
            val constraintLayout = holder.icon!!.parent as ConstraintLayout
            val constraintSet = ConstraintSet().apply { clone(constraintLayout) }
            constraintSet.clear(holder.description!!.id, ConstraintSet.START)
            constraintSet.clear(holder.description!!.id, ConstraintSet.END)
            constraintSet.clear(holder.description!!.id, ConstraintSet.LEFT)
            constraintSet.clear(holder.description!!.id, ConstraintSet.RIGHT)


            when (context.lawnchairPrefs.feedRaisedHeaderOnlyCardTitleAlignment) {
                TitleAlignmentPreference.ALIGNMENT_CENTER -> {
                    constraintSet.connect(holder.description!!.id, ConstraintSet.START,
                            holder.icon!!.id, ConstraintSet.END)
                    constraintSet.connect(holder.description!!.id, ConstraintSet.END,
                            ConstraintSet.PARENT_ID, ConstraintSet.END)
                }
                TitleAlignmentPreference.ALIGNMENT_END -> {
                    constraintSet.connect(holder.description!!.id, ConstraintSet.END,
                            ConstraintSet.PARENT_ID, ConstraintSet.END)
                }
                TitleAlignmentPreference.ALIGNMENT_START -> {
                    constraintSet.connect(holder.description!!.id, ConstraintSet.START,
                            holder.icon!!.id, ConstraintSet.END)
                }
            }
            constraintSet.applyTo(constraintLayout)
        }

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
                                    cards[holder.adapterPosition].actionListener?.invoke(it)
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
                                val card = cards[holder.adapterPosition]
                                val backupCards = ArrayList(cards)
                                holder.itemView.context.lawnchairPrefs.feedDisabledCards
                                        .add(cards[holder.adapterPosition].identifier)
                                if (backupCards[holder.adapterPosition].onRemoveListener != null) {
                                    backupCards[holder.adapterPosition].onRemoveListener!!(holder.itemView)
                                }
                                FeedScope.launch {
                                    cardCache.keys.forEach {
                                        if (cardCache.contains(it) && cardCache[it]!!.contains(
                                                        card)) {
                                            cardCache[it] = cardCache[it]!!.filterNot { it == card }
                                        }
                                    }
                                    holder.itemView.post {
                                        holder.itemView.removeView(
                                                holder.itemView.findViewById<View>(
                                                        R.id.card_removal_hint));
                                        notifyItemRemoved(holder.adapterPosition)
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
                    val card = cards[holder.adapterPosition]
                    val backupCards = ArrayList(cards)
                    holder.itemView.context.lawnchairPrefs.feedDisabledCards
                            .add(cards[holder.adapterPosition].identifier)
                    if (backupCards[holder.adapterPosition].onRemoveListener != null) {
                        backupCards[holder.adapterPosition].onRemoveListener!!(holder.itemView)
                    }
                    FeedScope.launch {
                        cardCache.keys.forEach {
                            if (cardCache.contains(it) && cardCache[it]!!.contains(
                                            card)) {
                                cardCache[it] = cardCache[it]!!.filterNot { it == card }
                            }
                        }
                        holder.itemView.post {
                            (holder.itemView as ViewGroup).removeView(
                                    holder.itemView.findViewById<View>(R.id.card_removal_hint));
                            notifyItemRemoved(holder.adapterPosition)
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
            holder.icon?.setImageDrawable(cards[holder.adapterPosition].icon.let {
                if (it is VectorDrawable) {
                    it.tint(if (holder.itemViewType and Card.RAISE != 0) getOverrideColor(
                            holder.itemView.context) else if (useWhiteText(backgroundColor,
                                    context)) R.color.qsb_background.fromColorRes(context).setAlpha(
                            255) else R.color.qsb_background_dark.fromColorRes(context).setAlpha(
                            255))
                } else {
                    it
                }
            })
        }
        holder.viewHolder.removeAllViewsInLayout()
        try {
            holder.viewHolder.addView(cards[holder.adapterPosition].inflateHelper.inflate(
                    holder.viewHolder).apply {
                if (this is ViewGroup) {
                    this.allChildren.forEach { view ->
                        if (view is TextView && view.tag != "font_ignore") {
                            CustomFontManager.getInstance(context)
                                    .loadFont(CustomFontManager.FONT_FEED, view.typeface.style,
                                            into = {
                                                view.typeface =
                                                        Typeface.create(it, view.typeface.style)
                                            })
                        }
                    }
                } else if (this is TextView) {
                    CustomFontManager.getInstance(context)
                            .loadFont(CustomFontManager.FONT_FEED, typeface.style,
                                    into = { typeface = Typeface.create(it, typeface.style) })
                }
            }.also { (it.parent as ViewGroup?)?.removeView(it) })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        cards[position].vhBindListener?.invoke(holder)
        if (holder.itemView is MaterialCardView) {
            if (!context.lawnchairPrefs.feedCardBlur && context.colorEngine.getResolver(
                            FEED_CARD) !is FeedBackgroundResolver) {
                holder.itemView.setCardBackgroundColor(
                        context.colorEngine.getResolver(FEED_CARD).resolveColor().setAlpha(
                                cards[position].overrideOpacity?.times(255)?.roundToInt()
                                        ?: context.lawnchairPrefs.feedCardOpacity.roundToInt()))
            } else {
                holder.itemView.setCardBackgroundColor(backgroundColor.setAlpha(
                        cards[position].overrideOpacity?.times(255)?.roundToInt()
                                ?: context.lawnchairPrefs.feedCardOpacity.roundToInt()))
            }
            if (context.lawnchairPrefs.feedCardOpacity.roundToInt() != 255 || context.lawnchairPrefs.feedCardBlur) {
                holder.itemView.cardElevation = 0f
            } else {
                holder.itemView.cardElevation = TypedValue
                        .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                context.lawnchairPrefs.feedCardElevation,
                                context.resources.displayMetrics)
            }
            if (holder.itemView.shapeAppearanceModel !=
                    CardStyleRegistry.ALL[context.feedPrefs.cardCornerTreatment]) {
                holder.itemView.shapeAppearanceModel =
                        CardStyleRegistry.ALL[context.feedPrefs.cardCornerTreatment]!!
            }
            holder.itemView.radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    context.feedPrefs.cardCornerRadius.toFloat(),
                    holder.itemView.context.resources.displayMetrics)
        }
    }

    companion object {
        @JvmOverloads
        fun getOverrideColor(c: Context, currentColor: Int = c.getColorEngineAccent(),
                             dark: Boolean = false): Int {
            return if (currentColor == R.color.colorAccent.fromColorRes(c)
                    || currentColor == R.color.colorAccentDark.fromColorRes(c)) {
                if (!dark) c.getColorAttr(
                        R.attr.colorAccent) else R.color.colorAccentDark.fromColorRes(c)
            } else {
                currentColor
            }
        }
    }
}

class CardViewHolder : RecyclerView.ViewHolder {
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
            ContextThemeWrapper(parent.context, if (useWhiteText(
                            if (type and Card.RAISE != 0) parent.context.colorEngine.getResolver(
                                    FEED_CARD).resolveColor() else backgroundColor,
                            parent.context) || parent.context.lawnchairPrefs.feedCardBlur) R.style.FeedTheme_Dark else R.style.FeedTheme_Light)).inflate(
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

        if (type and Card.RAISE == 0) {
            ((viewHolder.parent as View).layoutParams as ViewGroup.MarginLayoutParams)
                    .marginEnd = parent.context.feedPrefs.flatCardHorizontalPadding.roundToInt()
            ((viewHolder.parent as View).layoutParams as ViewGroup.MarginLayoutParams)
                    .marginStart = parent.context.feedPrefs.flatCardHorizontalPadding.roundToInt()
            ((viewHolder.parent as View).layoutParams as ViewGroup.MarginLayoutParams)
                    .bottomMargin = parent.context.feedPrefs.flatCardVerticalPadding.roundToInt()
            ((viewHolder.parent as View).layoutParams as ViewGroup.MarginLayoutParams)
                    .topMargin = parent.context.feedPrefs.flatCardVerticalPadding.roundToInt()
        }

        itemView.viewTreeObserver.addOnGlobalLayoutListener {
            (itemView as ViewGroup).allChildren.forEach { view ->
                if (view is TextView && view.tag != "font_ignore") {
                    CustomFontManager.getInstance(itemView.context)
                            .loadFont(CustomFontManager.FONT_TEXT, view.typeface.style,
                                    into = {
                                        view.typeface = Typeface.create(it, view.typeface.style)
                                    })
                }
            }
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
            (itemView as CardView).setCardBackgroundColor(
                    Color.TRANSPARENT)
            itemView.background.setTint(Color.argb(64, 255, 255, 255))
            backgroundBlurView!!.visibility = VISIBLE
            backgroundBlurView!!.setBlurRadius(
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25f,
                            itemView.context.resources.displayMetrics))
        }
    }
}

private class Decoration(private val spaceHeightVertical: Int,
                         private val spaceHeightHorizontal: Int) :
        RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView,
                                state: RecyclerView.State) {
        outRect.apply {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = spaceHeightVertical
            }
            left = if (spaceHeightHorizontal != 0) spaceHeightHorizontal else 1
            right = if (spaceHeightHorizontal != 0) spaceHeightHorizontal else 1
            bottom = spaceHeightVertical
        }
    }
}