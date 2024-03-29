package com.ab.telugumoviequiz.main;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.ab.telugumoviequiz.common.PostTask;
import com.ab.telugumoviequiz.common.PreferenceDialog;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.ShowHomeScreen;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.common.WinMsgHandler;
import com.ab.telugumoviequiz.constants.MoneyCreditStatus;
import com.ab.telugumoviequiz.customercare.CCTableView;
import com.ab.telugumoviequiz.customercare.NewCCReq;
import com.ab.telugumoviequiz.faq.FAQView;
import com.ab.telugumoviequiz.faq.MoreGamesView;
import com.ab.telugumoviequiz.games.CancelGameStatusTask;
import com.ab.telugumoviequiz.games.GameDetails;
import com.ab.telugumoviequiz.games.QuestionFragment;
import com.ab.telugumoviequiz.games.SelectGameTypeView;
import com.ab.telugumoviequiz.games.ShowGames;
import com.ab.telugumoviequiz.games.UserAnswer;
import com.ab.telugumoviequiz.history.HistoryView;
import com.ab.telugumoviequiz.kyc.KYCView;
import com.ab.telugumoviequiz.money.AddMoney;
import com.ab.telugumoviequiz.money.CoinStore;
import com.ab.telugumoviequiz.money.MoneyStatusInput;
import com.ab.telugumoviequiz.money.MoneyStatusOutput;
import com.ab.telugumoviequiz.notification.MyNotificationHandler;
import com.ab.telugumoviequiz.referals.MyReferralsView;
import com.ab.telugumoviequiz.transactions.TransactionsView;
import com.ab.telugumoviequiz.userprofile.UpdateUserProfile;
import com.ab.telugumoviequiz.withdraw.NewWithdrawReq;
import com.ab.telugumoviequiz.withdraw.VerifyWDOTP;
import com.ab.telugumoviequiz.withdraw.WithdrawReqsView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity
        implements Navigator, CallbackResponse, MessageListener,
        DialogAction, View.OnClickListener {

    public View activityView = null;
    private final Bundle appParams = new Bundle();
    private boolean stopped = false;
    private ScheduledFuture<?> allGamesStatusPollerTask = null;
    private ScheduledFuture<?> chatMsgCountPollerTask = null;
    private NavigationView navigationView;
    private MessageListener userMoneyFetchedListener;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "This is in onCreate");
        super.onCreate(savedInstanceState);

        // This is for notification initialization
        MyNotificationHandler.getInstance(getApplicationContext()).
                registerNotificationChannelChannel(getString(R.string.NEWS_CHANNEL_ID),
                        getString(R.string.CHANNEL_NEWS), getString(R.string.CHANNEL_DESCRIPTION));

        PreferenceDialog.readStateUpdateInMem(getApplicationContext());

        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate method savedInstanceState is not null");
        }

        Utils.clearState();

        setContentView(R.layout.activity_main);
        activityView = this.findViewById(android.R.id.content);

        // Toolbar initialization
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

            ImageView moneyIcon = mCustomView.findViewById(R.id.moneyIcon);
            if (!UserDetails.getInstance().isMoneyMode()) {
                moneyIcon.setImageResource(R.drawable.coins);
            }

            List<Integer> allToolBarButIds = new ArrayList<>();
            allToolBarButIds.add(R.id.settings);
            allToolBarButIds.add(R.id.help);
            allToolBarButIds.add(R.id.celebritySchedule);
            allToolBarButIds.add(R.id.chat);
            allToolBarButIds.add(R.id.search);
            List<ImageView> allToolBarButs = Utils.getImageViewButtons(mCustomView, allToolBarButIds);
            Utils.enableToolBarButtons(allToolBarButs, null, false);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        TextView nav_userName = hView.findViewById(R.id.userNameTxt);
        nav_userName.setText(UserDetails.getInstance().getUserProfile().getName());
        TextView nav_mailId = hView.findViewById(R.id.userMailId);
        nav_mailId.setText(UserDetails.getInstance().getUserProfile().getEmailAddress());

        if (UserDetails.getUserDetails().isMoneyMode()) {
            navigationView.inflateMenu(R.menu.activity_main_money_drawer);
        } else {
            navigationView.inflateMenu(R.menu.activity_main_coin_drawer);
        }

        /*String successMsg = getIntent().getStringExtra("msg");
        Snackbar.make(activityView, successMsg, Snackbar.LENGTH_SHORT).show();*/

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

        String initialView = Navigator.CURRENT_GAMES;

        if (gameInProgress) {
            Bundle gameState = new Bundle();
            gameState.putBoolean(FIFTYUSED, fiftyUsed);
            gameState.putBoolean(FLIPUSED, flipQuestionUsed);
            gameState.putParcelableArrayList(USERANSWERS, userAnswers);
            storeParams(Navigator.QUESTION_VIEW, gameState);
            initialView = Navigator.ENROLLED_GAMES;
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
        int waitTime = 1 + (int)(Math.random() * (10 - 1));
        long initialDelay = startTime - System.currentTimeMillis() - waitTime * 1000L;

        CancelGameStatusTask cancelGameStatusTask =
                CancelGameStatusTask.getInstance(startTime, this);
        allGamesStatusPollerTask = Scheduler.getInstance().submitRepeatedTask(cancelGameStatusTask,
                initialDelay, 5 * 60 * 1000, TimeUnit.MILLISECONDS);

        long chatEndTime = System.currentTimeMillis();
        long chatStartTime = chatEndTime - 30 * 1000;
        GetTask<Integer> getChatMsgCountTask = Request.getChatMsgCount(chatStartTime, chatEndTime);
        getChatMsgCountTask.setCallbackResponse(this);
        chatMsgCountPollerTask = Scheduler.getInstance().submitRepeatedTask(getChatMsgCountTask,
                0, 30, TimeUnit.SECONDS);

        updateMoneyInUI(UserDetails.getInstance().getUserMoney(), false);

        Bundle params = new Bundle();
        if (initialView.equals(Navigator.CURRENT_GAMES)) {
            params.putInt(SelectGameTypeView.HOME_SCREEN_GAME_TYPE, SelectGameTypeView.FUTURE_GAMES);
        } else {
            params.putInt(SelectGameTypeView.HOME_SCREEN_GAME_TYPE, SelectGameTypeView.ENROLLED_GAMES);
        }
        launchView(initialView, params, false);

        ServerErrorHandler.getInstance().addShutdownListener(this);

        // Notifications tuned off during the app runs
        registerWithAlarmManager(false);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPollers();
    }

    @Override
    protected void onDestroy () {
        Log.d(TAG, "In onDestroy");
        super.onDestroy();

        ServerErrorHandler.getInstance().removeShutdownListener(this);
        PreferenceDialog.storeState(getApplicationContext());

        if (UserDetails.getInstance().getNotificationValue()) {
            Log.d(TAG, "In register with AM");
            registerWithAlarmManager(true);
        }

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
            if (gameDetails == null) {
                return;
            }
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






    public void startTheWinMoneyStatus(long gameStartTime) {
        displayErrorAsToast("Winners money credited status: In-Progress");
        int waitTime = 1 + (int)(Math.random() * (10 - 1));
        queryMoneyCreditedStatus(gameStartTime, 1, waitTime);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.settings) {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            PreferenceDialog dialog = new PreferenceDialog();
            dialog.show(fragmentManager, "dialog");
        }
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
            params.putInt(SelectGameTypeView.HOME_SCREEN_GAME_TYPE, SelectGameTypeView.FUTURE_GAMES);
            launchView(Navigator.CURRENT_GAMES, params, false);
        } else if (id == R.id.nav_enrolled_games) {
            params.putInt(SelectGameTypeView.HOME_SCREEN_GAME_TYPE, SelectGameTypeView.ENROLLED_GAMES);
            launchView(Navigator.ENROLLED_GAMES, params, false);
        } else if (id == R.id.nav_history_games) {
            launchView(Navigator.HISTORY_VIEW, params, false);
        } else if (id == R.id.nav_transactions) {
            launchView(Navigator.TRANSACTIONS_VIEW, params, false);
        } else if (id == R.id.nav_referals) {
            launchView(Navigator.REFERRALS_VIEW, params, false);
        }  else if (id == R.id.nav_withdraw_view) {
            launchView(Navigator.WD_OTP, params, false);
        } else if (id == R.id.nav_chat) {
            launchView(Navigator.CHAT_VIEW, params, false);
        } else if (id == R.id.nav_user_profile) {
            launchView(Navigator.PROFILE_VIEW, params, false);
        } else if (id == R.id.nav_add_money) {
            String addMoneyView = Navigator.ADD_MONEY_VIEW;
            if (!UserDetails.getUserDetails().isMoneyMode()) {
                addMoneyView = Navigator.COIN_STORE_VIEW;
            }
            launchView(addMoneyView, params, false);
        } else if (id == R.id.nav_customercare) {
            launchView(Navigator.CC_REQ_VIEW, params, false);
        } else if (id == R.id.nav_kyc) {
            launchView(Navigator.KYC_VIEW, params, false);
        } else if (id == R.id.faq) {
            launchView(Navigator.FAQ, params, false);
        } else if (id == R.id.more_games) {
            launchView(Navigator.MORE_GAMES, params, false);
        } else if (id == R.id.logout) {
            ServerErrorHandler.getInstance().shutDownApp(this);
        } else if (id == R.id.share) {
            Resources resources = getResources();
            String shareTxt1 = resources.getString(R.string.share_text1);
            String shareTxt2 = resources.getString(R.string.share_text2);
            String shareBody = shareTxt1 + shareTxt2 +
                    UserDetails.getInstance().getUserProfile().getMyReferalId();
            Utils.showConfirmationMessage("Share with Friends", shareBody, this,
                    this, 2000, shareBody);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }


    private void stopPollers() {
        if (allGamesStatusPollerTask != null) {
            allGamesStatusPollerTask.cancel(true);
        }
        if (chatMsgCountPollerTask != null) {
            chatMsgCountPollerTask.cancel(true);
        }
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

        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            View view = mActionBar.getCustomView();

            List<Integer> mainViewToolbarButIds = new ArrayList<>();
            mainViewToolbarButIds.add(R.id.settings);

            List<ImageView> allToolBarButs = Utils.getImageViewButtons(view, mainViewToolbarButIds);
            boolean enabled = viewName.equals(Navigator.CURRENT_GAMES);
            Utils.enableToolBarButtons(allToolBarButs, this, enabled);
        }
    }

    private void updateMoneyInUI(UserMoney userMoney, boolean isGameOver) {
        UserDetails.getInstance().setUserMoney(userMoney);

        if (userMoneyFetchedListener != null) {
            List<String> values = new ArrayList<>();
            String isGameOverStr = "0";
            if (isGameOver) {
                isGameOverStr = "1";
            }
            values.add(isGameOverStr);
            this.userMoneyFetchedListener.passData(1000, values);
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
            TextView mainMoney = view.findViewById(R.id.main_main_money);

            mainMoney.setText(String.valueOf(userMoney.getAmount()));
            if (isGameOver) {
                displayErrorAsToast("Winning Money is updated for winners");
            }
        };
        this.runOnUiThread(run);
    }
    @Override
    public void passData(int reqId, List<String> data) {
        if (reqId == WinMsgHandler.WIN_MSG_ID) {
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
    }
    public boolean handleServerError(boolean exceptionThrown, boolean isAPIException,
                                     final Object response) {
        if ((exceptionThrown) && (!isAPIException)) {
            //displayError((String)response, new SwitchScreen(this));
            ServerErrorHandler.getInstance().handleServerError("Error", (String)response, this);
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
        Runnable run = () -> Utils.showErrorDialog("Error", errMsg,
                MainActivity.this, dialogAction, 0, null, false);
        this.runOnUiThread(run);
    }

    public void displayErrorWithTicket(String errMsg, DialogAction dialogAction, Object userObj) {
        Runnable run = () -> Utils.showErrorDialog("Error", errMsg,
                MainActivity.this, dialogAction, 0, userObj, true);
        this.runOnUiThread(run);
    }

    public void displayErrorAsToast(final String errMsg) {
        Runnable run = () -> {
            Toast toast = Toast.makeText(MainActivity.this, errMsg, Toast.LENGTH_LONG);
            View view = toast.getView();
            TextView text = view.findViewById(android.R.id.message);
            text.setTextColor(Color.RED);
            toast.show();
        };
        this.runOnUiThread(run);
    }

    public void displayErrorAsSnackBar(final String errMsg, View view) {
        Runnable run = () -> Snackbar.make(view, errMsg, Snackbar.LENGTH_LONG).show();
        this.runOnUiThread(run);
    }

    public void displayMsg(final String title, final String msg, DialogAction dialogAction) {
        Runnable run = () -> Utils.showMessage(title, msg, MainActivity.this, dialogAction);
        this.runOnUiThread(run);
    }


    public void setUserMoneyFetchedListener(MessageListener userMoneyFetchedListener) {
        this.userMoneyFetchedListener = userMoneyFetchedListener;
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException,
                               Object response, Object userObject) {
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
            Boolean isGameOverBoolean = (Boolean) userObject;
            updateMoneyInUI(userMoney, isGameOverBoolean);

        //} else if (reqId == Request.GET_CANCEL_GAMES_STATUS) {
            /*
            int error = -1;
            int userViewingGameId = -1;
            String creditMsg = null;
            GameStatusHolder result = (GameStatusHolder) response;
            HashMap<Long, GameStatus> statusHashMap = result.getVal();
            Log.d(TAG, "This is in cancel game response" + statusHashMap);

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
                    Map<Long, Integer> userAccountRevertStatus = gameStatus.getUserAccountRevertStatus();
                    Log.d(TAG, "This is in cancel game user status" + userAccountRevertStatus);
                    Integer revertStatus = userAccountRevertStatus.get(userProfileId);
                    if (revertStatus == null) {
                        continue;
                    }
                    userViewingGameId = gameStatus.getViewId();
                    if (revertStatus == MoneyCreditStatus.ALL_SUCCESS.getId()) {
                        error = 1;
                    } else if (revertStatus == MoneyCreditStatus.ALL_FAIL.getId()) {
                        error = 0;
                    }
                    creditMsg = Utils.getCancelledGameRevertMsg(revertStatus);
                }
            }
            if (error == -1) {
                return;
            }
            if (error == 1) {
                fetchUpdateMoney();
            }
            String gameCancelMsg = "GameId#:" + userViewingGameId
                    + " Cancelled as minimum users not present. \n Ticket Money credited status: " + creditMsg;
            displayInfo(gameCancelMsg, new ShowHomeScreen(this));*/
        } else if (reqId == Request.ADD_MONEY_REQ) {
            Boolean result = (Boolean) response;
            String msg = "Money added successfully";
            if (!result) {
                msg = "Server problem in updating";
            }
            displayErrorAsToast(msg);
            fetchUpdateMoney();
        } else if (reqId == Request.CHAT_MSG_COUNT_FETCH) {
            final Integer msgCount = (Integer) response;
            int chatImgResourceId = R.drawable.chat1;
            if (msgCount > 0) {
                chatImgResourceId = R.drawable.chat2;
            }
            ActionBar mActionBar = getSupportActionBar();
            if (mActionBar != null) {
                View customView = mActionBar.getCustomView();
                if (customView != null) {
                    final ImageView menuBarChatIV = customView.findViewById(R.id.chat);
                    final int finalImgId = chatImgResourceId;
                    Runnable run = () -> {
                        if (menuBarChatIV != null) {
                            menuBarChatIV.setImageResource(finalImgId);
                        }
                    };
                    this.runOnUiThread(run);
                }
            }
        } else if (Request.MONEY_TASK_STATUS == reqId) {
            MoneyStatusOutput statusRes = (MoneyStatusOutput) response;
            if (statusRes.getStatus() == -1) {
                // Not found in the server
                return;
            }
            String details = (String) userObject;
            int pos = details.indexOf(":");
            String startTime = details.substring(0, pos);
            long slotTime = Long.parseLong(startTime);
            UserDetails.getInstance().setLastPolledSlotGameTime(slotTime);
            UserDetails.getInstance().setLastPlayedGameWinMoneyCreditStatus(statusRes.getStatus());
            UserDetails.getInstance().setLastPlayedGameWinMoneyCreditMsg(statusRes.getMessage());
            if (statusRes.getStatus() == MoneyCreditStatus.IN_PROGRESS.getId()) {
                String retryCount = details.substring(pos + 1);
                int retryCountInt = Integer.parseInt(retryCount);
                if (retryCountInt == 11) {
                    displayInfo("Win Money Not Credited. \n"
                            + "Please create a customer ticket", null);
                    return;
                }
                queryMoneyCreditedStatus(Long.parseLong(startTime), retryCountInt, 30);
            } else {
                displayInfo(statusRes.getMessage(), null);
                fetchUpdateMoney();
            }
        } else if (Request.CANCEL_GAMES_REFUND_TASK_STATUS == reqId) {
            MoneyStatusOutput statusRes = (MoneyStatusOutput) response;
            Log.d("Money", statusRes.getStatus() + ":" +statusRes.getMessage());
            if (statusRes.getStatus() == -1) {
                // Not found in the server
                return;
            }
            String details = (String) userObject;
            int pos = details.indexOf(":");
            String gameTimeStr = details.substring(0, pos);
            long gameTime = Long.parseLong(gameTimeStr);
            if (statusRes.getStatus() == MoneyCreditStatus.IN_PROGRESS.getId()) {
                String retryCount = details.substring(pos + 1);
                int retryCountInt = Integer.parseInt(retryCount);
                if (retryCountInt == 11) {
                    displayInfo("Cancelled Game Ticket Money Not Credited. \n"
                            + "Please create a customer ticket", new ShowHomeScreen(this));
                    return;
                }
                queryCancelledGameMoneyStatus(gameTime, retryCountInt);
            } else {
                displayInfo(statusRes.getMessage(), new ShowHomeScreen(this));
                fetchUpdateMoney();
            }
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "MainActivity onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    public void doAction(int calledId, Object userObject) {
        if (calledId == DialogAction.SHARE_CONFIRM) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareSubject = "Quiz App";
            String shareBody = (String) userObject;
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

            startActivity(Intent.createChooser(shareIntent, "Share Using"));
        } else if (calledId == ServerErrorHandler.APP_SHUTDOWN) {
            stopPollers();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("In onActivityResult" + requestCode);
    }

    public void fetchUpdateMoney() {
        fetchUpdateMoney(false);
    }

    public void fetchUpdateMoney(boolean isGameOver) {
        UserProfile userProfile = UserDetails.getInstance().getUserProfile();
        GetTask<UserMoney> fetchMoney = Request.getMoneyTask(userProfile.getId());
        fetchMoney.setCallbackResponse(this);
        fetchMoney.setHelperObject(isGameOver);
        Scheduler.getInstance().submit(fetchMoney);
    }


    private void queryCancelledGameMoneyStatus(long gameStartTime, int retryCount) {
        MoneyStatusInput statusInput = new MoneyStatusInput();
        statusInput.setGameSlotTime(gameStartTime);
        statusInput.setUid(UserDetails.getInstance().getUserProfile().getId());
        statusInput.setOperType(2);

        PostTask<MoneyStatusInput, MoneyStatusOutput> getStatus = Request.getCancelGamesRefundMoneyStatusTask();
        getStatus.setCallbackResponse(this);
        getStatus.setPostObject(statusInput);
        String details = gameStartTime + ":" + retryCount;
        getStatus.setHelperObject(details);
        Scheduler.getInstance().submit(getStatus,
                30 * 1000, TimeUnit.MILLISECONDS);
    }

    private void queryMoneyCreditedStatus(long gameStartTime, int retryCount, int waitTime) {
        MoneyStatusInput statusInput = new MoneyStatusInput();
        statusInput.setGameSlotTime(gameStartTime);
        statusInput.setUid(UserDetails.getInstance().getUserProfile().getId());
        statusInput.setOperType(1);

        PostTask<MoneyStatusInput, MoneyStatusOutput> getWinStatus = Request.getMoneyStatusTask();
        getWinStatus.setPostObject(statusInput);
        getWinStatus.setCallbackResponse(this);
        String details = gameStartTime + ":" + retryCount;
        getWinStatus.setHelperObject(details);
        Scheduler.getInstance().submit(getWinStatus,
                waitTime * 1000L, TimeUnit.MILLISECONDS);
    }


    private Fragment getFragment(String viewId) {
        System.out.println("viewId: " + viewId);
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
            case Navigator.CURRENT_GAMES:
            case Navigator.ENROLLED_GAMES: {
                stopped = false;
                fragment = new SelectGameTypeView();
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
            case Navigator.WD_OTP: {
                stopped = true;
                fragment = new VerifyWDOTP();
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
            case Navigator.ADD_MONEY_VIEW: {
                stopped = true;
                fragment = new AddMoney();
                break;
            }
            case Navigator.COIN_STORE_VIEW: {
                stopped = true;
                fragment = new CoinStore();
                break;
            }
            case Navigator.NEW_WITHDRAW_REQUEST: {
                stopped = true;
                fragment = new NewWithdrawReq();
                break;
            }
            case Navigator.CC_REQ_VIEW: {
                stopped = true;
                fragment = new CCTableView();
                break;
            }
            case Navigator.NEW_CC_REQUEST: {
                stopped = true;
                fragment = new NewCCReq();
                break;
            }
            case Navigator.KYC_VIEW: {
                stopped = true;
                fragment = new KYCView();
                break;
            }
            case Navigator.FAQ: {
                stopped = true;
                fragment = new FAQView();
                break;
            }
            case Navigator.MORE_GAMES: {
                stopped = false;
                fragment = new MoreGamesView();
                break;
            }
        }
        if (stopped) {
            WinMsgHandler.getInstance().setListener(null);
        } else {
            WinMsgHandler.getInstance().setListener(this);
        }
        return fragment;
    }

    private void registerWithAlarmManager(boolean register) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (register) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + 1000, 5 * 60 * 1000, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
        }
    }




    /*private void startAlarm(boolean enable) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (enable) {
            //alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10 * 1000, pendingIntent);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10 * 1000, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
        }
    }*/
}