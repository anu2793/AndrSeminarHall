package vn.hanelsoft.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LamND on 11/09/2015.<br>
 * Pattern format:<br>
 * y = YEAR<br>
 * M = MONTH<br>
 * d = DAY<br>
 * H = HOUR<br>
 * h = hour use 12h - format<br>
 * m = MINUTE<br>
 * s = SECOND<br>
 */
public class DateTimeUtils {
    static final SimpleDateFormat format = new SimpleDateFormat();
    public static Date parse(String date, String pattern) {
        try {
            format.applyPattern(pattern);
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String format(Date date, String pattern) {
        format.applyPattern(pattern);
        return format.format(date);
    }

    public static String format(String dateFromServer, String patternParse, String patternFormat) {
        return format(parse(dateFromServer, patternParse), patternFormat);
    }

    public static String convertTime(long time) {
        long tmp = (System.currentTimeMillis() - time) / 1000; //second
        if (tmp < 60)
            return tmp + " seconds ago";
        if ((tmp /= 60) < 60)
            return tmp + " minutes ago";
        if ((tmp /= 60) < 24)
            return tmp + " hours ago";
        if ((tmp /= 24) < 4)
            return tmp + " days go";
        return format(new Date(time), "dd/MM/yyyy");
    }
}

