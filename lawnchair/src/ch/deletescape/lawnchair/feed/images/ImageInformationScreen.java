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

package ch.deletescape.lawnchair.feed.images;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.deletescape.lawnchair.feed.ProviderScreen;

public class ImageInformationScreen extends ProviderScreen {
    private final CharSequence description;

    public ImageInformationScreen(Context base, CharSequence description) {
        super(base);
        this.description = description;
    }

    @Override
    protected View getView(ViewGroup parent) {
        return new TextView(parent.getContext());
    }

    @Override
    protected void bindView(View view) {
        ((TextView) view).setText(description);
        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            view.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        });
    }
}
