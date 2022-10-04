package com.ab.telugumoviequiz.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.DialogAction;
import com.ab.telugumoviequiz.common.Keys;
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
import com.ab.telugumoviequiz.help.HelpReader;
import com.ab.telugumoviequiz.help.HelpTopic;
import com.ab.telugumoviequiz.help.ViewHelp;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnTouchListener, NotifyTextChanged, CallbackResponse,
        MessageListener, DialogAction {
    private final String TAG = "LoginActivity";
    private static final int FORGOT_PASSWD_CONFIRM = 10;
    private PATextWatcher mailTextWatcher, passwordTextWatcher, captchaTextWatcher;
    private boolean passwordShowing;

    // Completed.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.clientReset(getResources().getString(R.string.base_url));

        HelpReader.getInstance().initialize(getBaseContext());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_login);
        Log.d(TAG, "In OnCreate:" + Request.baseUri);

        generateCaptcha();

        int isCalledFromMain = getIntent().getIntExtra(Keys.LOGIN_SCREEN_CALLED_FROM_LOGOUT, 0);
        if (isCalledFromMain == 1) {
            showHelpWindow();
        }
    }

    // Completed.
    @Override
    public void onStart() {
        super.onStart();
        initializeClickables();
    }

    // Completed.
    @Override
    public void onResume() {
        super.onResume();
        handleListeners(this);
        handleTouchListeners(this);
        handleTextWatchers(true);
        WinMsgHandler.getInstance().setListener(this);
        WinMsgHandler.getInstance().setUserProfileId(-1);
    }

    // Completed.
    @Override
    public void onPause() {
        super.onPause();
        handleListeners(null);
        handleTouchListeners(null);
        handleTextWatchers(false);
    }

    // Completed.
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.loginBut) {
            LoginData loginData = getFromUI();
            if (loginData == null) {
                return;
            }
            Button loginButton = findViewById(R.id.loginBut);
            loginButton.setEnabled(false);
            // Send password check validation to server..
            PostTask<LoginData,UserProfile> loginReq = Request.getLogin();
            loginReq.setCallbackResponse(this);
            loginReq.setPostObject(loginData);
            loginReq.setActivity(LoginActivity.this, Utils.WAIT_MESSAGE);
            Scheduler.getInstance().submit(loginReq);
        } else if (viewId == R.id.forgotPasswordBut) {
            Utils.showConfirmationMessage("Confirm?", "Are you sure to proceed?",
                    this, this, FORGOT_PASSWD_CONFIRM, null);
        } else if (viewId == R.id.reloadCaptcha) {
            generateCaptcha();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int right = 2;
        TextView passwdTextView = findViewById(R.id.editTextPassword);
        if (event.getAction() == MotionEvent.ACTION_UP) {
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
        return false;
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
            final String successMsg  = resources.getString(R.string.user_login_success_msg);
            final Button loginButton = findViewById(R.id.loginBut);
            Log.d(TAG, "All tasks done.");
            Runnable loginRun = () -> {
                Snackbar.make(loginButton, successMsg, Snackbar.LENGTH_SHORT).show();
                Log.d(TAG, "Starting Main Activity");
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //intent.putExtra("msg", successMsg);
                startActivity(intent);
                finish();
            };
            runOnUiThread(loginRun);
        }
    }

    // Completed.
    @Override
    public void handleResponse(int reqId, boolean exceptionThrown,
                               boolean isAPIException,
                               final Object response, Object helperObj) {
        Runnable enableButtons = () -> {
            Button loginButton = findViewById(R.id.loginBut);
            loginButton.setEnabled(true);
        };
        this.runOnUiThread(enableButtons);

        if((exceptionThrown) && (!isAPIException)) {
            Runnable run = () -> {
                String error = (String) response;
                Utils.showMessage("Error", error, LoginActivity.this, null);
            };
            runOnUiThread(run);
            return;
        }
        if (reqId == Request.LOGIN_REQ) {
            if (isAPIException) {
                Runnable run = () -> {
                    String error = (String) response;
                    Utils.showMessage("Error", error, LoginActivity.this, null);
                };
                runOnUiThread(run);
                return;
            }

            UserProfile userProfile = (UserProfile) response;
            Resources resources = getResources();
            String msg = resources.getString(R.string.user_login_fail_msg);

            if ((userProfile != null) && (userProfile.getId() > 0)) {
                String quizServerURI = userProfile.getServerIpAddress();
                Log.d(TAG, "Quiz Server URI:" + quizServerURI);
                Log.d(TAG, quizServerURI.contains(":") + ";;" + quizServerURI.indexOf(":"));
                if (!quizServerURI.contains(":")) {
                    Log.d(TAG, "inside:" + quizServerURI);
                    Runnable run = () -> Utils.showMessage("Error", quizServerURI,
                            LoginActivity.this, null);
                    runOnUiThread(run);
                    return;
                }
                Log.d(TAG, "Proceeding here :" + quizServerURI);
                Request.baseUri = quizServerURI;
                UserDetails.getInstance().setUserProfile(userProfile);
                ClientInitializer.getInstance(this, this);
            } else {
                final String successMsg  = msg;
                Runnable run = () -> Utils.showMessage("Error", successMsg, LoginActivity.this, null);
                runOnUiThread(run);
            }
        } else if (reqId == Request.FORGOT_PASSWORD) {
            if (isAPIException) {
                Runnable run = () -> {
                    String error = (String) response;
                    Utils.showMessage("Error", error, LoginActivity.this, null);
                };
                runOnUiThread(run);
                return;
            }
            UserProfile userProfile = (UserProfile) response;
            if ((userProfile != null) && (userProfile.getId() > 0)) {
                UserDetails.getInstance().setUserProfile(userProfile);
                Resources resources = getResources();
                final String successMsg  = resources.getString(R.string.user_forgot_passwd_success_msg);
                Runnable run = () -> Utils.showMessage("Information", successMsg, LoginActivity.this, null);
                runOnUiThread(run);
            }
        }
    }

    // Completed.
    @Override
    public void textChanged(int viewId) {
        if (viewId == R.id.editTextEmail) {
            validateMailId();
        } else if (viewId == R.id.editTextPassword) {
            validatePasswd();
        } else if (viewId == R.id.enterCaptchaET) {
            validateCaptcha();
        }
    }

    // Completed.
    private void generateCaptcha() {
        int num1 = getRandomNumber();
        int num2 = getRandomNumber();

        TextView captchaText = findViewById(R.id.captchaQuestion);
        String str = num1 + " + " + num2;
        captchaText.setText(str);
    }

    // Completed.
    private void showHelpWindow() {
        int isSet = HelpPreferences.getInstance().readPreference(getBaseContext(), HelpPreferences.LOGOUT_TIPS);
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
                loginHelpEnglishTopics, HelpPreferences.WITHDRAW_TIPS);
        viewHelp.setLocalMainHeading("Main Heading Telugu");
        viewHelp.setEnglishMainHeading("Terms And Conditions");
        Utils.clearState();
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        viewHelp.show(fragmentManager, "dialog");
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
    private boolean validateData() {
        boolean result = validateMailId();
        if (!result) {
            return false;
        }
        result = validatePasswd();
        if (!result) {
            return false;
        }
        result = validateCaptcha();
        if (!result) {
            return false;
        }
        result = validateUserCaptcha();
        if (!result) {
            return false;
        }
        result = validateTermsConditions();
        return result;
    }

    // Completed.
    private LoginData getFromUI() {
        boolean uiValidationRes = validateData();
        if (!uiValidationRes) {
            return null;
        }
        return formEntity();
    }

    // Completed.
    private LoginData formEntity() {
        LoginData loginData = new LoginData();

        TextView mailTextView = findViewById(R.id.editTextEmail);
        TextView passwdTextView = findViewById(R.id.editTextPassword);

        String str = mailTextView.getText().toString().trim();
        loginData.setMailAddress(str);

        str = passwdTextView.getText().toString().trim();
        str = Utils.getPasswordHash(str);
        loginData.setPassword(str);

        return loginData;
    }

    // Completed.
    private void handleTextWatchers(boolean add) {
        TextView mailTextView = findViewById(R.id.editTextEmail);
        TextView passwdTextView = findViewById(R.id.editTextPassword);
        TextView captchaTextView = findViewById(R.id.enterCaptchaET);

        if (add) {
            mailTextWatcher = new PATextWatcher(mailTextView, this);
            passwordTextWatcher = new PATextWatcher(passwdTextView, this);
            captchaTextWatcher = new PATextWatcher(captchaTextView, this);

            mailTextView.addTextChangedListener(mailTextWatcher);
            passwdTextView.addTextChangedListener(passwordTextWatcher);
            captchaTextView.addTextChangedListener(captchaTextWatcher);

        } else {
            mailTextView.removeTextChangedListener(mailTextWatcher);
            passwdTextView.removeTextChangedListener(passwordTextWatcher);
            captchaTextView.removeTextChangedListener(captchaTextWatcher);
        }
    }

    // Completed.
    private void handleListeners(View.OnClickListener listener) {
        Button loginButton = findViewById(R.id.loginBut);
        loginButton.setOnClickListener(listener);

        TextView viewNewUserScreen = findViewById(R.id.viewNewUserBut);
        viewNewUserScreen.setOnClickListener(listener);

        TextView forgotPassword = findViewById(R.id.forgotPasswordBut);
        forgotPassword.setOnClickListener(listener);

        /*TextView termsConditions = findViewById(R.id.termsConditionsText1);
        termsConditions.setOnClickListener(listener);*/

        ImageView reloadCaptcha = findViewById(R.id.reloadCaptcha);
        reloadCaptcha.setOnClickListener(listener);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void handleTouchListeners(View.OnTouchListener listener) {
        TextView passwdTextView = findViewById(R.id.editTextPassword);
        passwdTextView.setOnTouchListener(listener);
    }

    // Completed.
    private boolean validateUserCaptcha() {
        TextView captchaText = findViewById(R.id.enterCaptchaET);
        String str = captchaText.getText().toString().trim();
        TextView captchaQuestion = findViewById(R.id.captchaQuestion);
        int captchaTextInt = Integer.parseInt(str);
        String captchaQuestionStr = captchaQuestion.getText().toString().trim();
        int pos = captchaQuestionStr.indexOf("+");
        String num1Str = captchaQuestionStr.substring(0, pos - 1).trim();
        String num2Str = captchaQuestionStr.substring(pos + 1).trim();
        int num1 = Integer.parseInt(num1Str);
        int num2 = Integer.parseInt(num2Str);
        boolean isCorrect =  ((num1 + num2) == captchaTextInt);
        if (!isCorrect) {
            captchaText.setError("Enter valid captcha");
            captchaText.requestFocus();
            return false;
        }
        return true;
    }

    // Completed.
    private boolean validateCaptcha() {
        TextView captchaText = findViewById(R.id.enterCaptchaET);
        String str = captchaText.getText().toString().trim();
        String result = Utils.fullValidate(str, "Captcha", false, -1, -1, true);
        if (result != null) {
            captchaText.setError(result);
            captchaText.requestFocus();
            return false;
        }
        return true;
    }

    // Completed.
    private boolean validatePasswd() {
        TextView passwdText = findViewById(R.id.editTextPassword);
        String str = passwdText.getText().toString().trim();
        String result = Utils.fullValidate(str, "Password", false, 8, 32, false);
        if (result != null) {
            passwdText.setError(result);
            passwdText.requestFocus();
            return false;
        }
        return true;
    }

    // Completed.
    private boolean validateMailId() {
        TextView mailUI = findViewById(R.id.editTextEmail);
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
    private static int getRandomNumber() {
        return 1 + (int)(Math.random() * (20 - 1));
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

        TextView newUserTV = findViewById(R.id.viewNewUserBut);
        String newUserReg = getResources().getString(R.string.login_screen_new_user_register_now);
        SpannableString ss1 = new SpannableString(newUserReg);
        ClickableSpan newUserRegSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(LoginActivity.this, NewUserActivity.class);
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
        if (calledId == FORGOT_PASSWD_CONFIRM) {
            boolean result = validateMailId();
            if (!result) {
                return;
            }
            LoginData loginData = formEntity();

            PostTask<LoginData, UserProfile> forgotPassword = Request.getForgotPassword();
            forgotPassword.setCallbackResponse(this);
            forgotPassword.setPostObject(loginData);
            forgotPassword.setActivity(LoginActivity.this, null);
            Scheduler.getInstance().submit(forgotPassword);
        }
    }
}
