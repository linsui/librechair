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

package ch.deletescape.lawnchair.feed.chips.contacts;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.util.List;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.feed.chips.ChipProvider;
import ch.deletescape.lawnchair.feed.contacts.ContactsUtil;

public class ContactsChipProvider extends ChipProvider {
    public ContactsChipProvider(Context context) {

    }

    @Override
    public List<Item> getItems(Context context) {
        return ContactsUtil.queryContacts(context).stream().map(it -> {
            Item item = new Item();
            item.title = it.name;
            if (it.avatar != null) {
                item.icon = RoundedBitmapDrawableFactory.create(context.getResources(), it.avatar);
                ((RoundedBitmapDrawable) item.icon).setCornerRadius(
                        Math.max(it.avatar.getHeight(), it.avatar.getWidth()));
            }
            item.click = () -> {
                try {
                    context.startActivity(it.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            };
            return item;
        }).collect(Collectors.toList());
    }
}
