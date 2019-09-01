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

package ch.deletescape.lawnchair.feed.impl

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import ch.deletescape.lawnchair.feed.images.providers.BitmapCache
import ch.deletescape.lawnchair.feed.images.providers.ImageProvider
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.util.extensions.d
import kotlinx.coroutines.runBlocking

class OverlayService : Service(), () -> Unit {
    lateinit var feed: LauncherFeed;
    val imageProvider by lazy { ImageProvider.inflate(lawnchairPrefs.feedBackground, this) }

    override fun onBind(intent: Intent): IBinder? {
        if (!::feed.isInitialized) {
            this()
        }
        return if (::feed.isInitialized) feed else null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Process.killProcess(Process.myPid());
        onDestroy()
        return true
    }

    override fun invoke() {
        if (imageProvider == null) {
            feed = LauncherFeed(this)
        } else {
            runBlocking {
                feed = LauncherFeed(this@OverlayService, BitmapCache.getBitmap(imageProvider!!, this@OverlayService))
            }
        }
    }

    override fun onDestroy() {
        d("onDestroy: killing overlay process", Throwable())
        super.onDestroy()
    }
}
