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

package io.weatherbit.api;

import io.weatherbase.api.model.Error;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BulkDownloadsApi {
  /**
   * Download pre-generated bulk datasets
   * **(Advanceed/Enterprise plans only)** Downloads bulk data files - OPTIONS: ( current.json.gz - Current observations for cities &gt; 1000 population). Units are Metric (Celcius, m/s, etc).
   * @param file Filename (ie. current.json.gz) (required)
   * @param key Your registered API key. (required)
   * @return Call&lt;Error&gt;
   */
  @GET("bulk/files/{file}")
  Call<Error> bulkFilesFileGet(

          @Path("file") String file, @Query("key") String key

  );

}
