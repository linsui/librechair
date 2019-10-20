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

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.view.View;

public final class FeedUtil {
    private FeedUtil() {
        throw new RuntimeException("putting your time on instantiating this class smh");
    }

    public static void startActivity(Context context, Intent intent, View view) {
        Rect rect = new Rect();
        int[] absPos = new int[2];
        context.startActivity(
                context instanceof Activity || (intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) != 0 ? intent : new Intent(
                        intent).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.getMeasuredWidth(),
                        view.getMeasuredHeight()).toBundle());
    }
}
