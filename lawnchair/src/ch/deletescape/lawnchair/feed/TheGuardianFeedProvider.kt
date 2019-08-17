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
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import org.apache.commons.io.IOUtils
import org.apache.commons.io.input.CharSequenceInputStream
import org.xml.sax.InputSource
import java.net.URL
import java.nio.charset.Charset

class TheGuardianFeedProvider(c: Context) : AbstractLocationAwareRSSProvider(c) {
    companion object {
        val feeds = mapOf("AFG" to "http://www.theguardian.com/world/afghanistan",
                          "ALB" to "http://www.theguardian.com/world/albania",
                          "DZA" to "http://www.theguardian.com/world/algeria",
                          "AND" to "http://www.theguardian.com/world/andorra",
                          "AGO" to "http://www.theguardian.com/world/angola",
                          "ATG" to "http://www.theguardian.com/world/antigua-barbuda",
                          "BLR" to "http://www.theguardian.com/world/belarus",
                          "" to "http://www.theguardian.com/world/belgium",
                          "" to "http://www.theguardian.com/world/belize",
                          "" to "http://www.theguardian.com/world/benin",
                          "" to "http://www.theguardian.com/world/bhutan",
                          "" to "http://www.theguardian.com/world/bolivia",
                          "" to "http://www.theguardian.com/world/bosnia-and-herzegovina",
                          "" to "http://www.theguardian.com/world/botswana",
                          "" to "http://www.theguardian.com/world/brazil",
                          "" to "http://www.theguardian.com/world/brunei",
                          "" to "http://www.theguardian.com/world/bulgaria",
                          "" to "http://www.theguardian.com/world/burkina-faso",
                          "" to "http://www.theguardian.com/world/burundi",
                          "" to "http://www.theguardian.com/world/cambodia",
                          "" to "http://www.theguardian.com/world/cameroon",
                          "" to "http://www.theguardian.com/world/canada",
                          "" to "http://www.theguardian.com/world/cape-verde",
                          "" to "http://www.theguardian.com/world/central-african-republic",
                          "" to "http://www.theguardian.com/world/chad",
                          "" to "http://www.theguardian.com/world/chile",
                          "CHN" to "http://www.theguardian.com/world/china",
                          "" to "http://www.theguardian.com/world/colombia",
                          "" to "http://www.theguardian.com/world/dominica",
                          "" to "http://www.theguardian.com/world/dominicanrepublic",
                          "" to "http://www.theguardian.com/world/ecuador",
                          "" to "http://www.theguardian.com/world/egypt",
                          "" to "http://www.theguardian.com/world/el-salvador",
                          "" to "http://www.theguardian.com/world/equatorial-guinea",
                          "" to "http://www.theguardian.com/world/eritrea",
                          "" to "http://www.theguardian.com/world/estonia",
                          "ETH" to "http://www.theguardian.com/world/ethiopia",
                          "" to "http://www.theguardian.com/world/fiji",
                          "" to "http://www.theguardian.com/world/finland",
                          "" to "http://www.theguardian.com/world/france",
                          "" to "http://www.theguardian.com/world/gabon",
                          "" to "http://www.theguardian.com/world/gambia",
                          "" to "http://www.theguardian.com/world/georgia-news",
                          "" to "http://www.theguardian.com/world/germany",
                          "" to "http://www.theguardian.com/world/ghana",
                          "" to "http://www.theguardian.com/world/greece",
                          "" to "http://www.theguardian.com/world/grenada",
                          "" to "http://www.theguardian.com/world/guatemala",
                          "" to "http://www.theguardian.com/world/guinea",
                          "" to "http://www.theguardian.com/world/guinea-bissau",
                          "" to "http://www.theguardian.com/world/guyana",
                          "" to "http://www.theguardian.com/world/haiti",
                          "" to "http://www.theguardian.com/world/honduras",
                          "" to "http://www.theguardian.com/world/hungary",
                          "" to "http://www.theguardian.com/world/iceland",
                          "IND" to "http://www.theguardian.com/world/india",
                          "" to "http://www.theguardian.com/world/indonesia",
                          "" to "http://www.theguardian.com/world/iran",
                          "" to "http://www.theguardian.com/world/iraq",
                          "" to "http://www.theguardian.com/world/ireland",
                          "" to "http://www.theguardian.com/world/israel",
                          "" to "http://www.theguardian.com/world/italy",
                          "" to "http://www.theguardian.com/world/ivory-coast",
                          "" to "http://www.theguardian.com/world/jamaica",
                          "" to "http://www.theguardian.com/world/japan",
                          "" to "http://www.theguardian.com/world/jordan",
                          "" to "http://www.theguardian.com/world/kazakhstan",
                          "" to "http://www.theguardian.com/world/kenya",
                          "" to "http://www.theguardian.com/world/kiribati",
                          "" to "http://www.theguardian.com/world/kosovo",
                          "" to "http://www.theguardian.com/world/kuwait",
                          "" to "http://www.theguardian.com/world/kyrgyzstan",
                          "" to "http://www.theguardian.com/world/laos",
                          "" to "http://www.theguardian.com/world/latvia",
                          "" to "http://www.theguardian.com/world/lebanon",
                          "" to "http://www.theguardian.com/world/lesotho",
                          "" to "http://www.theguardian.com/world/mauritius",
                          "" to "http://www.theguardian.com/world/mexico",
                          "" to "http://www.theguardian.com/world/micronesia",
                          "" to "http://www.theguardian.com/world/moldova",
                          "" to "http://www.theguardian.com/world/monaco",
                          "" to "http://www.theguardian.com/world/mongolia",
                          "" to "http://www.theguardian.com/world/montenegro",
                          "" to "http://www.theguardian.com/world/morocco",
                          "" to "http://www.theguardian.com/world/mozambique",
                          "" to "http://www.theguardian.com/world/namibia",
                          "" to "http://www.theguardian.com/world/nauru",
                          "" to "http://www.theguardian.com/world/nepal",
                          "" to "http://www.theguardian.com/world/netherlands",
                          "" to "http://www.theguardian.com/world/newzealand",
                          "" to "http://www.theguardian.com/world/nicaragua",
                          "" to "http://www.theguardian.com/world/niger",
                          "" to "http://www.theguardian.com/world/nigeria",
                          "PRK" to "http://www.theguardian.com/world/north-korea",
                          "" to "http://www.theguardian.com/world/norway",
                          "" to "http://www.theguardian.com/world/oman",
                          "" to "http://www.theguardian.com/world/pakistan",
                          "" to "http://www.theguardian.com/world/palau",
                          "" to "http://www.theguardian.com/world/peru",
                          "" to "http://www.theguardian.com/world/philippines",
                          "" to "http://www.theguardian.com/world/poland",
                          "" to "http://www.theguardian.com/world/portugal",
                          "" to "http://www.theguardian.com/world/qatar",
                          "" to "http://www.theguardian.com/world/romania",
                          "" to "http://www.theguardian.com/world/russia",
                          "" to "http://www.theguardian.com/world/rwanda",
                          "" to "http://www.theguardian.com/world/samoa",
                          "" to "http://www.theguardian.com/world/san-marino",
                          "" to "http://www.theguardian.com/world/sao-tome-and-principe",
                          "" to "http://www.theguardian.com/world/saudiarabia",
                          "" to "http://www.theguardian.com/world/senegal",
                          "" to "http://www.theguardian.com/world/serbia",
                          "" to "http://www.theguardian.com/world/seychelles",
                          "" to "http://www.theguardian.com/world/sierraleone",
                          "" to "http://www.theguardian.com/world/singapore",
                          "" to "http://www.theguardian.com/world/slovakia",
                          "" to "http://www.theguardian.com/world/slovenia",
                          "" to "http://www.theguardian.com/world/solomonislands",
                          "" to "http://www.theguardian.com/world/somalia",
                          "" to "http://www.theguardian.com/world/southafrica",
                          "KOR" to "http://www.theguardian.com/world/south-korea",
                          "" to "http://www.theguardian.com/world/south-sudan",
                          "" to "http://www.theguardian.com/world/spain",
                          "" to "http://www.theguardian.com/world/srilanka",
                          "" to "http://www.theguardian.com/world/saint-kitts-and-nevis",
                          "" to "http://www.theguardian.com/world/stlucia",
                          "" to "http://www.theguardian.com/world/saint-vincent-and-the-grenadines",
                          "" to "http://www.theguardian.com/world/sudan",
                          "" to "http://www.theguardian.com/world/suriname",
                          "" to "http://www.theguardian.com/world/swaziland",
                          "" to "http://www.theguardian.com/world/sweden",
                          "" to "http://www.theguardian.com/world/switzerland",
                          "" to "http://www.theguardian.com/world/syria",
                          "" to "http://www.theguardian.com/world/taiwan",
                          "" to "http://www.theguardian.com/world/tajikistan",
                          "" to "http://www.theguardian.com/world/tanzania",
                          "" to "http://www.theguardian.com/world/thailand",
                          "" to "http://www.theguardian.com/world/timor-leste",
                          "" to "http://www.theguardian.com/world/togo",
                          "" to "http://www.theguardian.com/world/tonga",
                          "" to "http://www.theguardian.com/world/trinidad-and-tobago",
                          "" to "http://www.theguardian.com/world/tunisia",
                          "" to "http://www.theguardian.com/world/turkey",
                          "" to "http://www.theguardian.com/world/turkmenistan",
                          "" to "http://www.theguardian.com/world/tuvalu",
                          "" to "http://www.theguardian.com/world/uganda",
                          "" to "http://www.theguardian.com/world/ukraine",
                          "GBR" to "https://www.theguardian.com/uk/rss",
                          "" to "http://www.theguardian.com/world/united-arab-emirates",
                          "" to "http://www.theguardian.com/world/uruguay",
                          "" to "http://www.theguardian.com/world/uzbekistan",
                          "" to "http://www.theguardian.com/world/vanuatu",
                          "" to "http://www.theguardian.com/world/venezuela",
                          "" to "http://www.theguardian.com/world/vietnam",
                          "" to "http://www.theguardian.com/world/yemen",
                          "" to "http://www.theguardian.com/world/zambia",
                          "" to "http://www.theguardian.com/world/zimbabwe")
    }

    override fun getLocationAwareFeed(location: Pair<Double, Double>, country: String): SyndFeed {
        val feed = IOUtils.toString(
                URL("https://www.theguardian.com/uk/rss").openConnection().getInputStream(),
                Charset.defaultCharset())
        return SyndFeedInput()
                .build(InputSource(CharSequenceInputStream(feed, Charset.defaultCharset())))
    }

    override fun getFallbackFeed(): SyndFeed {
        return getLocationAwareFeed(0.toDouble() to 0.toDouble(), "GBR")
    }
}
