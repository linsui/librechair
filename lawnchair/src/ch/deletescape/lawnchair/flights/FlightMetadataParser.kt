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

package ch.deletescape.lawnchair.flights

object FlightMetadataParser {
    fun parse(info: String): Flight {
        val flight = Flight();
        var index = 0;
        for (token in Flight.Types.values()) {
            if (index >= info.length && token.mandatory) {
                error("Mandatory token ${token.name} not met!")
            } else if (index + token.size >= info.length && token.mandatory) {
                error("Mandatory token ${token.name} underflow!")
            }
            val data = info.substring(index, index + token.size)
            token.addToFlight(flight, data);
            index += token.size
        }
        return flight;
    }
}