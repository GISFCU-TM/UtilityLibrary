package tm.gis.utilitylibrary;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

    /**
     * 取得現在時間
     * @param Format  時間格式
     * @return
     */
    public static String getNowTime(String Format){

        SimpleDateFormat sdFormat = new SimpleDateFormat(Format);
        Date date = new Date();
        String strDate = sdFormat.format(date);
        return strDate;

    }

    /**
     *  取得現在時間 - 格式 yyyy/MM/dd hh:mm:ss
     * @return
     */
    public static String getNowTime(){

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String strDate = sdFormat.format(date);
        return strDate;

    }


    /**
     * 日期時間 轉換格式 ，如 2020-01-01 01:23:45 轉 2020年01月01日 01時23分45秒
     * @param dateString 代入要轉的日期和時間
     * @param Format 代入想轉出的日期
     * @return
     */
    public static String ChangeDateFormat(String dateString,String Format){
        //設定日期格式
        SimpleDateFormat sdFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //進行轉換
        Date date1 = null;
        String strDate = "";
        try {
            date1 = sdFormat1.parse(dateString);

            SimpleDateFormat sdFormat = new SimpleDateFormat(Format);
            Date date = new Date();
            strDate = sdFormat.format(date1);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return strDate;

    }


    /**
     *  西元 轉 民國 日期時間 ，前面 4 個字元一定要是數字
     * @return
     */
    public static String CE2ROC(String dateString)
    {

       String year = dateString.substring(0,4);
       String other_dateString = dateString.substring(4,dateString.length());
       int intyear = Integer.parseInt(year) - 1911;
        return intyear + other_dateString;
    }


    /**
     *  時間(字串) 轉成 Long
     * @param DateTimeString
     * @param Format
     * @return
     */
    public static long DateTime2Long(String DateTimeString,String Format)
    {
        long milliseconds = 0;
        SimpleDateFormat f = new SimpleDateFormat(Format);
        try {
            Date d = f.parse(DateTimeString);
            milliseconds = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return milliseconds;
    }




    /**
     * 數字補 0
     * @param num  補 0 的數字
     * @param zero 需要補幾個 0
     * @return
     */
    public static String leftPad(int num,int zero)
    {
        String padded = String.format("%0" + zero + "d" , num);
        return padded;
    }


    /**
     * 取得小數點後 位數
     * @param num 浮點數
     * @param point 取到的位數
     * @return
     */
    public static String limit_decimal_point(double num,int point)
    {

        String result = String.format("%." + point + "f",num);
        return result;
    }

    /**
     *  WGS84 轉 TWD97
     * @param Latitude
     * @param Longitude
     * @return
     */
    public static double[] WGS84ToTWD97(double Latitude,double Longitude)
    {
        double[] trans_lat_lng = new double[2];
        CoordTransStruct ct = CoordinateTransformation.LAL97_TO_TM297(Latitude,Longitude);

        trans_lat_lng[0] = ct.getLon();
        trans_lat_lng[1] = ct.getLat();

        return trans_lat_lng;

    }

    /**
     *  浮點數 三位一撇
     * @param number
     * @return
     */
    public static String AddComma(double number) {
        DecimalFormat df = new DecimalFormat("#,###.000000");
        return df.format(number);
    }

    /**
     * 整數 三位一撇
     * @param number
     * @return
     */
    public static String AddComma(long number) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(number);
    }

}