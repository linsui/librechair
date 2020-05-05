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
import ch.deletescape.lawnchair.feed.util.FeedUtil
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import org.xml.sax.InputSource
import java.io.IOException

class TheGuardianFeedProvider(c: Context) : AbstractLocationAwareRSSProvider(c) {
    companion object {
        val feeds = mapOf("AFG" to "https://www.theguardian.com/world/afghanistan",
                "ALB" to "https://www.theguardian.com/world/albania",
                "DZA" to "https://www.theguardian.com/world/algeria",
                "AND" to "https://www.theguardian.com/world/andorra",
                "AGO" to "https://www.theguardian.com/world/angola",
                "ATG" to "https://www.theguardian.com/world/antigua-barbuda",
                "BLR" to "https://www.theguardian.com/world/belarus",
                "BEL" to "https://www.theguardian.com/world/belgium",
                "BLZ" to "https://www.theguardian.com/world/belize",
                "BMU" to "https://www.theguardian.com/world/benin",
                "BTN" to "https://www.theguardian.com/world/bhutan",
                "BOL" to "https://www.theguardian.com/world/bolivia",
                "BIH" to "https://www.theguardian.com/world/bosnia-and-herzegovina",
                "BWA" to "https://www.theguardian.com/world/botswana",
                "BRA" to "https://www.theguardian.com/world/brazil",
                "BRN" to "https://www.theguardian.com/world/brunei",
                "BGR" to "https://www.theguardian.com/world/bulgaria",
                "BFA" to "https://www.theguardian.com/world/burkina-faso",
                "BDI" to "https://www.theguardian.com/world/burundi",
                "KHM" to "https://www.theguardian.com/world/cambodia",
                "CMR" to "https://www.theguardian.com/world/cameroon",
                "CAN" to "https://www.theguardian.com/world/canada",
                "CPV" to "https://www.theguardian.com/world/cape-verde",
                "CAF" to "https://www.theguardian.com/world/central-african-republic",
                "TCD" to "https://www.theguardian.com/world/chad",
                "CHL" to "https://www.theguardian.com/world/chile",
                "CHN" to "https://www.theguardian.com/world/china",
                "" to "https://www.theguardian.com/world/colombia",
                "DMA" to "https://www.theguardian.com/world/dominica",
                "DOM" to "https://www.theguardian.com/world/dominicanrepublic",
                "ECU" to "https://www.theguardian.com/world/ecuador",
                "EGY" to "https://www.theguardian.com/world/egypt",
                "SLV" to "https://www.theguardian.com/world/el-salvador",
                "GNQ" to "https://www.theguardian.com/world/equatorial-guinea",
                "ERI" to "https://www.theguardian.com/world/eritrea",
                "EST" to "https://www.theguardian.com/world/estonia",
                "ETH" to "https://www.theguardian.com/world/ethiopia",
                "" to "https://www.theguardian.com/world/fiji",
                "" to "https://www.theguardian.com/world/finland",
                "FRA" to "https://www.theguardian.com/world/france",
                "" to "https://www.theguardian.com/world/gabon",
                "" to "https://www.theguardian.com/world/gambia",
                "GER" to "https://www.theguardian.com/world/georgia-news",
                "DEU" to "https://www.theguardian.com/world/germany",
                "GHA" to "https://www.theguardian.com/world/ghana",
                "GRC" to "https://www.theguardian.com/world/greece",
                "" to "https://www.theguardian.com/world/grenada",
                "" to "https://www.theguardian.com/world/guatemala",
                "GIN" to "https://www.theguardian.com/world/guinea",
                "GNB" to "https://www.theguardian.com/world/guinea-bissau",
                "GUY" to "https://www.theguardian.com/world/guyana",
                "HTI" to "https://www.theguardian.com/world/haiti",
                "HND" to "https://www.theguardian.com/world/honduras",
                "HKG" to "https://www.theguardian.com/world/hong-kong",
                "HUN" to "https://www.theguardian.com/world/hungary",
                "ISL" to "https://www.theguardian.com/world/iceland",
                "IND" to "https://www.theguardian.com/world/india",
                "IDN" to "https://www.theguardian.com/world/indonesia",
                "IRA" to "https://www.theguardian.com/world/iran",
                "IRQ" to "https://www.theguardian.com/world/iraq",
                "IRL" to "https://www.theguardian.com/world/ireland",
                "ISR" to "https://www.theguardian.com/world/israel",
                "ITA" to "https://www.theguardian.com/world/italy",
                "IVC" to "https://www.theguardian.com/world/ivory-coast",
                "JAM" to "https://www.theguardian.com/world/jamaica",
                "JPN" to "https://www.theguardian.com/world/japan",
                "JOR" to "https://www.theguardian.com/world/jordan",
                "KAZ" to "https://www.theguardian.com/world/kazakhstan",
                "KEN" to "https://www.theguardian.com/world/kenya",
                "" to "https://www.theguardian.com/world/kiribati",
                "" to "https://www.theguardian.com/world/kosovo",
                "" to "https://www.theguardian.com/world/kuwait",
                "" to "https://www.theguardian.com/world/kyrgyzstan",
                "" to "https://www.theguardian.com/world/laos",
                "LVA" to "https://www.theguardian.com/world/latvia",
                "LBN" to "https://www.theguardian.com/world/lebanon",
                "LSO" to "https://www.theguardian.com/world/lesotho",
                "MUS" to "https://www.theguardian.com/world/mauritius",
                "MEX" to "https://www.theguardian.com/world/mexico",
                "FSM" to "https://www.theguardian.com/world/micronesia",
                "" to "https://www.theguardian.com/world/moldova",
                "" to "https://www.theguardian.com/world/monaco",
                "" to "https://www.theguardian.com/world/mongolia",
                "" to "https://www.theguardian.com/world/montenegro",
                "" to "https://www.theguardian.com/world/morocco",
                "" to "https://www.theguardian.com/world/mozambique",
                "" to "https://www.theguardian.com/world/namibia",
                "" to "https://www.theguardian.com/world/nauru",
                "" to "https://www.theguardian.com/world/nepal",
                "NLD" to "https://www.theguardian.com/world/netherlands",
                "NZL" to "https://www.theguardian.com/world/newzealand",
                "" to "https://www.theguardian.com/world/nicaragua",
                "NER" to "https://www.theguardian.com/world/niger",
                "NGA" to "https://www.theguardian.com/world/nigeria",
                "PRK" to "https://www.theguardian.com/world/north-korea",
                "NOR" to "https://www.theguardian.com/world/norway",
                "OMN" to "https://www.theguardian.com/world/oman",
                "PAK" to "https://www.theguardian.com/world/pakistan",
                "" to "https://www.theguardian.com/world/palau",
                "" to "https://www.theguardian.com/world/peru",
                "PHL" to "https://www.theguardian.com/world/philippines",
                "POL" to "https://www.theguardian.com/world/poland",
                "PRT" to "https://www.theguardian.com/world/portugal",
                "QAT" to "https://www.theguardian.com/world/qatar",
                "ROU" to "https://www.theguardian.com/world/romania",
                "RUS" to "https://www.theguardian.com/world/russia",
                "" to "https://www.theguardian.com/world/rwanda",
                "" to "https://www.theguardian.com/world/samoa",
                "" to "https://www.theguardian.com/world/san-marino",
                "" to "https://www.theguardian.com/world/sao-tome-and-principe",
                "SAU" to "https://www.theguardian.com/world/saudiarabia",
                "" to "https://www.theguardian.com/world/senegal",
                "" to "https://www.theguardian.com/world/serbia",
                "" to "https://www.theguardian.com/world/seychelles",
                "" to "https://www.theguardian.com/world/sierraleone",
                "SGP" to "https://www.theguardian.com/world/singapore",
                "" to "https://www.theguardian.com/world/slovakia",
                "" to "https://www.theguardian.com/world/slovenia",
                "" to "https://www.theguardian.com/world/solomonislands",
                "" to "https://www.theguardian.com/world/somalia",
                "ZAF" to "https://www.theguardian.com/world/southafrica",
                "KOR" to "https://www.theguardian.com/world/south-korea",
                "USA" to "https://www.theguardian.com/us",
                "" to "https://www.theguardian.com/world/south-sudan",
                "" to "https://www.theguardian.com/world/spain",
                "" to "https://www.theguardian.com/world/srilanka",
                "" to "https://www.theguardian.com/world/saint-kitts-and-nevis",
                "" to "https://www.theguardian.com/world/stlucia",
                "" to "https://www.theguardian.com/world/saint-vincent-and-the-grenadines",
                "" to "https://www.theguardian.com/world/sudan",
                "" to "https://www.theguardian.com/world/suriname",
                "" to "https://www.theguardian.com/world/swaziland",
                "" to "https://www.theguardian.com/world/sweden",
                "" to "https://www.theguardian.com/world/switzerland",
                "" to "https://www.theguardian.com/world/syria",
                "TWN" to "https://www.theguardian.com/world/taiwan",
                "" to "https://www.theguardian.com/world/tajikistan",
                "" to "https://www.theguardian.com/world/tanzania",
                "THA" to "https://www.theguardian.com/world/thailand",
                "" to "https://www.theguardian.com/world/timor-leste",
                "" to "https://www.theguardian.com/world/togo",
                "" to "https://www.theguardian.com/world/tonga",
                "" to "https://www.theguardian.com/world/trinidad-and-tobago",
                "" to "https://www.theguardian.com/world/tunisia",
                "" to "https://www.theguardian.com/world/turkey",
                "" to "https://www.theguardian.com/world/turkmenistan",
                "" to "https://www.theguardian.com/world/tuvalu",
                "" to "https://www.theguardian.com/world/uganda",
                "" to "https://www.theguardian.com/world/ukraine",
                "GBR" to "https://www.theguardian.com/uk",
                "ARE" to "https://www.theguardian.com/world/united-arab-emirates",
                "" to "https://www.theguardian.com/world/uruguay",
                "" to "https://www.theguardian.com/world/uzbekistan",
                "" to "https://www.theguardian.com/world/vanuatu",
                "" to "https://www.theguardian.com/world/venezuela",
                "VNM" to "https://www.theguardian.com/world/vietnam",
                "" to "https://www.theguardian.com/world/yemen",
                "" to "https://www.theguardian.com/world/zambia",
                "" to "https://www.theguardian.com/world/zimbabwe",
                "WORLD" to "https://www.theguardian.com/world")
    }

    override fun getLocationAwareFeed(country: String): SyndFeed {
        return FeedUtil.downloadDirect(feeds[country] + "/rss", context, null)?.let {
            SyndFeedInput().build(InputSource(it))
        } ?: throw IOException("no feed available")
    }

    override fun getFallbackFeed(): SyndFeed {
        return getLocationAwareFeed("WORLD")
    }
}
