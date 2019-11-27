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

package ch.deletescape.lawnchair.feed.chips;

import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.UUID;
import java.util.function.Consumer;

public class ChipItemBridge {
    private final Voodo vh;
    private final UUID bindId;

    ChipItemBridge(Voodo vh, UUID bindId) {
        this.vh = vh;
        this.bindId = bindId;
    }

    public void setIcon(Drawable icns) {
        if (vh.getUUID().equals(bindId)) {
            vh.setIcon(icns);
        }
    }

    public void setTitle(String title) {
        if (vh.getUUID().equals(bindId)) {
            vh.setTitle(title);
        }
    }

    public void setOnClickListener(Consumer<View> onClickListener) {
        if (vh.getUUID().equals(bindId)) {
            vh.setOnClickListener(onClickListener::accept);
        }
    }

    public interface Voodo {
        UUID getUUID();
        void setOnClickListener(View.OnClickListener vocl);
        void setIcon(Drawable icns);
        void setTitle(String title);
    }
}
