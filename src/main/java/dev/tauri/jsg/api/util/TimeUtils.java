package dev.tauri.jsg.api.util;

import java.util.Calendar;

public class TimeUtils {
    public static boolean isAprilFirst() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1 && Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL;
    }
}
