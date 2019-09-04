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

import android.content.Context
import android.content.res.ColorStateList
import android.os.Handler
import android.os.HandlerThread
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceViewHolder
import android.util.AttributeSet
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import ch.deletescape.lawnchair.colors.ColorEngine
import com.android.launcher3.R
import kotlin.math.roundToInt

open class IntSeekbarPreference @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        Preference(context, attrs, defStyleAttr), SeekBar.OnSeekBarChangeListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener, ColorEngine.OnColorChangeListener {

    private var mSeekbar: SeekBar? = null
    protected var mValueText: TextView? = null
    protected var min: Int = 0
    protected var max: Int = 0
    protected var current: Int = 0
    protected var defaultValue: Int = 0
    private var multiplier: Int = 0
    private var format: String? = null
    private var lastPersist: Int? = null

    private val handlerThread = HandlerThread("debounce").apply { start() }
    private val persistHandler = Handler(handlerThread.looper)

    open val allowResetToDefault = true

    init {
        layoutResource = R.layout.preference_seekbar
        init(context, attrs!!)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SeekbarPreference)
        min = ta.getInt(R.styleable.IntSeekbarPreference_minValueI, 0)
        max = ta.getInt(R.styleable.IntSeekbarPreference_maxValueI, 100)
        multiplier = ta.getInt(R.styleable.SeekbarPreference_summaryMultiplier, 1)
        format = ta.getString(R.styleable.SeekbarPreference_summaryFormat)
        defaultValue = ta.getInt(R.styleable.IntSeekbarPreference_defaultSeekbarValueI, min)
        if (format == null) {
            format = "%.2d"
        }
        ta.recycle()
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val view = holder.itemView
        mSeekbar = view.findViewById(R.id.seekbar)
        mValueText = view.findViewById(R.id.txtValue)
        mSeekbar!!.max = max
        mSeekbar!!.min = min

        current = getPersistedInt(defaultValue)
        updateDisplayedValue()

        if (allowResetToDefault) view.setOnCreateContextMenuListener(this)
        ColorEngine.getInstance(context).addColorChangeListeners(this, ColorEngine.Resolvers.ACCENT)
    }

    override fun onColorChange(resolveInfo: ColorEngine.ResolveInfo) {
        if (resolveInfo.key == ColorEngine.Resolvers.ACCENT) {
            val stateList = ColorStateList.valueOf(resolveInfo.color)
            mSeekbar?.apply {
                thumbTintList = stateList
                progressTintList = stateList
                progressBackgroundTintList = stateList
            }
        }
    }

    override fun onDetached() {
        super.onDetached()
        ColorEngine.getInstance(context).removeColorChangeListeners(this, ColorEngine.Resolvers.ACCENT)
    }

    fun setValue(value: Int) {
        current = value
        persistInt(value)
        updateDisplayedValue()
    }

    protected open fun updateDisplayedValue() {
        mSeekbar?.setOnSeekBarChangeListener(null)
        val progress = current
        mSeekbar!!.progress = progress
        updateSummary()
        mSeekbar?.setOnSeekBarChangeListener(this)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        current = progress
        updateSummary()
    }

    protected open fun updateSummary() {
        mValueText!!.text = String.format(format!!, current * multiplier)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        persistInt(current)
    }

    override fun persistInt(value: Int): Boolean {
        if (value == lastPersist) return true
        lastPersist = value
        return super.persistInt(value)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu.setHeaderTitle(title)
        menu.add(0, 0, 0, R.string.reset_to_default)
        for (i in (0 until menu.size())) {
            menu.getItem(i).setOnMenuItemClickListener(this)
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        setValue(defaultValue)
        return true
    }
}
