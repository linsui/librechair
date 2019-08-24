/**
 * Helper class provides interoperability with Android AlarmClock database. Based on
 * com.android.alarmclock.Alarms class
 * <p/>
 * Copyright (c) 2005-2008, The Android Open Source Project Copyright (c) 2009, Alexander Kosenkov
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p/>
 * Project home: http://code.google.com/p/android-alarmclock-database/
 */

package ch.deletescape.lawnchair.alarms;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import java.util.Calendar;

public class AlarmDatabase {

    public static final Uri ALARM_URI = Uri.parse("content://com.android.alarmclock/alarm");

    private final ContentResolver mContentResolver;
    private final ContentObserver mContentObserver;

    private static int column_id = -1;
    private static int column_hour;
    private static int column_minutes;
    private static int column_daysofweek;
    private static int column_vibrate;
    private static int column_message;
    private static int column_alert;

    /**
     * Represents single Alarm Clock record in a database. Immutable
     */
    public static class Record {

        public final int id;
        public final boolean vibrate;
        public final String message;
        public final String audio;

        public final int hour;
        public final int minute;

        private Calendar nearestAlarmDate;
        private final int daysOfWeek;

        public Record(Cursor cur) {
            if (column_id == -1) {
                column_id = cur.getColumnIndexOrThrow("_id");
                column_hour = cur.getColumnIndexOrThrow("hour");
                column_minutes = cur.getColumnIndexOrThrow("minutes");
                column_daysofweek = cur.getColumnIndexOrThrow("daysofweek");
                column_vibrate = cur.getColumnIndexOrThrow("vibrate");
                column_message = cur.getColumnIndexOrThrow("message");
                column_alert = cur.getColumnIndexOrThrow("alert");
            }

            // Get the field values
            this.hour = cur.getInt(column_hour);
            this.minute = cur.getInt(column_minutes);
            this.daysOfWeek = cur.getInt(column_daysofweek);
            this.id = cur.getInt(column_id);
            this.vibrate = cur.getInt(column_vibrate) == 1;
            this.message = cur.getString(column_message);
            this.audio = cur.getString(column_alert);
        }

        public Calendar getNearestAlarmDate() {
            if (nearestAlarmDate == null) {
                this.nearestAlarmDate = calculateNextAlarm(hour, minute, daysOfWeek,
                        System.currentTimeMillis());
            }
            return nearestAlarmDate;
        }

        public Uri getRecordUri() {
            return Uri.withAppendedPath(ALARM_URI, String.valueOf(id));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Record record = (Record) o;

            if (daysOfWeek != record.daysOfWeek) {
                return false;
            }
            if (hour != record.hour) {
                return false;
            }
            if (id != record.id) {
                return false;
            }
            if (minute != record.minute) {
                return false;
            }
            if (vibrate != record.vibrate) {
                return false;
            }
            if (audio != null ? !audio.equals(record.audio) : record.audio != null) {
                return false;
            }
            if (message != null ? !message.equals(record.message) : record.message != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (vibrate ? 1 : 0);
            result = 31 * result + (message != null ? message.hashCode() : 0);
            result = 31 * result + (audio != null ? audio.hashCode() : 0);
            result = 31 * result + hour;
            result = 31 * result + minute;
            result = 31 * result + daysOfWeek;
            return result;
        }
    }

    /**
     * Creates database connection with no updates subscription. You'd probably need to subscribe for updates
     *
     * @param contentResolver get from context
     */
    public AlarmDatabase(ContentResolver contentResolver) {
        this(contentResolver, (ContentObserver) null);
    }

    /**
     * Creates database connection and subscribes for updates
     *
     * @param contentResolver get from context
     * @param contentObserver Runnable to be called if database changes
     * @param handler
     */
    public AlarmDatabase(ContentResolver contentResolver, final Runnable contentObserver,
            final Handler handler) {
        this(contentResolver, new ContentObserver(handler) {
            @Override
            public boolean deliverSelfNotifications() {
                return false;
            }

            @Override
            public void onChange(boolean selfChange) {
                contentObserver.run();
            }
        });
    }

    /**
     * Creates database connection and subscribes for updates
     *
     * @param contentResolver get from context
     * @param contentObserver will be called for external database updates
     */
    public AlarmDatabase(ContentResolver contentResolver, ContentObserver contentObserver) {
        this.mContentResolver = contentResolver;

        if (contentObserver != null) {
            this.mContentObserver = contentObserver;
            contentResolver.registerContentObserver(ALARM_URI, true, this.mContentObserver);
        } else {
            this.mContentObserver = null;
        }
    }

