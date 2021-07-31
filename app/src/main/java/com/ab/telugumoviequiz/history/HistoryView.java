package com.ab.telugumoviequiz.history;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
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
import com.ab.telugumoviequiz.games.PlayerSummary;
import com.ab.telugumoviequiz.games.ViewLeaderboard;
import com.ab.telugumoviequiz.main.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class HistoryView extends BaseFragment implements View.OnClickListener, CallbackResponse {

    private int startPosOffset = 0;
    private ViewAdapter tableAdapter;
    private final List<GameResults> tableData = new ArrayList<>();

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
        GetTask<UserHistoryGameDetails> request = Request.getUserHistoryGames(userProfile.getId(), startPosOffset);
        request.setCallbackResponse(this);
        request.setActivity(getActivity(), null);
        Scheduler.getInstance().submit(request);
    }

    private void populateTable(UserHistoryGameDetails details) {
        View view = getView();
        if (view == null) {
            return;
        }
        Button prevButton = view.findViewById(R.id.myreferals_prev_but);
        Button nextButton = view.findViewById(R.id.myreferals_next_but);

        prevButton.setEnabled(details.isPrevEnabled());
        nextButton.setEnabled(details.isNextEnabled());
        TextView totalView = view.findViewById(R.id.view_total);

        List<GameResults> list = details.getHistoryGames();
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

        String [] tableHeadings = new String[5];
        Resources resources = getResources();
        tableHeadings[0] = resources.getString(R.string.history_games_col1);
        tableHeadings[1] = resources.getString(R.string.history_games_col2);
        tableHeadings[2] = resources.getString(R.string.history_games_col3);
        tableHeadings[3] = resources.getString(R.string.history_games_col4);
        tableHeadings[4] = resources.getString(R.string.history_games_col5);

        View root = inflater.inflate(R.layout.myhistory, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        tableAdapter = new ViewAdapter(tableData, tableHeadings, this);
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
            startPosOffset = startPosOffset - maxRowCount;
            fetchRecords();
        } else if (id == R.id.myreferals_next_but) {
            startPosOffset = startPosOffset + maxRowCount;
            fetchRecords();
        } else if (id == R.id.col5) {
            String winList = (String) view.getTag();
            List<PlayerSummary> winnersList = new ArrayList<>(10);
            StringTokenizer winnerTokenizer = new StringTokenizer(winList, ":");
            while (winnerTokenizer.hasMoreTokens()) {
                String token = winnerTokenizer.nextToken();
                StringTokenizer localTokenizer = new StringTokenizer(token, ";");

                PlayerSummary playerSummary = new PlayerSummary();

                playerSummary.setUserName(localTokenizer.nextToken());
                playerSummary.setRank(Integer.parseInt(localTokenizer.nextToken()));
                playerSummary.setCorrectCount(Integer.parseInt(localTokenizer.nextToken()));
                String totalTimeStr = localTokenizer.nextToken();
                playerSummary.setTotalTime(Long.parseLong(totalTimeStr));
                playerSummary.setAmountWon(Integer.parseInt(localTokenizer.nextToken()));

                winnersList.add(playerSummary);
            }

            ViewLeaderboard viewLeaderboard = new ViewLeaderboard(getContext(), true, winnersList, true);
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            viewLeaderboard.show(fragmentManager, "dialog");
        }
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, final Object response, Object helperObject) {
        if((exceptionThrown) && (!isAPIException)) {
            showErrShowHomeScreen((String) response);
            return;
        }
        if (reqId == Request.USER_HISTORY_GAMES) {
            if (isAPIException) {
                showErrShowHomeScreen((String) response);
                return;
            }
            final UserHistoryGameDetails result = (UserHistoryGameDetails) response;
            Runnable run = () -> populateTable(result);
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(run);
            }
        }
    }
}