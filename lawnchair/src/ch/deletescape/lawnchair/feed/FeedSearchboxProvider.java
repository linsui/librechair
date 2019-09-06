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
import android.text.InputType;
import android.util.Log;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;

import com.android.launcher3.R;
import com.android.launcher3.Utilities;

import java.util.Collections;
import java.util.List;

import ch.deletescape.lawnchair.LawnchairUtilsKt;

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

    @Override
    public List<Card> getCards() {
        Log.d(getClass().getName(), "getCards: retrieving cards");
        return Collections.singletonList(new Card(
                LawnchairUtilsKt.tint(getContext().getDrawable(R.drawable.ic_search),
                        FeedAdapter.Companion.getOverrideColor(getContext())),
                getContext().getString(R.string.search), parent -> {
            EditText editText = new EditText(parent.getContext());
            editText.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            editText.setInputType(editText.getInputType() & ~InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            editText.setMaxLines(1);
            editText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Utilities.openURLinBrowser(getContext(), String.format(
                            Utilities.getLawnchairPrefs(getContext()).getFeedSearchUrl(),
                            editText.getText().toString()));
                }
                return true;
            });
            editText.setImeOptions(editText.getImeOptions() | EditorInfo.IME_ACTION_SEARCH);
            return editText;
        }, Card.Companion.getRAISE(),
                "nosort,top", "searchBar".hashCode()));
    }
}
