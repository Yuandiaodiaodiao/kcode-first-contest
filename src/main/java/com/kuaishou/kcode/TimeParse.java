package com.kuaishou.kcode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeParse {
    public static void main(String[] args){
        try {
            long x=1592299980131L;
            System.out.println((long)(format.parse("2020-06-15 11:07").getTime()));
            System.out.println(format.format(new Date(x)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static int sixMonthStartTime;

    static {
        try {
            sixMonthStartTime = (int)(format.parse("2020-06-01 00:00").getTime()/60000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static int parse(String time){


        return sixMonthStartTime+((time.charAt(9)-'0')+(time.charAt(8)-'0')*10-1)*1440+((time.charAt(11)-'0')*10+(time.charAt(12)-'0'))*60+(time.charAt(14)-'0')*10+(time.charAt(15)-'0');
    }
    public static int old_parse(String time){
        int data2=time.charAt(8)-'0';
        int data1=time.charAt(9)-'0';
        int day=data1+data2*10;
        int hour2=time.charAt(11)-'0';
        int hour1=time.charAt(12)-'0';
        int hour=hour2*10+hour1;
        int min2=time.charAt(14)-'0';
        int min1=time.charAt(15)-'0';
        int min=min2*10+min1;

        int minutes=0;
        minutes+=(day-1)*1440;
        minutes+=hour*60;
        minutes+=min;
        minutes+=sixMonthStartTime;
        return minutes;
    }
    public static int testParseData(String time){
        int timea=parse(time);
        try {
            int timeb= (int)((format.parse(time).getTime())/60000);
            System.out.println("timea= "+timea+ " timeb= "+ timeb);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timea;
    }
}
