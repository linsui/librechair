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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.feed.chips.ChipProvider;
import ch.deletescape.lawnchair.feed.impl.OverlayService;
import ch.deletescape.lawnchair.feed.util.FeedUtil;
import ch.deletescape.lawnchair.persistence.ChipPersistence;

public class PredictedAppsProvider extends ChipProvider {
    private final Context context;

    public PredictedAppsProvider(Context context) {
        this.context = context;
    }

    @Override
    public List<Item> getItems(Context context) {
        try {
            return OverlayService.CompanionService.InterfaceHolder.INSTANCE.getPredictions((int) Math.round(
                    ChipPersistence.Companion.getInstance(context).getMaxPredictions())).stream().map(
                    it -> {
                        try {
                            Item item = new Item();
                            item.icon = it.getIcon() != null ? new BitmapDrawable(
                                    context.getResources(), it.getIcon()) :
                                    context.getPackageManager().getActivityIcon(
                                            Objects.requireNonNull(
                                                    it.getComponentKey().componentName));
                            item.title = context.getPackageManager().getActivityInfo(
                                    Objects.requireNonNull(it.getComponentKey().componentName),
                                    0).loadLabel(
                                    context.getPackageManager()).toString();
                            item.viewClickListener = v -> FeedUtil.startActivity(context,
                                    new Intent().setComponent(
                                            it.getComponentKey().componentName), v);
                            return item;
                        } catch (PackageManager.NameNotFoundException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception /* RemoteException */ e) {
            Log.e(getClass().getSimpleName(), "getItems: error retrieving predictions", e);
            return Collections.emptyList();
        }
    }
}
