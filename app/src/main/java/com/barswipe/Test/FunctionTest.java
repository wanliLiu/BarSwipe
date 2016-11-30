package com.barswipe.Test;

import java.util.Calendar;

/**
 * Created by Soli on 2016/11/30.
 */

public class FunctionTest {

    /**
     * @param month
     * @return
     */
    private String getRightStr(int month) {
        if (month <= 0)
            month = 12 + month;

        return month > 9 ? month + "" : "0" + month;
    }

    /**
     *
     */
    public String[] initRequstMonth(int month) {
        int requestNum = 4;
        String[] data = new String[requestNum];
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH) + 1;
////        int month = 12;
        int count = month - requestNum;

        for (int i = requestNum - 1; month > count; month--) {
            data[i] = (month <= 0 ? year - 1 : year) + " - " + getRightStr(month);
            i--;
        }

        return data;
    }
}
