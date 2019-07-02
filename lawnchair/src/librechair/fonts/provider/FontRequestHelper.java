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

package librechair.fonts.provider;

import android.content.Context;
import android.os.Handler;
import android.support.v4.provider.FontRequest;
import android.support.v4.provider.FontsContractCompat.FontRequestCallback;
import java.util.HashMap;

public class FontRequestHelper {
    public static void postFontRequest(Context context, FontRequest request, FontRequestCallback callback, Handler handler) {
        /*
         * TODO: Liberate fonts
         */
    }
    public static void postFontRequest(Context context, HashMap<String, String> request, FontRequestCallback callback, Handler handler) {
        /*
         * TODO: Liberate fonts
         */
    }
}
