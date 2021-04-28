package com.ab.telugumoviequiz.games;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.PostTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.constants.UserMoneyAccountType;
import com.ab.telugumoviequiz.main.UserMoney;

import java.util.ArrayList;
import java.util.List;

public class PayGameDialog extends DialogFragment implements View.OnClickListener {

    private List<PayGameModel> modelList;
    private PaymentOptionsAdapter mAdapter;
    private CallbackResponse listener;
    private GameDetails gameDetails;

    public PayGameDialog(List<PayGameModel> modelList, CallbackResponse listener, GameDetails gameDetails) {
        this.modelList = modelList;
        this.listener = listener;
        this.gameDetails = gameDetails;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.list_games_view, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        mAdapter = new PaymentOptionsAdapter(modelList, this, getContext());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        return root;
    }

    @Override
    public void onResume() {
        // Sets the height and the width of the DialogFragment
        super.onResume();
        int[] points = Utils.getScreenWidth(getContext());
        int width = (points[0] * 3) /4;
        //int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = (points[1] * 3/ 4);
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(width, height);
            }
        }
    }

    @Override
    public void onClick(View view) {
        dismiss();
        int modelIndex = (Integer) view.getTag();
        PayGameModel model = modelList.get(modelIndex);
        UserMoneyAccountType userMoneyAccountType = UserMoneyAccountType.findById(model.getAccountNumber());

        PostTask<GameOperation, Boolean> joinTask = Request.gameJoinTask(gameDetails.getGameId());
        joinTask.setCallbackResponse(listener);

        GameOperation gm = new GameOperation();
        gm.setUserProfileId(UserDetails.getInstance().getUserProfile().getId());
        gm.setUserAccountType(userMoneyAccountType.getId());
        joinTask.setPostObject(gm);
        joinTask.setHelperObject(gameDetails);
        Scheduler.getInstance().submit(joinTask);
    }

    @Override
    public void onDismiss (@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
