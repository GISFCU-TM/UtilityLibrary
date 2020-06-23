package tm.gis.utilitylibrary;

public class CoordinateTransformation {

    /*
     * Definition of math related value
     */

    private static double COS67_5 = 0.3826834323650897717284599840304;
    private static double PI = 3.14159265358979323;
    private static double HALF_PI = 1.570796326794896615;
    private static double DEG_RAD = 0.01745329251994329572;
    private static double RAD_DEG = 57.295779513082321031;

    /*
     * Definition of datum related value
     */

    private static double AD_C = 1.0026000;

    private static double TWD67_A = 6378160.0;
    private static double TWD67_B = 6356774.7192;
    private static double TWD67_ECC = 0.00669454185458;
    private static double TWD67_ECC2 = 0.00673966079586;
    private static double TWD67_DX = -752.32; // different from garmin and
    // already knowned value, but
    // those all value only
    private static double TWD67_DY = -361.32; // got 5-15m accuracy. the real
    // offical value is holded by
    // somebody and not
    private static double TWD67_DZ = -180.51; // release to public. if can got
    // more enough twd67/twd97
    // control point coordinare,
    private static double TWD67_RX = -0.00000117; // then we can calculate a
    // better value than now.
    private static double TWD67_RY = 0.00000184; //
    private static double TWD67_RZ = 0.00000098; // and, also lack twd67/twd97
    // altitude convertion
    // value...
    private static double TWD67_S = 0.00002329; //

    private static double TWD97_A = 6378137.0;
    private static double TWD97_B = 6356752.3141;
    private static double TWD97_ECC = 0.00669438002290;
    private static double TWD97_ECC2 = 0.00673949677556;

    private static double TWD67_TM2 = 0.9999; // TWD67->TM2 scale
    private static double TWD97_TM2 = 0.9999; // TWD97->TM2 scale

    // ho = toTM2(TWD97_A, TWD97_ECC, TWD97_ECC2, 0, 121, TWD97_TM2, inX, inY);
    // center longitude of taiwan is 121, for penghu is 119
    private static int dx = 250000; // TM2 in Taiwan should add 250000

    public static double mercator(double y, double a, double ecc) {
        if (y == 0.0) {
            return 0.0;
        } else {
            return a
                    * ((1.0 - ecc / 4.0 - 3.0 * ecc * ecc / 64.0 - 5.0 * ecc
                    * ecc * ecc / 256.0)
                    * y
                    - (3.0 * ecc / 8.0 + 3.0 * ecc * ecc / 32.0 + 45.0
                    * ecc * ecc * ecc / 1024.0)
                    * Math.sin(2.0 * y)
                    + (15.0 * ecc * ecc / 256.0 + 45.0 * ecc * ecc
                    * ecc / 1024.0) * Math.sin(4.0 * y) - (35.0
                    * ecc * ecc * ecc / 3072.0)
                    * Math.sin(6.0 * y));
        }
    }

