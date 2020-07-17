package com.ukejee.das.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author .: Ukeje Emeka
 * @email ..: ukejee3@gmail.com
 * @created : 6/15/20
 */
public class AppUtils {

    private static String LOGGER_HANDLE = "DAS_APP";
    private static Boolean debugMode = true;
    private Activity activity;
    private Context context;

    public AppUtils(Activity activity) {
        context = activity;
        this.activity = activity;
    }

    public AppUtils(Context context) {
        this.context = context;
    }

    public static Date getCurrentDateTime() {
        Date currentDate = Calendar.getInstance().getTime();
        return currentDate;
    }

    private static String getLogger() {
        return LOGGER_HANDLE;
    }

    private static void debug(Level level, String log, String tag, Object... parameters) {
        if (log != null && debugMode) {

            String placeHolder = "\\{\\}";

            for (Object parameter : parameters) {
                log = log.replaceFirst(placeHolder, String.valueOf(parameter));
            }

            switch (level) {
                case INFO:
                    Log.i(tag, log);
                    break;
                case DEBUG:
                    Log.d(tag, log);
                    break;
                case ERROR:
                    Log.e(tag, log);
                    break;
                default:
                    Log.d(tag, log);
            }
        }

    }

    public static void info(String log, Object... parameters) {
        debug(Level.INFO, log, getLogger(), parameters);
    }

    public static void error(String log, Object... parameters) {
        debug(Level.ERROR, log, getLogger(), parameters);
    }

    public static void debug(String log, Object... parameters) {
        debug(Level.DEBUG, log, getLogger(), parameters);
    }

    public String getFormattedDateString(Date date) {

        try {

            SimpleDateFormat spf = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
            String dateString = spf.format(date);

            Date newDate = spf.parse(dateString);
            spf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
            return spf.format(newDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String generateHash(String password) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(password.getBytes());
            return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public void openKeyboard() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        }, 500);
    }

    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    enum Level {
        INFO,
        DEBUG,
        ERROR
    }
}
