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

package ch.deletescape.lawnchair.feed.contacts

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import ch.deletescape.lawnchair.feed.Card
import ch.deletescape.lawnchair.feed.FeedProvider
import ch.deletescape.lawnchair.util.extensions.d
import java.util.*
import kotlin.math.max

class FeedContactsProvider(c: Context?) : FeedProvider(c) {
    override fun isVolatile() = true

    override fun getCards(): List<Card> {
        return ContactsUtil.queryContacts(context).map {
            d("getCards: contact: $it")
            Card(if (it.avatar == null) null else RoundedBitmapDrawableFactory.create(
                    context.resources,
                    it.avatar).apply {
                cornerRadius =
                        max(it.avatar.height, it.avatar.width).toFloat()
            },
                    it.name, { _, _ -> View(context) },
                    Card.RAISE or Card.TEXT_ONLY, "",
                    UUID.randomUUID().hashCode()).apply {
                globalClickListener = { v ->
                    v.context.startActivity(it.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            }
        }
    }
}