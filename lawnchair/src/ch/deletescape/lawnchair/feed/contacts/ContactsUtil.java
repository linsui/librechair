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

package ch.deletescape.lawnchair.feed.contacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ContactsUtil {
    private static String[] projection = new String[]{
            ContactsContract.Profile._ID,
            ContactsContract.Profile.DISPLAY_NAME_PRIMARY,
            ContactsContract.Profile.LOOKUP_KEY,
            ContactsContract.Profile.PHOTO_THUMBNAIL_URI
    };

    public static List<Contact> queryContacts(Context context) {
        if (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return Collections.emptyList();
        }
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Profile.CONTENT_URI, projection, null,
                null);
        List<Contact> contacts = new LinkedList<>();
        for (; !cursor.isAfterLast(); cursor.moveToNext()) {
            Contact contact = new Contact();
            contact.name = cursor.getString(1);
            contacts.add(contact);
        }
        cursor.close();
        return contacts;
    }
}
