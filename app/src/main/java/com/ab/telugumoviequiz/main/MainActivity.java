package com.ab.telugumoviequiz.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.games.QuestionFragment;
import com.ab.telugumoviequiz.games.SelectGameTypeView;
import com.ab.telugumoviequiz.games.ShowGames;
import com.ab.telugumoviequiz.history.HistoryView;
import com.ab.telugumoviequiz.referals.MyReferralsView;
import com.ab.telugumoviequiz.transactions.TransactionsView;
import com.ab.telugumoviequiz.withdraw.WithdrawReqsView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Navigator, View.OnClickListener, CallbackResponse {

    public View activityView = null;
    private final Bundle appParams = new Bundle();

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
            System.out.println("No Fragment");
            return;
        }
        fragment.setArguments(params);
        System.out.println("Replace " + viewName);
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
        }
        return fragment;
    }

    public boolean handleServerError(boolean exceptionThrown, boolean isAPIException, final Object response) {
        if ((exceptionThrown) && (!isAPIException)) {
            showErr("Check your Internet Connectivity");
            return true;
        }
        return false;
    }

    public boolean handleAPIError(boolean isAPIException, final Object response) {
        if (isAPIException) {
            showErr("Server Problem. Please retry after some time." + (String)response);
            return true;
        }
        return false;
    }

    public void showErr(final String errMsg) {
        final Activity parentActvity = this;
        Runnable run = () -> Utils.showMessage("Error", errMsg, getApplicationContext(), null);
        if (parentActvity != null) {
            parentActvity.runOnUiThread(run);
        }
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, Object response, Object userObject) {
        boolean isHandled = handleServerError(exceptionThrown, isAPIException, response);
        if (isHandled) {
            return;
        }
        isHandled = handleAPIError(isAPIException, response);
        if (isHandled) {
            return;
        }

        if (reqId == Request.GET_USER_MONEY) {
            UserMoney userMoney = (UserMoney) response;
            UserDetails.getInstance().setUserMoney(userMoney);
            Runnable run = () -> {
                ActionBar mActionBar = getSupportActionBar();
                View view = mActionBar.getCustomView();
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