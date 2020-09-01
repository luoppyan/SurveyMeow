package com.meow.comp6442_groupproject.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    /**
     * 2020-05-05 22:11:00
     */
    public static String FORMAT_YMDHMS = "yyyy/MM/dd HH:mm";

    /**
     *
     * date to stamp
     * @param time
     * @return stamp
     */
    public static Date dateToStamp(String time) throws ParseException {
        SimpleDateFormat sdr = new SimpleDateFormat(FORMAT_YMDHMS);
        Date date = sdr.parse(time);
        return date;
    }

    /**
     * stamp to date
     * @param date
     * @return date
     */
    public static String stampToDate(Date date){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_YMDHMS);
        res = simpleDateFormat.format(date);
        return res;
    }

}
