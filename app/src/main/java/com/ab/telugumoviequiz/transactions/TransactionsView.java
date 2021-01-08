package com.ab.telugumoviequiz.transactions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.ab.telugumoviequiz.main.UserProfile;
import com.ab.telugumoviequiz.referals.ReferalDetails;
import com.ab.telugumoviequiz.referals.ReferalViewAdapter;
import com.ab.telugumoviequiz.referals.UserReferal;

import java.util.ArrayList;
import java.util.List;

public class TransactionsView extends BaseFragment implements View.OnClickListener, CallbackResponse {
    private AlertDialog alertDialog;
    private int rowCount = 0;
    private ViewAdapter tableAdapter;
    private final List<MyTransaction> tableData = new ArrayList<>();

    private void handleListeners(View.OnClickListener listener) {
        View view = getView();
        if (view == null) {
            return;
        }
        Button prevButton = view.findViewById(R.id.myreferals_prev_but);
        Button nextButton = view.findViewById(R.id.myreferals_next_but);

        prevButton.setOnClickListener(listener);
        nextButton.setOnClickListener(listener);
    }

    private void fetchRecords() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Information");
        alertDialogBuilder.setMessage("Loading. Please Wait!").setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        UserProfile userProfile = UserDetails.getInstance().getUserProfile();
        GetTask<TransactionsHolder> request = Request.getUserTransactions(userProfile.getId(), rowCount, -1);
        request.setCallbackResponse(this);
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
        List<MyTransaction> list = details.getTransactionsList();
        tableData.clear();
        tableData.addAll(list);
        tableAdapter.notifyDataSetChanged();
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
        tableHeadings[1] = resources.getString(R.string.transactionview_col2);
        tableHeadings[2] = resources.getString(R.string.transactionview_col3);
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
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedBundle) {
        super.onActivityCreated(savedBundle);
        fetchRecords();
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
        int maxRowCount = 10;
        if (id == R.id.myreferals_prev_but) {
            rowCount = rowCount - maxRowCount;
        } else if (id == R.id.myreferals_next_but) {
            rowCount = rowCount + maxRowCount;
        }
        fetchRecords();
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, final Object response, Object helperObject) {
        Runnable run = () -> {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
        };
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(run);
        }

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
            run = () -> populateTable(result);
            if (activity != null) {
                activity.runOnUiThread(run);
            }
        }
    }
}
