package com.llsoft.entestserver;

import java.util.Calendar;


public class TimeUtils {


  public static long getCurrentTime() {
    return Calendar.getInstance().getTime().getTime();
  }

  public static long calDelay(int hour, int minute, int second) {

    Calendar calendar = Calendar.getInstance();

    long currentTime = calendar.getTime().getTime();

    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    calendar.set(year, month, day, hour, minute, second);

    long targetTime = calendar.getTime().getTime();

    long delay = targetTime - currentTime;

    return delay;
  }

  public static long calPeriod(int seconds) {
    return seconds*1000;
  }

}
