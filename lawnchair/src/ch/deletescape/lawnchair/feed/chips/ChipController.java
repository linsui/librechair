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
import android.annotation.WorkerThread;
import android.content.Context;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.room.InvalidationTracker;

import com.android.launcher3.util.Preconditions;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import android.os.Handler;

import java.util.stream.Collectors;

import ch.deletescape.lawnchair.feed.SortingAlgorithm;
import ch.deletescape.lawnchair.feed.chips.sorting.MixerSortHelper;
import ch.deletescape.lawnchair.feed.chips.sorting.NormalSortHelper;
import ch.deletescape.lawnchair.feed.impl.LauncherFeed;
import ch.deletescape.lawnchair.persistence.ChipPersistence;

@SuppressWarnings("WeakerAccess")
public final class ChipController {
    @SuppressLint("StaticFieldLeak")
    private static ChipController sInstance;
    private final List<WeakReference<Consumer<List<ChipProvider.Item>>>> subscribers;
    private final ChipDao dao;
    private final InvalidationTracker tracker;
    private final Context context;
    private final LauncherFeed feed;


    private List<ChipProvider.Item> items;
    private List<ChipProvider> providers;

    private ChipController(@NonNull Context context, @NonNull LauncherFeed feed) {
        Preconditions.assertNotNull(context);
        Preconditions.assertNotNull(feed);
        this.feed = feed;
        this.subscribers = new LinkedList<>();
        this.context = context;
        this.dao = ChipDatabase.Holder.getInstance(context).dao();
        this.tracker = ChipDatabase.Holder.getInstance(context).getInvalidationTracker();
        this.providers = dao.getAll().stream().map(
                val -> ChipProvider.Cache.get(val, context)).collect(Collectors.toList());
        this.tracker.addWeakObserver(new InvalidationTracker.Observer("chipprovidercontainer") {
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
                providers = dao.getAll().stream().map(
                        val -> ChipProvider.Cache.get(val, context)).collect(Collectors.toList());
                Executors.newSingleThreadExecutor().submit(() -> refresh());
            }
        });
    }

    public static synchronized ChipController getInstance(@NonNull Context context, @NonNull
            LauncherFeed feed) {
        Preconditions.assertNotNull(context);
        Preconditions.assertNotNull(feed);
        return sInstance != null ? sInstance :
                (sInstance = new ChipController(context, feed));
    }

    public void subscribe(@NonNull @MainThread Consumer<List<ChipProvider.Item>> listener) {
        Preconditions.assertNotNull(listener);
        subscribers.add(new WeakReference<>(listener));
        listener.accept(items);
    }

    @WorkerThread
    @SuppressWarnings({"unchecked"})
    public void refresh() {
        SortingAlgorithm<ChipProvider.Item> algo;
        if (ChipPersistence.Companion.getInstance(context).getMixChips()) {
            algo = new MixerSortHelper();
        } else {
            algo = new NormalSortHelper();
        }
        items = algo.sort(providers.stream().filter(
                Objects::nonNull).peek(it -> {
            it.setController(ChipController.this);
            it.setLauncherFeed(feed);
        }).map(it -> it.getItems(context)).toArray(List[]::new));
        new Handler(context.getMainLooper()).post(() -> subscribers.stream().filter(it -> it.get() != null).forEach(
                subscriber -> subscriber.get().accept(items)));
    }
}
