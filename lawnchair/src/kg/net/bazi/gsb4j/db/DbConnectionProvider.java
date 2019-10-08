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

import android.database.sqlite.SQLiteDatabase;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import kg.net.bazi.gsb4j.properties.Gsb4jProperties;

/**
 * Data source provider for SQL local database.
 *
 * @author azilet
 */
class DbConnectionProvider implements Provider<SQLiteDatabase> {

    private SQLiteDatabase database;

    @Inject
    DbConnectionProvider(Gsb4jProperties properties) {
        Path dataDir = properties.getDataDirectory();
        if (!dataDir.toFile().exists()) {
            try {
                Files.createDirectories(dataDir);
            } catch (IOException ex) {
                throw new ProvisionException("Failed to create data directory", ex);
            }
        }

        this.database = SQLiteDatabase.openOrCreateDatabase(
                new File(dataDir.toFile(), "gsb4j.db"), null);
    }

    @Override
    public SQLiteDatabase get() {
        return database;
    }
}
