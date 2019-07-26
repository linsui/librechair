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

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.view.View
import android.widget.TimePicker
import ch.deletescape.lawnchair.applyAccent
import com.android.launcher3.R

class TimePickerFragment : PreferenceDialogFragmentCompat() {

    private val timePickerPreference get() = preference as TimePickerPreference
    private lateinit var timePicker: TimePicker

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        timePicker = view.findViewById(R.id.time_picker)
        val (hour, minute) = timePickerPreference.sharedPreferences.getString(
                timePickerPreference.key,
                "${timePickerPreference.defaultValue.first}:${String.format("%02d",
                                                                            timePickerPreference.defaultValue.second)}")!!.split(
                ":")
        timePicker.hour = hour.toInt()
        timePicker.minute = minute.toInt()
        timePicker.setOnTimeChangedListener { view2, hourOfDay, minute2 ->
            timePickerPreference.sharedPreferences.edit()
                    .putString(preference.key, "$hourOfDay:${String.format("%02d", minute2)}")
                    .apply()
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        timePickerPreference.onDialogFinish(positiveResult)
    }

    override fun onStart() {
        super.onStart()
        (dialog as AlertDialog).applyAccent()
    }

    companion object {

        fun newInstance(key: String?) = TimePickerFragment().apply {
            arguments = Bundle(1).apply {
                putString(ARG_KEY, key)
            }
        }
    }
}
