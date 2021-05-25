package com.ab.telugumoviequiz.userprofile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.NotifyTextChanged;
import com.ab.telugumoviequiz.common.PATextWatcher;
import com.ab.telugumoviequiz.common.PostTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.main.UserProfile;

public class UpdateUserProfile extends BaseFragment implements View.OnClickListener, CallbackResponse, NotifyTextChanged {

    private PATextWatcher userNameTextWatcher;
    private PATextWatcher passwordTextWatcher;
    private PATextWatcher confirmPasswordTextWatcher;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.update_user_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedBundle) {
        super.onActivityCreated(savedBundle);
        populateUI();
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

    @SuppressLint("NonConstantResourceId")
    public void textChanged(int viewId) {
        switch (viewId) {
            case R.id.profileName: {
                validateName();
                break;
            }
            case R.id.profileNewPasswd: {
                validatePasswd(1);
                break;
            }
            case R.id.profileConfirmPasswd: {
                validatePasswd(2);
                break;
            }
        }
    }

    private boolean validateData() {
        boolean result = validateName();
        if (!result) {
            return false;
        }
        result = validatePasswd(1);
        if (!result) {
            return false;
        }
        result = validatePasswd(2);
        if (!result) {
            return false;
        }
        View view = getView();
        if (view != null) {
            TextView newPasswdTextView = view.findViewById(R.id.profileNewPasswd);
            TextView confirmPasswdTextView = view.findViewById(R.id.profileConfirmPasswd);
            String str1 = newPasswdTextView.getText().toString().trim();
            String str2 = confirmPasswdTextView.getText().toString().trim();
            if (!str1.equals(str2)) {
                confirmPasswdTextView.setError("New Password and Confirm Password are not same. Please check");
                confirmPasswdTextView.requestFocus();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.changeButton) {
            boolean uiValidationRes = validateData();
            if (!uiValidationRes) {
                return;
            }
            View view1 = getView();
            if (view1 == null) {
                return;
            }

            UserProfile userProfile = new UserProfile();
            TextView userNameTextView = view1.findViewById(R.id.profileName);
            TextView passwdTextView = view1.findViewById(R.id.profileNewPasswd);
            String str1 = passwdTextView.getText().toString().trim();

            userProfile.setName(userNameTextView.getText().toString().trim());
            userProfile.setPasswordHash(Utils.getPasswordHash(str1));
            userProfile.setEmailAddress(UserDetails.getInstance().getUserProfile().getEmailAddress());

            PostTask<UserProfile, UserProfile> createUserReq = Request.getUpdateUserProfile();
            createUserReq.setCallbackResponse(this);
            createUserReq.setPostObject(userProfile);
            createUserReq.setActivity(getActivity(), "Processing. Please Wait!");
            Scheduler.getInstance().submit(createUserReq);
        }
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, final Object response,
                               Object helperObject) {
        boolean errorHandled = handleServerError(exceptionThrown, isAPIException, response);
        if (errorHandled) {
            return;
        }
        boolean isApiErrorHandled = handleAPIError(isAPIException, response, 1, null, null);
        if (isApiErrorHandled) {
            return;
        }
        if (reqId == Request.UPDATE_USER_PROFILE) {
            UserProfile newUserProfile = (UserProfile) response;
            UserDetails.getInstance().setUserProfile(newUserProfile);
            displayInfo("Successfully updated", null);
        }
    }

    private void handleTextWatchers(boolean add) {
        View view = getView();
        if (view == null) {
            return;
        }
        TextView nameTextView = view.findViewById(R.id.profileName);
        TextView passwdTextView = view.findViewById(R.id.profileNewPasswd);
        TextView confirmPasswdTextView = view.findViewById(R.id.profileConfirmPasswd);

        if (add) {
            userNameTextWatcher = new PATextWatcher(nameTextView, this);
            passwordTextWatcher = new PATextWatcher(passwdTextView, this);
            confirmPasswordTextWatcher = new PATextWatcher(confirmPasswdTextView, this);

            nameTextView.addTextChangedListener(userNameTextWatcher);
            passwdTextView.addTextChangedListener(passwordTextWatcher);
            confirmPasswdTextView.addTextChangedListener(confirmPasswordTextWatcher);

        } else {
            nameTextView.removeTextChangedListener(userNameTextWatcher);
            passwdTextView.removeTextChangedListener(passwordTextWatcher);
            confirmPasswdTextView.removeTextChangedListener(confirmPasswordTextWatcher);
        }
    }

    private boolean validateName() {
        View view = getView();
        if (view == null) {
            return false;
        }
        TextView nameText = view.findViewById(R.id.profileName);
        String str = nameText.getText().toString().trim();
        String result = Utils.fullValidate(str, "Name", false, 4, 8, false);
        if (result != null) {
            nameText.setError(result);
            nameText.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validatePasswd(int type) {
        View view = getView();
        if (view == null) {
            return false;
        }
        TextView passwdText = view.findViewById(R.id.profileNewPasswd);
        String compName = "Password";
        if (type == 2) {
            passwdText = view.findViewById(R.id.profileConfirmPasswd);
            compName = "Confirm Password";
        }
        String str = passwdText.getText().toString().trim();
        String result = Utils.fullValidate(str, compName, false, 4, 8, false);
        if (result != null) {
            passwdText.setError(result);
            passwdText.requestFocus();
            return false;
        }
        return true;
    }

    private void handleListeners(View.OnClickListener listener) {
        View view = getView();
        if (view == null) {
            return;
        }

        Button loginButton = view.findViewById(R.id.changeButton);
        loginButton.setOnClickListener(listener);
    }
    private void populateUI() {
        View view = getView();
        if (view == null) {
            return;
        }
        TextView mailIdTextView = view.findViewById(R.id.profileMailId);
        TextView mycodeTextView = view.findViewById(R.id.profileMyCode);
        TextView bossNameTextView = view.findViewById(R.id.profileBossName);
        TextView bossCodeTextView = view.findViewById(R.id.profileReferalCode);
        TextView userNameTextView = view.findViewById(R.id.profileName);
        TextView newPasswordTextView = view.findViewById(R.id.profileNewPasswd);
        TextView confirmPasswordTextView = view.findViewById(R.id.profileConfirmPasswd);

        UserProfile userProfile = UserDetails.getInstance().getUserProfile();
        mailIdTextView.setText(userProfile.getEmailAddress());
        mycodeTextView.setText(userProfile.getMyReferalId());
        bossNameTextView.setText(userProfile.getBossName());
        bossCodeTextView.setText(userProfile.getBossReferredId());
        userNameTextView.setText(userProfile.getName());
        newPasswordTextView.setText("");
        confirmPasswordTextView.setText("");
    }
}
