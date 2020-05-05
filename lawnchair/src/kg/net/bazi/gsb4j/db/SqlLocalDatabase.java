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
/*
 * Copyright 2018 Azilet B.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kg.net.bazi.gsb4j.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import kg.net.bazi.gsb4j.Gsb4jBinding;
import kg.net.bazi.gsb4j.data.ThreatListDescriptor;

/**
 * SQL database backed implementation of {@link LocalDatabase}.
 *
 * @author azilet
 */
class SqlLocalDatabase implements LocalDatabase {

    static final int BATCH_SIZE = 50 * 1000;

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlLocalDatabase.class);

    private static final Set<ThreatListDescriptor> CREATED_TABLES = new HashSet<>();
    private static final Lock LOCK = new ReentrantLock();

    @Inject
    @Gsb4jBinding
    SQLiteDatabase database;

    @Override
    public List<String> load(ThreatListDescriptor descriptor) throws IOException {
        checkTableForDescriptor(descriptor);

        String sql = "SELECT prefix FROM " + descriptor + " ORDER BY prefix";
        try (Cursor rs = database.rawQuery(sql, new String[0])) {
            List<String> result = new LinkedList<>();
            while (rs.moveToNext()) {
                result.add(rs.getString(1));
            }
            LOGGER.info("Loaded {} items", result.size());
            return result;
        }
    }

    @Override
    public void persist(ThreatListDescriptor descriptor, List<String> hashes) throws IOException {
        checkTableForDescriptor(descriptor);

        Log.d(getClass().getName(), "persist: " + hashes);

        String sql = "INSERT INTO " + descriptor + " VALUES (?)";
        int offset = 0;
        int inserted = 0;
        database.beginTransaction();
        for (String hash : hashes) {
            database.execSQL(sql, new Object[]{hash});
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    @Override
    public boolean contains(String hash, ThreatListDescriptor descriptor) throws IOException {
        checkTableForDescriptor(descriptor);

        String sql = "SELECT prefix FROM " + descriptor + " WHERE prefix=(?)";
        Cursor cursor = database.rawQuery(sql, new String[] {hash});
        boolean ret = cursor.moveToNext();
        cursor.close();
        return ret;
    }

    @Override
    public void clear(ThreatListDescriptor descriptor) throws IOException {
        database.beginTransaction();
        database.execSQL("DROP TABLE IF EXISTS " + descriptor, new String[0]);
        database.setTransactionSuccessful();
        database.endTransaction();
        LOCK.lock();
        try {
            CREATED_TABLES.remove(descriptor);
        } finally {
            LOCK.unlock();
        }
        LOGGER.info("Table {} dropped", descriptor);
    }

    private void checkTableForDescriptor(ThreatListDescriptor descriptor) throws IOException {
        if (CREATED_TABLES.contains(descriptor)) {
            return;
        }
        Lock ref = LOCK;
        ref.lock();
        try {
            if (!CREATED_TABLES.contains(descriptor)) {
                createTable(descriptor);
                CREATED_TABLES.add(descriptor);
            }
        } finally {
            ref.unlock();
        }
    }

    private void createTable(ThreatListDescriptor descriptor) {
        database.beginTransaction();
        String sql = "CREATE TABLE IF NOT EXISTS " + descriptor
                + " (prefix TEXT CONSTRAINT pk PRIMARY KEY ASC ON CONFLICT REPLACE)";
        database.execSQL(sql);
        database.setTransactionSuccessful();
        database.endTransaction();
    }

}
