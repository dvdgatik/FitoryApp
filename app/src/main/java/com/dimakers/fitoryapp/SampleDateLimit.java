package com.dimakers.fitoryapp;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

public class SampleDateLimit {
    public static void main(String args[]) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( stringToDate("2018-12-14"));
        // calendar.set(Calendar.WEEK_OF_MONTH, calendar.getActualMaximum(Calendar.WEEK_OF_MONTH));
        System.out.println("Today : " + calendar.getTime());
        System.out.println("First Day of Last Week: " + SampleDateLimit.firstDayOfLastWeek(calendar).getTime());
        System.out.println("Last Day of Last Week: " + SampleDateLimit.lastDayOfLastWeek(calendar).getTime());
        // if (SampleDateLimit.firstDayOfLastWeek(calendar).getTime().compareTo(calendar.getTime())==1) {
        //     System.out.println("Today IS First Day of Last Week!!!");
        // }
        // if (SampleDateLimit.lastDayOfLastWeek(calendar).getTime().compareTo(calendar.getTime())==1) {
        //     System.out.println("Today IS Last Day of Last Week!!!");
        // }
        System.out.println("Validity: " + SampleDateLimit.lastDayOfLastWeek(calendar).getTime());
        // System.out.println("Today is before Last Day of Last Week: " + SampleDateLimit.lastDayOfLastWeek(calendar).getTime().after(calendar.getTime()));
        if (SampleDateLimit.firstDayOfLastWeek(calendar).getTime().before(calendar.getTime()) &&  SampleDateLimit.lastDayOfLastWeek(calendar).getTime().after(calendar.getTime())) {
            System.out.println("Today is in Range of Last Week");
        }
        // System.out.println("Day of the week: "+calendar.get(Calendar.DAY_OF_WEEK));
        // System.out.println("Days of the month: "+calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    }

    public static Calendar firstDayOfLastWeek(Calendar c)
    {
        c = (Calendar) c.clone();
        // last week
        c.set(Calendar.WEEK_OF_MONTH, c.getActualMaximum(Calendar.WEEK_OF_MONTH));
        // first day
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        c.add(Calendar.DAY_OF_YEAR,-1);
        return c;
    }

    public static Calendar lastDayOfLastWeek(Calendar c)
    {
        c = (Calendar) c.clone();
        // last day
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        c.add(Calendar.DAY_OF_YEAR,1);
        return c;
    }


    public static Date stringToDate(String string) {
        String dtStart = string;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(dtStart);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date("");
    }
}
