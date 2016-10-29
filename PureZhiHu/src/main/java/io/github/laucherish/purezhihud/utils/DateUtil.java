package io.github.laucherish.purezhihud.utils;

/**
 * Created by laucherish on 16/3/28.
 */
public class DateUtil {
    private DateUtil() {
    }

    public static String formatDate(String date) {
        String dateFormat = null;
        try {
            dateFormat = date.substring(4, 6) + "月" + date.substring(6, 8) + "日";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateFormat;
    }
}
