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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class TransferBankAccount extends BaseFragment
        implements CallbackResponse, View.OnClickListener, NotifyTextChanged,
        View.OnFocusChangeListener,
        AdapterView.OnItemSelectedListener, DialogAction {

    private PATextWatcher accNumTextWatcher, confirmAccNumTextWatcher;
    private PATextWatcher bankNameTextWatcher, ifscCodeTextWatcher;
    private PATextWatcher wdAmtTextWatcher;

    public TransferBankAccount() {
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

        View view = inflater.inflate(R.layout.wd_transfer_bank, container, false);

        TextInputLayout textInputLayout = view.findViewById(R.id.accNumIL);
        textInputLayout.setHelperTextEnabled(true);
        textInputLayout.setHelperText("The Account Number Entered is final");
        textInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setCurrentBalance(false, -1);
        handleListeners(this);
        handleFocusListener(this);
        handleTextWatchers(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        handleListeners(null);
        handleFocusListener(null);
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

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        setCurrentBalance(false, -1);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public void onFocusChange (View v, boolean hasFocus) {
        if (v instanceof TextInputEditText) {
            if (hasFocus) {
                TextInputEditText txtInputEdit = (TextInputEditText) v;
                String compName = Objects.requireNonNull(txtInputEdit.getHint()).toString();
                displayErrorAsToast("You are typing " + compName + " details");
            }
        }
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
        wdUserInput.setRequestType(2);

        TextView accountNumTextView = view.findViewById(R.id.accNum);
        TextView bankNameTextView = view.findViewById(R.id.bankName);
        TextView bankIfscCodeTextView = view.findViewById(R.id.bankIfscCode);

        WithdrawReqByBank wdBankDetails = new WithdrawReqByBank();

        str = accountNumTextView.getText().toString().trim();
        wdBankDetails.setAccountNumber(str);

        str = bankIfscCodeTextView.getText().toString().trim();
        wdBankDetails.setIfscCode(str);

        str = bankNameTextView.getText().toString().trim();
        wdBankDetails.setBankName(str);

        wdBankDetails.setUserName(UserDetails.getInstance().getUserProfile().getName());

        wdRequestBankType.setByBankDetails(wdBankDetails);
        wdRequestBankType.setByPhoneDetails(null);

        return wdRequestBankType;
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
        if (viewId == R.id.accNum) {
            validateAccountNumber();
        } else if (viewId == R.id.confirmAccNum) {
            validateConfirmAccountNumber();
        } else if (viewId == R.id.bankName) {
            validateBankName();
        } else if (viewId == R.id.bankIfscCode) {
            validateBankIfSCCode();
        } else if (viewId == R.id.bankWithdrawAmt) {
            validateWDAmount();
        }
    }

    private boolean validateData() {
        boolean result = validateWDAmount();
        if (!result) {
            return false;
        }
        result = validateAccountNumber();
        if (!result) {
            return false;
        }
        result = validateConfirmAccountNumber();
        if (!result) {
            return false;
        }
        View view = getView();
        if (view != null) {
            TextView accountNumberTxtView = view.findViewById(R.id.accNum);
            String str1 = accountNumberTxtView.getText().toString().trim();
            TextView confirmAccNumberTxtView = view.findViewById(R.id.confirmAccNum);
            String str2 = confirmAccNumberTxtView.getText().toString().trim();
            if (!str1.equals(str2)) {
                confirmAccNumberTxtView.setError("Account Number and Confirm Number not same");
                confirmAccNumberTxtView.requestFocus();
                return false;
            }
        }
        result = validateBankName();
        if (!result) {
            return false;
        }
        result = validateBankIfSCCode();
        return result;
    }


    private boolean validateAccountNumber() {
        View view = getView();
        if (view == null) {
            return false;
        }

        TextView accountNumberTxtView = view.findViewById(R.id.accNum);
        String str = accountNumberTxtView.getText().toString().trim();
        String result = Utils.fullValidate(str, "Beneficiary Account Number", false, -1, -1, false);
        boolean showErr ;
        showErr = result == null;
        if (!showErr) {
            accountNumberTxtView.setError(result);
            accountNumberTxtView.requestFocus();
            return false;
        }
        //displayErrorAsToast("You are typing in account number");
        return true;
    }

    private boolean validateConfirmAccountNumber() {
        View view = getView();
        if (view == null) {
            return false;
        }

        TextView accountNumberTxtView = view.findViewById(R.id.confirmAccNum);
        String str = accountNumberTxtView.getText().toString().trim();
        String result = Utils.fullValidate(str, "Confirm Beneficiary Account Number", false, -1, -1, false);
        boolean showErr ;
        showErr = result == null;
        if (!showErr) {
            accountNumberTxtView.setError(result);
            accountNumberTxtView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateBankName() {
        View view = getView();
        if (view == null) {
            return false;
        }

        TextView txtView = view.findViewById(R.id.bankName);
        String str = txtView.getText().toString().trim();
        String result = Utils.fullValidate(str, "Bank Name", false, -1, -1, false);
        boolean showErr ;
        showErr = result == null;
        if (!showErr) {
            txtView.setError(result);
            txtView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateBankIfSCCode() {
        View view = getView();
        if (view == null) {
            return false;
        }

        TextView txtView = view.findViewById(R.id.bankIfscCode);
        String str = txtView.getText().toString().trim();
        String result = Utils.fullValidate(str, "IFSC Code", false, -1, -1, false);
        boolean showErr ;
        showErr = result == null;
        if (!showErr) {
            txtView.setError(result);
            txtView.requestFocus();
            return false;
        }
        return true;
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

    private void handleListeners(View.OnClickListener listener) {
        View view = getView();
        if (view == null) {
            return;
        }

        Button createNewBut = view.findViewById(R.id.wdBankCreateBut);
        createNewBut.setOnClickListener(listener);
    }

    private void handleFocusListener(View.OnFocusChangeListener listener) {
        View view = getView();
        if (view == null) {
            return;
        }
        TextView accountNumTextView = view.findViewById(R.id.accNum);
        TextView confirmAccNuTextView = view.findViewById(R.id.confirmAccNum);
        TextView bankNameTextView = view.findViewById(R.id.bankName);
        TextView bankIfscCodeTextView = view.findViewById(R.id.bankIfscCode);
        TextView withdrawTextView = view.findViewById(R.id.bankWithdrawAmt);

        accountNumTextView.setOnFocusChangeListener(listener);
        confirmAccNuTextView.setOnFocusChangeListener(listener);
        bankNameTextView.setOnFocusChangeListener(listener);
        bankIfscCodeTextView.setOnFocusChangeListener(listener);
        withdrawTextView.setOnFocusChangeListener(listener);
    }

    private void handleTextWatchers(boolean add) {
        View view = getView();
        if (view == null) {
            return;
        }
        TextView accountNumTextView = view.findViewById(R.id.accNum);
        TextView confirmAccNuTextView = view.findViewById(R.id.confirmAccNum);
        TextView bankNameTextView = view.findViewById(R.id.bankName);
        TextView bankIfscCodeTextView = view.findViewById(R.id.bankIfscCode);
        TextView withdrawTextView = view.findViewById(R.id.bankWithdrawAmt);

        if (add) {
            accNumTextWatcher = new PATextWatcher(accountNumTextView, this);
            confirmAccNumTextWatcher = new PATextWatcher(confirmAccNuTextView, this);
            bankNameTextWatcher = new PATextWatcher(bankNameTextView, this);
            ifscCodeTextWatcher = new PATextWatcher(bankIfscCodeTextView, this);
            wdAmtTextWatcher = new PATextWatcher(withdrawTextView, this);

            accountNumTextView.addTextChangedListener(accNumTextWatcher);
            confirmAccNuTextView.addTextChangedListener(confirmAccNumTextWatcher);
            bankNameTextView.addTextChangedListener(bankNameTextWatcher);
            bankIfscCodeTextView.addTextChangedListener(ifscCodeTextWatcher);
            withdrawTextView.addTextChangedListener(wdAmtTextWatcher);
        } else {
            accountNumTextView.removeTextChangedListener(accNumTextWatcher);
            confirmAccNuTextView.removeTextChangedListener(confirmAccNumTextWatcher);
            bankNameTextView.removeTextChangedListener(bankNameTextWatcher);
            bankIfscCodeTextView.removeTextChangedListener(ifscCodeTextWatcher);
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
