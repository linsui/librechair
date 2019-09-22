/*
 * Copyright (c) 2019 oldosfan.
 * Copyright (c) 2019 the Lawnchair developers
 *
 *     This file is part of Librechair.
 *
 *     Librechair is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Librechair is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.feed.preview

import android.content.Context
import android.text.Html
import android.view.View
import android.widget.TextView
import ch.deletescape.lawnchair.feed.Card
import ch.deletescape.lawnchair.feed.FeedAdapter
import ch.deletescape.lawnchair.fromColorRes
import ch.deletescape.lawnchair.fromDrawableRes
import ch.deletescape.lawnchair.tint
import ch.deletescape.lawnchair.useWhiteText
import com.android.launcher3.R

class FeedPreviewAdapter(backgroundColor: Int, context: Context)
    : FeedAdapter(listOf(), backgroundColor, context, null) {

    val previewCards = listOf(Card(R.drawable.ic_information.fromDrawableRes(context).tint(
            if (!useWhiteText(backgroundColor,
                            context)) R.color.textColorPrimaryInverse.fromColorRes(
                    context) else R.color.textColorPrimary.fromColorRes(context)),
            context.getString(R.string.title_card_feed_preview_header), { v, _ -> View(context) },
            Card.TEXT_ONLY, "nosort,top"),
            Card(R.drawable.ic_information.fromDrawableRes(context).tint(
                    FeedAdapter.getOverrideColor(context)),
                    context.getString(R.string.title_card_feed_preview_header),
                    { v, _ ->
                        TextView(v.context).apply {
                            text = Html.fromHtml("<h2>This is a header</h2>\n" +
                                    "<h1>Bigger header</h1>\n" +
                                    "Our Great Leader, <b>King Jong-Un</b> made this <i>great</i> sample text",
                                    0)
                        }
                    },
                    Card.RAISE, "nosort,top"))

    override fun refresh(): Int {
        cards.clear()
        cards.addAll(previewCards)
        return previewCards.size
    }
}