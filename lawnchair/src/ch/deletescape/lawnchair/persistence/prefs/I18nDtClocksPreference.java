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

package ch.deletescape.lawnchair.persistence.prefs;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;

import androidx.databinding.ObservableList;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.android.launcher3.R;

import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.persistence.FeedPersistence;
import ch.deletescape.lawnchair.preferences.FragmentInitializer;

public class I18nDtClocksPreference extends DialogPreference implements FragmentInitializer {
    public I18nDtClocksPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setKey("pref_feed_world_clocks");
        setSummary(FeedPersistence.Companion.getInstance(context).getClockTimeZones().stream().map(
                it -> ZoneId.of(it).getDisplayName(
                        TextStyle.SHORT, LawnchairUtilsKt.getLocale(context))).collect(
                Collectors.joining(", ")));
        FeedPersistence.Companion.getInstance(context).getClockTimeZones().addOnListChangedCallback(
                new ObservableList.OnListChangedCallback<ObservableList<String>>() {
                    @Override
                    public void onChanged(ObservableList<String> sender) {
                        setSummary(FeedPersistence.Companion.getInstance(
                                context).getClockTimeZones().stream().map(
                                it -> ZoneId.of(it).getDisplayName(
                                        TextStyle.SHORT,
                                        LawnchairUtilsKt.getLocale(context))).collect(
                                Collectors.joining(", ")));
                    }

                    @Override
                    public void onItemRangeChanged(ObservableList<String> sender, int positionStart,
                                                   int itemCount) {
                        setSummary(FeedPersistence.Companion.getInstance(
                                context).getClockTimeZones().stream().map(
                                it -> ZoneId.of(it).getDisplayName(
                                        TextStyle.SHORT,
                                        LawnchairUtilsKt.getLocale(context))).collect(
                                Collectors.joining(", ")));
                    }

                    @Override
                    public void onItemRangeInserted(ObservableList<String> sender,
                                                    int positionStart, int itemCount) {
                        setSummary(FeedPersistence.Companion.getInstance(
                                context).getClockTimeZones().stream().map(
                                it -> ZoneId.of(it).getDisplayName(
                                        TextStyle.SHORT,
                                        LawnchairUtilsKt.getLocale(context))).collect(
                                Collectors.joining(", ")));
                    }

                    @Override
                    public void onItemRangeMoved(ObservableList<String> sender, int fromPosition,
                                                 int toPosition, int itemCount) {
                        setSummary(FeedPersistence.Companion.getInstance(
                                context).getClockTimeZones().stream().map(
                                it -> ZoneId.of(it).getDisplayName(
                                        TextStyle.SHORT,
                                        LawnchairUtilsKt.getLocale(context))).collect(
                                Collectors.joining(", ")));
                    }

                    @Override
                    public void onItemRangeRemoved(ObservableList<String> sender, int positionStart,
                                                   int itemCount) {
                        setSummary(FeedPersistence.Companion.getInstance(
                                context).getClockTimeZones().stream().map(
                                it -> ZoneId.of(it).getDisplayName(
                                        TextStyle.SHORT,
                                        LawnchairUtilsKt.getLocale(context))).collect(
                                Collectors.joining(", ")));
                    }
                });
        setTitle(R.string.title_pref_feed_world_clocks);
    }

    @Override
    public CharSequence getDialogTitle() {
        return getContext().getString(R.string.title_pref_feed_world_clocks);
    }

    @Override
    public CharSequence getPositiveButtonText() {
        return getContext().getString(R.string.add_new_tab);
    }

    @Override
    public CharSequence getNegativeButtonText() {
        return null;
    }

    @NotNull
    @Override
    public PreferenceDialogFragmentCompat getPrefFragment(@NotNull String key) {
        Bundle keyArg = new Bundle();
        keyArg.putString("key", "pref_feed_world_clocks");
        I18nDtClocksFragment fragment = new I18nDtClocksFragment();
        fragment.setArguments(keyArg);
        return fragment;
    }

    @Override
    public int getDialogLayoutResource() {
        return R.layout.dialog_preference_recyclerview;
    }
}
