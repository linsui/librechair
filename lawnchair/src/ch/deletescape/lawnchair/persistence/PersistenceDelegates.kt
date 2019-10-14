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
import ch.deletescape.lawnchair.applyAsDip
import kotlin.reflect.KProperty

class StringDelegate<T>(val context: Context, val key: String) {
    operator fun setValue(t: T, property: KProperty<*>, value: String) =
            SimplePersistence.InstanceHolder.getInstance(context).put(key, value)

    operator fun getValue(t: T, property: KProperty<*>): String =
            SimplePersistence.InstanceHolder.getInstance(context).get(key, "")
}


class DefValueStringDelegate<T>(val context: Context, val key: String, val defaultValue: String) {
    operator fun setValue(t: T, property: KProperty<*>, value: String) =
            SimplePersistence.InstanceHolder.getInstance(context).put(key, value)

    operator fun getValue(t: T, property: KProperty<*>): String =
            SimplePersistence.InstanceHolder.getInstance(context).get(key, defaultValue)
}

open class NumberDelegate<T>(val context: Context, val key: String, val defValue: Double) {

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

class BooleanDelegate<T>(val context: Context, val key: String, val defValue: Boolean) {

    operator fun setValue(t: T, property: KProperty<*>, value: Boolean) =
            SimplePersistence.InstanceHolder.getInstance(context).put(key, value.toString())

    operator fun getValue(t: T, property: KProperty<*>): Boolean =
            SimplePersistence.InstanceHolder.getInstance(context).get(key,
                    defValue.toString())?.toBoolean() ?: defValue
}