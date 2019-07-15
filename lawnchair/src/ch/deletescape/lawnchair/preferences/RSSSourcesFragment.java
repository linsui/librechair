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

package ch.deletescape.lawnchair.preferences;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ch.deletescape.lawnchair.LawnchairPreferences;
import com.android.launcher3.R;

public class RSSSourcesFragment extends PreferenceDialogFragmentCompat {

    private RecyclerView recyclerView;
    private RSSPreferencesAdapter adapter;

    public static RSSSourcesFragment newInstance(String key) {
        RSSSourcesFragment rssSourcesFragment = new RSSSourcesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_KEY, key);
        rssSourcesFragment.setArguments(bundle);
        return rssSourcesFragment;
    }

    @Override
    public void onBindDialogView(View v) {
        recyclerView = v.findViewById(R.id.providers_list);
        recyclerView.setAdapter(adapter = new RSSPreferencesAdapter(this.getContext()));
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {

    }

    @Override
    public RSSSourcesPreference getPreference() {
        return ((RSSSourcesPreference) super.getPreference());
    }

    public static class RSSPreferencesAdapter extends RecyclerView.Adapter {
        private Context context;
        private LawnchairPreferences preferences;

        public RSSPreferencesAdapter(Context context) {
            this.context = context;
            this.preferences = LawnchairPreferences.Companion.getInstance(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false)) {};
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TextView itemView = (TextView) holder.itemView;
            itemView.setText(preferences.getFeedRSSSources().getAll().get(position));
        }

        @Override
        public int getItemCount() {
            Log.d(getClass().getSimpleName(), "getItemCount: " + preferences.getFeedRSSSources().getAll() + " " + preferences.getFeedRSSSources().getAll().size());
            return preferences.getFeedRSSSources().getAll().size();
        }
    }
}
