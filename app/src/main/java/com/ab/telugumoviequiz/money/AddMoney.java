package com.ab.telugumoviequiz.money;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.constants.UserMoneyAccountType;
import com.ab.telugumoviequiz.games.PayGameModel;
import com.ab.telugumoviequiz.main.AddMoneyProcessor;
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.UserMoney;

import java.util.ArrayList;
import java.util.List;

public class AddMoney extends BaseFragment
        implements CallbackResponse, View.OnClickListener, NotifyTextChanged {
    private final List<PayGameModel> accountsList = new ArrayList<>();
    private ViewAccountsAdapter viewAccountsAdapter;
    private PATextWatcher addMoneyWatcher;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.add_money, container, false);
        RecyclerView moreMoneyListView = root.findViewById(R.id.moneyButtons);
        moreMoneyListView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        moreMoneyListView.setLayoutManager(mLayoutManager);
        AddMoneyViewAdapter moneyListAdapter = new AddMoneyViewAdapter(this);
        moreMoneyListView.setAdapter(moneyListAdapter);
        moreMoneyListView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView allMoneyAccListView = root.findViewById(R.id.moneyListView);
        allMoneyAccListView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        allMoneyAccListView.setLayoutManager(mLayoutManager);
        viewAccountsAdapter =
                new ViewAccountsAdapter(accountsList, getContext());
        allMoneyAccListView.setAdapter(viewAccountsAdapter);
        allMoneyAccListView.setItemAnimator(new DefaultItemAnimator());
        updateMoneyEntries();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

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
        View view1 = getView();
        if (view1 == null) {
            return;
        }
        if (view.getId() == R.id.list_money_button) {
            EditText moneyValEditText = view1.findViewById(R.id.otherMoneyTextBox);
            moneyValEditText.setText(view.getTag().toString());
        } else if (view.getId() == R.id.add_money_extra_fees) {
            CheckBox checkBox = view.findViewById(R.id.add_money_extra_fees);
            Button addMoneyButton = view1.findViewById(R.id.addBut);
            addMoneyButton.setEnabled(checkBox.isChecked());
        } else if (view.getId() == R.id.addBut) {
            boolean validVal = validateMailId();
            if (!validVal) {
                return;
            }
            TextView mailUI = view1.findViewById(R.id.otherMoneyTextBox);
            String str = mailUI.getText().toString().trim();
            int amtInt = Integer.parseInt(str);
            MainActivity mainActivity = (MainActivity)getActivity();
            AddMoneyProcessor.getInstance().processAddMoneyRequest(amtInt, mainActivity, mainActivity);
        }
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, Object response, Object userObject) {
    }

    private void handleListeners(View.OnClickListener listener) {
        View view = getView();
        if (view == null) {
            return;
        }
        CheckBox checkBox = view.findViewById(R.id.add_money_extra_fees);
        checkBox.setOnClickListener(listener);
        Button addMoneyBut = view.findViewById(R.id.addBut);
        addMoneyBut.setOnClickListener(listener);
    }

    private void handleTextWatchers(boolean add) {
        View view = getView();
        if (view == null) {
            return;
        }
        TextView addMoneyTextView = view.findViewById(R.id.otherMoneyTextBox);
        if (add) {
            addMoneyWatcher = new PATextWatcher(addMoneyTextView, this);
            addMoneyTextView.addTextChangedListener(addMoneyWatcher);
        } else {
            addMoneyTextView.removeTextChangedListener(addMoneyWatcher);
        }
    }

    public void textChanged(int viewId) {
        if (viewId == R.id.otherMoneyTextBox) {
            validateMailId();
        }
    }

    private boolean validateMailId() {
        View view = getView();
        if (view == null) {
            return false;
        }
        TextView mailUI = view.findViewById(R.id.otherMoneyTextBox);
        String str = mailUI.getText().toString().trim();
        String result = Utils.fullValidate(str, "Amount", false, -1, -1, true);
        boolean showErr = true;
        if (result != null) {
            showErr = false;
        }
        if (!showErr) {
            mailUI.setError(result);
            mailUI.requestFocus();
            return false;
        }
        int amtInt = Integer.parseInt(str);
        if ((amtInt < 100) || (amtInt > 2000)) {
            mailUI.setError("Valid values are between 100 - 2000");
            mailUI.requestFocus();
            return false;
        }
        return true;
    }

    private void updateMoneyEntries() {
        UserMoney userMoney = UserDetails.getInstance().getUserMoney();
        final List<PayGameModel> modelList = new ArrayList<>();
        PayGameModel referralMoney = new PayGameModel();
        referralMoney.setAccountName("Referral Money");
        referralMoney.setAccountBalance(String.valueOf(userMoney.getAmount()));
        referralMoney.setAccountNumber(UserMoneyAccountType.findById(3).getId());
        modelList.add(referralMoney);

        PayGameModel winningMoney = new PayGameModel();
        winningMoney.setAccountName("Winning Money");
        winningMoney.setAccountBalance(String.valueOf(userMoney.getAmount()));
        assert UserMoneyAccountType.findById(2) != null;
        winningMoney.setAccountNumber(UserMoneyAccountType.findById(2).getId());
        modelList.add(winningMoney);

        PayGameModel mainMoney = new PayGameModel();
        mainMoney.setAccountName("Main Money");
        mainMoney.setAccountBalance(String.valueOf(userMoney.getAmount()));
        assert UserMoneyAccountType.findById(1) != null;
        mainMoney.setAccountNumber(UserMoneyAccountType.findById(1).getId());
        modelList.add(mainMoney);

        accountsList.clear();
        accountsList.addAll(modelList);
        viewAccountsAdapter.notifyDataSetChanged();
    }
}
