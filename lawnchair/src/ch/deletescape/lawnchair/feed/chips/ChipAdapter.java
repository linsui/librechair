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
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.InvalidationTracker;

import com.android.launcher3.R;
import com.google.android.material.chip.Chip;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.feed.FeedAdapter;

public class ChipAdapter extends RecyclerView.Adapter<ChipViewHolder> {
    private List<ChipProvider> providers;
    private boolean dark;
    private List<ChipProvider.Item> items;
    private ChipDao dao;
    private Context context;

    public ChipAdapter(Context context, boolean dark) {
        dao = ChipDatabase.Holder.getInstance(context).dao();
        this.context = context;
        providers = dao.getAll().stream().map(val -> ChipProvider.Cache.get(val, context)).filter(
                it -> it != null).collect(Collectors.toList());
        this.dark = dark;
        ChipDatabase.Holder.getInstance(context).getInvalidationTracker().addObserver(
                new InvalidationTracker.Observer("chipprovidercontainer") {
                    @Override
                    public void onInvalidated(@NonNull Set<String> tables) {
                        providers = dao.getAll().stream().map(
                                val -> ChipProvider.Cache.get(val, context)).filter(
                                it -> it != null).collect(Collectors.toList());
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
        chipViewHolder.itemView.setText(item.title);
        chipViewHolder.itemView.setChipIcon(item.icon);
        chipViewHolder.itemView.setChipBackgroundColor(ColorStateList.valueOf(
                dark ? context.getColor(R.color.qsb_background_dark) : context.getColor(
                        R.color.qsb_background)));
        chipViewHolder.itemView.setTextColor(dark ? Color.WHITE : Color.BLACK);
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
                v.requestLayout();
            }
        });
        chipViewHolder.itemView.setOnTouchListener((v, event) -> {
            v.getParent().getParent().requestDisallowInterceptTouchEvent(true);
            v.getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
            return false;
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
}

class ChipViewHolder extends RecyclerView.ViewHolder {
    public Chip itemView;

    public ChipViewHolder(View itemView) {
        super(itemView);
        this.itemView = (Chip) itemView;
    }
}
