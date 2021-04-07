package com.ab.telugumoviequiz.main;

import android.annotation.SuppressLint;
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
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.DialogAction;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.MessageListener;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.SwitchScreen;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.common.WinMsgHandler;
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

import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Navigator,
        View.OnClickListener, CallbackResponse, MessageListener {

    public View activityView = null;
    private final Bundle appParams = new Bundle();
    private boolean stopped = false;

    public void fetchUpdateMoney() {
        UserProfile userProfile = UserDetails.getInstance().getUserProfile();
        GetTask<UserMoney> fetchMoney = Request.getMoneyTask(userProfile.getId());
        fetchMoney.setCallbackResponse(this);
        Scheduler.getInstance().submit(fetchMoney);
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
    }

    public void onClick(View view) {
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
        fragment.setArguments(params);
        final FragmentTransaction ft = mgr.beginTransaction();
        ft.replace(R.id.content, fragment, viewName);
        ft.commit();
    }

    private Fragment getFragment(String viewId) {
        Fragment fragment = null;
        switch (viewId) {
            case Navigator.MIXED_GAMES_VIEW:
            case Navigator.MIXED_ENROLLED_GAMES_VIEW:
            case Navigator.CELEBRITY_GAMES_VIEW:
            case Navigator.CELEBRITY_ENROLLED_GAMES_VIEW: {
                fragment = new ShowGames();
                break;
            }
            case Navigator.QUESTION_VIEW: {
                stopped = true;
                fragment = new QuestionFragment();
                break;
            }
            case Navigator.CURRENT_GAMES: {
                fragment = new SelectGameTypeView(SelectGameTypeView.FUTURE_GAMES);
                break;
            }
            case Navigator.ENROLLED_GAMES: {
                fragment = new SelectGameTypeView(SelectGameTypeView.ENROLLED_GAMES);
                break;
            }
            case Navigator.REFERRALS_VIEW: {
                fragment = new MyReferralsView();
                break;
            }
            case Navigator.TRANSACTIONS_VIEW: {
                fragment = new TransactionsView();
                break;
            }
            case Navigator.HISTORY_VIEW: {
                fragment = new HistoryView();
                break;
            }
            case Navigator.WITHDRAW_REQ_VIEW: {
                fragment = new WithdrawReqsView();
                break;
            } case Navigator.CHAT_VIEW: {
                fragment = new ChatView();
                break;
            }
            case Navigator.PROFILE_VIEW: {
                fragment = new UpdateUserProfile();
                break;
            }
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
            showErr((String)response);
            return true;
        }
        return false;
    }

    public boolean handleAPIError(boolean isAPIException, final Object response, int errorType, View view) {
        if (isAPIException) {
            String errorMsg = (String) response;
            if (errorType == 1) {
                showErr(errorMsg);
            } else if (errorType == 2) {
                //showErrorAsToast(errorMsg);
            } else {
                //showErrorAsSnackBar(errorMsg, view);
            }
            return true;
        }
        return false;
    }

    public void showErr(final String errMsg) {
        Runnable run = () -> Utils.showMessage("Error", errMsg, MainActivity.this, null);
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

    public boolean handleServerErrorLatest(boolean exceptionThrown, boolean isAPIException, final Object response) {
        if ((exceptionThrown) && (!isAPIException)) {
            displayError((String)response, new SwitchScreen(this));
            return true;
        }
        return false;
    }

    public boolean handleAPIErrorLatest(boolean isAPIException, final Object response, int errorType,
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

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, Object response, Object userObject) {
        boolean isHandled = handleServerError(exceptionThrown, isAPIException, response);
        if (isHandled) {
            return;
        }
        isHandled = handleAPIError(isAPIException, response, 1, null);
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
        }
    }
}