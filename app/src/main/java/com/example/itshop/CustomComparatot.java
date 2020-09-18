package com.example.itshop;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class CustomComparatot implements Comparator<orderdet> {
    @Override
    public int compare(orderdet o1, orderdet o2) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.valueOf(o1.getTimestamp()) * 1000);
        Date date1 = cal.getTime();

        Calendar cal1 = Calendar.getInstance(Locale.ENGLISH);
        cal1.setTimeInMillis(Long.valueOf(o2.getTimestamp()) * 1000);
        Date date2 = cal1.getTime();

        return date2.compareTo(date1);
    }
}