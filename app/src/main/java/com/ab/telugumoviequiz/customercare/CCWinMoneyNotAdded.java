package com.ab.telugumoviequiz.customercare;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.DialogAction;
import com.ab.telugumoviequiz.common.Keys;
import com.ab.telugumoviequiz.common.NotifyTextChanged;
import com.ab.telugumoviequiz.common.PATextWatcher;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.constants.CustomerCareReqType;
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.Navigator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.StringTokenizer;

public class CCWinMoneyNotAdded extends BaseFragment implements View.OnClickListener, CallbackResponse,
        DatePickerDialog.OnDateSetListener, NotifyTextChanged, DialogAction {

    private PATextWatcher gameIdWatcher;
    private static final String ISSUE_DATE_KEY = "PLAYED_DATE";
    private static final String ISSUE_GAMEID_KEY = "GAME_ID";
    private int ccSubType = -1;

    public CCWinMoneyNotAdded() {
        super();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            ccSubType = bundle.getInt(Keys.CC_SUB_TYPE);
        } else {
            if (getArguments() != null) {
                ccSubType = getArguments().getInt(Keys.CC_SUB_TYPE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Keys.CC_SUB_TYPE, ccSubType);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.v("CC View: ", "ccSubType:" + ccSubType);
        Log.v("CC View:", "args:" + getArguments());
        View view;
        if (ccSubType == 1) {
            view = inflater.inflate(R.layout.cc_win_money_issue, container, false);
            Bundle args = getArguments();
            if (args != null) {
                TextView gameIdTV = view.findViewById(R.id.ccWinMoneyGameIdET);
                TextView playedDate = view.findViewById(R.id.ccWinMoneyDateET);
                String ISSUE_DATE_KEY = "PLAYED_DATE";
                String ISSUE_GAMEID_KEY = "GAME_ID";
                String playedDateStr = args.getString(ISSUE_DATE_KEY);
                String gameIdStr = args.getString(ISSUE_GAMEID_KEY);

                gameIdTV.setText(gameIdStr);
                playedDate.setText(playedDateStr);
            }
        } else {
            view = inflater.inflate(R.layout.cc_cancelled_game_money, container, false);
        }
        return view;
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
        if (viewId == R.id.ccWinMoneyGameIdET) {
            validateGameId();
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.ccWinMoneySetDate) {
            CCUtils.showDateChooser(getContext(), this);
        } else if (viewId == R.id.ccCreateBut) {
            boolean gameIdResult = validateGameId();
            if (!gameIdResult) {
                return;
            }
            View currentView = getView();
            if (currentView == null) {
                return;
            }
            TextView playedDate = currentView.findViewById(R.id.ccWinMoneyDateET);
            String playedDateStr = playedDate.getText().toString().trim();
            if (playedDateStr.length() == 0) {
                displayError("Enter game played date", null);
                return;
            }
            StringTokenizer stringTokenizer = new StringTokenizer(playedDateStr, "/");
            int day = Integer.parseInt(stringTokenizer.nextToken().trim());
            int month = Integer.parseInt(stringTokenizer.nextToken().trim());
            int year = Integer.parseInt(stringTokenizer.nextToken().trim());
            boolean issueDateValidation = CCUtils.validateDate(day, month, year, 72);
            if (!issueDateValidation) {
                if (ccSubType == 1) {
                    displayError("Money added date should be less than 72 hrs", null);
                } else {
                    displayError("Played Game date should be less than 72 hrs", null);
                }
                return;
            }

            TextView gameIdTV = currentView.findViewById(R.id.ccWinMoneyGameIdET);
            String gameIdStr = gameIdTV.getText().toString().trim();

            HashMap<String,String> ccExtraDetailMap = new HashMap<>();
            ccExtraDetailMap.put(ISSUE_DATE_KEY, playedDateStr);
            ccExtraDetailMap.put(ISSUE_GAMEID_KEY, gameIdStr);

            String ccExtraDetails = CCUtils.encodeCCExtraValues(ccExtraDetailMap);
            if (ccSubType == 1) {
                CCUtils.createdCCTicket(CustomerCareReqType.WIN_MONEY_NOT_ADDED.getId(),
                        this, ccExtraDetails, this.getActivity());
            } else {
                CCUtils.createdCCTicket(CustomerCareReqType.CANCELLED_GAME_MONEY_NOT_ADDED.getId(),
                        this, ccExtraDetails, this.getActivity());
            }
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
            String msg = "Successfully created Ticket. It will be checked and status will be updated";
            if (id == -1) {
                msg = "Failed to create ticket. Please retry";
            }
            displayInfo(msg, this);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        View currentView = getView();
        if (currentView == null) {
            return;
        }
        TextInputEditText ccAddedDateText = currentView.findViewById(R.id.ccWinMoneyDateET);
        String dateStr = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        ccAddedDateText.setText(dateStr);
    }

    private boolean validateGameId() {
        View view = getView();
        if (view == null) {
            return false;
        }
        TextView gameIdUI = view.findViewById(R.id.ccWinMoneyGameIdET);
        String str = gameIdUI.getText().toString().trim();
        String result = Utils.fullValidate(str, "Game Id", false, -1, -1, true);
        boolean showErr = true;
        if (result != null) {
            showErr = false;
        }
        if (!showErr) {
            gameIdUI.setError(result);
            gameIdUI.requestFocus();
            return false;
        }
        //int gameIdInt = Integer.parseInt(str);
        /*if ((gameIdInt < 100) || (gameIdInt > 2000)) {
            gameIdUI.setError("Valid values are between 100 - 2000");
            gameIdUI.requestFocus();
            return false;
        }*/
        return true;
    }

    private void handleListeners(View.OnClickListener listener) {
        View view = getView();
        if (view == null) {
            return;
        }
        Button createNewBut = view.findViewById(R.id.ccCreateBut);
        createNewBut.setOnClickListener(listener);

        Button setDateButton = view.findViewById(R.id.ccWinMoneySetDate);
        setDateButton.setOnClickListener(listener);
    }

    private void handleTextWatchers(boolean add) {
        View view = getView();
        if (view == null) {
            return;
        }
        TextView gameIdTextView = view.findViewById(R.id.ccWinMoneyGameIdET);
        if (add) {
            gameIdWatcher = new PATextWatcher(gameIdTextView, this);
            gameIdTextView.addTextChangedListener(gameIdWatcher);
        } else {
            gameIdTextView.removeTextChangedListener(gameIdWatcher);
        }
    }

    public void doAction(int calledId, Object userObject) {
        Activity mainActivity = getActivity();
        if (mainActivity instanceof MainActivity) {
            ((MainActivity)mainActivity).launchView(Navigator.CC_REQ_VIEW, null, false);
        }
    }
}