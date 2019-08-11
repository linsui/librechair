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

package ch.deletescape.lawnchair.todo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Parcel
import android.os.Parcelable

data class Note(val title: String, val content: String, val type: Types, val location: String) :
        Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()!!, parcel.readString()!!,
                                       Types.valueOf(parcel.readString()!!), parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(type.name)
        parcel.writeString(location)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }
}

object NoteUtils {
    @JvmStatic
    val INTENT_ACTION = "ch.deletescape.lawnchair.todo.TODO_PROVIDER"

    fun getTodoProviderIntents(context: Context): List<Intent> = getTodoProviders(context).map {
        Intent().setComponent(ComponentName(it.packageName, it.name))
    }

    fun getTodoProviders(
            context: Context): List<ServiceInfo> = context.packageManager.getInstalledApplications(
            0).mapNotNull {
        context.packageManager.resolveService(Intent(INTENT_ACTION).setPackage(it.packageName), 0)
                ?.serviceInfo
    }
}

enum class Types {
    NOTE, TODO
}