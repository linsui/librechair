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

// credit: https://github.com/jubincn/WGS84GCJ02Conversion
// (original version is also licensed under the GPLv3

package ch.deletescape.lawnchair.feed.maps;

public class XYOrLatLonPair {
    private double latitude;
    private double longitude;
    
    public XYOrLatLonPair(double lat, double lon){
        this.latitude = lat;
        this.longitude = lon;
    }
    public XYOrLatLonPair(XYOrLatLonPair pCoodinates) {
        this.latitude = pCoodinates.getLatOrY();
        this.longitude = pCoodinates.getLonOrX();
    }
    
    public double getLatOrY() {
        return latitude;
    }

    
    public double getLonOrX() {
        return longitude;
    }

    
    public void setLatOrY(double lat) {
        this.latitude = lat;
    }

    
    public void setLonOrX(double lon) {
        this.longitude = lon;
    }
    
    public XYOrLatLonPair substract(XYOrLatLonPair pCoordinates){
        double lat = this.latitude - pCoordinates.getLatOrY();
        double lon = this.longitude - pCoordinates.getLonOrX();
        return new XYOrLatLonPair(lat, lon);
    }
    
    public XYOrLatLonPair add(XYOrLatLonPair pCoordinates){
        double lat = this.latitude + pCoordinates.getLatOrY();
        double lon = this.longitude + pCoordinates.getLonOrX();
        
        return new XYOrLatLonPair(lat, lon);
    }
    
}