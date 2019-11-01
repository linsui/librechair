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

import android.annotation.MainThread;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import javax.annotation.Nonnull;

public final class FeedUtil {
    private FeedUtil() {
        throw new RuntimeException("putting your time on instantiating this class smh");
    }

    @MainThread
    public static void startActivity(@Nonnull Context context, @Nonnull Intent intent, @Nonnull View view) {
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
            Snackbar.make(view, "No application is available for the url " + url, BaseTransientBottomBar.LENGTH_LONG)
                    .show();
        }
    }
}