    public static CoordTransStruct toTWD67(double inx, double iny, double inz) {
        CoordTransStruct cts = new CoordTransStruct();
        double r = 0;
        boolean pole;
        double sin_lat = 0;
        double cos_lat = 0;
        double lat = 0;
        double lon = 0;
        double height = 0;
        double x1 = 0;
        double y1 = 0;
        double z1 = 0;
        double x2 = 0;
        double y2 = 0;
        double z2 = 0;
        double q = 0;
        double q2 = 0;
        double t = 0;
        double t1 = 0;
        double s = 0;
        double s1 = 0;
        double sum = 0;
        double sin_b = 0;
        double cos_b = 0;
        double sin_p = 0;
        double cos_p = 0;

        lon = inx * DEG_RAD;
        lat = iny * DEG_RAD;
        height = inz * DEG_RAD;

        if (lat < -HALF_PI && lat > -1.001 * HALF_PI)
            lat = -HALF_PI;
        else if (lat > HALF_PI && lat < 1.001 * HALF_PI)
            lat = HALF_PI;
        else if ((lat < -HALF_PI) || (lat > HALF_PI))
            return null;

        if (lon > PI)
            lon -= (2 * PI);

        sin_lat = Math.sin(lat);
        cos_lat = Math.cos(lat);
        r = TWD97_A / (Math.sqrt(1.0 - TWD97_ECC * sin_lat * sin_lat));
        x1 = (r + height) * cos_lat * Math.cos(lon);
        y1 = (r + height) * cos_lat * Math.sin(lon);
        z1 = ((r * (1 - TWD97_ECC)) + height) * sin_lat;

        x2 = x1 - TWD67_DX - TWD67_S
                * (lon + TWD67_RZ * lat - TWD67_RY * height);
        y2 = y1 - TWD67_DY - TWD67_S
                * (-TWD67_RZ * lon + lat + TWD67_RX * height);
        z2 = z1 - TWD67_DZ - TWD67_S
                * (TWD67_RY * lon - TWD67_RX * lat + height);

        pole = false;

        if (x2 != 0.0) {
            lon = Math.atan2(y2, x2);
        } else {
            if (y2 > 0) {
                lon = HALF_PI;
            } else if (y2 < 0) {
                lon = -HALF_PI;
            } else {
                pole = true;

                lon = 0;

                if (z2 > 0) {
                    lat = HALF_PI;
                } else if (z2 < 0) {
                    lat = -HALF_PI;
                } else {
                    lat = HALF_PI;
                    cts.setLon(lon * RAD_DEG);
                    cts.setLon(lat * RAD_DEG);
                    return cts;
                }
            }
        }

        q2 = x2 * x2 + y2 * y2;
        q = Math.sqrt(q2);
        t = z2 * AD_C;
        s = Math.sqrt(t * t + q2);
        sin_b = t / s;
        cos_b = q / s;
        t1 = z2 + TWD67_B * TWD67_ECC2 * sin_b * sin_b * sin_b;
        sum = q - TWD67_A * TWD67_ECC * cos_b * cos_b * cos_b;
        s1 = Math.sqrt(t1 * t1 + sum * sum);
        sin_p = t1 / s1;
        cos_p = sum / s1;
        r = TWD67_A / Math.sqrt(1.0 - TWD67_ECC * sin_p * sin_p);

        if (cos_p >= COS67_5) {
            height = q / cos_p - r;
        } else if (cos_p <= -COS67_5) {
            height = q / -cos_p - r;
        } else {
            height = z2 / sin_p + r * (TWD67_ECC - 1.0);
        }

        if (!pole) {
            lat = Math.atan(sin_p / cos_p);
        }
        cts.setLon(lon * RAD_DEG);
        cts.setLat(lat * RAD_DEG);
        return cts;
    }

    public static CoordTransStruct toTM2(double a, double ecc, double ecc2,
                                         double lat, double lon, double scale, double inx, double iny) // 經緯度轉TM2度
    {
        CoordTransStruct cts = new CoordTransStruct();
        double x0;
        double y0;
        double x1;
        double y1;
        double m0;
        double m1;

        double n;
        double t;
        double c;
        double A;

        x0 = inx * DEG_RAD;
        y0 = iny * DEG_RAD;

        x1 = lon * DEG_RAD;
        y1 = lat * DEG_RAD;

        m0 = mercator(y1, a, ecc);
        m1 = mercator(y0, a, ecc);

        n = a / Math.sqrt(1 - ecc * Math.pow(Math.sin(y0), 2.0));
        t = Math.pow(Math.tan(y0), 2.0);
        c = ecc2 * Math.pow(Math.cos(y0), 2.0);
        A = (x0 - x1) * Math.cos(y0);
        double xx = 0;
        xx = scale
                * n
                * (A + (1.0 - t + c) * A * A * A / 6.0 + (5.0 - 18.0 * t + t
                * t + 72.0 * c - 58.0 * ecc2)
                * Math.pow(A, 5.0) / 120.0) + 250000;
        double yy = 0;
        yy = scale
                * (m1 - m0 + n
                * Math.tan(y0)
                * (A * A / 2.0 + (5.0 - t + 9.0 * c + 4 * c * c)
                * Math.pow(A, 4.0) / 24.0 + (61.0 - 58.0 * t
                + t * t + 600.0 * c - 330.0 * ecc2)
                * Math.pow(A, 6.0) / 720.0));
        cts.setLon(xx);
        cts.setLat(yy);
        return cts;
    }

