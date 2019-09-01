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

package ch.deletescape.lawnchair.feed;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.deletescape.lawnchair.notes.SimpleNoteAdapter;
import com.android.launcher3.R;

public class NoteProviderScreen extends ProviderScreen {

    private SimpleNoteAdapter adapter;

    public NoteProviderScreen(Context base) {
        super(base);
        adapter = new SimpleNoteAdapter(base);
    }

    @Override
    protected View getView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.screen_notes, parent, false);
    }

    @Override
    protected void bindView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.notes_recycler);
        TabLayout tabLayout = view.findViewById(R.id.note_tabs);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
