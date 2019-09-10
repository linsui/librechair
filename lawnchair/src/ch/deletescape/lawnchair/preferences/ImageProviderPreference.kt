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

package ch.deletescape.lawnchair.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.preference.ListPreference
import ch.deletescape.lawnchair.feed.images.providers.*
import ch.deletescape.lawnchair.fromStringRes
import com.android.launcher3.R
import kotlin.reflect.KClass

class ImageProviderPreference(context: Context, attrs: AttributeSet) :
        ListPreference(context, attrs) {
    init {
        entries = getAllProviders().map { getNameForProvider(it).fromStringRes(context) }.toTypedArray()
        entryValues = getAllProviders().map { it.qualifiedName }.toTypedArray()
        setDefaultValue(ImageProvider::class.qualifiedName)
        setOnPreferenceChangeListener { preference, newValue ->
            notifyDependencyChange(newValue != ImageProvider::class.qualifiedName)
            true
        }
    }

    override fun shouldDisableDependents() = value == ImageProvider::class.qualifiedName

    companion object {
        fun getAllProviders(): List<KClass<out ImageProvider>> = listOf(ImageProvider::class,
                                                                        BingImageProvider::class,
                                                                        ApodImageProvider::class,
                                                                        NationalGeographicImageProvider::class,
                                                                        CustomBackgroundProvider::class)
        fun getNameForProvider(clazz: KClass<out ImageProvider>) = when (clazz) {
            ImageProvider::class -> R.string.none
            BingImageProvider::class -> R.string.title_feed_provider_bing_daily
            ApodImageProvider::class -> R.string.title_image_provider_apod
            NationalGeographicImageProvider::class -> R.string.title_image_provider_national_geographic
            CustomBackgroundProvider::class -> R.string.custom
            else -> error("there's no known name for the provider $clazz")
        }
    }
}