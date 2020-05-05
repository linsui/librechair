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

@file:Suppress("MemberVisibilityCanBePrivate")

package ch.deletescape.lawnchair.feed

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Parcel
import android.os.Parcelable
import android.view.ViewGroup
import ch.deletescape.lawnchair.theme.ThemeManager
import ch.deletescape.lawnchair.util.IRunnable

@Suppress("unused")
data class RemoteCard(val icon: Bitmap?, val title: String?, val inflateHelper: RemoteInflateHelper,
                      val type: Int, val algoFlags: String? = null,
                      val identifier: Int = title.hashCode()) : Parcelable {
    var canHide = false
    var categories: List<String>? = null
    var actionName: String? = null
    var actionOnCardActionSelectedListener: RemoteOnCardActionSelectedListener? = null
    var onRemoveListener: OnRemoveListener? = null
    var globalClickListener: IRunnable? = null
    var overrideOpacity: Float? = null
    var indexData: String? = null

    constructor(parcel: Parcel) : this(parcel.readParcelable(Bitmap::class.java.classLoader),
                                       parcel.readString(), RemoteInflateHelper.Stub.asInterface(
            parcel.readStrongBinder()), parcel.readInt(), parcel.readString(), parcel.readInt()) {
        canHide = parcel.readByte() != 0.toByte()
        categories = parcel.createStringArrayList()
        actionName = parcel.readString()
        actionOnCardActionSelectedListener =
                RemoteOnCardActionSelectedListener.Stub.asInterface(parcel.readStrongBinder())
        onRemoveListener = OnRemoveListener.Stub.asInterface(parcel.readStrongBinder())
        globalClickListener = IRunnable.Stub.asInterface(parcel.readStrongBinder())
        overrideOpacity = parcel.readString()?.toFloatOrNull()
        indexData = parcel.readString()
    }

    constructor(icon: Bitmap?, title: String?, inflateHelper: RemoteInflateHelper, type: Int,
                algoFlags: String? = null, identifier: Int = title.hashCode(),
                canHide: Boolean) : this(icon, title, inflateHelper, type, algoFlags, identifier) {
        this.canHide = canHide
    }

    constructor(icon: Bitmap?, title: String?, inflateHelper: RemoteInflateHelper, type: Int,
                algoFlags: String? = null, identifier: Int = title.hashCode(), canHide: Boolean,
                category: List<String>) : this(icon, title, inflateHelper, type, algoFlags,
                                               identifier) {
        this.canHide = canHide
        this.categories = category
    }

    object Types {
        const val DEFAULT = 0
        const val RAISE = 1 shl 1
        const val NARROW = 1 shl 2
        const val TEXT_ONLY = 1 shl 3
        const val NO_HEADER = 1 shl 4
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(icon, flags)
        parcel.writeString(title)
        parcel.writeStrongBinder(inflateHelper.asBinder())
        parcel.writeInt(type)
        parcel.writeString(algoFlags)
        parcel.writeInt(identifier)
        parcel.writeByte(if (canHide) 1 else 0)
        parcel.writeStringList(categories)
        parcel.writeString(actionName)
        parcel.writeStrongBinder(actionOnCardActionSelectedListener?.asBinder())
        parcel.writeStrongBinder(onRemoveListener?.asBinder())
        parcel.writeStrongBinder(globalClickListener?.asBinder())
        parcel.writeString(overrideOpacity?.toString())
        parcel.writeString(indexData)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RemoteCard> {
        override fun createFromParcel(parcel: Parcel): RemoteCard {
            return RemoteCard(parcel)
        }

        override fun newArray(size: Int): Array<RemoteCard?> {
            return arrayOfNulls(size)
        }
    }

    fun toCard(c: Context): Card {
        return Card(BitmapDrawable(c.resources, icon), title, { v, _ ->
            inflateHelper.inflate(!ThemeManager.getInstance(c).isDark)
                    .apply(v.context, v as ViewGroup)
        }, type, algoFlags, identifier).apply {
            canHide = this@RemoteCard.canHide
            this.categories = this@RemoteCard.categories
            if (actionOnCardActionSelectedListener != null && this@RemoteCard.actionName != null) {
                actionListener = { actionOnCardActionSelectedListener?.onAction() }
                actionName = this@RemoteCard.actionName
            }
            if (this@RemoteCard.onRemoveListener != null) {
                onRemoveListener = {
                    this@RemoteCard.onRemoveListener?.onRemove()
                }
            }
            if (this@RemoteCard.globalClickListener != null) {
                globalClickListener = {
                    this@RemoteCard.globalClickListener?.run()
                }
            }
        }
    }
}