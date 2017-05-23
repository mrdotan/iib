package com.iso8583;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class ISO8583Utils {
	public static int getRandomNumberInRange(int min, int max) {
        /*
        1000000 - 9999999 
         */
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static String getDateTimeGMT(String format, String timeZone) throws Exception {
        /*
         MMddHHmmss
         MMdd
         hhmmss
         */
 /*
        GMT+0
        GMT+7
         */
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(format);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone(timeZone));
        return dateFormatGmt.format(new Date());
    }
    
   //public static String buidISOTV
}
