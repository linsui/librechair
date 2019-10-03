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
 */// credit: https://github.com/jubincn/WGS84GCJ02Conversion
// (original version is also licensed under the GPLv3


package ch.deletescape.lawnchair.feed.maps;

import kotlin.Pair;

public class WgsGcjConverter {
    public static final double SEMI_MAJOR_AXIS = 6378245.0;
    public static final double BAIDU_SEMI_MAJOR_AXIS =
            SEMI_MAJOR_AXIS - 108;
    public static final double FLATTENING = 0.00335233;
    public static final double SEMI_MINOR_AXIS = SEMI_MAJOR_AXIS * (1.0 - FLATTENING);
    private static final double a = SEMI_MAJOR_AXIS;
    private static final double b = SEMI_MINOR_AXIS;
    public static final double EE = (a * a - b * b) / (a * b);

    private static double[] array1 = {75, 60, 45, 30, 15, 0};
    private static double[] array3 = {12890594.86, 8362377.87, 5591021, 3481989.83, 1678043.12, 0};
    private static double[][] array2 = {new double[]{-0.0015702102444, 111320.7020616939, 1704480524535203.00, -10338987376042340.00, 26112667856603880.00, -35149669176653700.00, 26595700718403920.00, -10725012454188240.00, 1800819912950474.00, 82.5}
            , new double[]{0.0008277824516172526, 111320.7020463578, 647795574.6671607, -4082003173.641316, 10774905663.51142, -15171875531.51559, 12053065338.62167, -5124939663.577472, 913311935.9512032, 67.5}
            , new double[]{0.00337398766765, 111320.7020202162, 4481351.045890365, -23393751.19931662, 79682215.47186455, -115964993.2797253, 97236711.15602145, -43661946.33752821, 8477230.501135234, 52.5}
            , new double[]{0.00220636496208, 111320.7020209128, 51751.86112841131, 3796837.749470245, 992013.7397791013, -1221952.21711287, 1340652.697009075, -620943.6990984312, 144416.9293806241, 37.5}
            , new double[]{-0.0003441963504368392, 111320.7020576856, 278.2353980772752, 2485758.690035394, 6070.750963243378, 54821.18345352118, 9540.606633304236, -2710.55326746645, 1405.483844121726, 22.5}
            , new double[]{-0.0003218135878613132, 111320.7020701615, 0.00369383431289, 823725.6402795718, 0.46104986909093, 2351.343141331292, 1.58060784298199, 8.77738589078284, 0.37238884252424, 7.45}};
    private static double[][] array4 = {new double[]{1.410526172116255e-8, 0.00000898305509648872, -1.9939833816331, 200.9824383106796, -187.2403703815547, 91.6087516669843, -23.38765649603339, 2.57121317296198, -0.03801003308653, 17337981.2}
            , new double[]{-7.435856389565537e-9, 0.000008983055097726239, -0.78625201886289, 96.32687599759846, -1.85204757529826, -59.36935905485877, 47.40033549296737, -16.50741931063887, 2.28786674699375, 10260144.86}
            , new double[]{-3.030883460898826e-8, 0.00000898305509983578, 0.30071316287616, 59.74293618442277, 7.357984074871, -25.38371002664745, 13.45380521110908, -3.29883767235584, 0.32710905363475, 6856817.37}
            , new double[]{-1.981981304930552e-8, 0.000008983055099779535, 0.03278182852591, 40.31678527705744, 0.65659298677277, -4.44255534477492, 0.85341911805263, 0.12923347998204, -0.04625736007561, 4482777.06}
            , new double[]{3.09191371068437e-9, 0.000008983055096812155, 0.00006995724062, 23.10934304144901, -0.00023663490511, -0.6321817810242, -0.00663494467273, 0.03430082397953, -0.00466043876332, 2555164.4}
            , new double[]{2.890871144776878e-9, 0.000008983055095805407, -3.068298e-8, 7.47137025468032, -0.00000353937994, -0.02145144861037, -0.00001234426596, 0.00010322952773, -0.00000323890364, 826088.5}};

