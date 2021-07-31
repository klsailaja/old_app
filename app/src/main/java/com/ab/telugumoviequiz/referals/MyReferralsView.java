package com.ab.telugumoviequiz.referals;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.ab.telugumoviequiz.main.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class MyReferralsView extends BaseFragment implements View.OnClickListener, CallbackResponse {
    private int rowCount = 0;
    private ReferalViewAdapter tableAdapter;
    private final List<UserReferal> tableData = new ArrayList<>();

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
        UserProfile userProfile = UserDetails.getInstance().getUserProfile();
        GetTask<ReferalDetails> request = Request.getUserReferalDetails(userProfile.getMyReferalId(), rowCount);
        request.setCallbackResponse(this);
        request.setActivity(getActivity(), null);
        Scheduler.getInstance().submit(request);
    }

    private void populateTable(ReferalDetails details) {

        View view = getView();
        if (view == null) {
            return;
        }
        Button prevButton = view.findViewById(R.id.myreferals_prev_but);
        Button nextButton = view.findViewById(R.id.myreferals_next_but);

        prevButton.setEnabled(details.isPrevEnabled());
        nextButton.setEnabled(details.isNextEnabled());
        List<UserReferal> list = details.getReferalList();
        tableData.clear();
        tableData.addAll(list);
        tableAdapter.notifyDataSetChanged();
        TextView totalView = view.findViewById(R.id.view_total);
        String totalPrefix = getResources().getString(R.string.total_prefix);
        int start;
        int end;
        if (details.getTotal() == 0) {
            start = 0;
            end = 0;
        } else {
            start = rowCount + 1;
            end = rowCount + list.size();
        }
        String totalStr = totalPrefix + start + " - " + end
                + " of " + details.getTotal();
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
        ReferalViewAdapter.screenWidth = points[0];
        ReferalViewAdapter.screenHeight = points[1];

        String [] tableHeadings = new String[3];
        Resources resources = getResources();
        tableHeadings[0] = resources.getString(R.string.table_view_col1);
        tableHeadings[1] = resources.getString(R.string.table_view_col2);
        tableHeadings[2] = resources.getString(R.string.table_view_col3);

        View root = inflater.inflate(R.layout.myreferals, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        tableAdapter = new ReferalViewAdapter(tableData, tableHeadings);
        recyclerView.setAdapter(tableAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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
        if((exceptionThrown) && (!isAPIException)) {
            showErrShowHomeScreen((String) response);
            return;
        }
        if (reqId == Request.USER_REFERRALS_LIST) {
            if (isAPIException) {
                showErrShowHomeScreen((String) response);
                return;
            }
            final ReferalDetails result = (ReferalDetails) response;
            Runnable run = () -> populateTable(result);
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(run);
            }
        }
    }
}
