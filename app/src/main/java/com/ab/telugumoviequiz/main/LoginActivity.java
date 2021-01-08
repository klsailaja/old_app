package com.ab.telugumoviequiz.main;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.NotifyTextChanged;
import com.ab.telugumoviequiz.common.PATextWatcher;
import com.ab.telugumoviequiz.common.PostTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, NotifyTextChanged, CallbackResponse {
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
            ProgressBar loginProgressBar = findViewById(R.id.login_screen_progressbar);
            loginButton.setVisibility(View.GONE);
            loginProgressBar.setVisibility(View.VISIBLE);
            // Send password check validation to server..
            PostTask<LoginData,UserProfile> loginReq = Request.getLogin();
            loginReq.setCallbackResponse(this);
            loginReq.setPostObject(loginData);
            Scheduler.getInstance().submit(loginReq);
        }
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, final Object response, Object helperObj) {
        Runnable enableButtons = () -> {
            Button loginButton = findViewById(R.id.loginBut);
            ProgressBar loginProgressBar = findViewById(R.id.login_screen_progressbar);
            loginButton.setVisibility(View.VISIBLE);
            loginProgressBar.setVisibility(View.GONE);
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

