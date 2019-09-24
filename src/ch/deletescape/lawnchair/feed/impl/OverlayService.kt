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
import ch.deletescape.lawnchair.feed.images.providers.ImageProvider
import ch.deletescape.lawnchair.lawnchairPrefs
import com.google.android.libraries.launcherclient.ILauncherOverlayCompanion
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class OverlayService : Service(), () -> Unit {
    companion object {
        lateinit var feed: LauncherFeed
        val feedInitialized
            get() = ::feed.isInitialized
    }

    val imageProvider by lazy { ImageProvider.inflate(lawnchairPrefs.feedBackground, this) }

    override fun onBind(intent: Intent): IBinder? {
        if (!feedInitialized) {
            this()
        }
        return if (feedInitialized) feed else null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return true
    }

    override fun invoke() {
        if (imageProvider == null) {
            feed = LauncherFeed(this)
        } else {
            feed = LauncherFeed(this@OverlayService) {
                GlobalScope.launch {
                    val refreshBitmap = {
                        GlobalScope.launch {
                            val bitmap = imageProvider?.getBitmap(this@OverlayService)
                            val desc = imageProvider?.getDescription(this@OverlayService)
                            val rmUrl = imageProvider?.getUrl(this@OverlayService)
                            if (rmUrl != null) {
                                feed.readMoreUrl = rmUrl
                            }
                            if (bitmap != null) {
                                it(bitmap)
                            }
                            if (desc != null) {
                                feed.infobox.text = desc
                            }
                        }
                        Unit
                    }
                    refreshBitmap()
                    imageProvider?.registerOnChangeListener(refreshBitmap)
                }
            }
        }
    }

    class CompanionService : Service() {
        override fun onBind(intent: Intent?): IBinder? = object : ILauncherOverlayCompanion.Stub() {
            override fun restartProcess() {
                Process.killProcess(Process.myPid())
            }

            override fun shouldFadeWorkspaceDuringScroll(): Boolean {
                return true
            }

            override fun shouldScrollWorkspace(): Boolean {
                if (feedInitialized) {
                    return feed.feedController.animationDelegate.shouldScroll
                } else {
                    return true
                }
            }

        }
    }
}
