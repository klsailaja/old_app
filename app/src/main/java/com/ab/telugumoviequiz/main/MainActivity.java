package com.ab.telugumoviequiz.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.ab.telugumoviequiz.history.HistoryView;
import com.ab.telugumoviequiz.referals.MyReferralsView;
import com.ab.telugumoviequiz.transactions.TransactionsView;
import com.ab.telugumoviequiz.userprofile.UpdateUserProfile;
import com.ab.telugumoviequiz.withdraw.WithdrawReqsView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.ab.telugumoviequiz.common.Constants.GAME_BEFORE_LOCK_PERIOD_IN_MILLIS;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Navigator,
        View.OnClickListener, CallbackResponse, MessageListener, DialogAction {

    public View activityView = null;
    private final Bundle appParams = new Bundle();
    private boolean stopped = false;
    private ScheduledFuture<?> pollerTask = null;
    private String currentView = null;
    private GameDetails questionViewGameDetails;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        String successMsg = getIntent().getStringExtra("msg");
        Snackbar.make(activityView, successMsg, Snackbar.LENGTH_SHORT).show();

        fetchUpdateMoney();

        WinMsgHandler.getInstance().setListener(this);
        UserProfile userProfile = UserDetails.getInstance().getUserProfile();
        WinMsgHandler.getInstance().setUserProfileId(userProfile.getId());

        launchView(Navigator.CURRENT_GAMES, new Bundle(), false);

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
    }

    @Override
    public void onBackPressed () {
        Utils.showMessage("","Back button disabled. Please use eft top most navigation buttons", this, null);
    }

    public void onClick(View view) {
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        if (currentView != null) {
            if (currentView.equals(Navigator.QUESTION_VIEW)) {
                long currentTime = System.currentTimeMillis();
                //long stat = questionViewGameStartTime - GAME_BEFORE_LOCK_PERIOD_IN_MILLIS;
                //long end = questionViewGameStartTime + 10 * 60 * 1000;
                //if ((currentTime >= stat) && (currentTime <= end)) {
                    /*Utils.showConfirmationMessage("", "Game in progress. Are you sure to leave?",
                            this, null, -1, null);*/
                //}
            }
        }
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
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        currentView = viewName;
        if (currentView.equals(Navigator.QUESTION_VIEW)) {
            questionViewGameDetails = (GameDetails) params.getSerializable("gd");
        }
        fragment.setArguments(params);
        final FragmentTransaction ft = mgr.beginTransaction();
        ft.replace(R.id.content, fragment, viewName);
        ft.commit();
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
                fragment = new SelectGameTypeView(SelectGameTypeView.FUTURE_GAMES);
                break;
            }
            case Navigator.ENROLLED_GAMES: {
                stopped = false;
                fragment = new SelectGameTypeView(SelectGameTypeView.ENROLLED_GAMES);
                break;
            }
            case Navigator.REFERRALS_VIEW: {
                stopped = true;
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
                stopped = true;
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
        }
        if (stopped) {
            WinMsgHandler.getInstance().setListener(null);
        } else {
            WinMsgHandler.getInstance().setListener(fragment);
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
            Runnable run = () -> {
                ActionBar mActionBar = getSupportActionBar();
                View view = mActionBar.getCustomView();
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
    public void doAction(int calledId, Object userObject) {
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}