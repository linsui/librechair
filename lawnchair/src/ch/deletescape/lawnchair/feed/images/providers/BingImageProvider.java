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
import ch.deletescape.lawnchair.feed.images.bing.BingResponse;
import ch.deletescape.lawnchair.feed.images.bing.BingRetrofitServiceFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import retrofit2.Response;

public class BingImageProvider extends BroadcastReceiver implements ImageProvider {

    private Context context;
    private File cache;

    @SuppressLint("DefaultLocale")
    public BingImageProvider(Context context) {
        this.context = context;
        this.cache = new File(context.getCacheDir(), String.format("bing_daily_epoch_day_%d.png", TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())));
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        context.registerReceiver(this, filter);
    }

    @Override
    public long getExpiryTime() {
        return TimeUnit.DAYS.toMillis(1);
    }


    @NotNull
    @Override
    public Bitmap getBitmap(@NotNull Context context) {
        if (cache.exists()) {
            return BitmapFactory.decodeFile(cache.getAbsolutePath());
        } else {
            Bitmap map = internalGetBitmap(context);
            try {
                map.compress(CompressFormat.PNG, 100, new FileOutputStream(cache));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return map;
        }
    }

    private Bitmap internalGetBitmap(Context context) {
        try {
            Response<BingResponse> response = BingRetrofitServiceFactory.INSTANCE.getApi(context).getPicOfTheDay().execute();
            if (response.isSuccessful() && response.body() != null && response.body().url != null) {
                return BitmapFactory.decodeStream(new URL(response.body().url).openStream());
            } else {
                return internalGetBitmap(context);
            }
        } catch (IOException e) {
            try {
                Thread.sleep(TimeUnit.MINUTES.toMillis(5));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            return internalGetBitmap(context);
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onReceive(Context context, Intent intent) {
        this.cache = new File(context.getCacheDir(), String.format("bing_daily_epoch_day_%d.png", TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())));
    }
}
