package com.rm.mydiet.utils;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alex
 */
public class TimeUtil {

    private static final long FOUR_HOURS = 14400000L;

    public static void setAlarm(Context context) {
//        Intent alarmIntent = new Intent(context, CurrencyUpdateReceiver.class);
//        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                context,
//                0,
//                alarmIntent,
//                0
//        );
//
//        manager.setRepeating(
//                AlarmManager.RTC_WAKEUP,
//                System.currentTimeMillis(),
//                FOUR_HOURS,
//                pendingIntent
//        );
    }

    public static long unixTime() {

        return System.currentTimeMillis()/1000;
    }

    public static String getDay(long unix) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d", new Locale("en", "US"));
        Date d = new Date();
        String resDate;

        d.setTime(unix * 1000);
        dateFormat.applyPattern("MMMM d");

        resDate = dateFormat.format(d);

        Log.d("TimeUtil", "getDay - resDate: "
                + resDate);

        return resDate;
    }

    public static String getTime(long unix) {

        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getInstance();
        Date d = new Date();
        String resDate;

        d.setTime(unix * 1000);
        dateFormat.applyPattern("h:mm");

        resDate = dateFormat.format(d);

        Log.d("TimeUtil", "getTime - resDate: "
                + resDate);

        return resDate;
    }

    public static long getStartOfTheDay(long time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time*1000);

        int year    = calendar.get(Calendar.YEAR);
        int month   = calendar.get(Calendar.MONTH);
        int day     = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.set(year, month, day, 0, 0, 0);

        long result = calendar.getTimeInMillis()/1000;

        Log.d("TimeUtil", "Today in millis: " + result);

        return result;
    }

    public static long getToday() {
        return getStartOfTheDay(unixTime());
    }

    public static String formatBirthDate(long birth) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy г.", new Locale("ru", "RU"));
        Date d = new Date();
        String resDate;

        d.setTime(birth);
        dateFormat.applyPattern("d MMM yyyy г.");

        resDate = dateFormat.format(d);

        Log.d("TimeUtil", "getDay - resDate: "
                + resDate);

        return resDate;
    }

    public static long getDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
        return calendar.getTimeInMillis();
    }
}
