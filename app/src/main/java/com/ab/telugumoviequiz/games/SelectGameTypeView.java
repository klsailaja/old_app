package com.ab.telugumoviequiz.games;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.Keys;
import com.ab.telugumoviequiz.common.ShowHelpFirstTimer;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.help.HelpPreferences;
import com.ab.telugumoviequiz.help.HelpTopic;
import com.ab.telugumoviequiz.help.ViewHelp;
import com.ab.telugumoviequiz.main.ClientInitializer;
import com.ab.telugumoviequiz.main.Navigator;

import java.util.ArrayList;
import java.util.List;

public class SelectGameTypeView extends BaseFragment implements View.OnClickListener {
    private final List<GameTypeModel> modelList = new ArrayList<>();
    public static final String HOME_SCREEN_GAME_TYPE = "HOME_SCREEN_GAME_TYPE";
    public static final int FUTURE_GAMES = 1; //
    public static final int ENROLLED_GAMES = 2; //
    private int viewType = FUTURE_GAMES;
    private final String SAVE_VIEW_TYPE = "SAVE_VIEW_TYPE";

    public SelectGameTypeView() {

    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle args = getArguments();
        if (args != null) {
            viewType = args.getInt(HOME_SCREEN_GAME_TYPE, FUTURE_GAMES);
        } else if (bundle != null) {
            viewType = bundle.getInt(SAVE_VIEW_TYPE);
        }
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

        enableActionBarButtons(true, this);
        long count = ClientInitializer.getInstance(null, null).getLoggedInUserCount();
        String loggedUserCtText = getResources().getString(R.string.logged_userCount_txt);
        loggedUserCtText = loggedUserCtText + count;
        TextView userCount = root.findViewById(R.id.loggedUserCount);
        userCount.setText(loggedUserCtText);
        if (ShowHelpFirstTimer.getInstance().isFirstTime(HelpPreferences.HOME_SCREEN_GENERAL_GAME_RULES)) {
            showHelpWindow();
        }
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void showHelpWindow() {
        /*int isSet = HelpPreferences.getInstance().readPreference(requireContext(), HelpPreferences.HOME_SCREEN_GENERAL_GAME_RULES);
        if (isSet == 1) {
            return;
        }*/
        List<String> helpKeys = new ArrayList<>();
        helpKeys.add("topic_name1");
        helpKeys.add("topic_name2");
        helpKeys.add("topic_name3");
        List<HelpTopic> loginHelpLocalTopics = Utils.getHelpTopics(helpKeys, 1);
        List<HelpTopic> loginHelpEnglishTopics = Utils.getHelpTopics(helpKeys, 2);

        ViewHelp viewHelp = new ViewHelp(loginHelpLocalTopics,
                loginHelpEnglishTopics, HelpPreferences.HOME_SCREEN_GENERAL_GAME_RULES);
        viewHelp.setLocalMainHeading("Main Heading Telugu");
        viewHelp.setEnglishMainHeading("Terms And Conditions");
        Utils.clearState();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            viewHelp.show(fragmentManager, "dialog");
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
        } else if (viewId == R.id.help) {
            showHelpWindow();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_VIEW_TYPE, viewType);
    }
}
