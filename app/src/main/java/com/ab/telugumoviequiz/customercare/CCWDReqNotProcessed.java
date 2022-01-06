package com.ab.telugumoviequiz.customercare;

import android.app.Activity;
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
import com.ab.telugumoviequiz.common.DialogAction;
import com.ab.telugumoviequiz.common.NotifyTextChanged;
import com.ab.telugumoviequiz.common.PATextWatcher;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.constants.CustomerCareReqType;
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.Navigator;

import java.util.HashMap;

public class CCWDReqNotProcessed extends BaseFragment
        implements View.OnClickListener, CallbackResponse, NotifyTextChanged, DialogAction {

    private static final String ISSUE_WDREF_KEY = "WD_REF_NUM";
    private PATextWatcher textWatcher;

    public CCWDReqNotProcessed() {
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
        return inflater.inflate(R.layout.cc_wd_req, container, false);
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
    public void textChanged(int viewId) {
        if (viewId == R.id.ccAddedMoneyAmtET) {
            validateWdRequestId();
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.ccCreateBut) {
            boolean validation = validateWdRequestId();
            if (!validation) {
                return;
            }
            View currentView = getView();
            if (currentView == null) {
                return;
            }
            TextView issueAmt = currentView.findViewById(R.id.ccWDReqET);
            String issueAmtStr = issueAmt.getText().toString().trim();

            HashMap<String, String> ccExtraDetailMap = new HashMap<>();
            ccExtraDetailMap.put(ISSUE_WDREF_KEY, issueAmtStr);

            String ccExtraDetails = CCUtils.encodeCCExtraValues(ccExtraDetailMap);

            CCUtils.createdCCTicket(CustomerCareReqType.WD_NOT_PROCESSED.getId(),
                    this, ccExtraDetails, this.getActivity());
        }
    }

    private boolean validateWdRequestId() {
        View view = getView();
        if (view == null) {
            return false;
        }
        TextView mailUI = view.findViewById(R.id.ccWDReqET);
        String str = mailUI.getText().toString().trim();
        String result = Utils.fullValidate(str, "Withdraw Ref Number", false, -1, 10, false);
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
        Button createNewBut = view.findViewById(R.id.ccCreateBut);
        createNewBut.setOnClickListener(listener);

    }

    private void handleTextWatchers(boolean add) {
        View view = getView();
        if (view == null) {
            return;
        }
        TextView textView = view.findViewById(R.id.ccWDReqET);
        if (add) {
            textWatcher = new PATextWatcher(textView, this);
            textView.addTextChangedListener(textWatcher);
        } else {
            textView.removeTextChangedListener(textWatcher);
        }
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown,
                               boolean isAPIException, Object response, Object userObject) {
        boolean errorHandled = handleServerError(exceptionThrown, isAPIException, response);
        if (errorHandled) {
            return;
        }
        boolean isApiErrorHandled = handleAPIError(isAPIException, response, 1, null, null);
        if (isApiErrorHandled) {
            return;
        }
        if (reqId == Request.CREATE_CC_ISSUE) {
            Long id = (Long) response;
            String msg = "Successfully created Ticket";
            if (id == -1) {
                msg = "Failed to create ticket. Please retry";
            }
            displayInfo(msg, this);
        }
    }

    public void doAction(int calledId, Object userObject) {
        Activity mainActivity = getActivity();
        if (mainActivity instanceof MainActivity) {
            ((MainActivity)mainActivity).launchView(Navigator.CC_REQ_VIEW, null, false);
        }
    }
}
