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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.VectorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.InvalidationTracker;

import com.google.android.material.chip.Chip;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.colors.ColorEngine;
import ch.deletescape.lawnchair.feed.SortingAlgorithm;
import ch.deletescape.lawnchair.feed.chips.sorting.MixerSortHelper;
import ch.deletescape.lawnchair.feed.chips.sorting.NormalSortHelper;
import ch.deletescape.lawnchair.feed.impl.FeedController;
import ch.deletescape.lawnchair.feed.impl.LauncherFeed;
import ch.deletescape.lawnchair.font.CustomFontManager;
import ch.deletescape.lawnchair.persistence.ChipPersistence;
import kotlin.Unit;

import static java.lang.StrictMath.round;

public class ChipAdapter extends RecyclerView.Adapter<ChipViewHolder> implements
        Consumer<List<ChipProvider.Item>> {
    private FeedController controller;
    private List<ChipProvider.Item> items;
    private ChipDao dao;
    private Context context;

    public ChipAdapter(Context context, LauncherFeed feed) {
        items = new ArrayList<>();
        this.context = context;
        ChipController.getInstance(context, feed).subscribe(this);

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent,
                                       @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = round(LawnchairUtilsKt.applyAsDip(4f, context));
                outRect.bottom = round(LawnchairUtilsKt.applyAsDip(4f, context));
                outRect.left = round(LawnchairUtilsKt.applyAsDip(8f, context));
                outRect.right = round(LawnchairUtilsKt.applyAsDip(8f, context));
            }
        });
    }

    @NotNull
    @Override
    public ChipViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ChipViewHolder(new Chip(viewGroup.getContext()));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NotNull ChipViewHolder chipViewHolder, int i) {
        ChipProvider.Item item = items.get(i);
        if (item.icon instanceof VectorDrawable) {
            item.icon = LawnchairUtilsKt.tint(item.icon,
                    ColorEngine.getInstance(context).getResolverCache(
                            ColorEngine.Resolvers.FEED_CHIP).getValue().computeForegroundColor());
        }
        if (ChipPersistence.Companion.getInstance(context).getOutlineChips()) {
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
                        (int) Math.round(ChipPersistence.Companion.getInstance(
                                context).getChipOpacity() * (255f)))));
        chipViewHolder.itemView.setTextColor(
                LawnchairUtilsKt.useWhiteText(ColorEngine.getInstance(context).getResolverCache(
                        ColorEngine.Resolvers.FEED_CHIP).getValue().resolveColor(),
                        context) ? Color.WHITE : Color.BLACK);
        chipViewHolder.itemView.setElevation(LawnchairUtilsKt.applyAsDip(
                (float) ChipPersistence.Companion.getInstance(context).getElevation(), context));
        chipViewHolder.itemView.setOnTouchListener((v, event) -> {
            if (controller != null) {
                controller.setDisallowInterceptCurrentTouchEvent(true);
            }
            return false;
        });
        CustomFontManager.Companion.getInstance(context)
                .loadFont(CustomFontManager.FONT_BUTTON, -1, typeface -> {
                    chipViewHolder.itemView.setTypeface(typeface);
                    return Unit.INSTANCE;
                });
        if (item.click != null || item.viewClickListener != null) {
            chipViewHolder.itemView.setOnClickListener(v -> {
                if (item.click != null) {
                    item.click.run();
                }
                if (item.viewClickListener != null) {
                    item.viewClickListener.accept(v);
                }
            });
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

    @Override
    public void accept(List<ChipProvider.Item> item) {
        this.items.clear();
        if (item != null) {
            this.items.addAll(item);
        }
        notifyDataSetChanged();
    }
}

@SuppressWarnings("WeakerAccess")
class ChipViewHolder extends RecyclerView.ViewHolder {
    public Chip itemView;

    public ChipViewHolder(View itemView) {
        super(itemView);
        this.itemView = (Chip) itemView;
    }
}
