package com.ab.telugumoviequiz.customercare;

import android.os.Bundle;
import android.util.Log;
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
import com.ab.telugumoviequiz.common.Keys;
import com.ab.telugumoviequiz.main.Navigator;

public class NewCCReq extends BaseFragment implements AdapterView.OnItemSelectedListener {

    private Spinner ccTypesSpinner;
    private Bundle args;
    public NewCCReq() {
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
        View root = inflater.inflate(R.layout.cc_root_panel, container, false);

        ccTypesSpinner = root.findViewById(R.id.wdTransferTypes);
        String[] ccTypes = {"Added Money Not Updated", "Win Money Not Added",
                "Cancelled Game Money Not Added", "Withdraw Request Not Processed",
                "Question/Answer Wrong", "Others"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_list_item, ccTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ccTypesSpinner.setAdapter(adapter);

        ccTypesSpinner.setOnItemSelectedListener(this);

        args = getArguments();
        if (args != null) {
            ccTypesSpinner.setSelection(1);
        } else {
            ccTypesSpinner.setSelection(0);
        }

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
        ccTypesSpinner.setOnItemSelectedListener(null);
    }

    private void showView(String viewName, Bundle args1) {
        FragmentActivity parentActivity = getActivity();
        if (parentActivity == null) {
            return;
        }
        FragmentManager mgr = parentActivity.getSupportFragmentManager();
        Fragment fragment = getFragment(viewName, args1);
        if (fragment == null) {
            return;
        }
        final FragmentTransaction ft = mgr.beginTransaction();
        ft.replace(R.id.content1, fragment, viewName);
        ft.commit();
    }

    private Fragment getFragment(String viewId, Bundle args) {
        BaseFragment fragment;
        if (args == null) {
            args = new Bundle();
        }
        switch (viewId) {
            case Navigator.ADDED_MONEY_NOT_UPDATED: {
                fragment = new CCAddedMoneyIssue();
                break;
            }
            case Navigator.WIN_MONEY_NOT_UPDATED: {
                fragment = new CCWinMoneyNotAdded();
                args.putInt(Keys.CC_SUB_TYPE, 1);
                fragment.setArguments(args);
                break;
            }
            case Navigator.CANCELLED_GAME_RATE_NOT_ADDED: {
                fragment = new CCWinMoneyNotAdded();
                args.putInt(Keys.CC_SUB_TYPE, 2);
                fragment.setArguments(args);
                break;
            }
            case Navigator.WD_REQ_NOT_PROCESSED: {
                fragment = new CCWDReqNotProcessed();
                break;
            }
            case Navigator.QUESTION_ANSWER_WRONG: {
                fragment = new CCQuestionWrong();
                break;
            }
            case Navigator.CC_OTHERS: {
                fragment = new CCOthersIssue();
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + viewId);
        }
        return fragment;
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        Log.v("Item", "position:" + position);
        String viewName = Navigator.ADDED_MONEY_NOT_UPDATED;
        if (position == 1) {
            viewName = Navigator.WIN_MONEY_NOT_UPDATED;
        } else if (position == 2) {
            viewName = Navigator.CANCELLED_GAME_RATE_NOT_ADDED;
        } else if (position == 3) {
            viewName = Navigator.WD_REQ_NOT_PROCESSED;
        } else if (position == 4) {
            viewName = Navigator.QUESTION_ANSWER_WRONG;
        } else if (position == 5) {
            viewName = Navigator.CC_OTHERS;
        }
        showView(viewName, args);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
}
