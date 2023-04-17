package com.ab.telugumoviequiz.money;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BaseFragment;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.main.AddMoneyProcessor;
import com.ab.telugumoviequiz.main.MainActivity;

public class CoinStore extends BaseFragment
        implements CallbackResponse, View.OnClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.coin_store, container, false);

        Resources resources = getResources();

        TextView optionTV = rootView.findViewById(R.id.buy_coin_1_tv);
        Button optionBut = rootView.findViewById(R.id.buy_coin_1_but);

        String descStr = resources.getString(R.string.buy_coin_desc, resources.getInteger(R.integer.buy_coin_1_number));
        optionTV.setText(descStr);

        int cost = resources.getInteger(R.integer.buy_coin_1_cost);
        int coinCt = resources.getInteger(R.integer.buy_coin_1_number);
        CoinPurchase coinPurchase = new CoinPurchase(cost, coinCt);
        String costStr = resources.getString(R.string.buy_coin_cost, cost);
        optionBut.setText(costStr);
        optionBut.setTag(coinPurchase);

        optionTV = rootView.findViewById(R.id.buy_coin_2_tv);
        optionBut = rootView.findViewById(R.id.buy_coin_2_but);

        descStr = resources.getString(R.string.buy_coin_desc, resources.getInteger(R.integer.buy_coin_2_number));
        optionTV.setText(descStr);

        cost = resources.getInteger(R.integer.buy_coin_2_cost);
        coinCt = resources.getInteger(R.integer.buy_coin_2_number);
        coinPurchase = new CoinPurchase(cost, coinCt);
        costStr = resources.getString(R.string.buy_coin_cost, cost);
        optionBut.setText(costStr);
        optionBut.setTag(coinPurchase);

        optionTV = rootView.findViewById(R.id.buy_coin_3_tv);
        optionBut = rootView.findViewById(R.id.buy_coin_3_but);

        descStr = resources.getString(R.string.buy_coin_desc, resources.getInteger(R.integer.buy_coin_3_number));
        optionTV.setText(descStr);

        cost = resources.getInteger(R.integer.buy_coin_3_cost);
        coinCt = resources.getInteger(R.integer.buy_coin_3_number);
        coinPurchase = new CoinPurchase(cost, coinCt);
        costStr = resources.getString(R.string.buy_coin_cost, cost);
        optionBut.setText(costStr);
        optionBut.setTag(coinPurchase);

        optionTV = rootView.findViewById(R.id.buy_coin_4_tv);
        optionBut = rootView.findViewById(R.id.buy_coin_4_but);

        descStr = resources.getString(R.string.buy_coin_desc, resources.getInteger(R.integer.buy_coin_4_number));
        optionTV.setText(descStr);

        cost = resources.getInteger(R.integer.buy_coin_4_cost);
        coinCt = resources.getInteger(R.integer.buy_coin_4_number);
        coinPurchase = new CoinPurchase(cost, coinCt);
        costStr = resources.getString(R.string.buy_coin_cost, cost);
        optionBut.setText(costStr);
        optionBut.setTag(coinPurchase);

        optionTV = rootView.findViewById(R.id.buy_coin_5_tv);
        optionBut = rootView.findViewById(R.id.buy_coin_5_but);

        descStr = resources.getString(R.string.buy_coin_desc, resources.getInteger(R.integer.buy_coin_5_number));
        optionTV.setText(descStr);

        cost = resources.getInteger(R.integer.buy_coin_5_cost);
        coinCt = resources.getInteger(R.integer.buy_coin_5_number);
        coinPurchase = new CoinPurchase(cost, coinCt);
        costStr = resources.getString(R.string.buy_coin_cost, cost);
        optionBut.setText(costStr);
        optionBut.setTag(coinPurchase);

        optionTV = rootView.findViewById(R.id.buy_coin_6_tv);
        optionBut = rootView.findViewById(R.id.buy_coin_6_but);

        descStr = resources.getString(R.string.buy_coin_desc, resources.getInteger(R.integer.buy_coin_6_number));
        optionTV.setText(descStr);

        cost = resources.getInteger(R.integer.buy_coin_6_cost);
        coinCt = resources.getInteger(R.integer.buy_coin_6_number);
        coinPurchase = new CoinPurchase(cost, coinCt);
        costStr = resources.getString(R.string.buy_coin_cost, cost);
        optionBut.setText(costStr);
        optionBut.setTag(coinPurchase);

        optionTV = rootView.findViewById(R.id.buy_coin_7_tv);
        optionBut = rootView.findViewById(R.id.buy_coin_7_but);

        descStr = resources.getString(R.string.buy_coin_desc, resources.getInteger(R.integer.buy_coin_7_number));
        optionTV.setText(descStr);

        cost = resources.getInteger(R.integer.buy_coin_7_cost);
        coinCt = resources.getInteger(R.integer.buy_coin_7_number);
        coinPurchase = new CoinPurchase(cost, coinCt);
        costStr = resources.getString(R.string.buy_coin_cost, cost);
        optionBut.setText(costStr);
        optionBut.setTag(coinPurchase);

        optionTV = rootView.findViewById(R.id.buy_coin_8_tv);
        optionBut = rootView.findViewById(R.id.buy_coin_8_but);

        descStr = resources.getString(R.string.buy_coin_desc, resources.getInteger(R.integer.buy_coin_8_number));
        optionTV.setText(descStr);

        cost = resources.getInteger(R.integer.buy_coin_8_cost);
        coinCt = resources.getInteger(R.integer.buy_coin_8_number);
        coinPurchase = new CoinPurchase(cost, coinCt);
        costStr = resources.getString(R.string.buy_coin_cost, cost);
        optionBut.setText(costStr);
        optionBut.setTag(coinPurchase);

        optionTV = rootView.findViewById(R.id.buy_coin_9_tv);
        optionBut = rootView.findViewById(R.id.buy_coin_9_but);

        descStr = resources.getString(R.string.buy_coin_desc, resources.getInteger(R.integer.buy_coin_9_number));
        optionTV.setText(descStr);

        cost = resources.getInteger(R.integer.buy_coin_9_cost);
        coinCt = resources.getInteger(R.integer.buy_coin_9_number);
        coinPurchase = new CoinPurchase(cost, coinCt);
        costStr = resources.getString(R.string.buy_coin_cost, cost);
        optionBut.setText(costStr);
        optionBut.setTag(coinPurchase);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
        handleListeners(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        handleListeners(null);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void handleListeners(View.OnClickListener listener) {
        View rootView = getView();
        if (rootView == null) {
            return;
        }
        Button optionBut = rootView.findViewById(R.id.buy_coin_1_but);
        optionBut.setOnClickListener(listener);
        optionBut = rootView.findViewById(R.id.buy_coin_2_but);
        optionBut.setOnClickListener(listener);
        optionBut = rootView.findViewById(R.id.buy_coin_3_but);
        optionBut.setOnClickListener(listener);
        optionBut = rootView.findViewById(R.id.buy_coin_4_but);
        optionBut.setOnClickListener(listener);
        optionBut = rootView.findViewById(R.id.buy_coin_5_but);
        optionBut.setOnClickListener(listener);
        optionBut = rootView.findViewById(R.id.buy_coin_6_but);
        optionBut.setOnClickListener(listener);
        optionBut = rootView.findViewById(R.id.buy_coin_7_but);
        optionBut.setOnClickListener(listener);
        optionBut = rootView.findViewById(R.id.buy_coin_8_but);
        optionBut.setOnClickListener(listener);
        optionBut = rootView.findViewById(R.id.buy_coin_9_but);
        optionBut.setOnClickListener(listener);
    }

    @Override
    public void onClick(View view) {
        CoinPurchase costVal = (CoinPurchase) view.getTag();

        MainActivity mainActivity = (MainActivity) getActivity();

        LoadMoney loadMoney = new LoadMoney();
        loadMoney.setUid(UserDetails.getInstance().getUserProfile().getId());
        loadMoney.setMoneyMoney(UserDetails.getInstance().isMoneyMode());
        loadMoney.setAmount(costVal.getCost());
        loadMoney.setCoinCount(costVal.getNumberOfCoins());


        AddMoneyProcessor.getInstance().processAddMoneyRequest(loadMoney,
                mainActivity, mainActivity);
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown,
                               boolean isAPIException, Object response,
                               Object userObject) {
        Activity activity = getActivity();
        if((exceptionThrown) && (!isAPIException)) {
            Runnable run = () -> {
                String error = (String) response;
                Utils.showMessage("Error", error, getContext(), null);
            };
            if (activity != null) {
                activity.runOnUiThread(run);
            }
            return;
        }
        if (isAPIException) {
            Runnable run = () -> {
                String error = (String) response;
                Utils.showMessage("Error", error, getContext(), null);
            };
            if (activity != null) {
                activity.runOnUiThread(run);
            }
            return;
        }
        if (reqId == Request.ADD_MONEY_REQ) {
            boolean result = (boolean) response;
            if (result) {
                displayInfo("Coins purchase success", null);
            } else {
                displayError("Error while purchasing coins", null);
            }
        }

    }
}