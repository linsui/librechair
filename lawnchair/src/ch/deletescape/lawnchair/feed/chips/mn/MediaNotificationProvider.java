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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.android.launcher3.R;
import com.android.launcher3.Utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.function.Consumer;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.feed.chips.ChipItemBridge;
import ch.deletescape.lawnchair.feed.chips.ChipProvider;
import ch.deletescape.lawnchair.feed.notifications.OMCMediaListener;

@SuppressWarnings("ConstantConditions")
public class MediaNotificationProvider extends ChipProvider {
    private final Context context;
    private final List<Consumer<OMCMediaListener.MediaNotificationController>> onMediaNotifChange = new Vector<>();
    private OMCMediaListener mediaListener;
    private Item item;

    public MediaNotificationProvider(Context context) {
        this.context = context;
        this.mediaListener = new OMCMediaListener(context, () -> {
            synchronized (onMediaNotifChange) {
                for (int i = 0; i < onMediaNotifChange.size(); ++i) {
                    if (i < onMediaNotifChange.size()) {
                        onMediaNotifChange.get(i).accept(mediaListener.getTracking());
                    }
                }
            }
        });
        this.item = new Item() {
            @Override
            public void bindVoodo(ChipItemBridge bridge) {
                bridge.setOnClickListener(v -> mediaListener.toggle());
                AnimatedVectorDrawable drawable = (AnimatedVectorDrawable)
                        Objects.requireNonNull(context.getDrawable(R.drawable.play_pause)).mutate();
                bridge.setIcon(drawable);
                OMCMediaListener.MediaNotificationController currentState;
                if ((currentState = mediaListener.getTracking()) != null) {
                    if (currentState.isPlaying()) {
                        drawable.start();
                    } else {
                        if (Utilities.HIDDEN_APIS_ALLOWED) {
                            try {
                                @SuppressWarnings("JavaReflectionMemberAccess") @SuppressLint("SoonBlockedPrivateApi")
                                Method method = AnimatedVectorDrawable.class.getDeclaredMethod(
                                        "reverse");
                                method.invoke(drawable);
                            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (currentState.getInfo().getTitle() != null) {
                        bridge.setTitle(currentState.getInfo().getTitle().toString());
                    } else {
                        bridge.setTitle(context.getString(R.string.title_chip_no_title));
                    }
                } else {
                    if (Utilities.HIDDEN_APIS_ALLOWED) {
                        try {
                            @SuppressWarnings("JavaReflectionMemberAccess") @SuppressLint("SoonBlockedPrivateApi")
                            Method method = AnimatedVectorDrawable.class.getDeclaredMethod(
                                    "reverse");
                            method.invoke(drawable);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    bridge.setTitle(context.getString(R.string.title_nothings_playing));
                }
                Consumer<OMCMediaListener.MediaNotificationController> tc;
                synchronized (onMediaNotifChange) {
                    onMediaNotifChange.add((tc = (tracking -> {
                        if (tracking != null) {
                            if (tracking.isPlaying()) {
                                drawable.start();
                            } else {
                                if (Utilities.HIDDEN_APIS_ALLOWED) {
                                    try {
                                        @SuppressWarnings("JavaReflectionMemberAccess") @SuppressLint("SoonBlockedPrivateApi")
                                        Method method = AnimatedVectorDrawable.class.getDeclaredMethod(
                                                "reverse");
                                        method.invoke(drawable);
                                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            if (tracking.getInfo().getTitle() != null) {
                                bridge.setTitle(tracking.getInfo().getTitle().toString());
                            } else {
                                bridge.setTitle(context.getString(R.string.title_chip_no_title));
                            }
                        } else {
                            if (Utilities.HIDDEN_APIS_ALLOWED) {
                                try {
                                    @SuppressWarnings("JavaReflectionMemberAccess") @SuppressLint("SoonBlockedPrivateApi")
                                    Method method = AnimatedVectorDrawable.class.getDeclaredMethod(
                                            "reverse");
                                    method.invoke(drawable);
                                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                            bridge.setTitle(context.getString(R.string.title_nothings_playing));
                        }
                    })));
                    bridge.onDetach = () -> {
                        synchronized (onMediaNotifChange) {
                            onMediaNotifChange.remove(tc);
                        }
                    };
                    bridge.setGestureDetector(
                            new GestureDetector(context,
                                    new GestureDetector.SimpleOnGestureListener() {
                                        @Override
                                        public void onLongPress(MotionEvent e) {
                                            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE))
                                                    .vibrate(VibrationEffect.createOneShot(50, 127));
                                            if (e.getX() <
                                                    LawnchairUtilsKt
                                                            .getPositionOnScreen(bridge.getView())
                                                            .getFirst() +
                                                            ((float) bridge.getView().getMeasuredWidth()) / 2) {
                                                if (LawnchairUtilsKt.getRtl(
                                                        (ViewGroup) bridge.getView().getParent())) {
                                                    mediaListener.next();
                                                } else {
                                                    mediaListener.previous();
                                                }
                                            } else {
                                                if (!LawnchairUtilsKt.getRtl(
                                                        (ViewGroup) bridge.getView().getParent())) {
                                                    mediaListener.next();
                                                } else {
                                                    mediaListener.previous();
                                                }
                                            }
                                        }
                                    }) {
                                @Override
                                public boolean onTouchEvent(MotionEvent ev) {
                                    getLauncherFeed().getFeedController().setDisallowInterceptCurrentTouchEvent(true);
                                    return super.onTouchEvent(ev);
                                }
                            });
                }
            }
        };
        this.item.title = context.getString(R.string.title_err_chip_unsupported);
    }

    @Override
    public List<Item> getItems(Context context) {
        return Collections.singletonList(item);
    }
}
