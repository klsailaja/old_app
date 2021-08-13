package com.ab.telugumoviequiz.games;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.Keys;
import com.ab.telugumoviequiz.common.MessageListener;
import com.ab.telugumoviequiz.common.PostTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.ShowHomeScreen;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.constants.UserMoneyAccountType;
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.Navigator;
import com.ab.telugumoviequiz.main.UserMoney;
import com.ab.telugumoviequiz.main.UserProfile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShowGames extends BaseFragment implements CallbackResponse, View.OnClickListener, MessageListener {
    private int fragmentIndex = -1;

    private final List<GameDetails> gameDetailsList = new ArrayList<>();
    private final List<GameDetails> adapterList = new ArrayList<>();
    private GameAdapter mAdapter;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private ScheduledFuture<?> fetchTask = null;
    private ScheduledFuture<?> pollerTask = null;
    private String searchKey = null, searchValue = null, showFreeGame = null;
    private final List<Long> gameStartTimeLongValues = new ArrayList<>();
    private final List<String> gameStartTimeStrValues = new ArrayList<>();
    private RecyclerView recyclerView;

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
        recyclerView = root.findViewById(R.id.recyclerView);
        mAdapter = new GameAdapter();
        mAdapter.setGameDetailsList(adapterList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(this);
        setBaseParams(false);
        return root;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (R.id.card_entry_join == viewId) {
            String tagName = (String) view.getTag();
            int pos = Integer.parseInt(tagName);
            GameDetails quesGameDetails = adapterList.get(pos);

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
        } else if (R.id.celebritySchedule == viewId) {
            GetTask<CelebrityFullDetails> celebrityFullDetailsGetTask = Request.getCelebrityScheduleTask();
            celebrityFullDetailsGetTask.setCallbackResponse(this);
            celebrityFullDetailsGetTask.setActivity(getActivity(), "Processing.Please Wait!");
            Scheduler.getInstance().submit(celebrityFullDetailsGetTask);
        } else if (R.id.search == viewId) {
            int gameMode = 2;
            if ((fragmentIndex == 1) || (fragmentIndex == 3)) {
                gameMode = 1;
            }
            List<String> searchGameIds = new ArrayList<>();
            List<String> celebrityNames = new ArrayList<>();
            List<String> searchRates = new ArrayList<>();

            gameStartTimeLongValues.clear();
            gameStartTimeStrValues.clear();

            lock.readLock().lock();
            for (GameDetails gameDetails : gameDetailsList) {
                searchGameIds.add(String.valueOf(gameDetails.getTempGameId()));
                if (!searchRates.contains(String.valueOf(gameDetails.getTicketRate()))) {
                    searchRates.add(String.valueOf(gameDetails.getTicketRate()));
                }

                Long searchStartTime = gameDetails.getStartTime();
                if (!gameStartTimeLongValues.contains(searchStartTime)) {
                    gameStartTimeLongValues.add(searchStartTime);
                }
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                String datePattern = "hh:mm";
                Date date = new Date(searchStartTime);
                simpleDateFormat.applyPattern(datePattern);
                String timeStr = simpleDateFormat.format(date);
                if (!gameStartTimeStrValues.contains(timeStr)) {
                    gameStartTimeStrValues.add(timeStr);
                }
                if (gameMode == 2) {
                    if (!celebrityNames.contains(gameDetails.getCelebrityName())) {
                        celebrityNames.add(gameDetails.getCelebrityName());
                    }
                }
            }
            lock.readLock().unlock();

            SearchGamesDialog searchGamesDialog = new SearchGamesDialog(gameMode);
            searchGamesDialog.setData(searchGameIds, celebrityNames, gameStartTimeStrValues, searchRates);
            searchGamesDialog.setListener(this);
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            searchGamesDialog.show(fragmentManager, "dialog");
        }
    }

    private void applyFilterCriteria() {
        List<GameDetails> filterSet = new ArrayList<>();
        if (searchKey == null) {
            filterSet = gameDetailsList;
            adapterList.clear();
            adapterList.addAll(filterSet);
            final List<GameDetails> finalFilterSet = filterSet;
            Runnable run = () -> {
                mAdapter.setGameDetailsList(finalFilterSet);
                mAdapter.notifyDataSetChanged();
            };
            Activity parentActivity = getActivity();
            if (parentActivity != null) parentActivity.runOnUiThread(run);
            return;
        }
        int searchType = 4;
        String[] searchConfiguredKeyValues = getResources().getStringArray(R.array.search_options);
        if (searchKey.equals(searchConfiguredKeyValues[0])) {
            searchType = 1;
        }
        if (searchKey.equals("Celebrity Name")) {
            searchType = 2;
        } else if (searchKey.equals("Game Start Time")) {
            searchType = 3;
        }
        lock.readLock().lock();
        for (GameDetails gameDetails : gameDetailsList) {
            if (searchType == 1) {
                if (String.valueOf(gameDetails.getTempGameId()).equals(searchValue)) {
                    filterSet.add(gameDetails);
                }
            } else if (searchType == 2) {
                if (searchValue.equals(gameDetails.getCelebrityName())) {
                    if (showFreeGame != null) {
                        if (showFreeGame.equals("true")) {
                            if (gameDetails.getCurrentCount() == 10) {
                                continue;
                            }
                        }
                    }
                    filterSet.add(gameDetails);
                }
            } else if (searchType == 3) {
                int startTimeIndex = gameStartTimeStrValues.indexOf(searchValue);
                if (startTimeIndex == -1) {
                    return;
                }
                Long longStartTime = gameStartTimeLongValues.get(startTimeIndex);
                if (longStartTime == gameDetails.getStartTime()) {
                    if (showFreeGame != null) {
                        if (showFreeGame.equals("true")) {
                            if (gameDetails.getCurrentCount() == 10) {
                                continue;
                            }
                        }
                    }
                    filterSet.add(gameDetails);
                }
            } else {
                if (searchValue.equals(String.valueOf(gameDetails.getTicketRate()))) {
                    if (showFreeGame != null) {
                        if (showFreeGame.equals("true")) {
                            if (gameDetails.getCurrentCount() == 10) {
                                continue;
                            }
                        }
                    }
                    filterSet.add(gameDetails);
                }
            }
        }
        lock.readLock().unlock();
        if (filterSet.size() == 0) {
            searchKey = null;
            searchValue = null;
            showFreeGame = null;
            filterSet = gameDetailsList;
            displayErrorAsToast("Searched data not found. Showing all");
        }
        adapterList.clear();
        adapterList.addAll(filterSet);
        final List<GameDetails> finalFilterSet = filterSet;
        Runnable run = () -> {
            mAdapter.setGameDetailsList(finalFilterSet);
            mAdapter.notifyDataSetChanged();
            if (finalFilterSet.size() > 0) {
                if (recyclerView.getLayoutManager() != null) {
                    recyclerView.getLayoutManager().scrollToPosition(0);
                }
            }
        };
        Activity parentActivity = getActivity();
        if (parentActivity != null) {
            parentActivity.runOnUiThread(run);
        }
    }

    @Override
    public void passData(int id, List<String> data) {
        if (id == 1) {
            // Search operation...
            searchKey = data.get(0).trim();
            searchValue = data.get(1).trim();
            showFreeGame = data.get(2).trim();
        } else if (id == 2) {
            searchKey = null;
            searchValue = null;
            showFreeGame = null;
        }
        applyFilterCriteria();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableCelebrityButton(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        enableCelebrityButton(false);
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

    public void setBaseParams(boolean runNow) {
        GetTask<GameDetails[]> getGamesTask;
        GetTask<GameStatusHolder> getGamesStatusTask;
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
            default:
                throw new IllegalStateException("Unexpected value: " + fragmentIndex);
        }
        getGamesTask.setCallbackResponse(this);
        getGamesTask.setActivity(getActivity(), "Processing. Please Wait!!");
        getGamesStatusTask.setCallbackResponse(this);

        if (runNow) {
            Scheduler.getInstance().submit(getGamesTask);
        } else {
            fetchTask = Scheduler.getInstance().submitRepeatedTask(getGamesTask, 0, 5, TimeUnit.MINUTES);
            pollerTask = Scheduler.getInstance().submitRepeatedTask(getGamesStatusTask, 10, 10, TimeUnit.SECONDS);
        }
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, final Object response, Object helperObject) {
        boolean isHandled = handleServerError(exceptionThrown, isAPIException, response);
        if (isHandled) {
            if (fetchTask != null) {
                fetchTask.cancel(true);
            }
            if (pollerTask != null) {
                pollerTask.cancel(true);
            }
            return;
        }
        switch (reqId) {
            case Request.JOIN_GAME: {
                isHandled = handleAPIError(isAPIException, response, 1, null, null);
                if (isHandled) {
                    setBaseParams(true);
                    return;
                }

                Bundle params = new Bundle();
                params.putSerializable("gd", (GameDetails)helperObject);
                params.putLong("gstime",  ((GameDetails)helperObject).getStartTime());
                ((Navigator) requireActivity()).launchView(Navigator.QUESTION_VIEW, params, false);
                Activity activity = getActivity();
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).fetchUpdateMoney();
                }
                break;
            }
            case Request.GET_ENROLLED_GAMES:
            case Request.GET_FUTURE_GAMES: {
                List<GameDetails> result = Arrays.asList((GameDetails[]) response);
                if (result.size() == 0) {
                    displayInfo("Not enrolled for any games", new ShowHomeScreen(getActivity()));
                    return;
                }

                lock.writeLock().lock();
                gameDetailsList.clear();
                gameDetailsList.addAll(result);
                lock.writeLock().unlock();
                applyFilterCriteria();
                break;
            }
            case Request.GET_FUTURE_GAMES_STATUS:
            case Request.GET_ENROLLED_GAMES_STATUS: {
                GameStatusHolder result = (GameStatusHolder) response;
                HashMap<Long, GameStatus> statusHashMap = result.getVal();
                lock.writeLock().lock();
                for (GameDetails gameDetails : gameDetailsList) {
                    Long gameId = gameDetails.getGameId();
                    GameStatus gameStatus = statusHashMap.get(gameId);
                    if (gameStatus == null) {
                        continue;
                    }
                    gameDetails.setCurrentCount(gameStatus.getCurrentCount());
                }
                lock.writeLock().unlock();
                applyFilterCriteria();
                break;
            }
            case Request.GAME_ENROLLED_STATUS: {
                isHandled = handleAPIError(isAPIException, response, 1, null, null);
                if (isHandled) {
                    return;
                }
                String isEnrolledStr = ((String) response);
                boolean isEnrolled = Boolean.parseBoolean(isEnrolledStr);
                if (isEnrolled) {
                    Bundle params = new Bundle();
                    params.putSerializable("gd", (GameDetails)helperObject);
                    ((Navigator) requireActivity()).launchView(Navigator.QUESTION_VIEW, params, false);
                    return;
                }
                GameDetails quesGameDetails = (GameDetails) helperObject;
                int tktRate = quesGameDetails.getTicketRate();

                UserMoney userMoney = UserDetails.getInstance().getUserMoney();

                final List<PayGameModel> modelList = new ArrayList<>();
                PayGameModel referralMoney = new PayGameModel();
                referralMoney.setAccountName("Referral Money");
                referralMoney.setAccountBalance(String.valueOf(userMoney.getReferalAmount()));
                assert UserMoneyAccountType.findById(3) != null;
                referralMoney.setAccountNumber(UserMoneyAccountType.findById(3).getId());
                referralMoney.setValid(userMoney.getReferalAmount() >= tktRate);
                modelList.add(referralMoney);

                PayGameModel winningMoney = new PayGameModel();
                winningMoney.setAccountName("Winning Money");
                winningMoney.setAccountBalance(String.valueOf(userMoney.getWinningAmount()));
                assert UserMoneyAccountType.findById(2) != null;
                winningMoney.setAccountNumber(UserMoneyAccountType.findById(2).getId());
                winningMoney.setValid(userMoney.getWinningAmount() >= tktRate);
                modelList.add(winningMoney);

                PayGameModel mainMoney = new PayGameModel();
                mainMoney.setAccountName("Main Money");
                mainMoney.setAccountBalance(String.valueOf(userMoney.getLoadedAmount()));
                assert UserMoneyAccountType.findById(1) != null;
                mainMoney.setAccountNumber(UserMoneyAccountType.findById(1).getId());
                mainMoney.setValid(userMoney.getLoadedAmount() >= tktRate);
                modelList.add(mainMoney);

                Runnable run = () -> {
                    PayGameDialog payOptions = new PayGameDialog(modelList, this, quesGameDetails);
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    payOptions.show(fragmentManager, "dialog");
                };
                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(run);
                }
                break;
            }
            case Request.CELEBRITY_SCHEDULE_DETAIS: {
                CelebrityFullDetails celebrityFullDetails = (CelebrityFullDetails) response;
                Runnable run = () -> {
                    ViewCelebritySchedule viewCelebritySchedule = new ViewCelebritySchedule(getContext(), celebrityFullDetails);
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    viewCelebritySchedule.show(fragmentManager, "dialog");
                };
                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(run);
                }
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + reqId);
        }
    }
    private void enableCelebrityButton(boolean enable) {
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
            if (mActionBar != null) {
                View view = mActionBar.getCustomView();
                if (view != null) {
                    ImageView searchButton = view.findViewById(R.id.search);
                    if (enable) {
                        searchButton.setVisibility(View.VISIBLE);
                        searchButton.setOnClickListener(this);
                    } else {
                        searchButton.setVisibility(View.GONE);
                        searchButton.setOnClickListener(null);
                    }
                    if ((fragmentIndex == 2) || (fragmentIndex == 4)) {
                        ImageView viewCelebritySchedules = view.findViewById(R.id.celebritySchedule);
                        if (enable) {
                            viewCelebritySchedules.setVisibility(View.VISIBLE);
                            viewCelebritySchedules.setOnClickListener(this);
                        } else {
                            viewCelebritySchedules.setVisibility(View.GONE);
                            viewCelebritySchedules.setOnClickListener(null);
                        }
                    }
                }
            }
        }
    }
}