    public static XYOrLatLonPair wgs84ToGcj02(double wgsLat, double wgsLon) {
        if (isOutOfChina(wgsLat, wgsLon)) {
            return new XYOrLatLonPair(wgsLat, wgsLon);
        }
        double dLat = transformLat(wgsLon - 105.0, wgsLat - 35.0);
        double dLon = transformLon(wgsLon - 105.0, wgsLat - 35.0);
        double radLat = wgsLat / 180.0 * StrictMath.PI;
        double magic = StrictMath.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = StrictMath.sqrt(magic);
        dLat = (dLat * 180.0) / ((SEMI_MAJOR_AXIS * (1 - EE)) / (magic * sqrtMagic) * StrictMath.PI);
        dLon = (dLon * 180.0) / (SEMI_MAJOR_AXIS / sqrtMagic * StrictMath.cos(
                radLat) * StrictMath.PI);
        double gcjLat = wgsLat + dLat;
        double gcjLon = wgsLon + dLon;

        return new XYOrLatLonPair(gcjLat, gcjLon);
    }

    public static XYOrLatLonPair gcj02ToWgs84(double gcjLat, double gcjLon) {
        XYOrLatLonPair g0 = new XYOrLatLonPair(gcjLat, gcjLon);
        XYOrLatLonPair w0 = new XYOrLatLonPair(g0);
        XYOrLatLonPair g1 = wgs84ToGcj02(w0.getLatOrY(), w0.getLonOrX());
        XYOrLatLonPair w1 = w0.substract(g1.substract(g0));
        while (maxAbsDiff(w1, w0) >= 1e-6) {
            w0 = w1;
            g1 = wgs84ToGcj02(w0.getLatOrY(), w0.getLonOrX());
            XYOrLatLonPair gpsDiff = g1.substract(g0);
            w1 = w0.substract(gpsDiff);
        }

        return w1;
    }

