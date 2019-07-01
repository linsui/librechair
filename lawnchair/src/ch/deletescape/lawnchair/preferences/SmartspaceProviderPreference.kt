/*
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
import android.support.v7.preference.ListPreference
import android.util.AttributeSet
import ch.deletescape.lawnchair.LawnchairPreferences
import ch.deletescape.lawnchair.smartspace.*
import ch.deletescape.lawnchair.smartspace.weather.owm.OWMWeatherDataProvider
import ch.deletescape.lawnchair.util.buildEntries
import com.android.launcher3.R
import com.android.launcher3.Utilities

class SmartspaceProviderPreference(context: Context, attrs: AttributeSet?) :
        ListPreference(context, attrs), LawnchairPreferences.OnPreferenceChangeListener {

    private val prefs = Utilities.getLawnchairPrefs(context)
    private val forWeather by lazy { key == "pref_smartspace_widget_provider" }

    init {
        buildEntries {
            getProviders().forEach {
                addEntry(displayNames[it] ?: error("No display name for provider $it"), it)
            }
        }
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        super.onSetInitialValue(true, defaultValue)
    }

    private fun getProviders(): List<String> {
        return if (forWeather) getWeatherProviders() else SmartspaceEventProvidersAdapter.getEventProviders(
            context)
    }

    private fun getWeatherProviders(): List<String> {
        val list = ArrayList<String>()
        list.add(BlankDataProvider::class.java.name)
        list.add(AccuWeatherDataProvider::class.java.name)
        list.add(OWMWeatherDataProvider::class.java.name)
        if (prefs.showDebugInfo) list.add(FakeDataProvider::class.java.name)
        return list
    }

    override fun shouldDisableDependents(): Boolean {
        return super.shouldDisableDependents() || value == BlankDataProvider::class.java.name
    }

    override fun onValueChanged(key: String, prefs: LawnchairPreferences, force: Boolean) {
        if (value != getPersistedValue()) {
            value = getPersistedValue()
        }
        notifyDependencyChange(shouldDisableDependents())
    }

    override fun onAttached() {
        super.onAttached()

        prefs.addOnPreferenceChangeListener(key, this)
    }

    override fun onDetached() {
        super.onDetached()

        prefs.removeOnPreferenceChangeListener(key, this)
    }

    override fun getPersistedString(defaultReturnValue: String?): String {
        return getPersistedValue()
    }

    private fun getPersistedValue() = prefs.sharedPrefs.getString(key, if (forWeather) OWMWeatherDataProvider::class.java.name else BuiltInCalendarProvider::class.java.name)

    override fun persistString(value: String?): Boolean {
        prefs.sharedPrefs.edit().putString(key, value ?: BlankDataProvider::class.java.name).apply()
        return true
    }

    companion object {

        val  displayNames =
                mapOf(Pair(BlankDataProvider::class.java.name, R.string.weather_provider_disabled),
                      Pair(BuiltInCalendarProvider::class.java.name,
                           R.string.provider_built_in_calendar_title),
                      Pair(OWMWeatherDataProvider::class.java.name, R.string.weather_provider_owm),
                      Pair(AccuWeatherDataProvider::class.java.name,
                           R.string.weather_provider_accu),
                      Pair(PEWeatherDataProvider::class.java.name, R.string.weather_provider_pe),
                      Pair(FakeDataProvider::class.java.name, R.string.weather_provider_testing),
                      Pair(NowPlayingProvider::class.java.name,
                           R.string.event_provider_now_playing),
                      Pair(NotificationUnreadProvider::class.java.name,
                           R.string.event_provider_unread_notifications),
                      Pair(BatteryStatusProvider::class.java.name, R.string.battery_status))

    }
}