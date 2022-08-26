package com.ab.telugumoviequiz.games;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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
import com.ab.telugumoviequiz.common.ShowHelpFirstTimer;
import com.ab.telugumoviequiz.common.ShowHomeScreen;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.constants.UserMoneyAccountType;
import com.ab.telugumoviequiz.help.HelpPreferences;
import com.ab.telugumoviequiz.help.HelpTopic;
import com.ab.telugumoviequiz.help.ViewHelp;
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.Navigator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShowGames extends BaseFragment implements CallbackResponse, View.OnClickListener,
        MessageListener {
    private int fragmentIndex = -1;

    private final List<GameDetails> gameDetailsList = new ArrayList<>();
    private final List<GameDetails> adapterList = new ArrayList<>();
    private GameAdapter mAdapter;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private String searchKey = null, searchValue = null, showFreeGame = null;
    private final List<Long> gameStartTimeLongValues = new ArrayList<>();
    private final List<String> gameStartTimeStrValues = new ArrayList<>();
    private RecyclerView recyclerView;
    private AlertDialog alertDialog;
    private boolean isErrorDialogShowing = false;

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
        LocalGamesManager.getInstance().setCallbackResponse(this);
        setBaseParams(false);
        TextView userCountsLabel = root.findViewById(R.id.loggedUserCount);
        userCountsLabel.setVisibility(View.GONE);
        return root;
    }

    @Override
    public void onClick(View view) {
        if (view == null) {
            // This is to open the search dialog from help window
            showSearchView();
            return;
        }
        int viewId = view.getId();
        if (R.id.card_entry_join == viewId) {
            String tagName = (String) view.getTag();
            int pos = Integer.parseInt(tagName);
            GameDetails quesGameDetails = adapterList.get(pos);

            PostTask<GameOperation, Boolean> joinTask = Request.gameJoinTask(quesGameDetails.getGameId());
            joinTask.setCallbackResponse(this);

            GameOperation gm = new GameOperation();
            gm.setUserProfileId(UserDetails.getInstance().getUserProfile().getId());
            gm.setUserAccountType(UserMoneyAccountType.LOADED_MONEY.getId());
            gm.setUserName(UserDetails.getInstance().getUserProfile().getName());
            gm.setUserBossId(UserDetails.getInstance().getUserProfile().getBossId());

            joinTask.setPostObject(gm);
            joinTask.setHelperObject(quesGameDetails);
            Scheduler.getInstance().submit(joinTask);
       } else if (R.id.celebritySchedule == viewId) {
            GetTask<CelebrityFullDetails> celebrityFullDetailsGetTask = Request.getCelebrityScheduleTask();
            celebrityFullDetailsGetTask.setCallbackResponse(this);
            celebrityFullDetailsGetTask.setActivity(getActivity(), "Processing.Please Wait!");
            Scheduler.getInstance().submit(celebrityFullDetailsGetTask);
        } else if (R.id.search == viewId) {
            showSearchView();
        } else if (R.id.help == viewId) {
            showHelpWindow(false);
        }
    }

    private void showSearchView() {
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalGamesManager.getInstance().setShowing(1, false);
        LocalGamesManager.getInstance().setShowing(2, false);
        LocalGamesManager.getInstance().setShowing(3, false);
        LocalGamesManager.getInstance().setShowing(4, false);
    }

    public void setBaseParams(boolean runNow) {
        boolean isLoading;
        switch (fragmentIndex) {
            case 1: {
                if (runNow) {
                    isLoading = true;
                    LocalGamesManager.getInstance().refreshNow(1);
                } else {
                    LocalGamesManager.getInstance().setShowing(2, false);
                    LocalGamesManager.getInstance().setShowing(3, false);
                    LocalGamesManager.getInstance().setShowing(4, false);
                    isLoading = LocalGamesManager.getInstance().setShowing(1, true);
                }
                break;
            }
            case 2: {
                if (runNow) {
                    isLoading = true;
                    LocalGamesManager.getInstance().refreshNow(2);
                } else {
                    LocalGamesManager.getInstance().setShowing(1, false);
                    LocalGamesManager.getInstance().setShowing(3, false);
                    LocalGamesManager.getInstance().setShowing(4, false);
                    isLoading = LocalGamesManager.getInstance().setShowing(2, true);
                }
                break;
            }
            case 3: {
                if (runNow) {
                    isLoading = true;
                    LocalGamesManager.getInstance().refreshNow(3);
                } else {
                    LocalGamesManager.getInstance().setShowing(1, false);
                    LocalGamesManager.getInstance().setShowing(2, false);
                    LocalGamesManager.getInstance().setShowing(4, false);
                    isLoading = LocalGamesManager.getInstance().setShowing(3, true);
                }
                break;
            }
            case 4: {
                if (runNow) {
                    isLoading = true;
                    LocalGamesManager.getInstance().refreshNow(4);
                } else {
                    LocalGamesManager.getInstance().setShowing(1, false);
                    LocalGamesManager.getInstance().setShowing(2, false);
                    LocalGamesManager.getInstance().setShowing(3, false);
                    isLoading = LocalGamesManager.getInstance().setShowing(4, true);
                }
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + fragmentIndex);
        }
        if (isLoading) {
            String waitingMessage = "Processing. Please wait!!!";
            alertDialog = Utils.getProgressDialog(getActivity(), waitingMessage);
            alertDialog.show();
        }
   }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, final Object response, Object helperObject) {
        if (getActivity() != null) {
            Runnable run = () -> {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
            };
            getActivity().runOnUiThread(run);
        }
        boolean isHandled = handleServerError(exceptionThrown, isAPIException, response);
        if (isHandled) {
            if (isErrorDialogShowing) {
                return;
            }
            isErrorDialogShowing = true;
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
                isHandled = handleAPIError(isAPIException, response, 1, null, null);
                if (isHandled) {
                    return;
                }
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
                if (reqId == Request.GET_FUTURE_GAMES) {
                    if (ShowHelpFirstTimer.getInstance().isFirstTime(HelpPreferences.GAME_TIPS)) {
                        showHelpWindow(true);
                    }
                }
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
                break;
            }
            case Request.CELEBRITY_SCHEDULE_DETAIS: {
                isHandled = handleAPIError(isAPIException, response, 1, null, null);
                if (isHandled) {
                    return;
                }
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

    private void showHelpWindow(boolean requireCallBack) {
        /*int isSet = HelpPreferences.getInstance().readPreference(requireContext(), HelpPreferences.GAME_TIPS);
        if (isSet == 1) {
            return;
        }*/
        List<String> helpKeys = new ArrayList<>();
        helpKeys.add("game_tips");
        List<HelpTopic> loginHelpLocalTopics = Utils.getHelpTopics(helpKeys, 1);
        List<HelpTopic> loginHelpEnglishTopics = Utils.getHelpTopics(helpKeys, 2);

        Runnable run = () -> {
            ViewHelp viewHelp = new ViewHelp(loginHelpLocalTopics,
                    loginHelpEnglishTopics, HelpPreferences.GAME_TIPS);
            viewHelp.setLocalMainHeading("Game Time Useful Tips");
            viewHelp.setEnglishMainHeading("Game Time Useful Tips");
            if (requireCallBack) {
                viewHelp.setOnClickListener(this);
            }
            Utils.clearState();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            viewHelp.show(fragmentManager, "dialog");
        };
        requireActivity().runOnUiThread(run);
    }

    private void enableCelebrityButton(boolean enable) {
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
            if (mActionBar != null) {
                View view = mActionBar.getCustomView();
                if (view != null) {
                    ImageView searchButton = view.findViewById(R.id.search);
                    ImageView helpButton = view.findViewById(R.id.help);
                    if (enable) {
                        searchButton.setVisibility(View.VISIBLE);
                        searchButton.setOnClickListener(this);

                        helpButton.setVisibility(View.VISIBLE);
                    } else {
                        searchButton.setVisibility(View.INVISIBLE);
                        searchButton.setOnClickListener(null);

                        helpButton.setVisibility(View.INVISIBLE);
                    }
                    helpButton.setOnClickListener(this);
                    if ((fragmentIndex == 2) || (fragmentIndex == 4)) {
                        ImageView viewCelebritySchedules = view.findViewById(R.id.celebritySchedule);
                        if (enable) {
                            viewCelebritySchedules.setVisibility(View.VISIBLE);
                            viewCelebritySchedules.setOnClickListener(this);
                        } else {
                            viewCelebritySchedules.setVisibility(View.INVISIBLE);
                            viewCelebritySchedules.setOnClickListener(null);
                        }
                    }
                }
            }
        }
    }
}