package com.ab.telugumoviequiz.withdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.main.Navigator;

public class NewWithdrawReq extends BaseFragment implements AdapterView.OnItemSelectedListener {

    public NewWithdrawReq() {
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
        View root = inflater.inflate(R.layout.withdraw_money, container, false);

        Spinner transferTypesSpinner = root.findViewById(R.id.wdTransferTypes);
        String[] transferTypes = {"Bank Account", "PhonePe"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_list_item, transferTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transferTypesSpinner.setAdapter(adapter);

        transferTypesSpinner.setSelection(0);

        showView(Navigator.TRANSFER_TO_BANK_ACCOUNT);
        transferTypesSpinner.setOnItemSelectedListener(this);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void showView(String viewName) {
        FragmentActivity parentActivity = getActivity();
        if (parentActivity == null) {
            return;
        }
        FragmentManager mgr = parentActivity.getSupportFragmentManager();
        Fragment fragment = getFragment(viewName);
        if (fragment == null) {
            return;
        }
        final FragmentTransaction ft = mgr.beginTransaction();
        ft.replace(R.id.content1, fragment, viewName);
        ft.commit();
    }

    private Fragment getFragment(String viewId) {
        BaseFragment fragment = null;
        switch (viewId) {
            case Navigator.TRANSFER_TO_BANK_ACCOUNT: {
                fragment = new TransferBankAccount();
                break;
            }
            case Navigator.TRANSFER_TO_PHONEPE_ACCOUNT: {
                fragment = new TransferByPhonePe();
                break;
            }
        }
        return fragment;
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        String viewName = Navigator.TRANSFER_TO_BANK_ACCOUNT;
        if (position == 1) {
            viewName = Navigator.TRANSFER_TO_PHONEPE_ACCOUNT;
        }
        showView(viewName);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
}
