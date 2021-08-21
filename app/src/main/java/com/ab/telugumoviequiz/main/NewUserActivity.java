package com.ab.telugumoviequiz.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

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
import com.ab.telugumoviequiz.help.HelpPreferences;
import com.ab.telugumoviequiz.help.HelpTopic;
import com.ab.telugumoviequiz.help.ViewHelp;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import static com.ab.telugumoviequiz.R.*;

public class NewUserActivity extends AppCompatActivity
        implements View.OnClickListener, NotifyTextChanged, CallbackResponse, MessageListener {
    private PATextWatcher mailTextWatcher;
    private PATextWatcher passwordTextWatcher;
    private PATextWatcher nameTextWatcher;
    private PATextWatcher referralTextWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_register);

        TextView mailTextView = findViewById(id.editTextEmail);
        mailTextView.requestFocus();

        Resources resources = getResources();
        TextInputLayout textInputLayout = findViewById(R.id.editReferralCodeIL);
        textInputLayout.setHelperTextEnabled(true);
        textInputLayout.setHelperText(resources.getText(R.string.referral_code_helper_text));
        textInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED));
        Request.baseUri = getString(string.base_url);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleListeners(this);
        handleTextWatchers(true);
        WinMsgHandler.getInstance().setListener(this);
        WinMsgHandler.getInstance().start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handleListeners(null);
        handleTextWatchers(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == id.viewLoginPageBut) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (viewId == id.registerButton) {
            UserProfile userProfile = getFromUI();
            if (userProfile == null) {
                Utils.showMessage("Error", "Please correct errors", NewUserActivity.this, null);
                return;
            }
            Button loginButton = findViewById(R.id.registerButton);
            loginButton.setEnabled(false);

            PostTask<UserProfile, UserProfile> createUserReq = Request.getCreateUserProfile();
            createUserReq.setCallbackResponse(this);
            createUserReq.setPostObject(userProfile);
            createUserReq.setActivity(NewUserActivity.this, null);
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
                    loginHelpEnglishTopics, ViewHelp.HORIZONTAL, HelpPreferences.REFERRAL_INFO);
            viewHelp.setLocalMainHeading("Main Heading Telugu");
            viewHelp.setEnglishMainHeading("Terms And Conditions");
            Utils.clearState();
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            viewHelp.show(fragmentManager, "dialog");
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
            Button button = findViewById(id.registerButton);
            button.setEnabled(true);
        };
        this.runOnUiThread(enableButtons);

        if((exceptionThrown) && (!isAPIException)) {
            Runnable run = () -> {
                String error = (String) response;
                Utils.showMessage("Error", error, NewUserActivity.this, null);
            };
            runOnUiThread(run);
            return;
        }
        if (reqId == Request.CREATE_USER_PROFILE) {
            if (isAPIException) {
                Runnable run = () -> {
                    String error = (String) response;
                    Utils.showMessage("Error", error, NewUserActivity.this, null);
                };
                runOnUiThread(run);
            } else {
                UserProfile dbUserProfile = (UserProfile) response;
                UserDetails.getInstance().setUserProfile(dbUserProfile);
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
    }

    @SuppressLint("NonConstantResourceId")
    public void textChanged(int viewId) {
        switch (viewId) {
            case id.editTextEmail: {
                validateMailId();
                break;
            }
            case id.editTextPassword: {
                validatePasswd();
                break;
            }
            case id.editTextName: {
                validateName();
                break;
            }
            case id.editReferalCode: {
                validateReferalCode();
            }
        }
    }

    private UserProfile getFromUI() {
        boolean uiValidationRes = validateData();
        if (!uiValidationRes) return null;
        return formEntity();
    }

    private boolean validateData() {
        boolean result = validateMailId();
        if (!result) {
            return false;
        }
        result = validatePasswd();
        if (!result) {
            return false;
        }
        result = validateName();
        if (!result) {
            return false;
        }
        result = validateReferalCode();
        return result;
    }

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

        } else {
            mailTextView.removeTextChangedListener(mailTextWatcher);
            passwdTextView.removeTextChangedListener(passwordTextWatcher);
            referralCodeTextView.removeTextChangedListener(nameTextWatcher);
            nameTextView.removeTextChangedListener(referralTextWatcher);
        }
    }

    private void handleListeners(View.OnClickListener listener) {
        Button loginButton = findViewById(id.registerButton);
        loginButton.setOnClickListener(listener);

        TextView viewLoginPageBut = findViewById(id.viewLoginPageBut);
        viewLoginPageBut.setOnClickListener(listener);

        Button referralReadMore = findViewById(id.referralReadMore);
        referralReadMore.setOnClickListener(listener);
    }

    private boolean validateReferalCode() {
        TextView referalText = findViewById(id.editReferalCode);
        String str = referalText.getText().toString().trim();
        String result = Utils.fullValidate(str, "Referral Code", true, -1, -1, false);
        if (result != null) {
            referalText.setError(result);
            referalText.requestFocus();
            return false;
        }
        return true;
    }

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

    private boolean validatePasswd() {
        TextView passwdText = findViewById(id.editTextPassword);
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
}