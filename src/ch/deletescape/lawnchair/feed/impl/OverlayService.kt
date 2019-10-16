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
import android.content.pm.ShortcutInfo
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import android.os.Process
import android.service.notification.StatusBarNotification
import ch.deletescape.lawnchair.allapps.ParcelableComponentKeyMapper
import ch.deletescape.lawnchair.feed.FeedScope
import ch.deletescape.lawnchair.feed.images.providers.ImageProvider
import ch.deletescape.lawnchair.feed.notifications.INotificationsChangedListener
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.util.ParcelablePair
import com.android.overlayclient.client.CustomOverscrollClient.ACTIONS_CALL
import com.android.overlayclient.client.CustomOverscrollClient.PREDICTIONS_CALL
import com.android.overlayclient.client.CustomServiceClient
import com.google.android.libraries.launcherclient.ILauncherInterface
import com.google.android.libraries.launcherclient.ILauncherOverlayCompanion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

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
                FeedScope.launch(Dispatchers.IO) {
                    val refreshBitmap = {
                        FeedScope.launch {
                            val bitmap = imageProvider?.getBitmap(this@OverlayService)
                            if (bitmap != null) {
                                it(bitmap)
                            }
                            val desc = imageProvider?.getDescription(this@OverlayService)
                            val rmUrl = imageProvider?.getUrl(this@OverlayService)
                            if (rmUrl != null && desc != null && bitmap != null) {
                                feed.initBitmapInfo(rmUrl, desc, bitmap)
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
            override fun onBackPressed() = feed.onBackPressed()

            override fun attachInterface(interfaze: ILauncherInterface) {
                d("attacheInterface: interface is $interfaze")
                InterfaceHolder.interfaze = interfaze
            }

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

        object InterfaceHolder {
            internal var interfaze: ILauncherInterface? = null
                set(value) = {
                    value?.call(CustomServiceClient.NOTIFICATIONS_CALL, Bundle().apply {
                        putBinder("listener", object : INotificationsChangedListener.Stub() {
                            val notifs = Vector<StatusBarNotification>()
                            override fun notificationRemoved(key: String) {
                                notifs.removeIf { it.key == key }
                            }

                            override fun notificationPosted(notif: StatusBarNotification) {
                                notifs += notif
                            }

                            override fun notificationsChanged(
                                    notifsNew: MutableList<StatusBarNotification>) {
                                notifs.clear()
                                notifs.addAll(notifsNew)
                                notifChangedListener?.invoke(notifs)
                            }
                        })
                    })
                    Unit
                }()
            var notifChangedListener: ((list: List<StatusBarNotification>) -> Unit)? = null
                set(value) {
                    field = value
                    if (value != null) {
                        interfaze?.call(CustomServiceClient.NOTIFICATIONS_CALL, Bundle().apply {
                            putBinder("listener", object : INotificationsChangedListener.Stub() {
                                val notifs = Vector<StatusBarNotification>()
                                override fun notificationRemoved(key: String) {
                                    notifs.removeIf { it.key == key }
                                }

                                override fun notificationPosted(notif: StatusBarNotification) {
                                    notifs += notif
                                }

                                override fun notificationsChanged(
                                        notifsNew: MutableList<StatusBarNotification>) {
                                    notifs.clear()
                                    notifs.addAll(notifsNew)
                                    notifChangedListener?.invoke(notifs)
                                }
                            })
                        })
                    }
                }

            fun getPredictions(): List<ParcelableComponentKeyMapper> = if (interfaze?.supportedCalls?.contains(
                            PREDICTIONS_CALL) == true) interfaze?.call(
                    PREDICTIONS_CALL, null)?.apply {
                classLoader = ParcelableComponentKeyMapper::class.java.classLoader
            }?.getParcelableArrayList(
                    "retval") ?: emptyList() else emptyList()

            fun getActions(): List<ParcelablePair<Bitmap?, ShortcutInfo>> = if (interfaze?.supportedCalls?.contains(
                            ACTIONS_CALL) == true) interfaze?.call(
                    ACTIONS_CALL, null)?.apply {
                classLoader = ParcelablePair::class.java.classLoader
            }?.getParcelableArrayList(
                    "retval") ?: emptyList() else emptyList()
        }
    }
}
