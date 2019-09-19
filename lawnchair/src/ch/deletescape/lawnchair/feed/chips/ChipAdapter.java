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
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.InvalidationTracker;

import com.google.android.material.chip.Chip;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChipAdapter extends RecyclerView.Adapter<ChipViewHolder> {
    private List<ChipProvider> providers;
    private List<ChipProvider.Item> items;
    private ChipDao dao;

    public ChipAdapter(Context context) {
        dao = ChipDatabase.Holder.getInstance(context).dao();
        providers = dao.getAll().stream().map(it -> {
            try {
                return (ChipProvider) Class.forName(it.clazz).getConstructor().newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }).filter(it -> it != null).collect(Collectors.toList());
        ChipDatabase.Holder.getInstance(context).getInvalidationTracker().addObserver(
                new InvalidationTracker.Observer("chipprovidercontainer") {
                    @Override
                    public void onInvalidated(@NonNull Set<String> tables) {
                        providers = dao.getAll().stream().map(it -> {
                            try {
                                return (ChipProvider) Class.forName(it.clazz).getConstructor().newInstance();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }).filter(it -> it != null).collect(Collectors.toList());
                        items = providers.stream().map(it -> it.getItems(context)).flatMap(List::stream).collect(Collectors.toList());
                        notifyDataSetChanged();
                    }
                });
        items = providers.stream().map(it -> it.getItems(context)).flatMap(List::stream).collect(Collectors.toList());
        notifyDataSetChanged();
    }

    @Override
    public ChipViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ChipViewHolder(new Chip(viewGroup.getContext()));
    }

    @Override
    public void onBindViewHolder(ChipViewHolder chipViewHolder, int i) {
        ChipProvider.Item item = items.get(i);
        chipViewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
        chipViewHolder.itemView.setText(item.title);
        chipViewHolder.itemView.setChipIcon(item.icon);
        chipViewHolder.itemView.setOnClickListener(v -> item.click.run());
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
