package com.nata.xdroid.hooks;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import com.nata.xdroid.notifier.CommonNotice;
import com.nata.xdroid.notifier.Notifier;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

import static de.robv.android.xposed.XposedBridge.log;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by Calvin on 2016/11/21.
 */

public class CalendarHook implements Hook {
    private Context context;

    public CalendarHook(Context context) {
        this.context = context;
    }


    @Override
    public void hook(ClassLoader loader) {
        findAndHookMethod("android.content.ContentResolver", loader, "query", Uri.class, String[].class, String.class, String[].class, String.class
                , new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("afterHookedMethod: " + "query");
                        Uri uri = (Uri) param.args[0];
                        Cursor cursor = (Cursor) param.getResult();
                        processURI(uri, cursor);
                    }
                });
    }

    private void processURI(Uri uri, Cursor cursor) {
        log(uri.toString());

        // 日历相关
        boolean isCalendarEventURI = uri.equals(CalendarContract.Events.CONTENT_URI);
        boolean isCalendarRemindersURI = uri.equals(CalendarContract.Reminders.CONTENT_URI);
        boolean isCalendarCalendarsURI = uri.equals(CalendarContract.Calendars.CONTENT_URI);
        boolean isCalendarInstancesURI = uri.equals(CalendarContract.Instances.CONTENT_URI);
        boolean isCalendarAttendeesURI = uri.equals(CalendarContract.Attendees.CONTENT_URI);

        boolean isCalendar = isCalendarEventURI || isCalendarRemindersURI || isCalendarCalendarsURI || isCalendarInstancesURI || isCalendarAttendeesURI;

        if (isCalendar && cursor.getCount() == 0) {
            Notifier.notice(context, CommonNotice.CALENDAR);
        }
    }
}
