/*
 *     Copyright (c) 2017-2019 the Lawnchair team
 *     Copyright (c)  2019 oldosfan (would)
 *     This file is part of Lawnchair Launcher.
 *
 *     Lawnchair Launcher is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Lawnchair Launcher is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.feed.images.providers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.SystemClock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.feed.images.nasa.ApodResponse;
import ch.deletescape.lawnchair.feed.images.nasa.ApodRetrofitServiceFactory;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function0;
import retrofit2.Response;

public class ApodImageProvider extends BroadcastReceiver implements ImageProvider {

    private Context context;
    private File cache;

    @SuppressLint("DefaultLocale")
    public ApodImageProvider(Context context) {
        this.context = context;
        this.cache = new File(context.getCacheDir(), String.format("apod_epoch_%d.png",
                TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())));
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        context.registerReceiver(this, filter);
    }

    @Override
    public long getExpiryTime() {
        return TimeUnit.DAYS.toMillis(1);
    }


    @Override
    public Object getBitmap(@NotNull Context context, Continuation<? super Bitmap> o) {
        if (cache.exists()) {
            Bitmap cachedBitmap = BitmapFactory.decodeFile(cache.getAbsolutePath());
            if (cachedBitmap == null) {
                Bitmap map = internalGetBitmap(context);
                try {
                    Objects.requireNonNull(map).compress(CompressFormat.PNG, 100,
                            new FileOutputStream(cache));
                } catch (FileNotFoundException | NullPointerException e) {
                    e.printStackTrace();
                }
                return map;
            } else {
                return cachedBitmap;
            }
        } else {
            Bitmap map = internalGetBitmap(context);
            try {
                map.compress(CompressFormat.PNG, 100, new FileOutputStream(cache));
            } catch (FileNotFoundException | NullPointerException e) {
                e.printStackTrace();
            }
            return map;
        }
    }

    private Bitmap internalGetBitmap(Context context) {
        try {
            Response<ApodResponse> response = ApodRetrofitServiceFactory.manufacture(context).apod().execute();
            assert response.body() != null;
            if (response.body().media_type.equals("image")) {
                return BitmapFactory.decodeStream(new URL(response.body().hdurl).openStream());
            } else {
                return null;
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onReceive(Context context, Intent intent) {
        this.cache = new File(this.context.getCacheDir(),
                String.format("apod_epoch_%d.png",
                        TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())));
    }

    @Override
    public void registerOnChangeListener(@NotNull Function0<Unit> listener) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LawnchairUtilsKt.getMainHandler().postAtTime(this,SystemClock.uptimeMillis() + (ZonedDateTime.now()
                        .plusDays(1)
                        .withHour(0)
                        .withSecond(0)
                        .withMinute(0)
                        .withNano(0)
                        .toEpochSecond() * 1000 - System.currentTimeMillis()));
                listener.invoke();
            }
        };
        LawnchairUtilsKt.getMainHandler().postAtTime(runnable, SystemClock.uptimeMillis() + (ZonedDateTime.now()
                .plusDays(1)
                .withHour(0)
                .withSecond(0)
                .withMinute(0)
                .withNano(0)
                .toEpochSecond() * 1000 - System.currentTimeMillis()));
    }

    @Nullable
    @Override
    public Object getDescription(@NotNull Context context,
                                 @NotNull Continuation<? super String> o) {
        try {
            Response<ApodResponse> response = ApodRetrofitServiceFactory.manufacture(context).apod().execute();
            if (Objects.requireNonNull(response.body()).media_type.equals("image")) {
                return response.body().explanation;
            } else {
                return null;
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    @Override
    public Object getUrl(@NotNull Context context, @NotNull Continuation<? super String> o) {
        try {
            Response<ApodResponse> response = ApodRetrofitServiceFactory.manufacture(context).apod().execute();
            if (Objects.requireNonNull(response.body()).media_type.equals("image")) {
                return response.body().url;
            } else {
                return null;
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
}
