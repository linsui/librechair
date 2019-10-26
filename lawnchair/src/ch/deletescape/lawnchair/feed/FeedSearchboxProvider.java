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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;

import com.android.launcher3.R;
import com.android.launcher3.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.feed.web.WebViewScreen;
import ch.deletescape.lawnchair.globalsearch.SearchProvider;
import ch.deletescape.lawnchair.globalsearch.SearchProviderController;
import ch.deletescape.lawnchair.globalsearch.providers.web.WebSearchProvider;

public class FeedSearchboxProvider extends FeedProvider {

    public FeedSearchboxProvider(Context c) {
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

    @SuppressWarnings("unchecked")
    @Override
    public List<Card> getCards() {
        Log.d(getClass().getName(), "getCards: retrieving cards");
        return Collections.singletonList(new Card(
                LawnchairUtilsKt.tint(
                        Objects.requireNonNull(getContext().getDrawable(R.drawable.ic_search)),
                        FeedAdapter.Companion.getOverrideColor(getContext())),
                getContext().getString(R.string.search), parent -> {
            LinearLayout layout = new LinearLayout(getContext());
            layout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            AutoCompleteTextView editText = new MultiAutoCompleteTextView(parent.getContext());
            editText.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.addView(editText);
            ListView predictions = new ListView(parent.getContext());
            layout.addView(predictions);
            predictions.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            editText.setThreshold(0);
            editText.setInputType(editText.getInputType() & ~InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            ArrayAdapter<String> adapter;
            predictions.setAdapter(adapter = new ArrayAdapter(parent.getContext(),
                    android.R.layout.simple_list_item_1,
                    new ArrayList<String>()));
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Executors.newSingleThreadExecutor().submit(() -> {
                        SearchProvider provider;
                        if ((provider = SearchProviderController.Companion.getInstance(
                                getContext()).getSearchProvider()) instanceof WebSearchProvider) {
                            List<String> suggestions = ((WebSearchProvider) provider).getSuggestions(
                                    s.toString());
                            Log.d(FeedSearchboxProvider.this.getClass().getName(),
                                    "afterTextChanged: suggestions " + suggestions);
                            editText.post(() -> {
                                adapter.clear();
                                adapter.addAll(suggestions.toArray(new String[0]));
                                adapter.notifyDataSetChanged();
                                predictions.setOnItemClickListener(
                                        (parent1, view, position, id) -> {
                                            int x, y;
                                            x = LawnchairUtilsKt.getPostionOnScreen(
                                                    view).getFirst();
                                            y = LawnchairUtilsKt.getPostionOnScreen(
                                                    view).getSecond();
                                            WebViewScreen.obtain(parent.getContext(), String.format(
                                                    Utilities.getLawnchairPrefs(
                                                            getContext()).getFeedSearchUrl(),
                                                    suggestions.get(position))).display(FeedSearchboxProvider.this, x, y, view);
                                        });
                            });
                        }
                    });
                }
            });
            editText.setDropDownHeight((int) LawnchairUtilsKt.applyAsDip(256f, getContext()));
            editText.setMaxLines(1);
            editText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    int x, y;
                    x = LawnchairUtilsKt.getPostionOnScreen(v).getFirst();
                    y = LawnchairUtilsKt.getPostionOnScreen(v).getSecond();
                    WebViewScreen.obtain(parent.getContext(), String.format(
                            Utilities.getLawnchairPrefs(getContext()).getFeedSearchUrl(),
                            editText.getText().toString())).display(this, x, y, v);
                }
                return true;
            });
            editText.setImeOptions(editText.getImeOptions() | EditorInfo.IME_ACTION_SEARCH);
            return layout;
        }, Card.Companion.getRAISE(),
                "nosort,top", "searchBar".hashCode()));
    }
}