    public static CoordTransStruct fromTM2(double a, double ecc, double ecc2,
                                           double lat, double lon, double scale, double x, double y) // TM2 to
    // 經緯度
    {
        CoordTransStruct cts = new CoordTransStruct();
        double x0;
        double y0;
        double x1;
        double y1;
        double phi;
        double m;
        double m0;
        double mu;
        double e1;
        double c1;
        double t1;
        double n1;
        double r1;
        double d;

        x0 = x - dx;
        y0 = y;

        x1 = lon * DEG_RAD;
        y1 = lat * DEG_RAD;

        m0 = mercator(y1, a, ecc);
        m = m0 + y0 / scale;

        e1 = (1.0 - Math.sqrt(1.0 - ecc)) / (1.0 + Math.sqrt(1.0 - ecc));
        mu = m
                / (a * (1.0 - ecc / 4.0 - 3.0 * ecc * ecc / 64.0 - 5.0 * ecc
                * ecc * ecc / 256.0));

        phi = mu + (3.0 * e1 / 2.0 - 27.0 * Math.pow(e1, 3.0) / 32.0)
                * Math.sin(2.0 * mu)
                + (21.0 * e1 * e1 / 16.0 - 55.0 * Math.pow(e1, 4.0) / 32.0)
                * Math.sin(4.0 * mu) + 151.0 * Math.pow(e1, 3.0) / 96.0
                * Math.sin(6.0 * mu) + 1097.0 * Math.pow(e1, 4.0) / 512.0
                * Math.sin(8.0 * mu);

        c1 = ecc2 * Math.pow(Math.cos(phi), 2.0);
        t1 = Math.pow(Math.tan(phi), 2.0);
        n1 = a / Math.sqrt(1 - ecc * Math.pow(Math.sin(phi), 2.0));
        r1 = a * (1.0 - ecc)
                / Math.pow(1.0 - ecc * Math.pow(Math.sin(phi), 2.0), 1.5);
        d = x0 / (n1 * scale);
        double xx = 0;
        xx = (x1 + (d - (1.0 + 2.0 * t1 + c1) * Math.pow(d, 3.0) / 6.0 + (5.0
                - 2.0 * c1 + 28.0 * t1 - 3.0 * c1 * c1 + 8.0 * ecc2 + 24.0 * t1
                * t1)
                * Math.pow(d, 5.0) / 120.0)
                / Math.cos(phi))
                * RAD_DEG;
        double yy = 0;
        yy = (phi - n1
                * Math.tan(phi)
                / r1
                * (d
                * d
                / 2.0
                - (5.0 + 3.0 * t1 + 10.0 * c1 - 4.0 * c1 * c1 - 9.0 * ecc2)
                * Math.pow(d, 4.0) / 24.0 + (61.0 + 90.0 * t1 + 298.0
                * c1 + 45.0 * t1 * t1 - 252.0 * ecc2 - 3.0 * c1 * c1)
                * Math.pow(d, 6.0) / 72.0))
                * RAD_DEG;
        cts.setLon(xx);
        cts.setLat(yy);
        return cts;
    }

    public static CoordTransStruct toTWD97(double x, double y, double z) // 67經緯度
    // to
    // 97經緯度
    {
        CoordTransStruct cts = new CoordTransStruct();
        double r;
        boolean pole;
        double sin_lat;
        double cos_lat;
        double lat;
        double lon;
        double height;
        double x1;
        double y1;
        double z1;
        double x2;
        double y2;
        double z2;
        double q;
        double q2;
        double t;
        double t1;
        double s;
        double s1;
        double sum;
        double sin_b;
        double cos_b;
        double sin_p;
        double cos_p;

        lon = x * DEG_RAD;
        lat = y * DEG_RAD;
        height = z * DEG_RAD;

        if (lat < -HALF_PI && lat > -1.001 * HALF_PI)
            lat = -HALF_PI;
        else if (lat > HALF_PI && lat < 1.001 * HALF_PI)
            lat = HALF_PI;
        else if ((lat < -HALF_PI) || (lat > HALF_PI))
            return null;

        if (lon > PI)
            lon -= (2 * PI);

        sin_lat = Math.sin(lat);
        cos_lat = Math.cos(lat);
        r = TWD67_A / (Math.sqrt(1.0 - TWD67_ECC * sin_lat * sin_lat));
        x1 = (r + height) * cos_lat * Math.cos(lon);
        y1 = (r + height) * cos_lat * Math.sin(lon);
        z1 = ((r * (1 - TWD67_ECC)) + height) * sin_lat;

        x2 = x1 + TWD67_DX + TWD67_S
                * (lon + TWD67_RZ * lat - TWD67_RY * height);
        y2 = y1 + TWD67_DY + TWD67_S
                * (-TWD67_RZ * lon + lat + TWD67_RX * height);
        z2 = z1 + TWD67_DZ + TWD67_S
                * (TWD67_RY * lon - TWD67_RX * lat + height);

        pole = false;
        if (x2 != 0.0) {
            lon = Math.atan2(y2, x2);
        } else {
            if (y2 > 0) {
                lon = HALF_PI;
            } else if (y2 < 0) {
                lon = -HALF_PI;
            } else {
                pole = true;

                lon = 0;

                if (z2 > 0) {
                    lat = HALF_PI;
                } else if (z2 < 0) {
                    lat = -HALF_PI;
                } else {
                    lat = HALF_PI;

                    x = lon * RAD_DEG;
                    y = lat * RAD_DEG;
                    cts.setLon(lon * RAD_DEG);
                    cts.setLat(lat * RAD_DEG);
                    return cts;
                }
            }
        }

        q2 = x2 * x2 + y2 * y2;
        q = Math.sqrt(q2);
        t = z2 * AD_C;
        s = Math.sqrt(t * t + q2);
        sin_b = t / s;
        cos_b = q / s;
        t1 = z2 + TWD97_B * TWD97_ECC2 * sin_b * sin_b * sin_b;
        sum = q - TWD97_A * TWD97_ECC * cos_b * cos_b * cos_b;
        s1 = Math.sqrt(t1 * t1 + sum * sum);
        sin_p = t1 / s1;
        cos_p = sum / s1;
        r = TWD97_A / Math.sqrt(1.0 - TWD97_ECC * sin_p * sin_p);

        if (cos_p >= COS67_5) {
            height = q / cos_p - r;
        } else if (cos_p <= -COS67_5) {
            height = q / -cos_p - r;
        } else {
            height = z2 / sin_p + r * (TWD97_ECC - 1.0);
        }

        if (!pole) {
            lat = Math.atan(sin_p / cos_p);
        }

        cts.setLon(lon * RAD_DEG);
        cts.setLat(lat * RAD_DEG);
        return cts;

    }