    public Record getAlarmById(int alarmId) {
        final Cursor cur = mContentResolver.query(
                ALARM_URI,
                null,
                "_id=?",
                new String[]{String.valueOf(alarmId)},
                null);

        if (!cur.moveToFirst()) {
            Log.w("AlarmDatabase", "no record for id " + alarmId);
            return null;
        }
        Record entity = new Record(cur);
        cur.close();

        return entity;
    }

    public Record getNearestEnabledAlarm() throws UnsupportedOperationException {
        return getNearestEnabledAlarm(System.currentTimeMillis());
    }

    public Record getNearestEnabledAlarm(long minimumTime) throws UnsupportedOperationException {
        final Cursor cur = mContentResolver.query(
                ALARM_URI,
                null,
                "enabled=?",
                new String[]{"1"},
                null);

        if (cur == null) {
            Log.w("AlarmDatabase", "Cannot resolve provider for " + ALARM_URI);
            throw new UnsupportedOperationException();
        }

        if (!cur.moveToFirst()) {
            Log.d("AlarmDatabase", "No enabled alarms");
            return null;
        }

        new Record(cur); // NB: side-effect!!
        Record nearest = null;

        do {
            // Get the field values
            int hour = cur.getInt(column_hour);
            int minute = cur.getInt(column_minutes);
            int daysOfWeek = cur.getInt(column_daysofweek);

            final Calendar cal = calculateNextAlarm(hour, minute, daysOfWeek, minimumTime);

            if ((nearest == null || cal.compareTo(nearest.getNearestAlarmDate()) == -1)
                    && cal.getTimeInMillis() > minimumTime) {
                nearest = new Record(cur);
                nearest.nearestAlarmDate = cal;
            }

        } while (cur.moveToNext());
        cur.close();
        return nearest;
    }

    public static Calendar calculateNextAlarm(int hour, int minute, int daysOfWeek,
            long minimumTime) {

        // newRecord with now
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(minimumTime);

        int nowHour = c.get(Calendar.HOUR_OF_DAY);
        int nowMinute = c.get(Calendar.MINUTE);

        // if alarmclock is behind current time, advance one day
        if (hour < nowHour ||
                hour == nowHour && minute <= nowMinute) {
            c.add(Calendar.DAY_OF_YEAR, 1);
        }
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        int addDays = getNextAlarm(c, daysOfWeek);
		/* Log.v("** TIMES * " + c.getTimeInMillis() + " hour " + hour +
				   " minute " + minute + " dow " + c.get(Calendar.DAY_OF_WEEK) + " from now " +
				   addDays); */
        if (addDays > 0) {
            c.add(Calendar.DAY_OF_WEEK, addDays);
        }
        return c;
    }

    @Deprecated
    public static Intent changeAlarmSettings() {
        return changeAlarmSettings(null);
    }

    /**
     * Call startActivity() on result of this method to show default UI for changing Alarm Clock settings
     *
     * @param packageManager may be null
     * @return Intent for changing alarmclock settings
     */
    public static Intent changeAlarmSettings(PackageManager packageManager) {
        final Intent i = new Intent();
        i.setAction(Intent.ACTION_MAIN);
        i.setClassName("com.android.alarmclock", "com.android.alarmclock.AlarmClock");
        if (packageManager == null) {
            return i;
        }

        ResolveInfo resolved = packageManager.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolved != null) {
            return i; // yes, we have default Alarm Clock Application
        }

        i.setClassName("com.htc.android.worldclock",
                "com.htc.android.worldclock.WorldClockTabControl");
        resolved = packageManager.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolved != null) {
            return i; // HTC custom UI
        }

        Log.e("AlarmDatabase", "No known Alarm Clock UI provider");
        return null;

//        i.setClassName("com.android.alarmclock", "com.android.alarmclock.SetAlarm"); - private access
//        i.putExtra(Alarms.ID, next_alarm_id);
    }

    public static Intent startNightClock() {
        final Intent i = new Intent();
        i.setAction("android.alarmclock.NightClockUI");
        i.addCategory(Intent.CATEGORY_DEFAULT);
        return i;
    }

    /**
     * returns number of days from today until next alarmclock
     *
     * @param c     must be set to today
     * @param mDays alarmclock-clock internal days representation
     * @return days count
     */
    private static int getNextAlarm(Calendar c, int mDays) {
        if (mDays == 0) {
            return -1;
        }
        int today = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7;

        int day, dayCount;
        for (dayCount = 0; dayCount < 7; dayCount++) {
            day = (today + dayCount) % 7;
            if ((mDays & (1 << day)) > 0) {
                break;
            }
        }
        return dayCount;
    }

    @Override
    protected void finalize() throws Throwable {
        if (mContentResolver != null && mContentObserver != null) {
            mContentResolver.unregisterContentObserver(mContentObserver);
        }
        super.finalize();
    }
}