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

package ch.deletescape.lawnchair.shade;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils.TruncateAt;
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController;
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.CardData;
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.Line;
import com.android.launcher3.plugin.PluginManager;
import com.android.launcher3.plugin.unread.UnreadPluginClient;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class CompatSmartspaceProvider extends LawnchairSmartspaceController.DataProvider implements
        UnreadPluginClient.UnreadListener {

    private final UnreadPluginClient client;

    public CompatSmartspaceProvider(
            @NotNull LawnchairSmartspaceController controller) {
        super(controller);
        client = PluginManager.getInstance(controller.getContext())
                .getClient(UnreadPluginClient.class);
        client.setListener(this);
    }

    @Override
    public void forceUpdate() {
        super.forceUpdate();
        if (client.getText() != null && client.getText().size() > 0) {
            updateData(null, new CardData(null,
                    client.getText().stream().map(it -> new Line(it, TruncateAt.END)).collect(
                            Collectors.toList()), v -> client.clickView(0, new Bundle()), false));
        } else {
            updateData(null, null);
        }
    }

    @Override
    public void onChange() {
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            getContext().getMainExecutor().execute(this::forceUpdate);
        } else {
            new Handler(getContext().getMainLooper()).post(this::forceUpdate);
        }
    }
}
