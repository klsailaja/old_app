package com.ab.telugumoviequiz.common;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import android.app.AlertDialog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class Utils {
    private static final String EMPTY_MESSAGE = "Value is empty for : %s";
    private static final String MAX_LENGTH = "Value exceeds %d for : %s";
    private static final String MIN_LENGTH = "Minimum length is %d for : %s";
    private static final String ONLY_NUMERICS = "Ony numeric values allowed for : %s";

    public static int screenWidth;
    public static int screenHeight;

    public static boolean isEmpty(String str) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }
        return str.equalsIgnoreCase("null");
    }
    public static String fullValidate(String str, String componentName, boolean canBeEmpty, int minLen, int maxLen, boolean onlyNumerics) {
        if (isEmpty(str)) {
            if (!canBeEmpty) {
                return String.format(EMPTY_MESSAGE, componentName);
            } else {
                return null;
            }
        }
        if ((minLen != -1) && (str.length() < minLen))
            return String.format((Locale) null, MIN_LENGTH, minLen, componentName);
        if ((maxLen != -1) && (str.length() > maxLen)) {
            return String.format((Locale) null, MAX_LENGTH, maxLen, componentName);
        }
        if (onlyNumerics) {
            if (!TextUtils.isDigitsOnly(str)) {
                return String.format(ONLY_NUMERICS, componentName);
            }
        }
        return null;
    }

    public static void showMessage(String title, String message, final Context context,
                                   final DialogAction dialogAction, int id, Object userObject) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", (dialogInterface, i) -> {
            alertDialog.hide();
            alertDialog.dismiss();
            alertDialog.cancel();
            if (dialogAction != null) {
                dialogAction.doAction(id, userObject);
            }
        });
        /*alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialogInterface, i) -> {
            alertDialog.hide();
            alertDialog.dismiss();
            alertDialog.cancel();
        });*/
        alertDialog.show();
    }

    public static void showMessage(String title, String message, final Context context, final DialogAction dialogAction) {
        showMessage(title, message, context, dialogAction, -1, null);
    }

    public static String getPasswordHash(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        if (md == null) {
            return null;
        }
        md.update(password.getBytes());
        byte [] byteData = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte byteDatum : byteData) {
            sb.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }


    public static String getUserNotionTimeStr(long timeTaken, boolean includeMinutes) {
        if (timeTaken == 0) {
            return null;
        }
        long minutes = (timeTaken / 1000) / 60;
        long seconds = (timeTaken / 1000);
        timeTaken = timeTaken - (minutes * 60 * 1000) - (seconds * 1000);

        StringBuilder stringBuilder = new StringBuilder();

        if (includeMinutes) {
            String str = String.valueOf(minutes);
            if (str.length() == 1) {
                str = "0" + str;
            }
            stringBuilder.append(str);
            stringBuilder.append(" m: ");
        }
        String str = String.valueOf(seconds);
        if (str.length() == 1) {
            str = "0" + str;
        }
        stringBuilder.append(seconds);
        stringBuilder.append(" s: ");

        str = String.valueOf(timeTaken);
        if (str.length() == 1) {
            str = "00" + str;
        } else if (str.length() == 2) {
            str = "0" + str;
        }
        stringBuilder.append(str);
        stringBuilder.append(" ms ");
        return stringBuilder.toString();
    }

    public static int[] getScreenWidth(Context context) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
            screenHeight = size.y;
        }
        int[] points = new int[2];
        points[0] = screenWidth;
        points[1] = screenHeight;
        return points;
    }

    public static AlertDialog getProgressDialog(Context context, String waitingMessage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        //alertDialogBuilder.setTitle("Information");
        alertDialogBuilder.setMessage(waitingMessage).setCancelable(false);
        return alertDialogBuilder.create();
    }
}
