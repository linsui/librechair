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

package ch.deletescape.lawnchair.feed.util;

import android.annotation.AnyThread;
import android.annotation.MainThread;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.util.okhttp.OkHttpClientBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressWarnings("WeakerAccess")
public final class FeedUtil {
    private static volatile OkHttpClient client;
    private static final Object CLIENT_INSTANTIATION_LOCK = new Object();

    private FeedUtil() {
        throw new RuntimeException("putting your time on instantiating this class smh");
    }

    @AnyThread
    public static void download(@Nonnull String url, @Nonnull Context context,
                                @Nonnull Consumer<InputStream> consumer,
                                @Nullable Consumer<IOException> error) {
        synchronized (CLIENT_INSTANTIATION_LOCK) {
            if (client == null) {
                client = new OkHttpClientBuilder().build(context);
            }
        }

        Request request = new Request.Builder()
                .url(url)
                .build();

        if (!request.isHttps()) {
            throw new SecurityException("Mandatory HTTPS requirement not met");
        }

        try {
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    Response response = client.newCall(request).execute();
                    if (response.body() == null) {
                        if (error != null) {
                            error.accept(new IOException());
                        }
                    } else {
                        consumer.accept(Objects.requireNonNull(response.body()).byteStream());
                    }
                } catch (IOException e) {
                    if (error != null) {
                        error.accept(e);
                    }
                }
            });
        } catch (RuntimeException e) {
            Log.e("FeedUtil", "download: fatal error", e);
        }
    }

    @AnyThread
    @Nullable
    public static InputStream downloadDirect(@Nonnull String url, @Nonnull Context context,
                                @Nullable Consumer<IOException> error) {
        synchronized (CLIENT_INSTANTIATION_LOCK) {
            if (client == null) {
                client = new OkHttpClientBuilder().build(context);
            }
        }

        Request request = new Request.Builder()
                .url(url)
                .build();

        if (!request.isHttps()) {
            throw new SecurityException("Mandatory HTTPS requirement not met");
        }

        try {
            Response response = client.newCall(request).execute();
            if (response.body() == null) {
                if (error != null) {
                    error.accept(new IOException());
                }
                return null;
            } else {
                return Objects.requireNonNull(response.body()).byteStream();
            }
        } catch (IOException e) {
            if (error != null) {
                error.accept(e);
            }
        } catch (RuntimeException e) {
            Log.e("FeedUtil", "download: fatal error", e);
        }
        return null;
    }

    @MainThread
    public static void startActivity(@Nonnull Context context, @Nonnull Intent intent,
                                     @Nonnull View view) {
        context.startActivity(
                context instanceof Activity || (intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) != 0 ? intent : new Intent(
                        intent).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.getMeasuredWidth(),
                        view.getMeasuredHeight()).toBundle());
    }

    @MainThread
    public static void openUrl(@Nonnull Context context, @Nonnull String url, @Nonnull View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        try {
            context.startActivity(
                    context instanceof Activity || (intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) != 0 ? intent : new Intent(
                            intent).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.getMeasuredWidth(),
                            view.getMeasuredHeight()).toBundle());
        } catch (ActivityNotFoundException e) {
            Snackbar.make(view, "No application is available for the url " + url,
                    BaseTransientBottomBar.LENGTH_LONG)
                    .show();
        }
    }

    @MainThread
    public static Bitmap dumpViewTreeToBitmap(@Nonnull View view) {
        if (view instanceof SurfaceView) {
            throw new IllegalArgumentException("SurfaceViews are incompatible with this function");
        }
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable background = view.getBackground();

        if (background != null) {
            background.draw(canvas);
        }
        view.draw(canvas);
        return bitmap;
    }

    @MainThread
    public static Bitmap blur(@Nonnull View view) {
        return LawnchairUtilsKt.blur(dumpViewTreeToBitmap(view), view.getContext());
    }

    @AnyThread
    public static <T> T apply(@Nullable T object,
                              @Nonnull Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }
}