    private static double maxAbsDiff(XYOrLatLonPair w1, XYOrLatLonPair w0) {
        XYOrLatLonPair diff = w1.substract(w0);
        double absLatDiff = StrictMath.abs(diff.getLatOrY());
        double absLonDiff = StrictMath.abs(diff.getLonOrX());

        return (absLatDiff > absLonDiff ? absLatDiff : absLonDiff);
    }

    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * StrictMath.sqrt(
                StrictMath.abs(x));
        ret = ret + (20.0 * StrictMath.sin(6.0 * x * StrictMath.PI) + 20.0 * StrictMath.sin(
                2.0 * x * StrictMath.PI)) * 2.0 / 3.0;
        ret = ret + (20.0 * StrictMath.sin(x * StrictMath.PI) + 40.0 * StrictMath.sin(
                x / 3.0 * StrictMath.PI)) * 2.0 / 3.0;
        ret = ret + (150.0 * StrictMath.sin(x / 12.0 * StrictMath.PI) + 300.0 * StrictMath.sin(
                x * StrictMath.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * StrictMath.sqrt(
                StrictMath.abs(x));
        ret = ret + (20.0 * StrictMath.sin(6.0 * x * StrictMath.PI) + 20.0 * StrictMath.sin(
                2.0 * x * StrictMath.PI)) * 2.0 / 3.0;
        ret = ret + (20.0 * StrictMath.sin(y * StrictMath.PI) + 40.0 * StrictMath.sin(
                y / 3.0 * StrictMath.PI)) * 2.0 / 3.0;
        ret = ret + (160.0 * StrictMath.sin(y / 12.0 * StrictMath.PI) + 320.0 * StrictMath.sin(
                y * StrictMath.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static boolean isOutOfChina(double wgsLat, double wgsLon) {
        if (wgsLat < 0.8293 || wgsLat > 55.8271) {
            return true;
        }
        if (wgsLon < 72.004 || wgsLon > 137.8347) {
            return true;
        }
        return false;
    }


    public static XYOrLatLonPair baiduToWgs(double baiduLat, double baiduLon) {
        double x = baiduLon - 0.0065, y = baiduLat - 0.006;
        double z = StrictMath.sqrt(x * x + y * y) - 0.00002 * StrictMath.sin(y * StrictMath.PI);
        double theta = StrictMath.atan2(y, x) - 0.000003 * StrictMath.cos(x * StrictMath.PI);
        return new XYOrLatLonPair(z * StrictMath.cos(theta), z * StrictMath.sin(theta));
    }

    public static XYOrLatLonPair wgsToBaidu(double wgsLat, double WgsLon) {
        double z = StrictMath.sqrt(WgsLon * WgsLon + wgsLat * wgsLat) + 0.00002 * StrictMath.sin(
                wgsLat * StrictMath.PI);
        double theta = StrictMath.atan2(wgsLat, WgsLon) + 0.000003 * StrictMath.cos(
                WgsLon * StrictMath.PI);
        return new XYOrLatLonPair(z * StrictMath.cos(theta) + 0.0065,
                z * StrictMath.sin(theta) + 0.006);
    }

    public static long lonZoomToBdX(double lon, int zoom) {
        return (long) (StrictMath.pow(2,
                zoom - 26) * ((StrictMath.PI * lon * BAIDU_SEMI_MAJOR_AXIS) / 180));
    }

    public static long latZoomToBdY(double lat, int zoom) {
        return (long) (StrictMath.pow(2,
                zoom - 26) * BAIDU_SEMI_MAJOR_AXIS * StrictMath.log(
                StrictMath.tan(StrictMath.PI * lat) + (1 / StrictMath.cos(StrictMath.PI * lat))));
    }

    public static Pair<Double, Double> num2deg(int xtile, int ytile, int zoom) {
        double n = StrictMath.pow(2, zoom);
        double lonDeg = xtile / n * 360.0 - 180.0;
        double latRad = StrictMath.atan(StrictMath.sinh(StrictMath.PI * (1 - 2 * ytile / n)));
        double latDeg = StrictMath.toDegrees(latRad);
        return new Pair<>(latDeg, lonDeg);
    }

    public static Pair<Integer, Integer> deg2num(double latDeg, double lonDeg, int zoom) {
        double latRad = StrictMath.toRadians(latDeg);
        double n = StrictMath.pow(2.0, zoom);
        int xtile = (int) ((lonDeg + 180.0) / 360.0 * n);
        int ytile = (int) ((1.0 - asinh(StrictMath.tan(latRad)) / StrictMath.PI) / 2.0 * n);
        return new Pair<>(xtile, ytile);
    }

    private static double asinh(double x) {
        return StrictMath.log(x + StrictMath.sqrt(x * x + 1.0));
    }

    public static XYOrLatLonPair latLng2Mercator(XYOrLatLonPair p) {
        double[] arr = null;
        double n_lat = p.getLatOrY() > 74 ? 74 : p.getLatOrY();
        n_lat = n_lat < -74 ? -74 : n_lat;
        for (int i = 0; i < array1.length; i++) {
            if (p.getLatOrY() >= array1[i]) {
                arr = array2[i];
                break;
            }
        }
        if (arr == null) {
            for (int i = array1.length - 1; i >= 0; i--) {
                if (p.getLatOrY() <= -array1[i]) {
                    arr = array2[i];
                    break;
                }
            }
        }
        double[] res = convert(p.getLatOrY(), p.getLonOrX(), arr);
        return new XYOrLatLonPair(res[0], res[1]);
    }

    public static XYOrLatLonPair mercator2LatLng(XYOrLatLonPair p) {
        double[] arr = new double[0];
        XYOrLatLonPair np = new XYOrLatLonPair(StrictMath.abs(p.getLonOrX()),
                StrictMath.abs(p.getLatOrY()));
        for (int i = 0; i < array3.length; i++) {
            if (np.getLatOrY() >= array3[i]) {
                arr = array4[i];
                break;
            }
        }
        double[] res = convert(np.getLonOrX(), np.getLatOrY(), arr);
        return new XYOrLatLonPair(res[0], res[1]);
    }

    private static double[] convert(double x, double y, double[] param) {
        double T = param[0] + param[1] * StrictMath.abs(x);
        double cC = StrictMath.abs(y) / param[9];
        double cF = param[2] + param[3] * cC + param[4] * cC * cC + param[5] *
                cC * cC * cC + param[6] * cC * cC * cC * cC + param[7] *
                cC * cC * cC * cC * cC + param[8] * cC * cC * cC * cC * cC * cC;
        T *= (x < 0 ? -1 : 1);
        cF *= (y < 0 ? -1 : 1);
        return new double[]{T, cF};
    }
}