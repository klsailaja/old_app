package com.ab.telugumoviequiz.withdraw;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.DialogAction;
import com.ab.telugumoviequiz.common.NotifyTextChanged;
import com.ab.telugumoviequiz.common.PATextWatcher;
import com.ab.telugumoviequiz.common.PostTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.Navigator;
import com.ab.telugumoviequiz.main.UserMoney;
import com.google.android.material.textfield.TextInputLayout;

public class TransferByPhonePe extends BaseFragment implements CallbackResponse, View.OnClickListener, NotifyTextChanged,
        AdapterView.OnItemSelectedListener, DialogAction {

    private PATextWatcher phoneNumberWatcher, confirmPhoneNumberWatcher;
    private PATextWatcher userNameTextWatcher;
    private PATextWatcher wdAmtTextWatcher;

    public TransferByPhonePe() {
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

        View view = inflater.inflate(R.layout.wd_transfer_phonepe, container, false);

        TextInputLayout textInputLayout = view.findViewById(R.id.phNumberIL);
        textInputLayout.setHelperTextEnabled(true);
        textInputLayout.setHelperText("The Phone Number Entered is final");
        textInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setCurrentBalance(false, -1);
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
    public void onClick(View view) {
        boolean uiValidationRes = validateData();
        if (!uiValidationRes) {
            return;
        }

        WithdrawRequestInput withdrawRequestInput = formEntity();
        PostTask<WithdrawRequestInput, Boolean> wdNewRequest = Request.createNewWDRequest();
        wdNewRequest.setCallbackResponse(this);
        wdNewRequest.setPostObject(withdrawRequestInput);
        wdNewRequest.setActivity(getActivity(), "Processing. Please Wait!");
        Scheduler.getInstance().submit(wdNewRequest);
    }

    private WithdrawRequestInput formEntity() {
        View view = getView();
        if (view == null) {
            return null;
        }

        WithdrawRequestInput wdRequestBankType = new WithdrawRequestInput();

        TextView withdrawTextView = view.findViewById(R.id.bankWithdrawAmt);
        String str = withdrawTextView.getText().toString().trim();

        WDUserInput wdUserInput = new WDUserInput();
        wdUserInput.setUserProfileId(UserDetails.getInstance().getUserProfile().getId());
        wdUserInput.setOpenedTime(System.currentTimeMillis());
        wdUserInput.setAmount(Integer.parseInt(str));
        wdRequestBankType.setWithdrawUserInput(wdUserInput);
        wdUserInput.setRequestType(1);

        TextView phNumTextView = view.findViewById(R.id.phNumber);
        TextView userNameTextView = view.findViewById(R.id.userName);

        WithdrawReqByPhone reqByPhone = new WithdrawReqByPhone();
        str = phNumTextView.getText().toString().trim();
        reqByPhone.setPhNumber(str);

        str = userNameTextView.getText().toString().trim();
        reqByPhone.setAccountHolderName(str);
        reqByPhone.setPaymentMethod(1);

        wdRequestBankType.setByBankDetails(null);
        wdRequestBankType.setByPhoneDetails(reqByPhone);

        return wdRequestBankType;
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        setCurrentBalance(false, -1);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    private boolean setCurrentBalance(boolean checkRangeValue, int wdAmt) {
        View view = getView();
        if (view == null) {
            return false;
        }
        UserMoney userMoney = UserDetails.getInstance().getUserMoney();
        long currentBalance = userMoney.getAmount();
        TextInputLayout textInputLayout = view.findViewById(R.id.bankWithdrawAmtIL);
        textInputLayout.setHelperTextEnabled(true);
        textInputLayout.setHelperText("Current Balance is: " + currentBalance);
        textInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED));
        if (checkRangeValue) {
            if (wdAmt > currentBalance) {
                displayInfo("Withdraw Amount more than current balance", null);
                return false;
            }
        }
        return true;
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException,
                               Object response, Object userObject) {
        boolean errorHandled = handleServerError(exceptionThrown, isAPIException, response);
        if (errorHandled) {
            return;
        }
        boolean isApiErrorHandled = handleAPIError(isAPIException, response, 1, null, null);
        if (isApiErrorHandled) {
            return;
        }
        if (reqId == Request.CREATE_NEW_WD_REQ) {
            displayInfo("Withdraw request placed. Will be processed in 2 to 3 days", this);
        }
    }

    @Override
    public void textChanged(int viewId) {
        if (viewId == R.id.phNumber) {
            validatePhoneNumber();
        } else if (viewId == R.id.confirmPhNum) {
            validateConfirmPhNumber();
        } else if (viewId == R.id.userName) {
            validateUserName();
        } else if (viewId == R.id.bankWithdrawAmt) {
            validateWDAmount();
        }
    }

    private boolean validateData() {
        boolean result = validateWDAmount();
        if (!result) {
            return false;
        }
        result = validatePhoneNumber();
        if (!result) {
            return false;
        }
        result = validateConfirmPhNumber();
        if (!result) {
            return false;
        }
        View view = getView();
        if (view != null) {
            TextView accountNumberTxtView = view.findViewById(R.id.phNumber);
            String str1 = accountNumberTxtView.getText().toString().trim();
            TextView confirmAccNumberTxtView = view.findViewById(R.id.confirmPhNum);
            String str2 = confirmAccNumberTxtView.getText().toString().trim();
            if (!str1.equals(str2)) {
                confirmAccNumberTxtView.setError("Phone Number and Confirm Phone Number not same");
                confirmAccNumberTxtView.requestFocus();
                return false;
            }
        }
        result = validateUserName();
        return result;
    }

    private boolean validateWDAmount() {
        View view = getView();
        if (view == null) {
            return false;
        }

        TextView txtView = view.findViewById(R.id.bankWithdrawAmt);
        String str = txtView.getText().toString().trim();
        String result = Utils.fullValidate(str, "Withdraw Amount", false, -1, -1, true);
        boolean showErr ;
        showErr = result == null;
        if (!showErr) {
            txtView.setError(result);
            txtView.requestFocus();
            return false;
        }
        return setCurrentBalance(true, Integer.parseInt(str));
    }

    private boolean validatePhoneNumber() {
        View view = getView();
        if (view == null) {
            return false;
        }

        TextView accountNumberTxtView = view.findViewById(R.id.phNumber);
        String str = accountNumberTxtView.getText().toString().trim();
        String result = Utils.fullValidate(str, "Phone Pe PhoneNumber", false, 10, 10, true);
        boolean showErr ;
        showErr = result == null;
        if (!showErr) {
            accountNumberTxtView.setError(result);
            accountNumberTxtView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateConfirmPhNumber() {
        View view = getView();
        if (view == null) {
            return false;
        }

        TextView accountNumberTxtView = view.findViewById(R.id.confirmPhNum);
        String str = accountNumberTxtView.getText().toString().trim();
        String result = Utils.fullValidate(str, "Confirm Phone Pe PhoneNumber", false, 10, 10, true);
        boolean showErr ;
        showErr = result == null;
        if (!showErr) {
            accountNumberTxtView.setError(result);
            accountNumberTxtView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateUserName() {
        View view = getView();
        if (view == null) {
            return false;
        }

        TextView txtView = view.findViewById(R.id.userName);
        String str = txtView.getText().toString().trim();
        String result = Utils.fullValidate(str, "User Name", false, -1, -1, false);
        boolean showErr ;
        showErr = result == null;
        if (!showErr) {
            txtView.setError(result);
            txtView.requestFocus();
            return false;
        }
        return true;
    }

    private void handleListeners(View.OnClickListener listener) {
        View view = getView();
        if (view == null) {
            return;
        }

        Button createNewBut = view.findViewById(R.id.wdPhoneCreate);
        createNewBut.setOnClickListener(listener);
    }

    private void handleTextWatchers(boolean add) {
        View view = getView();
        if (view == null) {
            return;
        }
        TextView phoneNumTextView = view.findViewById(R.id.phNumber);
        TextView confirmPhoneNumTextView = view.findViewById(R.id.confirmPhNum);
        TextView userNameTextView = view.findViewById(R.id.userName);
        TextView withdrawTextView = view.findViewById(R.id.bankWithdrawAmt);

        if (add) {
            phoneNumberWatcher = new PATextWatcher(phoneNumTextView, this);
            confirmPhoneNumberWatcher = new PATextWatcher(confirmPhoneNumTextView, this);
            userNameTextWatcher = new PATextWatcher(userNameTextView, this);
            wdAmtTextWatcher = new PATextWatcher(withdrawTextView, this);

            phoneNumTextView.addTextChangedListener(phoneNumberWatcher);
            confirmPhoneNumTextView.addTextChangedListener(confirmPhoneNumberWatcher);
            userNameTextView.addTextChangedListener(userNameTextWatcher);
            withdrawTextView.addTextChangedListener(wdAmtTextWatcher);
        } else {
            phoneNumTextView.removeTextChangedListener(phoneNumberWatcher);
            confirmPhoneNumTextView.removeTextChangedListener(confirmPhoneNumberWatcher);
            userNameTextView.removeTextChangedListener(userNameTextWatcher);
            withdrawTextView.removeTextChangedListener(wdAmtTextWatcher);
        }
    }

    public void doAction(int calledId, Object userObject) {
        Activity mainActivity = getActivity();
        if (mainActivity instanceof MainActivity) {
            ((MainActivity)mainActivity).fetchUpdateMoney();
            ((MainActivity)mainActivity).launchView(Navigator.WITHDRAW_REQ_VIEW, null, false);
        }
    }
}
