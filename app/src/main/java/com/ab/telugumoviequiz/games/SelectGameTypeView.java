package com.ab.telugumoviequiz.games;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.Keys;
import com.ab.telugumoviequiz.main.Navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SelectGameTypeView extends BaseFragment implements View.OnClickListener {
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
        typeModel1.setGameTypeName("Movie Mixture");
        typeModel1.setCelebrityName("Quiz questions mix of all movies");
        modelList.add(typeModel1);

        GameTypeModel typeModel2 = new GameTypeModel();
        typeModel2.setGameTypeName("Celebrity Special");
        typeModel2.setCelebrityName("Quiz questions related to celebrity");
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
        return root;
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
            ((Navigator) Objects.requireNonNull(getActivity())).launchView(fragmentName, params, false);
        }
    }
}
