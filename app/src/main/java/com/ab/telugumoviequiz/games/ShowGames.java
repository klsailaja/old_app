package com.ab.telugumoviequiz.games;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
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
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.Navigator;
import com.ab.telugumoviequiz.main.UserMoney;
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

            int tktRate = quesGameDetails.getTicketRate();
            if (tktRate == 0) {
                PostTask<GameOperation, Boolean> joinTask = Request.gameJoinTask(quesGameDetails.getGameId());
                joinTask.setCallbackResponse(this);

                GameOperation gm = new GameOperation();
                gm.setUserProfileId(UserDetails.getInstance().getUserProfile().getId());
                gm.setUserAccountType(UserMoneyAccountType.LOADED_MONEY.getId());
                joinTask.setPostObject(gm);
                joinTask.setHelperObject(quesGameDetails);
                Scheduler.getInstance().submit(joinTask);
            } else {
                long userProfileId = UserDetails.getInstance().getUserProfile().getId();
                GetTask<String> enrolledStatus = Request.getEnrolledStatus(quesGameDetails.getGameId(), userProfileId);
                enrolledStatus.setCallbackResponse(this);
                enrolledStatus.setHelperObject(quesGameDetails);
                Scheduler.getInstance().submit(enrolledStatus);
            }
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
        super.onStop();
        if (fetchTask != null) {
            fetchTask.cancel(true);
        }
        if (pollerTask != null) {
            pollerTask.cancel(true);
        }
    }

    public void setBaseParams() {
        switch (fragmentIndex) {
            case 1: {
                getGamesTask = Request.getFutureGames(1);
                getGamesStatusTask = Request.getFutureGamesStatusTask(1);
                break;
            }
            case 2: {
                getGamesTask = Request.getFutureGames(2);
                getGamesStatusTask = Request.getFutureGamesStatusTask(2);
                break;
            }
            case 3: {
                UserProfile userProfile = UserDetails.getInstance().getUserProfile();
                long userProfileId = -1;
                if (userProfile != null) {
                    userProfileId = userProfile.getId();
                }
                getGamesTask = Request.getEnrolledGames(1,userProfileId);
                getGamesStatusTask = Request.getEnrolledGamesStatus(1, userProfileId);
                break;
            }
            case 4: {
                UserProfile userProfile = UserDetails.getInstance().getUserProfile();
                long userProfileId = -1;
                if (userProfile != null) {
                    userProfileId = userProfile.getId();
                }
                getGamesTask = Request.getEnrolledGames(2,userProfileId);
                getGamesStatusTask = Request.getEnrolledGamesStatus(2, userProfileId);
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
        if((exceptionThrown) && (!isAPIException)) {
            if (fetchTask != null) {
                fetchTask.cancel(true);
            }
            if (pollerTask != null) {
                pollerTask.cancel(true);
            }
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
                ((Navigator) Objects.requireNonNull(getActivity())).launchView(Navigator.QUESTION_VIEW, params, false);

                Activity activity = getActivity();
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).fetchUpdateMoney();
                }
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
                Activity parentAct = getActivity();
                if (parentAct != null) {
                    parentAct.runOnUiThread(run);
                }
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
                    int userViewingGameId = gameDetails.getTempGameId();
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
                            gameCancelMsg = "GameId#" + userViewingGameId + " Cancelled. Ticket Money credited successfully";
                        } else {
                            gameCancelMsg = "GameId#" + userViewingGameId + " Cancelled. Ticket Money could not be credited";
                        }
                    }
                    gameDetails.setCurrentCount(gameStatus.getCurrentCount());
                }
                lock.writeLock().unlock();
                if (gameCancelMsg == null) {
                    return;
                }
                ((MainActivity) Objects.requireNonNull(getActivity())).fetchUpdateMoney();
                final String finalGameCancelMsg = gameCancelMsg;
                Runnable run = () -> Utils.showMessage("Info", finalGameCancelMsg, getContext(), null);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(run);
                }
                break;
            }
            case Request.GAME_ENROLLED_STATUS: {
                if (isAPIException) {
                    Runnable run = () -> {
                        String error = (String) response;
                        Utils.showMessage("Error", error, getContext(), null);
                    };
                    Objects.requireNonNull(getActivity()).runOnUiThread(run);
                    return;
                }
                Boolean isEnrolled = ((Boolean) response);
                if (isEnrolled) {
                    return;
                }
                GameDetails quesGameDetails = (GameDetails) helperObject;
                int tktRate = quesGameDetails.getTicketRate();

                UserMoney userMoney = UserDetails.getInstance().getUserMoney();

                final List<PayGameModel> modelList = new ArrayList<>();
                PayGameModel referralMoney = new PayGameModel();
                referralMoney.setAccountName("Referral Money");
                referralMoney.setAccountBalance(String.valueOf(userMoney.getReferalAmount()));
                referralMoney.setAccountNumber(UserMoneyAccountType.findById(3).getId());
                referralMoney.setValid(userMoney.getReferalAmount() >= tktRate);
                modelList.add(referralMoney);

                PayGameModel winningMoney = new PayGameModel();
                winningMoney.setAccountName("Winning Money");
                winningMoney.setAccountBalance(String.valueOf(userMoney.getWinningAmount()));
                winningMoney.setAccountNumber(UserMoneyAccountType.findById(2).getId());
                winningMoney.setValid(userMoney.getWinningAmount() >= tktRate);
                modelList.add(winningMoney);

                PayGameModel mainMoney = new PayGameModel();
                mainMoney.setAccountName("Main Money");
                mainMoney.setAccountBalance(String.valueOf(userMoney.getLoadedAmount()));
                mainMoney.setAccountNumber(UserMoneyAccountType.findById(1).getId());
                mainMoney.setValid(userMoney.getLoadedAmount() >= tktRate);
                modelList.add(mainMoney);

                Runnable run = () -> {
                    PayGameDialog payOptions = new PayGameDialog(modelList, this, quesGameDetails);
                    FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                    payOptions.show(fragmentManager, "dialog");
                };
                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(run);
                }
                break;
            }
        }
    }
}