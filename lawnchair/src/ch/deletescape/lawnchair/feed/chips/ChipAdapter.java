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

package ch.deletescape.lawnchair.feed.chips;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.InvalidationTracker;

import com.google.android.material.chip.Chip;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.colors.ColorEngine;
import ch.deletescape.lawnchair.feed.FeedAdapter;
import ch.deletescape.lawnchair.feed.chips.battery.BatteryMeterDrawableBase;
import ch.deletescape.lawnchair.feed.impl.FeedController;
import ch.deletescape.lawnchair.font.CustomFontManager;
import ch.deletescape.lawnchair.persistence.FeedPersistence;
import kotlin.Unit;

public class ChipAdapter extends RecyclerView.Adapter<ChipViewHolder> {
    private List<ChipProvider> providers;
    private boolean dark;
    private FeedController controller;
    private List<ChipProvider.Item> items;
    private ChipDao dao;
    private Context context;

    public ChipAdapter(Context context, boolean dark) {
        dao = ChipDatabase.Holder.getInstance(context).dao();
        this.context = context;
        providers = dao.getAll().stream().map(val -> ChipProvider.Cache.get(val, context)).filter(
                it -> it != null).collect(Collectors.toList());
        providers.forEach(it -> it.setAdapter(ChipAdapter.this));
        this.dark = dark;
        ChipDatabase.Holder.getInstance(context).getInvalidationTracker().addObserver(
                new InvalidationTracker.Observer("chipprovidercontainer") {
                    @Override
                    public void onInvalidated(@NonNull Set<String> tables) {
                        providers = dao.getAll().stream().map(
                                val -> ChipProvider.Cache.get(val, context)).filter(
                                it -> it != null).collect(Collectors.toList());
                        providers.forEach(it -> it.setAdapter(ChipAdapter.this));
                        items = providers.stream().map(it -> it.getItems(context)).flatMap(
                                List::stream).collect(Collectors.toList());
                        notifyDataSetChanged();
                    }
                });
        items = providers.stream().map(it -> it.getItems(context)).flatMap(List::stream).collect(
                Collectors.toList());
        notifyDataSetChanged();
    }

    public void rebindData() {
        items = providers.stream().map(it -> it.getItems(context)).flatMap(List::stream).collect(
                Collectors.toList());
    }

    @Override
    public ChipViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ChipViewHolder(new Chip(viewGroup.getContext()));
    }

    @Override
    public void onBindViewHolder(ChipViewHolder chipViewHolder, int i) {
        ChipProvider.Item item = items.get(i);
        item.icon = item.icon == null ? null : LawnchairUtilsKt.tint(item.icon,
                FeedAdapter.Companion.getOverrideColor(context));
        if (FeedPersistence.Companion.getInstance(context).getOutlineChips()) {
            chipViewHolder.itemView.setChipStrokeColor(
                    ColorStateList.valueOf(ColorEngine.getInstance(context).getResolverCache(
                            ColorEngine.Resolvers.FEED_CHIP).getValue().resolveColor()));
            chipViewHolder.itemView.setChipStrokeWidth(LawnchairUtilsKt.applyAsDip(1f, context));
        } else {
            chipViewHolder.itemView.setChipStrokeWidth(0f);
        }
        chipViewHolder.itemView.setText(item.title);
        chipViewHolder.itemView.setChipIcon(item.icon);
        chipViewHolder.itemView.setChipBackgroundColor(ColorStateList.valueOf(
                ColorUtils.setAlphaComponent(ColorEngine.getInstance(context).getResolverCache(
                        ColorEngine.Resolvers.FEED_CHIP).getValue().resolveColor(),
                        (int) Math.round(FeedPersistence.Companion.getInstance(
                                context).getChipOpacity() * (255f)))));
        chipViewHolder.itemView.setTextColor(
                LawnchairUtilsKt.useWhiteText(ColorEngine.getInstance(context).getResolverCache(
                        ColorEngine.Resolvers.FEED_CHIP).getValue().resolveColor(),
                        context) ? Color.WHITE : Color.BLACK);
        chipViewHolder.itemView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                ((RecyclerView.LayoutParams) chipViewHolder.itemView.getLayoutParams()).leftMargin = (int) LawnchairUtilsKt.applyAsDip(
                        8f, context);
                ((RecyclerView.LayoutParams) chipViewHolder.itemView.getLayoutParams()).rightMargin = (int) LawnchairUtilsKt.applyAsDip(
                        8f, context);
                ((RecyclerView.LayoutParams) chipViewHolder.itemView.getLayoutParams()).topMargin = (int) LawnchairUtilsKt.applyAsDip(
                        4f, context);
                if (item.icon instanceof BatteryMeterDrawableBase) {
                    ((BatteryMeterDrawableBase) item.icon).postInvalidate();
                }
                v.requestLayout();
            }
        });
        chipViewHolder.itemView.setOnTouchListener((v, event) -> {
            if (controller != null) {
                controller.setDisallowInterceptTouchEventsUntilNextUp(true);
            }
            return false;
        });
        CustomFontManager.Companion.getInstance(context)
                .loadFont(CustomFontManager.FONT_BUTTON, -1, typeface -> {
                    chipViewHolder.itemView.setTypeface(typeface);
                    return Unit.INSTANCE;
                });
        if (item.click != null) {
            chipViewHolder.itemView.setOnClickListener(v -> item.click.run());
        } else {
            chipViewHolder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setController(FeedController controller) {
        this.controller = controller;
    }
}

class ChipViewHolder extends RecyclerView.ViewHolder {
    public Chip itemView;

    public ChipViewHolder(View itemView) {
        super(itemView);
        this.itemView = (Chip) itemView;
    }
}
