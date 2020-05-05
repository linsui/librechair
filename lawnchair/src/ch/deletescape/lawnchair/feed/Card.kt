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

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup

/*
 * algoFlags is a simple comma-separated string with flags for the sorting algorithm
 * An example for the Mixer algorithm would be:
 *     "nosort,top"
 * which will ensure that the card appears on the top of the screen, in user-defined order
 */

data class Card constructor(val icon: Drawable?, val title: String?, val inflateHelper: InflateHelper,
                val type: Int, val algoFlags: String? = null,
                val identifier: Int = title.hashCode()) {
    var canHide = false
    var categories: List<String>? = null
    var actionName: String? = null
    var actionListener: ((view: View) -> Unit)? = null
    var onRemoveListener: ((view: View) -> Unit)? = null
    var indexData: String? = null

    var vhBindListener: ((vh: CardViewHolder) -> Unit)? = null
    var overrideOpacity: Float? = null
    lateinit var globalClickListener: (v: View) -> Unit

    constructor(icon: Drawable?, title: String?,
                inflateHelper: (parent: View, _: Unit /* This is used to resolve ambiguities in Java and is unused */) -> View,
                type: Int, algoFlags: String? = null, identifier: Int = title.hashCode()) : this(
            icon, title, object : InflateHelper {
        override fun inflate(parent: ViewGroup): View {
            return inflateHelper(parent, Unit)
        }
    }, type, algoFlags, identifier)

    constructor(icon: Drawable?, title: String?, inflateHelper: InflateHelper, type: Int,
                algoFlags: String? = null, identifier: Int = title.hashCode(),
                canHide: Boolean) : this(icon, title, inflateHelper, type, algoFlags, identifier) {
        this.canHide = canHide
    }

    constructor(icon: Drawable?, title: String?, inflateHelper: InflateHelper, type: Int,
                algoFlags: String? = null, identifier: Int = title.hashCode(), canHide: Boolean,
                category: List<String>) : this(icon, title, inflateHelper, type, algoFlags,
            identifier) {
        this.canHide = canHide
        this.categories = category
    }

    companion object {
        const val DEFAULT = 0
        const val RAISE = 1 shl 1
        const val NARROW = 1 shl 2
        const val TEXT_ONLY = 1 shl 3
        const val NO_HEADER = 1 shl 4

        interface InflateHelper {
            fun inflate(parent: ViewGroup): View
        }
    }

    fun hasGlobalClickListener() =
            ::globalClickListener.isInitialized

    override fun toString(): String {
        return "Card(icon=$icon, title=$title, inflateHelper=$inflateHelper, type=$type, algoFlags=$algoFlags, identifier=$identifier, canHide=$canHide, internalCategory=$categories, actionName=$actionName, actionListener=$actionListener)"
    }
}

