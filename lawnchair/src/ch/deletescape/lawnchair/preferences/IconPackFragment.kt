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

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import ch.deletescape.lawnchair.LawnchairPreferences

class IconPackFragment : RecyclerViewFragment() {

    private val adapter by lazy { IconPackAdapter(requireContext()) }

    override fun onRecyclerViewCreated(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        (recyclerView.itemAnimator as? androidx.recyclerview.widget.DefaultItemAnimator)?.supportsChangeAnimations = false
        adapter.itemTouchHelper = ItemTouchHelper(adapter.TouchHelperCallback()).apply {
            attachToRecyclerView(recyclerView)
        }
    }

    override fun onPause() {
        super.onPause()

        LawnchairPreferences.getInstance(requireContext()).iconPacks.setAll(adapter.saveSpecs())
    }
}
