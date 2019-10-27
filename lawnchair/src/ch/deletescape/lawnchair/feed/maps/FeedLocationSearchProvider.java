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

package ch.deletescape.lawnchair.feed.maps;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;

import com.android.launcher3.R;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.feed.Card;
import ch.deletescape.lawnchair.feed.FeedAdapter;
import ch.deletescape.lawnchair.feed.FeedProvider;
import ch.deletescape.lawnchair.feed.maps.locationsearch.LocationSearchManager;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FeedLocationSearchProvider extends FeedProvider {
    public FeedLocationSearchProvider(Context c) {
        super(c);
    }

    @Override
    public void onFeedShown() {

    }

    @Override
    public void onFeedHidden() {

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public List<Card> getCards() {
        Log.d(getClass().getName(), "getCards: retrieving cards");
        return ImmutableList.of(new Card(
                LawnchairUtilsKt.tint(
                        Objects.requireNonNull(getContext().getDrawable(R.drawable.ic_search)),
                        FeedAdapter.Companion.getOverrideColor(getContext())),
                getContext().getString(R.string.title_feed_provider_location_search), parent -> {
            LinearLayout layout = new LinearLayout(getContext());
            layout.setLayoutParams(
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            AutoCompleteTextView editText = new MultiAutoCompleteTextView(parent.getContext());
            editText.setLayoutParams(
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.addView(editText);
            ListView predictions = new ListView(parent.getContext());
            layout.addView(predictions);
            predictions.setLayoutParams(
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            editText.setThreshold(0);
            editText.setInputType(editText.getInputType() & ~InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            editText.setDropDownHeight((int) LawnchairUtilsKt.applyAsDip(256f, getContext()));
            editText.setMaxLines(1);
            editText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //noinspection ResultOfMethodCallIgnored
                    Flowable.fromCallable(() -> LocationSearchManager.getInstance(getContext()).get(
                            editText.getText().toString())).doOnError(e -> editText.post(() -> editText.setText(""))).subscribeOn(
                            Schedulers.newThread()).subscribe(pair -> editText.post(() -> new MapScreen(getContext(), getFeed(), pair.first, pair.second, 18.0,
                                    pair.first, pair.second).display(this, null, null)));
                }
                return true;
            });
            editText.setImeOptions(editText.getImeOptions() | EditorInfo.IME_ACTION_SEARCH);
            return layout;
        }, Card.RAISE,
                "nosort,top", "searchBar".hashCode()));
    }
}
