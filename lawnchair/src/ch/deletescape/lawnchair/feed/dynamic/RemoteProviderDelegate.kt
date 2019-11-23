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

package ch.deletescape.lawnchair.feed.dynamic

import android.content.Context
import android.content.res.Resources
import ch.deletescape.lawnchair.feed.FeedProviderContainer
import ch.deletescape.lawnchair.feed.METADATA_CONTROLLER_PACKAGE
import ch.deletescape.lawnchair.feed.RemoteFeedProvider

class RemoteProviderDelegate(context: Context) : DynamicProviderDelegate(context) {
    override fun getAvailableContainers(): List<FeedProviderContainer> {
        return RemoteFeedProvider.allProviders(context).map {
            FeedProviderContainer(RemoteFeedProvider::class.qualifiedName,
                    mapOf(RemoteFeedProvider.COMPONENT_KEY to it.flattenToString(),
                            METADATA_CONTROLLER_PACKAGE to it.packageName,
                            RemoteFeedProvider.COMPONENT_CATEGORY to run {
                                try {
                                    return@run context.packageManager.getServiceInfo(
                                            it, 0).metaData?.getString(
                                            RemoteFeedProvider.METADATA_CATEGORY) ?: "other"
                                } catch (e: Resources.NotFoundException) {
                                    return@run "other"
                                } catch (e: ClassCastException) {
                                    e.printStackTrace()
                                    return@run "other"
                                } catch (e: NullPointerException) {
                                    e.printStackTrace()
                                    return@run "other"
                                }
                            }),
                    context.packageManager.getServiceInfo(it, 0).loadLabel(
                            context.packageManager).toString())
        }
    }
}