package com.ab.telugumoviequiz.withdraw;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.PostTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.main.Navigator;
import com.ab.telugumoviequiz.main.OTPDetails;

import java.util.ArrayList;

public class VerifyWDOTP extends BaseFragment implements CallbackResponse, View.OnClickListener {

    private final ArrayList<TextView> verifyCodeTextViewList = new ArrayList<>(4);

    public VerifyWDOTP() {
        super();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.wd_otp, container, false);
        TextView mailTextView = root.findViewById(R.id.editTextEmail);
        mailTextView.setText(UserDetails.getInstance().getUserProfile().getEmailAddress());
        mailTextView.setEnabled(false);

        Button verifyCodeButton = root.findViewById(R.id.wdverifyCode);
        verifyCodeButton.setEnabled(false);

        verifyCodeTextViewList.clear();
        verifyCodeTextViewList.add(root.findViewById(R.id.digit1));
        verifyCodeTextViewList.add(root.findViewById(R.id.digit2));
        verifyCodeTextViewList.add(root.findViewById(R.id.digit3));
        verifyCodeTextViewList.add(root.findViewById(R.id.digit4));

        for (int index = 0; index < verifyCodeTextViewList.size(); index ++) {
            TextView textView = verifyCodeTextViewList.get(index);
            textView.setOnKeyListener((view, i, keyEvent) -> {
                if ((i == KeyEvent.KEYCODE_DEL) &&
                        (keyEvent.getAction() == KeyEvent.ACTION_DOWN)) {
                    int componentPosition = 0;
                    if (view.getId() == R.id.digit2) {
                        componentPosition = 1;
                    } else if (view.getId() == R.id.digit3) {
                        componentPosition = 2;
                    } else if (view.getId() == R.id.digit4) {
                        componentPosition = 3;
                    }
                    verifyCodeTextViewList.get(componentPosition).setText("");
                    verifyCodeTextViewList.get(componentPosition).requestFocus();
                    return true;
                }
                return false;
            });
        }
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        handleListeners(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        handleListeners(null);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void handleListeners(View.OnClickListener listener) {
        View view = getView();
        if (view == null) {
            return;
        }

        Button sendCode = view.findViewById(R.id.wdsendCode);
        sendCode.setOnClickListener(listener);

        Button verifyCode = view.findViewById(R.id.wdverifyCode);
        verifyCode.setOnClickListener(listener);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.wdsendCode) {
            String mailId = UserDetails.getInstance().getUserProfile().getEmailAddress().trim();
            PostTask<String,String> sendCodeTask = Request.sendWDCodeTask();
            sendCodeTask.setPostObject(mailId);
            sendCodeTask.setCallbackResponse(this);
            Scheduler.getInstance().submit(sendCodeTask);
        } else if (viewId == R.id.wdverifyCode) {
            String otpText = getEnteredCode();
            if (otpText == null) {
                Utils.showMessage("Error", "Please enter a valid 4-digit Code",
                        getContext(), null);
                return;
            }
            PostTask<OTPDetails, String> verifyCodeTask = Request.verifyWDCodeTask();
            OTPDetails otpDetails = new OTPDetails();

            String mailIdStr = UserDetails.getInstance().getUserProfile().getEmailAddress();
            otpDetails.setMailId(mailIdStr);
            otpDetails.setOtp_hash(Utils.getPasswordHash(otpText));

            verifyCodeTask.setPostObject(otpDetails);
            verifyCodeTask.setCallbackResponse(this);
            Scheduler.getInstance().submit(verifyCodeTask);
        }
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException,
                               Object response, Object userObject) {
        if((exceptionThrown) && (!isAPIException)) {
            showErrShowHomeScreen((String) response);
            return;
        }
        if (isAPIException) {
            Runnable run = () -> {
                String error = (String) response;
                Utils.showMessage("Error", error, getContext(), null);
            };
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(run);
            }
            return;
        }
        if (reqId == Request.SEND_WD_OTP_CODE) {
            Runnable run = () -> {
                String error = (String) response;
                if (error.toLowerCase().equals("true")) {
                    View view = getView();
                    if (view == null) {
                        return;
                    }
                    Button sendCodeButton = view.findViewById(R.id.wdsendCode);
                    sendCodeButton.setText(R.string.resend_code);
                    sendCodeButton.setTag(true);

                    String mailId = UserDetails.getInstance().getUserProfile().getEmailAddress();
                    for (int index = 0; index < verifyCodeTextViewList.size(); index ++) {
                        TextView textView = verifyCodeTextViewList.get(index);
                        textView.setEnabled(true);
                    }
                    Button verifyCodeButton = view.findViewById(R.id.wdverifyCode);
                    verifyCodeButton.setEnabled(true);
                    String successMsg = "OTP Sent to : " + mailId + " Please Check mail and enter code";
                    Utils.showMessage("Information", successMsg, getContext(), null);
                }
            };
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(run);
            }
        } else if (reqId == Request.VERIFY_WD_OTP_CODE) {
            Runnable run = () -> {
                String error = (String) response;
                if (error.toLowerCase().equals("true")) {
                    Bundle params = new Bundle();
                    ((Navigator) requireActivity()).launchView(Navigator.WITHDRAW_REQ_VIEW, params, false);
                } else {
                    String msg = "Verification Code mismatch. Please try again";
                    Utils.showMessage("Information", msg, getContext(), null);
                }
            };
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(run);
            }
        }
    }

    private String getEnteredCode() {
        StringBuilder stringBuilder = new StringBuilder();
        int digitsEntered = 0;
        for (int index = 0; index < verifyCodeTextViewList.size(); index ++) {
            TextView textView = verifyCodeTextViewList.get(index);
            String strValue = textView.getText().toString().trim();
            if (strValue.length() > 0) {
                digitsEntered++;
                stringBuilder.append(strValue);
            }
        }
        if (digitsEntered != 4) {
            return null;
        }
        return stringBuilder.toString();
    }
}
