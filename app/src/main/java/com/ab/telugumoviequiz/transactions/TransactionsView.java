package com.ab.telugumoviequiz.transactions;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.customercare.CCUtils;
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.Navigator;
import com.ab.telugumoviequiz.main.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransactionsView extends BaseFragment implements PopupMenu.OnMenuItemClickListener,
        View.OnClickListener, CallbackResponse {

    private int startPosOffset = 0;
    private ViewAdapter tableAdapter;
    private final List<MyTransaction> tableData = new ArrayList<>();
    private int accountType = -1;
    public final static int MORE_OPTIONS_BUTTON_ID = 1;
    private final static int VIEW_MORE_DETAILS = 10;
    private final static int OPEN_CUSTOMER_CARE_TKT = 11;

    private void handleListeners(View.OnClickListener listener) {
        View view = getView();
        if (view == null) {
            return;
        }
        Button prevButton = view.findViewById(R.id.myreferals_prev_but);
        Button nextButton = view.findViewById(R.id.myreferals_next_but);

        prevButton.setOnClickListener(listener);
        nextButton.setOnClickListener(listener);

        Button filterAccType = view.findViewById(R.id.filterAccType);
        filterAccType.setOnClickListener(listener);
    }

    private void fetchRecords() {
        UserProfile userProfile = UserDetails.getInstance().getUserProfile();
        GetTask<TransactionsHolder> request = Request.getUserTransactions(userProfile.getId(), startPosOffset, accountType);
        request.setCallbackResponse(this);
        request.setActivity(getActivity(), null);
        Scheduler.getInstance().submit(request);
    }

    private void populateTable(TransactionsHolder details) {

        View view = getView();
        if (view == null) {
            return;
        }
        Button prevButton = view.findViewById(R.id.myreferals_prev_but);
        Button nextButton = view.findViewById(R.id.myreferals_next_but);

        prevButton.setEnabled(details.isPrevEnabled());
        nextButton.setEnabled(details.isNextEnabled());
        TextView totalView = view.findViewById(R.id.view_total);
        List<MyTransaction> list = details.getTransactionsList();
        tableData.clear();
        tableData.addAll(list);
        tableAdapter.notifyDataSetChanged();
        String totalPrefix = getResources().getString(R.string.total_prefix);
        int start;
        int end;
        if (details.getTotal() == 0) {
            start = 0;
            end = 0;
        } else {
            start = startPosOffset + 1;
            end = startPosOffset + list.size();
        }
        String totalStr = totalPrefix + start + " - " + end + " of " + details.getTotal();
        totalView.setText(totalStr);
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        int[] points = Utils.getScreenWidth(getContext());
        ViewAdapter.screenWidth = points[0];

        String [] tableHeadings = new String[8];
        Resources resources = getResources();
        tableHeadings[0] = resources.getString(R.string.transactionview_col1);
        tableHeadings[2] = resources.getString(R.string.transactionview_col3);
        tableHeadings[1] = resources.getString(R.string.transactionview_col2);
        tableHeadings[3] = resources.getString(R.string.transactionview_col4);
        tableHeadings[4] = resources.getString(R.string.transactionview_col5);
        tableHeadings[5] = resources.getString(R.string.transactionview_col6);
        tableHeadings[6] = resources.getString(R.string.transactionview_col7);
        tableHeadings[7] = resources.getString(R.string.transactionview_col8);

        View root = inflater.inflate(R.layout.mytrans, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        tableAdapter = new ViewAdapter(tableData, tableHeadings);
        recyclerView.setAdapter(tableAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        tableAdapter.setClickListener(this);
        fetchRecords();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        handleListeners(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        handleListeners(null);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        int maxRowCount = 5;
        if (id == R.id.myreferals_prev_but) {
            startPosOffset = startPosOffset - maxRowCount;
            fetchRecords();
        } else if (id == R.id.myreferals_next_but) {
            startPosOffset = startPosOffset + maxRowCount;
            fetchRecords();
        } else if (id == R.id.filterAccType) {
            Resources resources = requireActivity().getResources();
            CharSequence[] accTypes = resources.getTextArray(R.array.myreferal_acc_options);
            PopupMenu popupMenu = new PopupMenu(getActivity(), view);
            for (CharSequence s : accTypes) {
                MenuItem item = popupMenu.getMenu().add(s);
                item.setActionView(view);
            }
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        } else if (id == MORE_OPTIONS_BUTTON_ID) {
            MyTransaction selectedTransaction = (MyTransaction) view.getTag();
            Resources resources = requireActivity().getResources();
            CharSequence[] accTypes = resources.getTextArray(R.array.transactions_more_options);
            PopupMenu popupMenu = new PopupMenu(getActivity(), view);
            int counter = 10;
            for (CharSequence s : accTypes) {
                MenuItem item = popupMenu.getMenu().add(0,counter++, 0, s);
                view.setTag(selectedTransaction);
                item.setActionView(view);
            }
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }
    }

    @Override
    public boolean onMenuItemClick (MenuItem item) {
        int id = item.getItemId();
        if (id == VIEW_MORE_DETAILS) {
            MyTransaction selectedTransaction = (MyTransaction) item.getActionView().getTag();
            StringBuilder stringBuilder = new StringBuilder("Transaction Id: ");
            stringBuilder.append(selectedTransaction.getTransactionId());
            stringBuilder.append("\n");
            stringBuilder.append("Transaction Result: ");
            String operResult = "Fail";
            if (selectedTransaction.getOperResult() == 1) {
                operResult = "Success";
            }
            stringBuilder.append(operResult);
            Utils.showMessage("", stringBuilder.toString(), this.getContext(), null);
            return true;
        } else if (id == OPEN_CUSTOMER_CARE_TKT) {
            MyTransaction selectedTransaction = (MyTransaction) item.getActionView().getTag();
            String extraDetails = selectedTransaction.getExtraDetails();
            HashMap<String,String> extraDetailsMap = CCUtils.decodeCCExtraValues(extraDetails);

            String GAME_START_TIME_KEY = "GAME_START_TIME";
            String GAME_CLIENT_ID = "GAME_CLIENT_ID";

            String gameStartTime = extraDetailsMap.get(GAME_START_TIME_KEY);
            String gameCientId = extraDetailsMap.get(GAME_CLIENT_ID);


            String ISSUE_DATE_KEY = "PLAYED_DATE";
            String ISSUE_GAMEID_KEY = "GAME_ID";
            Bundle bundle = new Bundle();
            bundle.putString(ISSUE_DATE_KEY, gameStartTime);
            bundle.putString(ISSUE_GAMEID_KEY, gameCientId);
            Activity parentActivity = getActivity();
            if (parentActivity instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) parentActivity;
                mainActivity.launchView(Navigator.NEW_CC_REQUEST, bundle, false);
            }
        }
        String text = (String) item.getTitle();
        accountType = -1;
        if (text.contains("Others")) {
            accountType = 1;
        } else if (text.contains("Win")) {
            accountType = 2;
        } else if (text.contains("Referral")) {
            accountType = 3;
        }
        startPosOffset = 0;
        fetchRecords();
        return true;
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, final Object response, Object helperObject) {
        if((exceptionThrown) && (!isAPIException)) {
            showErrShowHomeScreen((String) response);
            return;
        }
        if (reqId == Request.USER_TRANSACTIONS) {
            if (isAPIException) {
                showErrShowHomeScreen((String) response);
                return;
            }
            final TransactionsHolder result = (TransactionsHolder) response;
            Runnable run = () -> populateTable(result);
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(run);
            }
        }
    }
}
