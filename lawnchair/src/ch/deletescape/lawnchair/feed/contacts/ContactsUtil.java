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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.android.launcher3.R;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public class ContactsUtil {
    private static String[] projection = new String[]{
            ContactsContract.Data._ID,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.Data.LOOKUP_KEY,
            ContactsContract.Data.PHOTO_THUMBNAIL_URI,
            ContactsContract.Data.CONTACT_ID
    };

    public static List<Contact> queryContacts(Context context) {
        if (context.checkSelfPermission(
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return Collections.emptyList();
        }
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Data.CONTENT_URI, projection, null,
                null);
        LinkedHashSet<Contact> contacts = new LinkedHashSet<>();
        Log.d(ContactsUtil.class.getSimpleName(), "queryContacts: contacts cursor: " + cursor);
        assert cursor != null;
        while (cursor.moveToNext()) {
            Log.d(ContactsUtil.class.getSimpleName(), "queryContacts loading contact: " + cursor);
            Contact contact = new Contact();
            contact.lbcLookupKey = String.valueOf(cursor.getInt(4));
            try {
                contact.intent = new Intent(Intent.ACTION_VIEW,
                        Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(cursor.getInt(4))));
                contact.name = cursor.getString(1);
                try {
                    contact.avatar = getAvatar(cursor.getString(3), context);
                } catch (RuntimeException ignored) {

                }
            } catch (RuntimeException e) {
                contact = null;
            }
            if (contact != null) {
                contacts.add(contact);
            }
        }
        cursor.close();
        return new LinkedList<>(contacts);
    }

    public static Bitmap getAvatar(String address, Context context) {
        Bitmap contactAvatar = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.work_tab_user_education);
        Cursor phones = context.getContentResolver().query(Uri.parse(address),
                null, null, null, null);
        try {
            while (phones.moveToNext()) {
                String imageUri = phones.getString(phones.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                if (imageUri != null) {
                    try {
                        contactAvatar = MediaStore.Images.Media
                                .getBitmap(context.getContentResolver(),
                                        Uri.parse(imageUri));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return contactAvatar;
    }
}
