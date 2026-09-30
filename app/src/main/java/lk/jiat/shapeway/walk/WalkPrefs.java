package lk.jiat.shapeway.walk;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WalkPrefs {
    private static final String PREF_NAME = "walk_prefs";

    private static final String KEY_TARGET = "daily_target";
    private static final String KEY_TODAY_STEPS = "today_steps";
    private static final String KEY_LAST_DATE = "last_date";
    private static final String KEY_SENSOR_BASE = "sensor_base";
    private static final String KEY_SESSION_RUNNING = "session_running";

    private final SharedPreferences prefs;

    public WalkPrefs(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    public void ensureTodayReset() {
        String today = getTodayDate();
        String savedDate = prefs.getString(KEY_LAST_DATE, "");

        if (!today.equals(savedDate)) {
            prefs.edit()
                    .putString(KEY_LAST_DATE, today)
                    .putInt(KEY_TODAY_STEPS, 0)
                    .putFloat(KEY_SENSOR_BASE, -1f)
                    .putBoolean(KEY_SESSION_RUNNING, false)
                    .apply();
        }
    }

    public int getDailyTarget() {
        return prefs.getInt(KEY_TARGET, 10000);
    }

    public void setDailyTarget(int target) {
        prefs.edit().putInt(KEY_TARGET, target).apply();
    }

    public int getTodaySteps() {
        ensureTodayReset();
        return prefs.getInt(KEY_TODAY_STEPS, 0);
    }

    public void setTodaySteps(int steps) {
        ensureTodayReset();
        prefs.edit().putInt(KEY_TODAY_STEPS, Math.max(0, steps)).apply();
    }

    public int getRemainingSteps() {
        ensureTodayReset();
        return Math.max(0, getDailyTarget() - getTodaySteps());
    }

    public float getSensorBase() {
        ensureTodayReset();
        return prefs.getFloat(KEY_SENSOR_BASE, -1f);
    }

    public void setSensorBase(float value) {
        ensureTodayReset();
        prefs.edit().putFloat(KEY_SENSOR_BASE, value).apply();
    }

    public boolean isSessionRunning() {
        ensureTodayReset();
        return prefs.getBoolean(KEY_SESSION_RUNNING, false);
    }

    public void setSessionRunning(boolean running) {
        ensureTodayReset();
        prefs.edit().putBoolean(KEY_SESSION_RUNNING, running).apply();
    }
}
