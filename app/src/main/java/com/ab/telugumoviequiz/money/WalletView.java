package com.ab.telugumoviequiz.money;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.ab.telugumoviequiz.constants.UserMoneyAccountType;
import com.ab.telugumoviequiz.games.PayGameModel;
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.UserMoney;

import java.util.ArrayList;
import java.util.List;

public class WalletView extends BaseFragment implements CallbackResponse, View.OnClickListener, NotifyTextChanged {

    private ViewAccountsAdapter mAdapter;
    private final List<PayGameModel> accountsList = new ArrayList<>();
    private PATextWatcher mailTextWatcher;

    public WalletView() {
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
        View root = inflater.inflate(R.layout.wallet_view, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        mAdapter = new ViewAccountsAdapter(accountsList, this.getContext());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        updateMoneyEntries();

        Spinner fromSpinner = root.findViewById(R.id.transferFromSpin);
        Spinner toSpinner = root.findViewById(R.id.transferToSpin);

        String[] accountNames = {"Referral Money", "Winning Money", "Main Money"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, accountNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(adapter);
        fromSpinner.setSelection(0);

        toSpinner.setAdapter(adapter);
        toSpinner.setSelection(1);
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
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, final Object response, Object helperObject) {
        boolean isHandled = handleServerError(exceptionThrown, isAPIException, response);
        if (isHandled) {
            return;
        }
        if (reqId == Request.TRANSFER_MONEY_REQ) {
            if (isAPIException) {
                handleAPIError(true, response, 1, null, null);
                return;
            }
            boolean transferResult = (Boolean) response;
            String msg = "Transfer Successful";
            if (!transferResult) {
                msg = "Transfer Unsuccessful";
            }
            if (getView() == null) {
                return;
            }
            final String msgFinal = msg;
            Runnable run = () -> {
                View loadButton = getView().findViewById(R.id.transferAmtBut);
                displayErrorAsSnackBar(msgFinal, loadButton);
                Activity mainActivity = getActivity();
                if (mainActivity != null) {
                    ((MainActivity)mainActivity).fetchUpdateMoney();
                }
            };
            requireActivity().runOnUiThread(run);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.walletViewLoadBut) {
        } else if (id == R.id.transferAmtBut) {
            View view1 = getView();
            if (view1 == null) {
                return;
            }
            Spinner fromSpinner = view1.findViewById(R.id.transferFromSpin);
            Spinner toSpinner = view1.findViewById(R.id.transferToSpin);

            int fromSelectedValue = fromSpinner.getSelectedItemPosition();
            int toSelectedValue = toSpinner.getSelectedItemPosition();
            if (fromSelectedValue == toSelectedValue) {
                displayError("Both From and To Account names are same", null);
                return;
            }
            boolean isValid = validateMailId();
            if (!isValid) {
                return;
            }
            TextView transferAmtTextView = view1.findViewById(R.id.transferAmtTxt);
            String amount = transferAmtTextView.getText().toString();
            int amtInt = -1;
            amount = amount.trim();
            if (amount.length() > 0) {
                amtInt = Integer.parseInt(amount);
                if (amtInt <= 0) {
                    displayError("Enter a valid amount", null);
                    return;
                }
            }
            String[] accountNames = {"REFERALAMOUNT", "WINNINGAMOUNT", "LOADEDAMOUNT"};
            int sourceAccType = 3;
            if (fromSelectedValue == 1) {
                sourceAccType = 2;
            } else if (fromSelectedValue == 2) {
                sourceAccType = 1;
            }
            int destAccType = 3;
            if (toSelectedValue == 1) {
                destAccType = 2;
            } else if (toSelectedValue == 2) {
                destAccType = 1;
            }

            UserMoney userMoney = UserDetails.getInstance().getUserMoney();
            long currentBalance = userMoney.getLoadedAmount();
            if (sourceAccType == 2) {
                currentBalance = userMoney.getWinningAmount();
            } else if (sourceAccType == 3) {
                currentBalance = userMoney.getReferalAmount();
            }
            if (amtInt > currentBalance) {
                displayError("Amount is more than the current balance", null);
                return;
            }
            MainActivity mainActivity = (MainActivity) getActivity();
            assert mainActivity != null;
            mainActivity.setUserMoneyFetchedListener(this);
            TransferRequest transferRequest = new TransferRequest();
            String TRANSFER_AMOUNT_BY_USER_ID = "UPDATE USERMONEY SET "
                    + accountNames[fromSelectedValue] + " = " + accountNames[fromSelectedValue] + " + ? , "
                    + accountNames[toSelectedValue] + " = " + accountNames[toSelectedValue] + " + ? ";
            transferRequest.setSqlQry(TRANSFER_AMOUNT_BY_USER_ID);
            transferRequest.setAmount(amtInt);
            transferRequest.setSourceAccType(sourceAccType);
            transferRequest.setDestAccType(destAccType);

            PostTask<TransferRequest, Boolean> transferReq = Request.getTransferRequest();
            transferReq.setCallbackResponse(this);
            transferReq.setPostObject(transferRequest);
            transferReq.setActivity(getActivity(), "Processing. Please Wait!");
            Scheduler.getInstance().submit(transferReq);
            transferAmtTextView.setText("0");
        }
    }

    private void updateMoneyEntries() {

        UserMoney userMoney = UserDetails.getInstance().getUserMoney();
        final List<PayGameModel> modelList = new ArrayList<>();
        PayGameModel referralMoney = new PayGameModel();
        referralMoney.setAccountName("Referral Money");
        referralMoney.setAccountBalance(String.valueOf(userMoney.getReferalAmount()));
        referralMoney.setAccountNumber(UserMoneyAccountType.findById(3).getId());
        modelList.add(referralMoney);

        PayGameModel winningMoney = new PayGameModel();
        winningMoney.setAccountName("Winning Money");
        winningMoney.setAccountBalance(String.valueOf(userMoney.getWinningAmount()));
        assert UserMoneyAccountType.findById(2) != null;
        winningMoney.setAccountNumber(UserMoneyAccountType.findById(2).getId());
        modelList.add(winningMoney);

        PayGameModel mainMoney = new PayGameModel();
        mainMoney.setAccountName("Main Money");
        mainMoney.setAccountBalance(String.valueOf(userMoney.getLoadedAmount()));
        assert UserMoneyAccountType.findById(1) != null;
        mainMoney.setAccountNumber(UserMoneyAccountType.findById(1).getId());
        modelList.add(mainMoney);

        accountsList.clear();
        accountsList.addAll(modelList);
        mAdapter.notifyDataSetChanged();
    }

    private void handleTextWatchers(boolean add) {
        View view = getView();
        if (view == null) {
            return;
        }
        TextView transferTextView = view.findViewById(R.id.transferAmtTxt);

        if (add) {
            mailTextWatcher = new PATextWatcher(transferTextView, this);
            transferTextView.addTextChangedListener(mailTextWatcher);
        } else {
            transferTextView.removeTextChangedListener(mailTextWatcher);
        }
    }

    public void textChanged(int viewId) {
        if (viewId == R.id.transferAmtTxt) {
            validateMailId();
        }
    }

    private boolean validateMailId() {
        View view = getView();
        if (view == null) {
            return false;
        }
        TextView mailUI = view.findViewById(R.id.transferAmtTxt);
        String str = mailUI.getText().toString().trim();
        String result = Utils.fullValidate(str, "Transfer Amount", false, -1, -1, true);
        boolean showErr = true;
        if (result != null) {
            showErr = false;
        }
        if (!showErr) {
            mailUI.setError(result);
            mailUI.requestFocus();
            return false;
        }
        return true;
    }

    private void handleListeners(View.OnClickListener listener) {
        View view = getView();
        if (view == null) {
            return;
        }
        Button loadButton = view.findViewById(R.id.walletViewLoadBut);
        loadButton.setOnClickListener(listener);

        Button transferButton = view.findViewById(R.id.transferAmtBut);
        transferButton.setOnClickListener(listener);
    }

    @Override
    public void passData(int reqId, List<String> data) {
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        mainActivity.setUserMoneyFetchedListener(null);
        if (reqId == 1000) {
            Runnable run = this::updateMoneyEntries;
            requireActivity().runOnUiThread(run);
            return;
        }
        super.passData(reqId, data);
    }
}
