package com.ab.telugumoviequiz.games;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.Keys;
import com.ab.telugumoviequiz.common.PostTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.constants.UserMoneyAccountType;
import com.ab.telugumoviequiz.main.Navigator;
import com.ab.telugumoviequiz.main.UserProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShowGames extends BaseFragment implements CallbackResponse, View.OnClickListener  {
    private int fragmentIndex = -1;

    private final List<GameDetails> gameDetailsList = new ArrayList<>();
    private GameAdapter mAdapter;
    private GetTask<GameDetails[]> getGamesTask;
    private GetTask<GameStatusHolder> getGamesStatusTask;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private ScheduledFuture<?> fetchTask = null;
    private ScheduledFuture<?> pollerTask = null;

    public ShowGames() {
        super();
    }

    @Override
    public void onCreate(Bundle bundle) {
        System.out.println("In onCreate");
        super.onCreate(bundle);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(Keys.GAMES_VIEW_GAME_TYPE);
        }
        fragmentIndex = index;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        System.out.println("In onCreateView");
        View root = inflater.inflate(R.layout.list_games_view, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        mAdapter = new GameAdapter(gameDetailsList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(this);
        setBaseParams();
        return root;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (R.id.card_entry_join == viewId) {
            String tagName = (String) view.getTag();
            int pos = Integer.parseInt(tagName);
            GameDetails quesGameDetails = gameDetailsList.get(pos);

            PostTask<GameOperation, Boolean> joinTask = Request.gameJoinTask(quesGameDetails.getGameId());
            joinTask.setCallbackResponse(this);
            GameOperation gm = new GameOperation();
            gm.setUserProfileId(UserDetails.getInstance().getUserProfile().getId());
            gm.setUserAccountType(UserMoneyAccountType.findById(1).getId());
            joinTask.setPostObject(gm);
            joinTask.setHelperObject(quesGameDetails);
            Scheduler.getInstance().submit(joinTask);
        }
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
        System.out.println("In onStop");
        super.onStop();
        if (fetchTask != null) {
            fetchTask.cancel(true);
        }
        if (pollerTask != null) {
            pollerTask.cancel(true);
        }
    }

    public void setBaseParams() {
        System.out.println("In setBaseParams");
        switch (fragmentIndex) {
            case 1: {
                getGamesTask = Request.getFutureGames();
                getGamesStatusTask = Request.getFutureGamesStatusTask();
                break;
            }
            case 2: {
                UserProfile userProfile = UserDetails.getInstance().getUserProfile();
                long userProfileId = -1;
                if (userProfile != null) {
                    userProfileId = userProfile.getId();
                }
                getGamesTask = Request.getEnrolledGames(userProfileId);
                getGamesStatusTask = Request.getEnrolledGamesStatus(userProfileId);
                break;
            }
        }
        getGamesTask.setCallbackResponse(this);
        getGamesStatusTask.setCallbackResponse(this);

        fetchTask = Scheduler.getInstance().submitRepeatedTask(getGamesTask, 0, 5, TimeUnit.MINUTES);
        pollerTask = Scheduler.getInstance().submitRepeatedTask(getGamesStatusTask, 10, 10, TimeUnit.SECONDS);
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, final Object response, Object helperObject) {
        System.out.println("In handleResponse" + reqId);
        if((exceptionThrown) && (!isAPIException)) {
            Runnable run = () -> {
                String error = (String) response;
                Utils.showMessage("Error", error, getContext(), null);
            };
            Objects.requireNonNull(getActivity()).runOnUiThread(run);
            return;
        }

        String gameCancelMsg = null;
        switch (reqId) {
            case Request.JOIN_GAME: {
                if (isAPIException) {
                    Runnable run = () -> {
                        String error = (String) response;
                        Utils.showMessage("Error", error, getContext(), null);
                    };
                    Objects.requireNonNull(getActivity()).runOnUiThread(run);
                    return;
                }

                Bundle params = new Bundle();
                params.putSerializable("gd", (GameDetails)helperObject);
                Resources resources = getResources();
                String joinMsg = resources.getString(R.string.game_join_success_msg);
                params.putString("msg", joinMsg);
                ((Navigator)getActivity()).launchView(Navigator.QUESTION_VIEW, params, false);
                break;
            }
            case Request.GET_ENROLLED_GAMES:
            case Request.GET_FUTURE_GAMES: {
                List<GameDetails> result = Arrays.asList((GameDetails[]) response);
                lock.writeLock().lock();
                gameDetailsList.clear();
                gameDetailsList.addAll(result);
                lock.writeLock().unlock();
                Runnable run = () -> mAdapter.notifyDataSetChanged();
                Objects.requireNonNull(getActivity()).runOnUiThread(run);
                break;
            }
            case Request.GET_FUTURE_GAMES_STATUS:
            case Request.GET_ENROLLED_GAMES_STATUS: {
                GameStatusHolder result = (GameStatusHolder) response;
                HashMap<Long, GameStatus> statusHashMap = result.getVal();

                UserProfile userProfile = UserDetails.getInstance().getUserProfile();
                long userProfileId = -1;
                if (userProfile != null) {
                    userProfileId = userProfile.getId();
                }

                lock.writeLock().lock();
                for (GameDetails gameDetails : gameDetailsList) {
                    Long gameId = gameDetails.getGameId();
                    GameStatus gameStatus = statusHashMap.get(gameId);
                    if (gameStatus == null) {
                        continue;
                    }
                    if (gameStatus.getGameStatus() == -1) {
                        Map<Long, Boolean> userAccountRevertStatus = gameStatus.getUserAccountRevertStatus();
                        Boolean revertStatus = userAccountRevertStatus.get(userProfileId);
                        if (revertStatus == null) {
                            continue;
                        }
                        if (revertStatus) {
                            gameCancelMsg = "GameId#" + gameId + " Cancelled. Ticket Money credited successfully";
                        } else {
                            gameCancelMsg = "GameId#" + gameId + " Cancelled. Ticket Money could not be credited";
                        }
                    }
                    gameDetails.setCurrentCount(gameStatus.getCurrentCount());
                }
                lock.writeLock().unlock();
                if (gameCancelMsg == null) {
                    return;
                }
                final String finalGameCancelMsg = gameCancelMsg;
                Runnable run = () -> Utils.showMessage("Info", finalGameCancelMsg, getContext(), null);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(run);
                }
                break;
            }
        }
    }
}