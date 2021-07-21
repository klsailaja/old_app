package com.ab.telugumoviequiz.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.chat.ChatView;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.DialogAction;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.Keys;
import com.ab.telugumoviequiz.common.MessageListener;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.ShowHomeScreen;
import com.ab.telugumoviequiz.common.SwitchScreen;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.common.WinMsgHandler;
import com.ab.telugumoviequiz.games.GameDetails;
import com.ab.telugumoviequiz.games.GameStatus;
import com.ab.telugumoviequiz.games.GameStatusHolder;
import com.ab.telugumoviequiz.games.QuestionFragment;
import com.ab.telugumoviequiz.games.SelectGameTypeView;
import com.ab.telugumoviequiz.games.ShowGames;
import com.ab.telugumoviequiz.games.UserAnswer;
import com.ab.telugumoviequiz.history.HistoryView;
import com.ab.telugumoviequiz.money.WalletView;
import com.ab.telugumoviequiz.referals.MyReferralsView;
import com.ab.telugumoviequiz.transactions.TransactionsView;
import com.ab.telugumoviequiz.userprofile.UpdateUserProfile;
import com.ab.telugumoviequiz.withdraw.NewWithdrawReq;
import com.ab.telugumoviequiz.withdraw.WithdrawReqsView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity
        implements Navigator, CallbackResponse, MessageListener {

    public View activityView = null;
    private final Bundle appParams = new Bundle();
    private boolean stopped = false;
    private ScheduledFuture<?> pollerTask = null;
    private NavigationView navigationView;
    private MessageListener userMoneyFetchedListener;

    public void fetchUpdateMoney() {
        UserProfile userProfile = UserDetails.getInstance().getUserProfile();
        GetTask<UserMoney> fetchMoney = Request.getMoneyTask(userProfile.getId());
        fetchMoney.setCallbackResponse(this);
        Scheduler.getInstance().submit(fetchMoney);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pollerTask != null) {
            pollerTask.cancel(true);
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        Bundle gameState = getParams(Navigator.QUESTION_VIEW);
        if (gameState != null) {
            String FIFTYUSED = "FIFTYUSED";
            String FLIPUSED = "FLIPUSED";
            String USERANSWERS = "USERANS";
            String GAMEDETAILS = "GAMEDETAILS";

            boolean fiftyUsed = gameState.getBoolean(FIFTYUSED);
            boolean flipQuestionUsed = gameState.getBoolean(FLIPUSED);
            ArrayList<UserAnswer> userAnswers  = gameState.getParcelableArrayList(USERANSWERS);
            GameDetails gameDetails = (GameDetails) gameState.getSerializable(GAMEDETAILS);
            long gameStartTime = gameDetails.getStartTime();

            boolean gameInProgress = false;
            long currentTime = System.currentTimeMillis();
            long gameEndTime = gameStartTime + 10 * 60 * 1000;
            if ((currentTime >= gameStartTime) && (currentTime <= gameEndTime)) {
                gameInProgress = true;
            }
            if (!gameInProgress) {
                return;
            }
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(Keys.QUESTION_VIEW_FIFTY_FIFTY, fiftyUsed);
            editor.putBoolean(Keys.QUESTION_VIEW_FLIP_QUESTION, flipQuestionUsed);
            int userAnsSize = userAnswers.size();
            editor.putInt(Keys.QUESTION_VIEW_USER_ANS_LENGTH, userAnsSize);
            editor.putLong(Keys.QUESTION_VIEW_GAME_START_TIME, gameStartTime);
            for (int index = 0; index < userAnsSize; index ++) {
                UserAnswer userAnswer = userAnswers.get(index);

                String qNoKey = Keys.QUESTION_VIEW_USER_ANS_QNO_PREFIX + index + 1;
                editor.putInt(qNoKey, userAnswer.getqNo());

                String isCorrectKey = Keys.QUESTION_VIEW_USER_ANS_IS_CORRECT_PREFIX + index + 1;
                editor.putBoolean(isCorrectKey, userAnswer.isCorrect());

                String quesTimeKey = Keys.QUESTION_VIEW_USER_ANS_TIME_PREFIX + index + 1;
                editor.putLong(quesTimeKey, userAnswer.getTimeTaken());
            }
            editor.apply();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.clearState();
        setContentView(R.layout.activity_main);
        activityView = this.findViewById(android.R.id.content);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            LayoutInflater mInflater = LayoutInflater.from(this);
            @SuppressLint("InflateParams") View mCustomView = mInflater.inflate(R.layout.main_action_bar, null);
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayShowCustomEnabled(true);
            mActionBar.setCustomView(mCustomView);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        TextView nav_userName = (TextView)hView.findViewById(R.id.userNameTxt);
        nav_userName.setText(UserDetails.getInstance().getUserProfile().getName());
        TextView nav_mailId = (TextView)hView.findViewById(R.id.userMailId);
        nav_mailId.setText(UserDetails.getInstance().getUserProfile().getEmailAddress());

        String successMsg = getIntent().getStringExtra("msg");
        Snackbar.make(activityView, successMsg, Snackbar.LENGTH_SHORT).show();

        fetchUpdateMoney();

        WinMsgHandler.getInstance().setListener(this);
        UserProfile userProfile = UserDetails.getInstance().getUserProfile();
        WinMsgHandler.getInstance().setUserProfileId(userProfile.getId());

        String FIFTYUSED = "FIFTYUSED";
        String FLIPUSED = "FLIPUSED";
        String USERANSWERS = "USERANS";

        ArrayList<UserAnswer> userAnswers  = new ArrayList<>(10);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean fiftyUsed = sharedPref.getBoolean(Keys.QUESTION_VIEW_FIFTY_FIFTY, false);
        boolean flipQuestionUsed = sharedPref.getBoolean(Keys.QUESTION_VIEW_FLIP_QUESTION, false);
        long gameStartTime = sharedPref.getLong(Keys.QUESTION_VIEW_GAME_START_TIME, -1);
        int userAnsSize = sharedPref.getInt(Keys.QUESTION_VIEW_USER_ANS_LENGTH, 0);

        for (int index = 0; index < userAnsSize; index ++) {
            UserAnswer userAnswer = new UserAnswer();

            String qNoKey = Keys.QUESTION_VIEW_USER_ANS_QNO_PREFIX + index + 1;
            int qNo = sharedPref.getInt(qNoKey, 0);
            userAnswer.setqNo(qNo);

            String isCorrectKey = Keys.QUESTION_VIEW_USER_ANS_IS_CORRECT_PREFIX + index + 1;
            boolean isCorrect = sharedPref.getBoolean(isCorrectKey, false);
            userAnswer.setCorrect(isCorrect);

            String quesTimeKey = Keys.QUESTION_VIEW_USER_ANS_TIME_PREFIX + index + 1;
            long timeTaken = sharedPref.getLong(quesTimeKey, 0);
            userAnswer.setTimeTaken(timeTaken);

            userAnswers.add(userAnswer);
        }

        boolean gameInProgress = false;
        long currentTime = System.currentTimeMillis();
        long gameEndTime = gameStartTime + 10 * 60 * 1000;
        if ((currentTime >= gameStartTime) && (currentTime <= gameEndTime)) {
            gameInProgress = true;
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(Keys.QUESTION_VIEW_FIFTY_FIFTY);
        editor.remove(Keys.QUESTION_VIEW_FLIP_QUESTION);
        editor.remove(Keys.QUESTION_VIEW_GAME_START_TIME);
        editor.remove(Keys.QUESTION_VIEW_USER_ANS_LENGTH);
        for (int index = 0; index < userAnsSize; index ++) {
            String qNoKey = Keys.QUESTION_VIEW_USER_ANS_QNO_PREFIX + index + 1;
            editor.remove(qNoKey);
            String isCorrectKey = Keys.QUESTION_VIEW_USER_ANS_IS_CORRECT_PREFIX + index + 1;
            editor.remove(isCorrectKey);
            String quesTimeKey = Keys.QUESTION_VIEW_USER_ANS_TIME_PREFIX + index + 1;
            editor.remove(quesTimeKey);
        }
        editor.apply();

        if (gameInProgress) {
            Bundle gameState = new Bundle();
            gameState.putBoolean(FIFTYUSED, fiftyUsed);
            gameState.putBoolean(FLIPUSED, flipQuestionUsed);
            gameState.putParcelableArrayList(USERANSWERS, userAnswers);
            storeParams(Navigator.QUESTION_VIEW, gameState);
        }

        long startTime = System.currentTimeMillis();
        startTime = startTime + 5 * 60 * 1000;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);

        int minute = calendar.get(Calendar.MINUTE);
        minute = minute / 5;
        minute = minute * 5;
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        startTime = calendar.getTimeInMillis();
        long initialDelay = startTime - System.currentTimeMillis() - 10 * 1000;
        GetTask<GameStatusHolder> getGamesStatusTask = Request.getFutureGamesStatusTask(-1);
        getGamesStatusTask.setCallbackResponse(this);
        pollerTask = Scheduler.getInstance().submitRepeatedTask(getGamesStatusTask, initialDelay, 5 * 60 * 1000, TimeUnit.MILLISECONDS);

        launchView(Navigator.CURRENT_GAMES, new Bundle(), false);
    }

    @Override
    public void onBackPressed () {
        Utils.showMessage("","Back button disabled. Please use left top most navigation buttons",
                this, null);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Bundle params = new Bundle();
        if (id == R.id.nav_current_games) {
            launchView(Navigator.CURRENT_GAMES, params, false);
        } else if (id == R.id.nav_enrolled_games) {
            launchView(Navigator.ENROLLED_GAMES, params, false);
        } else if (id == R.id.nav_history_games) {
            launchView(Navigator.HISTORY_VIEW, params, false);
        }
        else if (id == R.id.nav_transactions) {
            launchView(Navigator.TRANSACTIONS_VIEW, params, false);
        } else if (id == R.id.nav_referals) {
            launchView(Navigator.REFERRALS_VIEW, params, false);
        }  else if (id == R.id.nav_withdraw_view) {
            launchView(Navigator.WITHDRAW_REQ_VIEW, params, false);
        } else if (id == R.id.nav_chat) {
            launchView(Navigator.CHAT_VIEW, params, false);
        } else if (id == R.id.nav_user_profile) {
            launchView(Navigator.PROFILE_VIEW, params, false);
        } else if (id == R.id.nav_wallet) {
            launchView(Navigator.WALLET_VIEW, params, false);
        } else if (id == R.id.logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    public void storeParams(String viewName, Bundle params) {
        appParams.putBundle(viewName, params);
    }

    public Bundle getParams(String viewName) {
        return appParams.getBundle(viewName);
    }

    @Override
    public void launchView(String viewName, Bundle params, boolean storeState) {
        FragmentManager mgr = getSupportFragmentManager();
        if (storeState) {
            storeParams(viewName, params);
        }
        Fragment fragment = mgr.findFragmentByTag(viewName);
        if (fragment == null) {
            fragment = getFragment(viewName);
        }
        if (fragment == null) {
            return;
        }
        fragment.setArguments(params);
        final FragmentTransaction ft = mgr.beginTransaction();
        ft.replace(R.id.content, fragment, viewName);
        ft.commit();
        if (fragment instanceof BaseFragment) {
            navigationView.setNavigationItemSelectedListener((BaseFragment) fragment);
        }
    }

    private Fragment getFragment(String viewId) {
        BaseFragment fragment = null;
        switch (viewId) {
            case Navigator.MIXED_GAMES_VIEW:
            case Navigator.MIXED_ENROLLED_GAMES_VIEW:
            case Navigator.CELEBRITY_GAMES_VIEW:
            case Navigator.CELEBRITY_ENROLLED_GAMES_VIEW: {
                stopped = false;
                fragment = new ShowGames();
                break;
            }
            case Navigator.QUESTION_VIEW: {
                stopped = true;
                fragment = new QuestionFragment();
                break;
            }
            case Navigator.CURRENT_GAMES: {
                stopped = false;
                fragment = new SelectGameTypeView();
                ((SelectGameTypeView)fragment).setViewType(SelectGameTypeView.FUTURE_GAMES);
                break;
            }
            case Navigator.ENROLLED_GAMES: {
                stopped = false;
                fragment = new SelectGameTypeView();
                ((SelectGameTypeView)fragment).setViewType(SelectGameTypeView.ENROLLED_GAMES);
                break;
            }
            case Navigator.REFERRALS_VIEW: {
                stopped = false;
                fragment = new MyReferralsView();
                break;
            }
            case Navigator.TRANSACTIONS_VIEW: {
                stopped = true;
                fragment = new TransactionsView();
                break;
            }
            case Navigator.HISTORY_VIEW: {
                stopped = false;
                fragment = new HistoryView();
                break;
            }
            case Navigator.WITHDRAW_REQ_VIEW: {
                stopped = false;
                fragment = new WithdrawReqsView();
                break;
            } case Navigator.CHAT_VIEW: {
                stopped = true;
                fragment = new ChatView();
                break;
            }
            case Navigator.PROFILE_VIEW: {
                stopped = true;
                fragment = new UpdateUserProfile();
                break;
            }
            case Navigator.WALLET_VIEW: {
                stopped = true;
                fragment = new WalletView();
                break;
            }
            case Navigator.NEW_WITHDRAW_REQUEST: {
                stopped = true;
                fragment = new NewWithdrawReq();
            }
        }
        if (stopped) {
            WinMsgHandler.getInstance().setListener(null);
        } else {
            WinMsgHandler.getInstance().setListener(this);
        }
        return fragment;
    }

    @Override
    public void passData(int reqId, List<String> data) {
        String msg = data.get(0);
        Runnable run = () -> {
            TextView winMsgBar = findViewById(R.id.winMsgs);
            if (winMsgBar == null) {
                return;
            }
            winMsgBar.setText(msg);
        };
        this.runOnUiThread(run);
    }


    public boolean handleServerError(boolean exceptionThrown, boolean isAPIException, final Object response) {
        if ((exceptionThrown) && (!isAPIException)) {
            displayError((String)response, new SwitchScreen(this));
            return true;
        }
        return false;
    }

    public boolean handleAPIError(boolean isAPIException, final Object response, int errorType,
                                  View view, DialogAction dialogAction) {
        if (isAPIException) {
            String errorMsg = (String) response;
            if (errorType == 1) {
                displayError(errorMsg, dialogAction);
            } else if (errorType == 2) {
                displayErrorAsToast(errorMsg);
            } else {
                displayErrorAsSnackBar(errorMsg, view);
            }
            return true;
        }
        return false;
    }

    public void displayInfo(String infoMsg, DialogAction dialogAction) {
        displayMsg("Information", infoMsg, dialogAction);
    }

    public void displayError(String errMsg, DialogAction dialogAction) {
        displayMsg("Error", errMsg, dialogAction);
    }

    public void displayMsg(final String title, final String msg, DialogAction dialogAction) {
        Runnable run = () -> Utils.showMessage(title, msg, MainActivity.this, dialogAction);
        this.runOnUiThread(run);
    }

    public void displayErrorAsToast(final String errMsg) {
        Runnable run = () -> Toast.makeText(MainActivity.this, errMsg, Toast.LENGTH_LONG).show();
        this.runOnUiThread(run);
    }
    public void displayErrorAsSnackBar(final String errMsg, View view) {
        Runnable run = () -> Snackbar.make(view, errMsg, Snackbar.LENGTH_LONG).show();
        this.runOnUiThread(run);
    }

    public void setUserMoneyFetchedListener(MessageListener userMoneyFetchedListener) {
        this.userMoneyFetchedListener = userMoneyFetchedListener;
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, Object response, Object userObject) {
        boolean isHandled = handleServerError(exceptionThrown, isAPIException, response);
        if (isHandled) {
            return;
        }
        isHandled = handleAPIError(isAPIException, response, 2, null, null);
        if (isHandled) {
            return;
        }

        if (reqId == Request.GET_USER_MONEY) {
            UserMoney userMoney = (UserMoney) response;
            UserDetails.getInstance().setUserMoney(userMoney);
            if (userMoneyFetchedListener != null) {
                this.userMoneyFetchedListener.passData(1000, null);
            }
            Runnable run = () -> {
                ActionBar mActionBar = getSupportActionBar();
                View view = null;
                if (mActionBar != null) {
                    view = mActionBar.getCustomView();
                }
                if (view == null) {
                    return;
                }
                TextView referMoney = view.findViewById(R.id.main_refer_money);
                TextView winMoney = view.findViewById(R.id.main_win_money);
                TextView mainMoney = view.findViewById(R.id.main_main_money);

                referMoney.setText(String.valueOf(userMoney.getReferalAmount()));
                winMoney.setText(String.valueOf(userMoney.getWinningAmount()));
                mainMoney.setText(String.valueOf(userMoney.getLoadedAmount()));
            };
            this.runOnUiThread(run);
        } else if (reqId == Request.GET_FUTURE_GAMES_STATUS) {
            String gameCancelMsg = null;
            GameStatusHolder result = (GameStatusHolder) response;
            HashMap<Long, GameStatus> statusHashMap = result.getVal();

            UserProfile userProfile = UserDetails.getInstance().getUserProfile();
            long userProfileId = -1;
            if (userProfile != null) {
                userProfileId = userProfile.getId();
            }
            for (Map.Entry<Long,GameStatus> entry : statusHashMap.entrySet()) {
                Long gameId = entry.getKey();

                GameStatus gameStatus = statusHashMap.get(gameId);
                if (gameStatus == null) {
                    continue;
                }
                int status = gameStatus.getGameStatus();
                if (status == -1) {
                    Map<Long, Boolean> userAccountRevertStatus = gameStatus.getUserAccountRevertStatus();
                    Boolean revertStatus = userAccountRevertStatus.get(userProfileId);
                    if (revertStatus == null) {
                        continue;
                    }
                    int userViewingGameId = gameStatus.getViewId();
                    if (revertStatus) {
                        gameCancelMsg = "GameId#:" + userViewingGameId + " Cancelled as minimum users not present. Ticket Money credited successfully";
                    }
                }
            }
            fetchUpdateMoney();
            if (gameCancelMsg == null) {
                return;
            }
            displayInfo(gameCancelMsg, new ShowHomeScreen(this));
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

    /*@Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        System.out.println("Activity onSaveInstanceState ");
        super.onSaveInstanceState(outState);
    }*/
}