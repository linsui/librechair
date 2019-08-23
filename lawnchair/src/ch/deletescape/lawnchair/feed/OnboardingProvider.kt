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

package ch.deletescape.lawnchair.feed

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import ch.deletescape.lawnchair.settings.ui.SettingsActivity
import com.android.launcher3.R

class OnboardingProvider(c: Context) : FeedProvider(c) {
    override fun onFeedShown() {
    }

    override fun onFeedHidden() {
    }

    override fun onCreate() {
    }

    override fun onDestroy() {
    }

    override fun getCards(): List<Card> {
        return listOf(Card(null, null, { v, _ ->
            LayoutInflater.from(v.context)
                    .inflate(R.layout.card_welcome_feed, v as ViewGroup, false).also {
                        it.findViewById<Button>(R.id.customize_feed).setOnClickListener {
                            Intent(v.context, SettingsActivity::class.java)
                                    .putExtra(SettingsActivity.SubSettingsFragment.TITLE,
                                              v.context.getString(R.string.title_pref_feed))
                                    .putExtra(SettingsActivity.SubSettingsFragment.CONTENT_RES_ID,
                                              R.xml.lawnchair_feed_preferences)
                                    .putExtra(SettingsActivity.SubSettingsFragment.HAS_PREVIEW,
                                              true).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .also { v.context.startActivity(it) }
                        }
                    }
        }, Card.RAISE or Card.NO_HEADER, "nosort,top", "feedWelcome".hashCode()))
    }
}