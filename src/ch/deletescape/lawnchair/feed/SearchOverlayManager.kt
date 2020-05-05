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

package ch.deletescape.lawnchair.feed

import android.content.Intent
import android.net.Uri
import android.os.Process
import ch.deletescape.lawnchair.LawnchairLauncher
import ch.deletescape.lawnchair.util.SingletonHolder
import com.android.launcher3.Utilities
import com.android.overlayclient.client.*

class SearchOverlayManager private constructor(val launcher: LawnchairLauncher) {
    private val factory: CompanionServiceFactory

    var searchClient: SearchClient? = null
        get() = if (factory.supportsUnifiedConnection()) launcher.overlay?.client else field!!

    init {
        factory = object : CompanionServiceFactory(launcher) {
            override fun getService(): Intent {
                val pkg = Utilities.getLawnchairPrefs(launcher).feedProviderPackage
                return Intent("com.android.launcher3.WINDOW_OVERLAY")
                        .setPackage(pkg)
                        .setData(Uri.parse("app://" +
                                launcher.getPackageName() +
                                ":" +
                                Process.myUid())
                                .buildUpon()
                                .appendQueryParameter("v", Integer.toString(7))
                                .appendQueryParameter("cv", Integer.toString(9))
                                .build())
            }
        }

        if (!factory.supportsUnifiedConnection()) {
            searchClient = ServiceClient(launcher, factory,
                    object : OverlayCallback {
                        override fun overlayScrollChanged(progress: Float) {

                        }

                        override fun overlayStatusChanged(status: Int) {

                        }

                    }, Runnable { }, Runnable { },
                    ServiceMode.SEARCH)
        }
    }

    companion object : SingletonHolder<SearchOverlayManager, LawnchairLauncher>(::SearchOverlayManager)
}