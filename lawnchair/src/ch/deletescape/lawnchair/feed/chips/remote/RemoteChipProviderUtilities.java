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

package ch.deletescape.lawnchair.feed.chips.remote;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.android.launcher3.Utilities;

import java.util.List;
import java.util.stream.Collectors;

public final class RemoteChipProviderUtilities {
    public static final String INTENT_ACTION = "ch.deletescape.lawnchair.feed.chips.CHIP_PROVIDER";

    private RemoteChipProviderUtilities() {
        Utilities.error("This class only has static members, and cannot be instantiated");
    }

    public static List<ComponentName> getRemoteChipProviders(Context context) {
        return context.getPackageManager().queryIntentServices(
                new Intent().setAction(INTENT_ACTION), 0).stream().map(
                resolveInfo -> new ComponentName(resolveInfo.serviceInfo.packageName,
                        resolveInfo.serviceInfo.name)).collect(
                Collectors.toList());
    }
}
