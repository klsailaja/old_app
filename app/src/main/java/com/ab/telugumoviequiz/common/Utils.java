package com.ab.telugumoviequiz.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.constants.MoneyCreditStatus;
import com.ab.telugumoviequiz.games.LocalGamesManager;
import com.ab.telugumoviequiz.help.HelpMessage;
import com.ab.telugumoviequiz.help.HelpReader;
import com.ab.telugumoviequiz.help.HelpTopic;
import com.ab.telugumoviequiz.main.ClientInitializer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Utils {
    private static final String EMPTY_MESSAGE = "Value is empty for : %s";
    private static final String MAX_LENGTH = "Value exceeds %d for : %s";
    private static final String MIN_LENGTH = "Minimum length is %d for : %s";
    private static final String ONLY_NUMERICS = "Only numeric values allowed for : %s";

    public static int screenWidth;
    public static int screenHeight;

    public static final String WAIT_MESSAGE = "Processing...Please Wait!";

    public static boolean isEmpty(String str) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }
        if (str.length() == 0) {
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
            return String.format(Locale.ENGLISH, MIN_LENGTH, minLen, componentName);
        if ((maxLen != -1) && (str.length() > maxLen)) {
            return String.format(Locale.ENGLISH, MAX_LENGTH, maxLen, componentName);
        }
        if (onlyNumerics) {
            if (!TextUtils.isDigitsOnly(str)) {
                return String.format(ONLY_NUMERICS, componentName);
            }
        }
        return null;
    }

    public static void showConfirmationMessage(String title,String message, final Context context,
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
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialogInterface, i) -> {
            alertDialog.hide();
            alertDialog.dismiss();
            alertDialog.cancel();
        });
        alertDialog.show();

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
        alertDialog.show();
    }

    public static void showUserIssue(String title, String message, final Context context,
                                   final DialogAction dialogAction, int id, Object userObject) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Open Ticket", (dialogInterface, i) -> {
            alertDialog.hide();
            alertDialog.dismiss();
            alertDialog.cancel();
            if (dialogAction != null) {
                dialogAction.doAction(id, userObject);
            }
        });
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
        long seconds = (timeTaken / 1000) % 60;
        long milliseconds = timeTaken - (minutes * 60 * 1000) - (seconds * 1000);

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
        stringBuilder.append(str);
        stringBuilder.append(" s: ");

        str = String.valueOf(milliseconds);
        if (str.length() == 1) {
            str = "00" + str;
        } else if (str.length() == 2) {
            str = "0" + str;
        }
        stringBuilder.append(str);
        stringBuilder.append(" ms ");
        return stringBuilder.toString();
    }

    public static void clearState() {
        screenWidth = 0;
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

    public static AlertDialog getProgressDialog(Activity parentActivity, String waitingMessage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parentActivity);
        final View customLayout = parentActivity.
                getLayoutInflater().inflate(R.layout.progress_dialog, null);
        TextView waitingMsg = customLayout.findViewById(R.id.status_bar_msg);
        if (waitingMessage != null) {
            waitingMsg.setText(waitingMessage);
        }
        alertDialogBuilder.setView(customLayout);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        // create and show the alert dialog
        return alertDialog;
    }


    /*public static AlertDialog getProgressDialog(Context context, String waitingMessage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        //alertDialogBuilder.setTitle("Information");
        alertDialogBuilder.setMessage(waitingMessage).setCancelable(false);
        return alertDialogBuilder.create();
    }*/

    public static String getHelpMessage(String key, int localeType) {
        String value = HelpReader.getInstance().getString(key, localeType);
        if (value == null) {
            value = "";
        }
        return value;
    }

    public static List<HelpTopic> getHelpTopics(List<String> helpTopicNames, int localeType) {
        List<HelpTopic> helpTopicList = new ArrayList<>();
        for (String helpTopicKeyStr : helpTopicNames) {
            int index = 1;
            List<HelpMessage> topicHelpMessages = new ArrayList<>();
            while (index <= 100) {
                String pointStrKey = helpTopicKeyStr + "_pt" + index;
                String pointSeverityKey = helpTopicKeyStr + "_pt" + index + "_s";
                String pointStrValue = getHelpMessage(pointStrKey, localeType);
                if (pointStrValue.length() == 0) {
                    break;
                }
                String pointSeverityValue = getHelpMessage(pointSeverityKey, localeType);
                int pointSeverityValueInt;
                try {
                    pointSeverityValueInt = Integer.parseInt(pointSeverityValue);
                } catch (NumberFormatException ex) {
                    pointSeverityValueInt = 1;
                }
                int secondIndex = 1;
                List<HelpMessage> secondLevelMsgs = new ArrayList<>();
                while (secondIndex <= 100) {
                   String subPointStrKey = pointStrKey + "_sec" + secondIndex;
                   String subPointSeverityKey = subPointStrKey + index + "_s";
                   System.out.println("subPointStrKey is : " + subPointStrKey);
                   String subPointStrValue = getHelpMessage(subPointStrKey, localeType);
                   if (subPointStrValue.length() == 0) {
                       System.out.println("subPointStrKey is break: " + subPointStrKey);
                       break;
                   }
                   String secondSeverityValue = getHelpMessage(subPointSeverityKey, localeType);
                   int secondSeverityValueInt;
                   try {
                       secondSeverityValueInt = Integer.parseInt(secondSeverityValue);
                   } catch (NumberFormatException ex) {
                       secondSeverityValueInt = 1;
                   }
                   HelpMessage topicSecondLevelMessages = new HelpMessage(subPointStrValue, secondSeverityValueInt);
                   secondLevelMsgs.add(topicSecondLevelMessages);
                   secondIndex++;
                }
                HelpMessage topicHelpMsg = new HelpMessage(pointStrValue, pointSeverityValueInt);
                topicHelpMsg.setSecondLevelMessages(secondLevelMsgs);
                topicHelpMessages.add(topicHelpMsg);
                index++;
            }
            String topicName = getHelpMessage(helpTopicKeyStr, localeType);
            HelpTopic helpTopic = new HelpTopic(topicName, topicHelpMessages);
            helpTopicList.add(helpTopic);
        }
        return helpTopicList;
    }
    public static void shutdown(String baseURL) {
        Scheduler.getInstance().shutDown();
        GetTask.IGNORE = true;
        PostTask.IGNORE = true;
        Request.baseUri = baseURL;
        ClientInitializer.destroy();
        Log.d("Utils", "ServerErrorHandler : before LocalGamesManager destroy");
        LocalGamesManager.getInstance().destroy();
        ShowHelpFirstTimer.getInstance().destroy();
    }

    public static void clientReset(String baseURL) {
        GetTask.IGNORE = false;
        PostTask.IGNORE = false;
        Request.baseUri = baseURL;
    }

    public static String getHistoryViewMoneyCreditStatusMsg(int state) {
        String msg = "";
        if (state == MoneyCreditStatus.ALL_SUCCESS.getId()) {
            msg = "Winners money credited status: SUCCESS" ;
        } else if (state == MoneyCreditStatus.ALL_FAIL.getId()) {
            // All Records Fail
            msg = "Winners money credited status: FAIL" ;
        } else if (state == MoneyCreditStatus.PARTIAL_RESULTS.getId()) {
            msg = "Winners money credited status: FAIL" ;
        } else if (state == MoneyCreditStatus.IN_PROGRESS.getId()) {
            msg = "Winners money credited status: In-Progress" ;
        }
        return msg;
    }

    public static String getMoneyCreditStatusMessage(int state) {
        String msg = "";
        if (state == MoneyCreditStatus.ALL_SUCCESS.getId()) {
            // All Records success
            msg = "Winners money credited status: SUCCESS" ;
        } else if (state == MoneyCreditStatus.ALL_FAIL.getId()) {
            // All Records Fail
            msg = "Winners money credited status: FAIL \n" +
                    "Customer Tickets Raised by the app for this Issue. Please check";
        } else if (state == MoneyCreditStatus.PARTIAL_RESULTS.getId()) {
            msg = "Winners money credited status: FAIL \n" +
                    "Please Check in MyTransaction view and File a Customer Ticket " +
                    "if win money not credited";
        }
        return msg;
    }

    public static void showSuccessDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View view = LayoutInflater.
                from(context).inflate(
                R.layout.layout_success_dailog,
                null
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText("Success");
        ((TextView) view.findViewById(R.id.textMessage)).setText("Msg");
        ((Button) view.findViewById(R.id.buttonAction)).setText("Ok");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.done);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
            }
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
    public static void showWarningDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_warning_dailog,
                null
        );
        builder.setView(view);
        String WIN_MONEY_FREE_GAME_MSG = "No Win Money for free game. Win Money Credit result will be shown for paid games. \n"
                + "If not credited for some reason, Customer Ticket is created automatically and \n"
                + " resolved within 3-5 days";

        ((TextView) view.findViewById(R.id.textTitle)).setText("Warning");
        ((TextView) view.findViewById(R.id.textMessage)).setText(WIN_MONEY_FREE_GAME_MSG);
        ((Button) view.findViewById(R.id.buttonYes)).setText("WarnYes");
        ((Button) view.findViewById(R.id.buttonNo)).setText("WarnNo");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.warning);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Toast.makeText(context, "Yes", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Toast.makeText(context, "No", Toast.LENGTH_SHORT).show();
            }
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    public static void showErrorDialog(Context context, boolean showTicket){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_error_dailog, null
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText("Error");
        ((TextView) view.findViewById(R.id.textMessage)).setText("ErrText");
        ((Button) view.findViewById(R.id.buttonYes)).setText("Ok");
        ((Button) view.findViewById(R.id.buttonNo)).setText("Open Ticket");
        if (!showTicket) {
            ((Button) view.findViewById(R.id.buttonNo)).setVisibility(View.GONE);
        }
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.error);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}
