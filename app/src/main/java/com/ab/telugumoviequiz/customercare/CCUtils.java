package com.ab.telugumoviequiz.customercare;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;

import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.PostTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.constants.CustomerCareReqState;
import com.ab.telugumoviequiz.constants.CustomerCareReqType;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class CCUtils {
    public static String encodeCCExtraValues(HashMap<String,String> extraValues) {

        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, String> mapEntry : extraValues.entrySet()) {
            String key = mapEntry.getKey();
            String value = mapEntry.getValue();
            stringBuilder.append(key);
            stringBuilder.append("=");
            stringBuilder.append(value);
            stringBuilder.append(";");
        }
        return stringBuilder.toString();
    }

    public static HashMap<String,String> decodeCCExtraValues(String extraValues) {
        extraValues = extraValues.trim();
        StringTokenizer firstTokenizer = new StringTokenizer(extraValues, ";");
        HashMap<String, String> extraValuesMap = new HashMap<>();
        while (firstTokenizer.hasMoreTokens()) {
            StringTokenizer secondTokenizer = new StringTokenizer(firstTokenizer.nextToken(), "=");
            String key = secondTokenizer.nextToken();
            String value = secondTokenizer.nextToken();
            extraValuesMap.put(key, value);
       }
       return extraValuesMap;
    }

    public static boolean validateDate(int dayOfMonth, int monthOfYear,
                                       int year, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear - 1, dayOfMonth, 0, 0, 0);
        long loadedTime = calendar.getTimeInMillis();
        return (System.currentTimeMillis() - loadedTime) <= (hours * 60 * 60 * 1000);
    }

    public static void showDateChooser(Context context,
                                       DatePickerDialog.OnDateSetListener listener) {

        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        DatePickerDialog picker = new DatePickerDialog(context, android.R.style.Theme_Holo_Dialog,
                listener, year, month, day);
        picker.getDatePicker().setSpinnersShown(true);
        picker.getDatePicker().setCalendarViewShown(false);
        picker.show();
    }

    public static void createdCCTicket(int ccTktType, CallbackResponse listener,
                                       String extraDetails, Activity activity) {

        CustomerTicket customerTicket = new CustomerTicket();
        customerTicket.setUserId(UserDetails.getInstance().getUserProfile().getId());
        customerTicket.setRequestType(ccTktType);
        customerTicket.setExtraDetails(extraDetails);

        PostTask<CustomerTicket,Long> createCCTask = Request.getCreateCCTask();
        createCCTask.setCallbackResponse(listener);
        createCCTask.setPostObject(customerTicket);
        createCCTask.setActivity(activity, "Processing...");
        Scheduler.getInstance().submit(createCCTask);
    }
}
