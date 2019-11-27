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

package ch.deletescape.lawnchair.feed.chips.mn;

import android.content.Context;

import com.android.launcher3.R;

import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

import ch.deletescape.lawnchair.feed.chips.ChipItemBridge;
import ch.deletescape.lawnchair.feed.chips.ChipProvider;
import ch.deletescape.lawnchair.feed.notifications.OMCMediaListener;

public class MediaNotificationProvider extends ChipProvider {
    private final Context context;
    private final List<Consumer<OMCMediaListener.MediaNotificationController>> onMediaNotifChange = new Vector<>();
    private OMCMediaListener mediaListener;
    private Item item;

    public MediaNotificationProvider(Context context) {
        this.context = context;
        this.mediaListener = new OMCMediaListener(context,
                () -> onMediaNotifChange.forEach(it -> it.accept(mediaListener.getTracking())));
        this.item = new Item() {
            @Override
            public void bindVoodo(ChipItemBridge bridge) {
                bridge.setOnClickListener(v -> mediaListener.toggle());
                OMCMediaListener.MediaNotificationController currentState;
                if ((currentState = mediaListener.getTracking()) != null) {
                    if (currentState.isPlaying()) {
                        bridge.setIcon(context.getDrawable(R.drawable.ic_pause_black_24dp));
                    } else {
                        bridge.setIcon(context.getDrawable(R.drawable.ic_play_arrow_black_24dp));
                    }
                    bridge.setTitle(currentState.getInfo().getTitle().toString());
                } else {
                    bridge.setIcon(context.getDrawable(R.drawable.ic_play_arrow_black_24dp));
                    bridge.setTitle(context.getString(R.string.title_nothings_playing));
                }
                onMediaNotifChange.add(tracking -> {
                    if (tracking != null) {
                        if (tracking.isPlaying()) {
                            bridge.setIcon(context.getDrawable(R.drawable.ic_pause_black_24dp));
                        } else {
                            bridge.setIcon(context.getDrawable(R.drawable.ic_play_arrow_black_24dp));
                        }
                        bridge.setTitle(tracking.getInfo().getTitle().toString());
                    } else {
                        bridge.setIcon(context.getDrawable(R.drawable.ic_play_arrow_black_24dp));
                        bridge.setTitle(context.getString(R.string.title_nothings_playing));
                    }
                });
            }
        };
        this.item.title = context.getString(R.string.title_err_chip_unsupported);
    }

    @Override
    public List<Item> getItems(Context context) {
        return null;
    }
}
