/*
 * Copyright 2016 Priyesh Patel
 * Copyright 2019 Lawnchair Launcher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.deletescape.lawnchair.colors

import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import ch.deletescape.lawnchair.applyAccent
import ch.deletescape.lawnchair.theme.ThemeOverride
import com.android.launcher3.R
import me.priyesh.chroma.ColorMode
import me.priyesh.chroma.orientation
import me.priyesh.chroma.percentOf
import me.priyesh.chroma.screenDimensions

class SingleUseColorDialog(c: Context, val initialColor: Int, val resolvers: List<String>,
                           val colorMode: ColorMode, val onColorSelected: (i: Int) -> Unit) :
        AlertDialog(c, ThemeOverride.AlertDialog().getTheme(c)) {
    private var tabbedPickerView: PickerView
    private var height: Int
    private var width: Int

    init {
        val context = context
        tabbedPickerView = PickerView(context, initialColor, colorMode, resolvers.toTypedArray()) {
            dismiss()
        }
        tabbedPickerView.onResolvedListener = {
            dismiss()
            onColorSelected(it);
        }
        setView(tabbedPickerView)
        if (orientation(context) == ORIENTATION_LANDSCAPE) {
            height = WindowManager.LayoutParams.WRAP_CONTENT
            width = 80 percentOf screenDimensions(context).widthPixels
        } else {
            height = WindowManager.LayoutParams.WRAP_CONTENT
            width = context.resources.getDimensionPixelSize(R.dimen.chroma_dialog_width)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        window?.setLayout(width, height)
        window?.setBackgroundDrawable(context.getDrawable(R.drawable.dialog_material_background))
        applyAccent()
        window?.clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
    }
}
