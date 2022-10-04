package com.ab.telugumoviequiz.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.DialogAction;
import com.ab.telugumoviequiz.common.MessageListener;
import com.ab.telugumoviequiz.common.NotifyTextChanged;
import com.ab.telugumoviequiz.common.PATextWatcher;
import com.ab.telugumoviequiz.common.PostTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.common.WinMsgHandler;
import com.ab.telugumoviequiz.help.HelpPreferences;
import com.ab.telugumoviequiz.help.HelpTopic;
import com.ab.telugumoviequiz.help.ViewHelp;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import static com.ab.telugumoviequiz.R.id;
import static com.ab.telugumoviequiz.R.layout;
import static com.ab.telugumoviequiz.R.string;

public class NewUserActivity extends AppCompatActivity
        implements View.OnClickListener, NotifyTextChanged, CallbackResponse,
        View.OnTouchListener, MessageListener, TextWatcher, DialogAction {

    private static final int NETWORK_ERROR_SEND_OTP = 1;
    private static final int NETWORK_ERROR_VERIFY_OTP = 2;
    private static final int NETWORK_ERROR_CREATE_USER = 3;

    public static int NEW_USER_SEND_CODE = 100;

    private PATextWatcher mailTextWatcher;
    private PATextWatcher passwordTextWatcher;
    private PATextWatcher nameTextWatcher;
    private PATextWatcher referralTextWatcher;
    private final ArrayList<TextView> verifyCodeTextViewList = new ArrayList<>(4);

    private boolean passwordShowing = false, confirmPasswordShowing = false;
    private final String TAG = "NewUserActivity";

    // Completed.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "This is in onCreate");
        super.onCreate(savedInstanceState);
        Utils.clientReset(getResources().getString(R.string.base_url));
        setContentView(layout.activity_register);

        TextView mailTextView = findViewById(id.editTextEmail);
        mailTextView.requestFocus();

        Resources resources = getResources();
        TextInputLayout textInputLayout = findViewById(R.id.editReferralCodeIL);
        textInputLayout.setHelperTextEnabled(true);
        textInputLayout.setHelperText(resources.getText(R.string.referral_code_helper_text));
        textInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED));
        Request.baseUri = getString(string.base_url);

        verifyCodeTextViewList.clear();
        verifyCodeTextViewList.add(findViewById(id.digit1));
        verifyCodeTextViewList.add(findViewById(id.digit2));
        verifyCodeTextViewList.add(findViewById(id.digit3));
        verifyCodeTextViewList.add(findViewById(id.digit4));

        for (int index = 0; index < verifyCodeTextViewList.size(); index ++) {
            TextView textView = verifyCodeTextViewList.get(index);
            textView.setOnKeyListener((view, i, keyEvent) -> {
                if ((i == KeyEvent.KEYCODE_DEL) &&
                        (keyEvent.getAction() == KeyEvent.ACTION_DOWN)) {
                    int componentPosition = 0;
                    if (view.getId() == id.digit2) {
                        componentPosition = 1;
                    } else if (view.getId() == id.digit3) {
                        componentPosition = 2;
                    } else if (view.getId() == id.digit4) {
                        componentPosition = 3;
                    }
                    verifyCodeTextViewList.get(componentPosition).setText("");
                    verifyCodeTextViewList.get(componentPosition).requestFocus();
                    return true;
                }
                return false;
            });
        }
        stepsPostValidMailId(false);
    }

    // Completed.
    @Override
    protected void onStart() {
        super.onStart();
        initializeClickables();
    }

    // Completed.
    @Override
    protected void onResume() {
        super.onResume();
        handleListeners(this);
        handleTouchListeners(this);
        handleTextWatchers(true);
        WinMsgHandler.getInstance().setListener(this);
        WinMsgHandler.getInstance().start();
    }

    // Completed.
    @Override
    protected void onPause() {
        super.onPause();
        handleListeners(null);
        handleTouchListeners(null);
        handleTextWatchers(false);
    }

    // Completed.
    @Override
    protected void onStop() {
        super.onStop();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int right = 2;
        if (v.getId() == id.editTextPassword) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                TextView passwdTextView = findViewById(R.id.editTextPassword);
                if (event.getRawX() >= passwdTextView.getRight() - passwdTextView.getCompoundDrawables()[right].getBounds().width()) {
                    if (!passwordShowing) {
                        passwdTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.showeye, 0);
                        passwdTextView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordShowing = true;
                    } else {
                        passwdTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.hideeye, 0);
                        passwdTextView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordShowing = false;
                    }
                }
            }
        } else if (v.getId() == id.confirmTextPassword) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                TextView confirmPasswod = findViewById(R.id.confirmTextPassword);
                if (event.getRawX() >= confirmPasswod.getRight() - confirmPasswod.getCompoundDrawables()[right].getBounds().width()) {
                    if (!confirmPasswordShowing) {
                        confirmPasswod.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.showeye, 0);
                        confirmPasswod.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        confirmPasswordShowing = true;
                    } else {
                        confirmPasswod.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.hideeye, 0);
                        confirmPasswod.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        confirmPasswordShowing = false;
                    }
                }
            }
        }
        return false;
    }

    // Completed.
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == id.sendCode) {
            Boolean isResend = (Boolean) view.getTag();
            if ((isResend == null) || (!isResend)) {
                TextView mailIdTextView = findViewById(id.editTextEmail);
                String mailIdEntered = mailIdTextView.getText().toString().trim();
                String confirmMsg = "The Mail id is : " + mailIdEntered + " Confirm if this is correct?";
                Utils.showConfirmationMessage("Confirmation", confirmMsg,
                        this, this, NEW_USER_SEND_CODE, null);
            } else {
                doAction(NEW_USER_SEND_CODE, null);
            }
        } else if (viewId == id.verifyCode) {
            String otpText = getEnteredCode();
            if (otpText == null) {
                Utils.showMessage("Error", "Please enter a valid 4-digit code",
                        this, null);
                return;
            }
            view.setEnabled(false);
            PostTask<OTPDetails, String> verifyCodeTask = Request.verifyCodeTask();
            OTPDetails otpDetails = new OTPDetails();

            TextView mailTextView = findViewById(id.editTextEmail);
            String mailIdStr = mailTextView.getText().toString().trim();
            otpDetails.setMailId(mailIdStr);
            otpDetails.setOtp_hash(Utils.getPasswordHash(otpText));

            verifyCodeTask.setPostObject(otpDetails);
            verifyCodeTask.setCallbackResponse(this);
            verifyCodeTask.setActivity(NewUserActivity.this, Utils.WAIT_MESSAGE);
            verifyCodeTask.setHelperObject(NETWORK_ERROR_VERIFY_OTP);
            Scheduler.getInstance().submit(verifyCodeTask);
        } else if (viewId == id.registerButton) {
            UserProfile userProfile = getFromUI();
            if (userProfile == null) {
                Utils.showMessage("Error", "Please correct above errors", NewUserActivity.this, null);
                return;
            }
            boolean result = validateTermsConditions();
            if (!result) {
                return;
            }

            view.setEnabled(false);

            PostTask<UserProfile, UserProfile> createUserReq = Request.getCreateUserProfile();
            createUserReq.setCallbackResponse(this);
            createUserReq.setPostObject(userProfile);
            createUserReq.setActivity(NewUserActivity.this, Utils.WAIT_MESSAGE);
            createUserReq.setHelperObject(NETWORK_ERROR_CREATE_USER);
            Scheduler.getInstance().submit(createUserReq);
        } else if (viewId == id.referralReadMore) {
            int isSet = HelpPreferences.getInstance().readPreference(getBaseContext(), HelpPreferences.REFERRAL_INFO);
            if (isSet == 1) {
                return;
            }
            List<String> helpKeys = new ArrayList<>();
            helpKeys.add("topic_name1");
            helpKeys.add("topic_name2");
            helpKeys.add("topic_name3");
            List<HelpTopic> loginHelpLocalTopics = Utils.getHelpTopics(helpKeys, 1);
            List<HelpTopic> loginHelpEnglishTopics = Utils.getHelpTopics(helpKeys, 2);

            ViewHelp viewHelp = new ViewHelp(loginHelpLocalTopics,
                    loginHelpEnglishTopics, HelpPreferences.REFERRAL_INFO);
            viewHelp.setLocalMainHeading("Main Heading Telugu");
            viewHelp.setEnglishMainHeading("Terms And Conditions");
            Utils.clearState();
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            viewHelp.show(fragmentManager, "dialog");
        }
    }
    // Completed.
    @Override
    public void passData(int reqId, List<String> data) {
        if (reqId == WinMsgHandler.WIN_MSG_ID) {
            String msg = data.get(0);
            Runnable run = () -> {
                TextView winMsgBar = findViewById(R.id.winMsgs);
                winMsgBar.setText(msg);
            };
            this.runOnUiThread(run);
        } else if (reqId == MessageListener.QUIZ_SEVER_VERIFIED) {
            Resources resources = getResources();
            final String msg = resources.getString(string.new_user_register_success);
            Runnable run = () -> {
                Intent intent = new Intent(NewUserActivity.this, MainActivity.class);
                intent.putExtra("msg", msg);
                startActivity(intent);
                finish();
            };
            runOnUiThread(run);
        }
    }

    // Completed.
    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException,
                               final Object response, final Object helperObj) {
        if((exceptionThrown) && (!isAPIException)) {
            Runnable run = () -> {
                if (helperObj instanceof Integer) {
                    int networkErrCode = (Integer) helperObj;
                    if (networkErrCode == NETWORK_ERROR_SEND_OTP) {
                        Button button = findViewById(id.sendCode);
                        button.setEnabled(true);
                    } else if (networkErrCode == NETWORK_ERROR_VERIFY_OTP) {
                        Button button = findViewById(id.verifyCode);
                        button.setEnabled(true);
                    } else if (networkErrCode == NETWORK_ERROR_CREATE_USER) {
                        Button button = findViewById(id.registerButton);
                        button.setEnabled(true);
                    }
                }
                String error = (String) response;
                Utils.showMessage("Error", error, NewUserActivity.this, null);
            };
            runOnUiThread(run);
            return;
        }
        if (isAPIException) {
            Runnable run = () -> {
                Button loginButton = findViewById(R.id.registerButton);
                loginButton.setEnabled(true);
                String error = (String) response;
                Utils.showMessage("Error", error, NewUserActivity.this, null);
            };
            runOnUiThread(run);
            return;
        }
        if (reqId == Request.CREATE_USER_PROFILE) {
            UserProfile dbUserProfile = (UserProfile) response;
            String quizServerURI = dbUserProfile.getServerIpAddress();
            if (!quizServerURI.contains(":")) {
                Log.d(TAG, "inside:" + quizServerURI);
                Runnable run = () -> Utils.showMessage("Error", quizServerURI,
                        NewUserActivity.this, null);
                runOnUiThread(run);
                return;
            }
            Log.d(TAG, "proceeding here:" + quizServerURI);
            Request.baseUri = dbUserProfile.getServerIpAddress();
            UserDetails.getInstance().setUserProfile(dbUserProfile);
            ClientInitializer.getInstance(this, this);
        } else if (reqId == Request.SEND_OTP_CODE) {
            Runnable run = () -> {
                String error = (String) response;
                if (error.toLowerCase().equals("true")) {
                    TextView mailidTextView = findViewById(id.editTextEmail);
                    mailidTextView.setEnabled(false);

                    Button sendCodeButton = findViewById(id.sendCode);
                    sendCodeButton.setText(string.resend_code);
                    sendCodeButton.setTag(true);

                    for (int index = 0; index < verifyCodeTextViewList.size(); index ++) {
                        TextView textView = verifyCodeTextViewList.get(index);
                        textView.setEnabled(true);
                    }
                    verifyCodeTextViewList.get(0).requestFocus();

                    Button verifyCodeButton = findViewById(id.verifyCode);
                    verifyCodeButton.setEnabled(true);
                    String successMsg = "Verification Code Sent. Please enter code from mail";
                    Utils.showMessage("Information", successMsg, this, null);
                }
            };
            runOnUiThread(run);
        } else if (reqId == Request.VERIFY_OTP_CODE) {
            Runnable run = () -> {
                String error = (String) response;
                String msg = "Verification Code mismatch. Please try again";
                if (error.toLowerCase().equals("true")) {
                    msg = "Verification Code matched. Please configure password";

                    Button verifyCodeButton = findViewById(id.verifyCode);
                    verifyCodeButton.setEnabled(false);

                    stepsPostValidMailId(false);

                    TextView passwdTextBox = findViewById(id.editTextPassword);
                    passwdTextBox.setEnabled(true);

                    TextView confirmTextBox = findViewById(id.confirmTextPassword);
                    confirmTextBox.setEnabled(true);

                    TextView nameTextBox = findViewById(id.editTextName);
                    nameTextBox.setEnabled(true);

                    TextView referalTextBox = findViewById(id.editReferalCode);
                    referalTextBox.setEnabled(true);

                    Button registerButton = findViewById(id.registerButton);
                    registerButton.setEnabled(true);

                }
                Utils.showMessage("Information", msg, this, null);
            };
            runOnUiThread(run);
        }
    }

    // Completed.
    @SuppressLint("NonConstantResourceId")
    public void textChanged(int viewId) {
        boolean result;
        switch (viewId) {
            case id.editTextEmail: {
                result = validateMailId();
                stepsPostValidMailId(result);
                break;
            }
            case id.editTextPassword: {
                validatePasswd();
                break;
            }
            case id.confirmTextPassword: {
                validateConfirmPasswd();
                break;
            }
            case id.editTextName: {
                validateName();
                break;
            }
            case id.editReferalCode: {
                validateReferalCode();
                break;
            }
        }
    }
    // Completed.
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    // Completed.
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    // Completed.
    @Override
    public void afterTextChanged(Editable s) {
        for (int index = 0; index < verifyCodeTextViewList.size(); index ++) {
            TextView textView = verifyCodeTextViewList.get(index);
            if (textView.getEditableText() == s) {
                 if (s.length() == 0) {
                     break;
                 }
                 if (index != 3) {
                    verifyCodeTextViewList.get(index + 1).requestFocus();
                    break;
                }
            }
        }
    }
    // Completed.
    private void stepsPostValidMailId(boolean isValid) {
        Button sendCodeBut = findViewById(R.id.sendCode);
        sendCodeBut.setEnabled(isValid);

        for (int index = 0; index < verifyCodeTextViewList.size(); index ++) {
            TextView textView = verifyCodeTextViewList.get(index);
            textView.setEnabled(isValid);
        }
    }
    // Completed.
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
        /*Button checkCode = findViewById(id.verifyCode);
        checkCode.setEnabled(digitsEntered == 4);*/
        if (digitsEntered != 4) {
            return null;
        }
        return stringBuilder.toString();
    }

    // Completed.
    private UserProfile getFromUI() {
        boolean uiValidationRes = validateData();
        if (!uiValidationRes) return null;
        return formEntity();
    }

    // Completed.
    private boolean validateData() {
        boolean result = validateMailId();
        if (!result) {
            stepsPostValidMailId(false);
            return false;
        }
        result = validatePasswd();
        if (!result) {
            return false;
        }
        result = validateConfirmPasswd();
        if (!result) {
            return false;
        }
        TextView passwdTextBox = findViewById(id.editTextPassword);
        TextView confirmPasswdTextBox = findViewById(id.confirmTextPassword);
        String passwd = passwdTextBox.getText().toString().trim();
        String confirmPasswd = confirmPasswdTextBox.getText().toString().trim();
        if (passwd.equals(confirmPasswd)) {
            result = validateName();
            if (!result) {
                return false;
            }
            result = validateReferalCode();
            return result;
        } else {
            confirmPasswdTextBox.setError("Password and Confirm Password are not same");
            confirmPasswdTextBox.requestFocus();
            return false;
        }
    }

    // Completed.
    private UserProfile formEntity() {
        UserProfile userProfile = new UserProfile();

        TextView mailTextView = findViewById(id.editTextEmail);
        TextView passwdTextView = findViewById(id.editTextPassword);
        TextView userNameTextView = findViewById(id.editTextName);
        TextView bossReferalTextView = findViewById(id.editReferalCode);

        String str = mailTextView.getText().toString().trim();
        userProfile.setEmailAddress(str);

        str = passwdTextView.getText().toString().trim();
        str = Utils.getPasswordHash(str);
        userProfile.setPasswordHash(str);

        str = userNameTextView.getText().toString().trim();
        userProfile.setName(str);

        str = bossReferalTextView.getText().toString().trim();
        if (str.length() == 0) {
            str = null;
        }
        userProfile.setBossReferredId(str);
        return userProfile;
    }

    // Completed.
    private void handleTextWatchers(boolean add) {
        TextView mailTextView = findViewById(id.editTextEmail);
        TextView passwdTextView = findViewById(id.editTextPassword);
        TextView nameTextView = findViewById(id.editTextName);
        TextView referralCodeTextView = findViewById(id.editReferalCode);

        if (add) {
            mailTextWatcher = new PATextWatcher(mailTextView, this);
            passwordTextWatcher = new PATextWatcher(passwdTextView, this);
            nameTextWatcher = new PATextWatcher(nameTextView, this);
            referralTextWatcher = new PATextWatcher(referralCodeTextView, this);

            mailTextView.addTextChangedListener(mailTextWatcher);
            passwdTextView.addTextChangedListener(passwordTextWatcher);
            nameTextView.addTextChangedListener(nameTextWatcher);
            referralCodeTextView.addTextChangedListener(referralTextWatcher);

            verifyCodeTextViewList.get(0).addTextChangedListener(this);
            verifyCodeTextViewList.get(1).addTextChangedListener(this);
            verifyCodeTextViewList.get(2).addTextChangedListener(this);
            verifyCodeTextViewList.get(3).addTextChangedListener(this);
       } else {
            mailTextView.removeTextChangedListener(mailTextWatcher);
            passwdTextView.removeTextChangedListener(passwordTextWatcher);
            referralCodeTextView.removeTextChangedListener(nameTextWatcher);
            nameTextView.removeTextChangedListener(referralTextWatcher);

            verifyCodeTextViewList.get(0).removeTextChangedListener(this);
            verifyCodeTextViewList.get(1).removeTextChangedListener(this);
            verifyCodeTextViewList.get(2).removeTextChangedListener(this);
            verifyCodeTextViewList.get(3).removeTextChangedListener(this);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void handleTouchListeners(View.OnTouchListener listener) {
        TextView passwdTextView = findViewById(R.id.editTextPassword);
        passwdTextView.setOnTouchListener(listener);
    }

    // Completed.
    private void handleListeners(View.OnClickListener listener) {
        Button loginButton = findViewById(id.registerButton);
        loginButton.setOnClickListener(listener);

        TextView viewLoginPageBut = findViewById(id.viewLoginPageBut);
        viewLoginPageBut.setOnClickListener(listener);

        Button referralReadMore = findViewById(id.referralReadMore);
        referralReadMore.setOnClickListener(listener);

        Button sendCodeButton = findViewById(id.sendCode);
        sendCodeButton.setOnClickListener(listener);

        Button verifyCodeButton = findViewById(id.verifyCode);
        verifyCodeButton.setOnClickListener(listener);
    }

    // Completed.
    private boolean validateTermsConditions() {
        CheckBox checkBox = findViewById(R.id.termsConditionsCheck1);
        if (!checkBox.isChecked()) {
            Utils.showMessage("Info",
                    "Read Terms and Conditions and Accept", this, null);
            return false;
        }
        return true;
    }

    // Completed.
    private boolean validateReferalCode() {
        TextView referalText = findViewById(id.editReferalCode);
        String str = referalText.getText().toString().trim();
        String result = Utils.fullValidate(str, "Referral Code", false, -1, -1, false);
        if (result != null) {
            referalText.setError(result);
            referalText.requestFocus();
            return false;
        }
        return true;
    }

    // Completed.
    private boolean validateName() {
        TextView nameText = findViewById(id.editTextName);
        String str = nameText.getText().toString().trim();
        String result = Utils.fullValidate(str, "Name", false, 2, 20, false);
        if (result != null) {
            nameText.setError(result);
            nameText.requestFocus();
            return false;
        }
        return true;
    }

    // Completed.
    private boolean validateConfirmPasswd() {
        TextView confirmPasswdText = findViewById(id.confirmTextPassword);
        String str = confirmPasswdText.getText().toString().trim();
        String result = Utils.fullValidate(str, "Confirm Password", false, 8, 25, false);
        if (result != null) {
            confirmPasswdText.setError(result);
            confirmPasswdText.requestFocus();
            return false;
        }
        return true;
    }

    // Completed.
    private boolean validatePasswd() {
        TextView passwdText = findViewById(id.editTextPassword);
        String str = passwdText.getText().toString().trim();
        String result = Utils.fullValidate(str, "Password", false, 8, 25, false);
        if (result != null) {
            passwdText.setError(result);
            passwdText.requestFocus();
            return false;
        }
        return true;
    }

    // Completed.
    private boolean validateMailId() {
        TextView mailUI = findViewById(id.editTextEmail);
        String str = mailUI.getText().toString().trim();
        String result = Utils.fullValidate(str, "Mail Id", false, -1, -1, false);
        boolean showErr;
        if (result != null) {
            showErr = false;
        } else {
            showErr = android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches();
            result = "Invalid Mail Address";
        }
        if (!showErr) {
            mailUI.setError(result);
            mailUI.requestFocus();
            return false;
        }
        return true;
    }

    // Completed.
    private void initializeClickables() {
        CheckBox terms1TV = findViewById(R.id.termsConditionsCheck1);
        String terms1 = getResources().getString(R.string.terms_conditions1);
        SpannableString ss = new SpannableString(terms1);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Request.getTermsConditionsURL())));
            }

            public void updateDrawState (TextPaint ds) {
                ds.setColor(Color.parseColor("#FF0000"));
            }
        };
        String termsLinkText = "Click here";
        int startPos = terms1.indexOf(termsLinkText);
        int endPos = startPos + termsLinkText.length();
        ss.setSpan(clickableSpan, startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        terms1TV.setText(ss);

        terms1TV.setMovementMethod(LinkMovementMethod.getInstance());

        TextView newUserTV = findViewById(R.id.viewLoginPageBut);
        String newUserReg = getResources().getString(R.string.new_user_already_registered);
        SpannableString ss1 = new SpannableString(newUserReg);
        ClickableSpan newUserRegSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(NewUserActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            public void updateDrawState (TextPaint ds) {
                ds.setColor(Color.parseColor("#FF0000"));
            }
        };
        ss1.setSpan(newUserRegSpan, 0, newUserReg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        newUserTV.setText(ss1);
        newUserTV.setMovementMethod(LinkMovementMethod.getInstance());
    }

    // Completed.
    @Override
    public void doAction(int calledId, Object userObject) {
        if (calledId == NEW_USER_SEND_CODE) {
            Button sendOtpBut = findViewById(id.sendCode);
            sendOtpBut.setEnabled(false);
            TextView mailIdTxtView = findViewById(id.editTextEmail);
            String mailId = mailIdTxtView.getText().toString().trim();
            PostTask<String,String> sendCodeTask = Request.sendCodeTask();
            sendCodeTask.setPostObject(mailId);
            sendCodeTask.setCallbackResponse(this);
            sendCodeTask.setHelperObject(NETWORK_ERROR_SEND_OTP);
            sendCodeTask.setActivity(NewUserActivity.this, Utils.WAIT_MESSAGE);
            Scheduler.getInstance().submit(sendCodeTask);
        }
    }
}