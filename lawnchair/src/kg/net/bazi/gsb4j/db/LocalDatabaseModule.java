
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

package kg.net.bazi.gsb4j.db;

import android.database.sqlite.SQLiteDatabase;

import com.google.inject.AbstractModule;

import kg.net.bazi.gsb4j.Gsb4j;
import kg.net.bazi.gsb4j.Gsb4jBinding;

/**
 * Guice module to initialize database related bindings.
 * <p>
 * This module is not supposed to be used directly! To bootstrap, consider bootstrap methods in {@link Gsb4j} or methods
 * that return list of all necessary modules.
 *
 * @author azilet
 */
public class LocalDatabaseModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(LocalDatabase.class).to(SqlLocalDatabase.class);

        bind(SQLiteDatabase.class).annotatedWith(Gsb4jBinding.class)
            .toProvider(DbConnectionProvider.class).asEagerSingleton();
    }
}
