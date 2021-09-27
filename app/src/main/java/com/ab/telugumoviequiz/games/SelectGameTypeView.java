package com.ab.telugumoviequiz.games;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.help.HelpPreferences;
import com.ab.telugumoviequiz.help.HelpTopic;
import com.ab.telugumoviequiz.help.ViewHelp;
import com.ab.telugumoviequiz.main.Navigator;

import java.util.ArrayList;
import java.util.List;

public class SelectGameTypeView extends BaseFragment implements View.OnClickListener, CallbackResponse {
    private final List<GameTypeModel> modelList = new ArrayList<>();
    public static final int FUTURE_GAMES = 1; //
    public static final int ENROLLED_GAMES = 2; //
    private int viewType;

    public SelectGameTypeView() {

    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.list_games_view, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);

        modelList.clear();

        GameTypeModel typeModel1 = new GameTypeModel();
        typeModel1.setGameTypeName("Movie Mix");
        typeModel1.setCelebrityName("Quiz questions from mix of all movies");
        modelList.add(typeModel1);

        GameTypeModel typeModel2 = new GameTypeModel();
        typeModel2.setGameTypeName("Celebrity Special");
        typeModel2.setCelebrityName("Questions related to celebrity acted movies");
        modelList.add(typeModel2);

        SelectGameTypeAdapter mAdapter = new SelectGameTypeAdapter(modelList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        Bundle args = getArguments();
        if (args != null) {
            String msg = args.getString(Keys.LEAVE_ACTION_RESULT);
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }

        TextView userCountsLabel = root.findViewById(R.id.winMsgs);
        userCountsLabel.setVisibility(View.GONE);

        GetTask<Long> loggedInUserCtTask = Request.getLoggedInUserCount();
        loggedInUserCtTask.setCallbackResponse(this);
        Scheduler.getInstance().submit(loggedInUserCtTask);
        showHelpWindow();
        return root;
    }

    private void showHelpWindow() {
        int isSet = HelpPreferences.getInstance().readPreference(requireContext(), HelpPreferences.HOME_SCREEN_GENERAL_GAME_RULES);
        if (isSet == 1) {
            return;
        }
        List<String> helpKeys = new ArrayList<>();
        helpKeys.add("topic_name1");
        helpKeys.add("topic_name2");
        helpKeys.add("topic_name3");
        List<HelpTopic> loginHelpLocalTopics = Utils.getHelpTopics(helpKeys, 1);
        List<HelpTopic> loginHelpEnglishTopics = Utils.getHelpTopics(helpKeys, 2);

        ViewHelp viewHelp = new ViewHelp(loginHelpLocalTopics,
                loginHelpEnglishTopics, ViewHelp.HORIZONTAL, HelpPreferences.HOME_SCREEN_GENERAL_GAME_RULES);
        viewHelp.setLocalMainHeading("Main Heading Telugu");
        viewHelp.setEnglishMainHeading("Terms And Conditions");
        Utils.clearState();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        viewHelp.show(fragmentManager, "dialog");
    }
    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, final Object response, Object helperObject) {
        boolean isHandled = handleServerError(exceptionThrown, isAPIException, response);
        if (isHandled) {
            return;
        }
        if (reqId == Request.GET_LOGGEG_IN_USER_COUNT) {
            isHandled = handleAPIError(isAPIException, response, 1, null, null);
            if (isHandled) {
                return;
            }
            Long count = (Long) response;
            String loggedUserCtText = getResources().getString(R.string.logged_userCount_txt);
            loggedUserCtText = loggedUserCtText + count;
            String finalLoggedUserCtText = loggedUserCtText;
            Runnable run = () -> {
                View view = getView();
                if (view == null) {
                    return;
                }
                TextView userCount = view.findViewById(R.id.loggedUserCount);
                userCount.setText(finalLoggedUserCtText);
            };
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(run);
            }
        }
    }
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.gameTypeEntryBut) {
            int rowCount = (Integer) view.getTag();
            int gameType = -1;
            String fragmentName = null;
            if (rowCount == 0) {
                gameType = 3;
                fragmentName = Navigator.MIXED_ENROLLED_GAMES_VIEW;
                if (viewType == FUTURE_GAMES) {
                    gameType = 1;
                    fragmentName = Navigator.MIXED_GAMES_VIEW;
                }
            } else if (rowCount == 1) {
                gameType = 4;
                fragmentName = Navigator.CELEBRITY_ENROLLED_GAMES_VIEW;
                if (viewType == FUTURE_GAMES) {
                    gameType = 2;
                    fragmentName = Navigator.CELEBRITY_GAMES_VIEW;
                }
            }
            Bundle params = new Bundle();
            params.putInt(Keys.GAMES_VIEW_GAME_TYPE, gameType);
            ((Navigator) requireActivity()).launchView(fragmentName, params, false);
        }
    }
}
