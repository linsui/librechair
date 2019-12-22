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

package ch.deletescape.lawnchair.feed.chips.predict;

import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.util.Log;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.feed.chips.ChipProvider;
import ch.deletescape.lawnchair.feed.impl.OverlayService;

public class PredictedActionsProvider extends ChipProvider {
    private final Context context;

    public PredictedActionsProvider(Context context) {
        this.context = context;
    }

    @Override
    public List<Item> getItems(Context context) {
        try {
            return OverlayService.CompanionService.InterfaceHolder.INSTANCE.getActions(4).stream().map(
                    it -> {
                        Item item = new Item();
                        item.icon = it.first != null ? RoundedBitmapDrawableFactory.create(
                                context.getResources(), it.first) : null;
                        if (item.icon != null) {
                            ((RoundedBitmapDrawable) item.icon).setCornerRadius(
                                    Math.max(it.first.getHeight(), it.first.getWidth()));
                        }
                        try {
                            item.title = Objects.requireNonNull(
                                    it.second.getShortLabel()).toString();
                        } catch (NullPointerException e) {
                            item.title = "";
                        }
                        item.viewClickListener = v -> {
                            try {
                                ((LauncherApps) Objects.requireNonNull(context.getSystemService(
                                        Context.LAUNCHER_APPS_SERVICE)))
                                        .startShortcut(it.second, null,
                                                ActivityOptions.makeClipRevealAnimation(v, 0, 0,
                                                        v.getMeasuredWidth(),
                                                        v.getMeasuredHeight()).toBundle());
                            } catch (ActivityNotFoundException e) {
                                Log.e(getClass().getSimpleName(),
                                        "getItems: failiure starting shortcut", e);
                            }
                        };
                        return item;
                    }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (Exception /* RemoteException */ e) {
            Log.e(getClass().getSimpleName(), "getItems: error retrieving predictions", e);
            return Collections.emptyList();
        }
    }
}
