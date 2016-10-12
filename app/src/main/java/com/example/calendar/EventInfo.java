package com.example.calendar;

import android.provider.BaseColumns;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by ryo on 2016/10/01.
 */
public class EventInfo {
    public static final String DB_NAME = "events";
    public static final String ID = BaseColumns._ID;
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String WHERE = "gd_where";
    public static final String END_TIME = "gd_when_endTime";
    public static final String START_TIME = "gd_when_startTime";
    public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private long id;
    private String title;
    private String where;
    private GregorianCalendar start;
    private GregorianCalendar end;
    private String content;

    public static GregorianCalendar toDateTimeCalendar(String dateTimeString) {
        try {
            Log.d("dateTimeString", dateTimeString);
            Date d = dateTimeFormat.parse(dateTimeString);
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(d);
            return c;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static GregorianCalendar toDateCalendar(String dateString) {
        try {
            Log.d("dateString", dateString);
            Date d = dateFormat.parse(dateString);
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(d);
            return c;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static GregorianCalendar toTimeCalendar(String timeString) {
        try {
            Log.d("timeString", timeString);
            Date d = timeFormat.parse(timeString);
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(d);
            return c;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        if (start.get(Calendar.HOUR_OF_DAY) == 0 && start.get(Calendar.MINUTE) == 0) {
            GregorianCalendar startCal = (GregorianCalendar)start.clone();
            startCal.add(Calendar.DAY_OF_MONTH, 1);
            if (startCal.equals(end)) {
                return getTitle() + "\n"
                        + getStartDateString() + "\n"
                        + getWhere() + "\n"
                        + getContent();
            }
        }
        return getTitle() + "\n"
                + getStartDateString() + " " + getStartTimeString() + "\n"
                + getEndDateString() + " " + getEndTimeString() + "\n"
                + getWhere() + "\n"
                + getContent();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public GregorianCalendar getStart() {
        return start;
    }

    public String getStartDateString() {
        return dateFormat.format(start.getTime());
    }

    public String getStartTimeString() {
        return timeFormat.format(start.getTime());
    }

    public void setStart(GregorianCalendar start) {
        this.start = start;
    }

    public void setStart(String start) {
        this.start = toDateTimeCalendar(start);
    }

    public GregorianCalendar getEnd() {
        return end;
    }

    public String getEndDateString() {
        return dateFormat.format(end.getTime());
    }

    public String getEndTimeString() {
        return timeFormat.format(end.getTime());
    }

    public void setEnd(GregorianCalendar end) {
        this.end = end;
    }

    public void setEnd(String end) {
        this.end = toDateTimeCalendar(end);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}