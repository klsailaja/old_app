package com.ab.telugumoviequiz.main;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.MessageListener;
import com.ab.telugumoviequiz.common.NotifyTextChanged;
import com.ab.telugumoviequiz.common.PATextWatcher;
import com.ab.telugumoviequiz.common.PostTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.common.WinMsgHandler;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, NotifyTextChanged,
        CallbackResponse, MessageListener {
    private PATextWatcher mailTextWatcher, passwordTextWatcher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);
        Request.baseUri = getString(R.string.base_url);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        handleListeners(this);
        handleTextWatchers(true);
        WinMsgHandler.getInstance().setListener(this);
        WinMsgHandler.getInstance().start();
    }

    @Override
    public void onPause() {
        super.onPause();
        handleListeners(null);
        handleTextWatchers(false);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.viewNewUserBut) {
            Intent intent = new Intent(this, NewUserActivity.class);
            startActivity(intent);
            finish();
        } else if (viewId == R.id.loginBut) {
            LoginData loginData = getFromUI();
            if (loginData == null) {
                return;
            }
            Button loginButton = findViewById(R.id.loginBut);
            loginButton.setVisibility(View.GONE);
            // Send password check validation to server..
            PostTask<LoginData,UserProfile> loginReq = Request.getLogin();
            loginReq.setCallbackResponse(this);
            loginReq.setPostObject(loginData);
            loginReq.setActivity(LoginActivity.this, "Processing. Please Wait!");
            Scheduler.getInstance().submit(loginReq);
        } else if (viewId == R.id.forgotPasswordBut) {
            boolean result = validateMailId();
            if (!result) {
                return;
            }
            LoginData loginData = formEntity();

            PostTask<LoginData, UserProfile> forgotPassword = Request.getForgotPassword();
            forgotPassword.setCallbackResponse(this);
            forgotPassword.setPostObject(loginData);
            forgotPassword.setActivity(LoginActivity.this, "Processing. Please Wait!");
            Scheduler.getInstance().submit(forgotPassword);
        }
    }

    @Override
    public void passData(int reqId, List<String> data) {
        String msg = data.get(0);
        Runnable run = () -> {
            TextView winMsgBar = findViewById(R.id.winMsgs);
            winMsgBar.setText(msg);
        };
        this.runOnUiThread(run);
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, final Object response, Object helperObj) {
        Runnable enableButtons = () -> {
            Button loginButton = findViewById(R.id.loginBut);
            loginButton.setVisibility(View.VISIBLE);
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
            }
            UserProfile userProfile = (UserProfile) response;
            Resources resources = getResources();
            String msg = resources.getString(R.string.user_login_fail_msg);

            if ((userProfile != null) && (userProfile.getId() > 0)) {
                UserDetails.getInstance().setUserProfile(userProfile);
                msg = resources.getString(R.string.user_login_success_msg);
                final String successMsg  = msg;
                Runnable loginRun = () -> {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("msg", successMsg);
                    startActivity(intent);
                    finish();
                };
                runOnUiThread(loginRun);
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

    @Override
    public void textChanged(int viewId) {
        if (viewId == R.id.editTextEmail) {
            validateMailId();
        } else if (viewId == R.id.editTextPassword) {
            validatePasswd();
        }
    }

    private boolean validateData() {
        boolean result = validateMailId();
        if (!result) {
            return false;
        }
        result = validatePasswd();
        return result;
    }

    private LoginData getFromUI() {
        boolean uiValidationRes = validateData();
        if (!uiValidationRes) {
            return null;
        }
        return formEntity();
    }

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

    private void handleTextWatchers(boolean add) {
        TextView mailTextView = findViewById(R.id.editTextEmail);
        TextView passwdTextView = findViewById(R.id.editTextPassword);

        if (add) {
            mailTextWatcher = new PATextWatcher(mailTextView, this);
            passwordTextWatcher = new PATextWatcher(passwdTextView, this);

            mailTextView.addTextChangedListener(mailTextWatcher);
            passwdTextView.addTextChangedListener(passwordTextWatcher);
        } else {
            mailTextView.removeTextChangedListener(mailTextWatcher);
            passwdTextView.removeTextChangedListener(passwordTextWatcher);
        }
    }

    private void handleListeners(View.OnClickListener listener) {
        Button loginButton = findViewById(R.id.loginBut);
        loginButton.setOnClickListener(listener);

        TextView viewNewUserScreen = findViewById(R.id.viewNewUserBut);
        viewNewUserScreen.setOnClickListener(listener);

        TextView forgotPassword = findViewById(R.id.forgotPasswordBut);
        forgotPassword.setOnClickListener(listener);
    }

    private boolean validatePasswd() {
        TextView passwdText = findViewById(R.id.editTextPassword);
        String str = passwdText.getText().toString().trim();
        String result = Utils.fullValidate(str, "Password", false, 4, 8, false);
        if (result != null) {
            passwdText.setError(result);
            passwdText.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateMailId() {
        TextView mailUI = findViewById(R.id.editTextEmail);
        String str = mailUI.getText().toString().trim();
        String result = Utils.fullValidate(str, "Mail Id", false, -1, -1, false);
        boolean showErr;
        if (result != null) {
            showErr = true;
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
}