    /**
     * TODO TM267 轉 TM297
     *
     * @param x 經度
     * @param y 緯度
     * @return
     */
    public static CoordTransStruct TM267_TO_TM297(double x, double y) {
        CoordTransStruct p1 = new CoordTransStruct();
        CoordTransStruct p2 = new CoordTransStruct();

        p1 = TM267_TO_LAL97(x, y);
        p2 = toTM2(TWD97_A, TWD97_ECC, TWD97_ECC2, 0, 121, TWD97_TM2,
                p1.getLon(), p1.getLat());
        return p2;
    }

    /**
     * TODO TM297 轉 TM267
     *
     * @param x 經度
     * @param y 緯度
     * @return
     */
    public static CoordTransStruct TM297_TO_TM267(double x, double y) {
        CoordTransStruct p1 = new CoordTransStruct();
        CoordTransStruct p2 = new CoordTransStruct();
        p1 = TM297_TO_LAL67(x, y);
        p2 = toTM2(TWD67_A, TWD67_ECC, TWD67_ECC2, 0, 121, TWD67_TM2,
                p1.getLon(), p1.getLat());
        return p2;
    }

    /**
     * TODO TM267 轉 WGS84_97
     *
     * @param x 經度
     * @param y 緯度
     * @return
     */
    public static CoordTransStruct TM267_TO_LAL97(double x, double y) {
        CoordTransStruct p1 = new CoordTransStruct();
        CoordTransStruct p2 = new CoordTransStruct();
        p1 = TM267_TO_LAL67(x, y);
        p2 = toTWD97(p1.getLon(), p1.getLat(), 0);
        return p2;
    }

    /**
     * TODO WGS84_97 轉 WGS84_97
     *
     * @param x 經度
     * @param y 緯度
     * @return
     */
    public static CoordTransStruct TM297_TO_LAL97(double x, double y) {
        CoordTransStruct p1 = new CoordTransStruct();
        p1 = fromTM2(TWD97_A, TWD97_ECC, TWD97_ECC2, 0, 121, TWD97_TM2, x, y);
        return p1;
    }

    /**
     * TODO WGS84_97 轉 TM267
     *
     * @param x 經度
     * @param y 緯度
     * @return
     */
    public static CoordTransStruct LAL97_TO_TM267(double x, double y) {
        CoordTransStruct p1 = new CoordTransStruct();
        CoordTransStruct p2 = new CoordTransStruct();
        p1 = toTWD67(x, y, 0);
        p2 = toTM2(TWD67_A, TWD67_ECC, TWD67_ECC2, 0, 121, TWD67_TM2,
                p1.getLon(), p1.getLat());
        return p2;
    }

    /**
     * TODO WGS84_97 轉 TM297
     *
     * @param x 經度
     * @param y 緯度
     * @return
     */
    public static CoordTransStruct LAL97_TO_TM297(double x, double y) {
        CoordTransStruct p1 = new CoordTransStruct();
        p1 = toTM2(TWD97_A, TWD97_ECC, TWD97_ECC2, 0, 121, TWD97_TM2, x, y);
        return p1;
    }

    /**
     * TODO TM267轉WGS84_67
     *
     * @param x 經度
     * @param y 緯度
     * @return
     */
    public static CoordTransStruct TM267_TO_LAL67(double x, double y) {
        CoordTransStruct p1 = new CoordTransStruct();
        p1 = fromTM2(TWD67_A, TWD67_ECC, TWD67_ECC2, 0, 121, TWD67_TM2, x, y);
        return p1;
    }

    /**
     * TODO TM297轉WGS84_67
     *
     * @param x 經度
     * @param y 緯度
     * @return
     */
    public static CoordTransStruct TM297_TO_LAL67(double x, double y) {
        CoordTransStruct p1 = new CoordTransStruct();
        CoordTransStruct p2 = new CoordTransStruct();
        p1 = TM297_TO_LAL97(x, y);
        p2 = toTWD67(p1.getLon(), p1.getLat(), 0);
        return p2;
    }

}
