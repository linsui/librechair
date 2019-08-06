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

package ch.deletescape.lawnchair.predictions

import android.app.Service
import android.content.Intent
import android.os.IBinder
import ch.deletescape.lawnchair.LawnchairLauncher
import ch.deletescape.lawnchair.allapps.ContextFreeComponentKeyMapper
import ch.deletescape.lawnchair.allapps.PredictionsProvider
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.Launcher
import com.google.android.apps.nexuslauncher.CustomAppPredictor
import com.google.android.apps.nexuslauncher.NexusLauncherActivity

class PredictionsProviderService : Service() {
    private val service = object : PredictionsProvider.Stub() {
        override fun getPredictions(): List<ContextFreeComponentKeyMapper> {
            d("getPredictions: retrieving predictions")
            return (LawnchairLauncher.getLauncher(
                    applicationContext).userEventDispatcher as CustomAppPredictor).predictions
                    .map { ContextFreeComponentKeyMapper(it) }
                    .also { d("getPredictions: returning $it ") }
        }
    }

    init {
        d("init: predictions initialized")
    }

    override fun onBind(intent: Intent): IBinder {
        d("onBind: returning service $service")
        return service
    }
}
