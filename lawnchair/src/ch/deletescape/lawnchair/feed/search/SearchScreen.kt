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

package ch.deletescape.lawnchair.feed.search

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ch.deletescape.lawnchair.feed.ProviderScreen
import ch.deletescape.lawnchair.feed.impl.LauncherFeed
import ch.deletescape.lawnchair.inflate
import com.android.launcher3.R
import kotlinx.android.synthetic.lawnchair.search_screen.view.*
import kotlinx.coroutines.Job

class SearchScreen(private val feed: LauncherFeed) : ProviderScreen(feed.context) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var editText: EditText
    private lateinit var adapter: SearchAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var currentJob: Job

    override fun bindView(view: View) {
        recyclerView = view.feed_search_rv
        editText = view.search_edit_text
        adapter = SearchAdapter(feed.adapter, null)
        swipeRefreshLayout = view.feed_search_swipe_layout

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.searchQuery = if (s.toString().trim().isNotEmpty()) s.toString() else null
                adapter.notifyDataSetChanged()
            }

        })
    }

    override fun getView(parent: ViewGroup): View {
        return parent.inflate(R.layout.search_screen)
    }
}