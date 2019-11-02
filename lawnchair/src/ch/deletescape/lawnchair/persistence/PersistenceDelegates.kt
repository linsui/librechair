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

package ch.deletescape.lawnchair.persistence

import android.content.Context
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.room.InvalidationTracker
import ch.deletescape.lawnchair.applyAsDip
import ch.deletescape.lawnchair.persistence.db.StringDatabase
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import kotlin.reflect.KProperty

@Suppress("unused")
class StringDelegate<T>(val context: Context, val key: String) {
    operator fun setValue(t: T, property: KProperty<*>, value: String) =
            SimplePersistence.InstanceHolder.getInstance(context).put(key, value)

    operator fun getValue(t: T, property: KProperty<*>): String =
            SimplePersistence.InstanceHolder.getInstance(context).get(key, "")
}


class DefValueStringDelegate<T>(val context: Context, val key: String,
                                private val defaultValue: String) {
    operator fun setValue(t: T, property: KProperty<*>, value: String) =
            SimplePersistence.InstanceHolder.getInstance(context).put(key, value)

    operator fun getValue(t: T, property: KProperty<*>): String =
            SimplePersistence.InstanceHolder.getInstance(context).get(key, defaultValue)
}

open class NumberDelegate<T>(val context: Context, val key: String, private val defValue: Double) {

    open operator fun setValue(t: T, property: KProperty<*>, value: Double) =
            SimplePersistence.InstanceHolder.getInstance(context).put(key, value.toString())

    open operator fun getValue(t: T, property: KProperty<*>): Double =
            SimplePersistence.InstanceHolder.getInstance(context).get(key,
                    defValue.toString()).toDouble()
}

class DipDimenDelegate<T>(context: Context, key: String, defValue: Double) :
        NumberDelegate<T>(context, key, defValue) {
    override fun setValue(t: T, property: KProperty<*>,
                          value: Double) = throw UnsupportedOperationException(
            "setValue unsupported in dimen delegates")

    override fun getValue(t: T, property: KProperty<*>): Double = super.getValue(t,
            property).toFloat().applyAsDip(context).toDouble()
}

class BooleanDelegate<T>(val context: Context, val key: String, private val defValue: Boolean) {

    operator fun setValue(t: T, property: KProperty<*>, value: Boolean) =
            SimplePersistence.InstanceHolder.getInstance(context).put(key, value.toString())

    operator fun getValue(t: T, property: KProperty<*>): Boolean =
            SimplePersistence.InstanceHolder.getInstance(context).get(key,
                    defValue.toString())?.toBoolean() ?: defValue
}

class ListDelegate<T>(val context: Context, val key: String, val defValue: List<String>) {

    private val internal = ObservableArrayList<String>().apply {
        addAll(defValue)
    }
    private var lastData = StringDatabase.getInstance(context).dao().getSafe(key)

    init {
        if (lastData != null) {
            synchronized(internal) {
                internal.clear()
                internal.addAll(JsonParser().parse(lastData).asJsonArray.map { it.asString })
            }
        }
        internal.addOnListChangedCallback(object :
                ObservableList.OnListChangedCallback<ObservableList<String>>() {
            override fun onChanged(sender: ObservableList<String>?) {
                save()
            }

            override fun onItemRangeRemoved(sender: ObservableList<String>?, positionStart: Int,
                                            itemCount: Int) {
                save()
            }

            override fun onItemRangeMoved(sender: ObservableList<String>?, fromPosition: Int,
                                          toPosition: Int, itemCount: Int) {
                save()
            }

            override fun onItemRangeInserted(sender: ObservableList<String>?,
                                             positionStart: Int, itemCount: Int) {
                save()
            }

            override fun onItemRangeChanged(sender: ObservableList<String>?, positionStart: Int,
                                            itemCount: Int) {
                save()
            }

        })
        StringDatabase.getInstance(context).invalidationTracker.addObserver(object :
                InvalidationTracker.Observer("stringentry") {
            override fun onInvalidated(tables: MutableSet<String>) {
                val currentData = StringDatabase.getInstance(context).dao().getSafe(key)
                if (lastData != currentData) {
                    val ja = JsonParser().parse(currentData).asJsonArray
                    synchronized(internal) {
                        internal.clear()
                        internal.addAll(ja.map { it.asString })
                    }
                    synchronized(lastData) {
                        lastData = ja.toString()
                    }
                }
            }
        })
    }

    private fun save() {
        val jsonArray = JsonArray(internal.size)
        internal.map { JsonPrimitive(it) }.forEach { jsonArray.add(it) }
        lastData = jsonArray.toString()
        StringDatabase.getInstance(context).dao().put(key, jsonArray.toString())
    }

    operator fun getValue(t: T?, property: KProperty<*>?): ObservableList<String> = internal
}