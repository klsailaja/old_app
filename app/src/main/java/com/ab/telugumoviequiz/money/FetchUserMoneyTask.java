package com.ab.telugumoviequiz.money;

import com.ab.telugumoviequiz.main.MainActivity;

public class FetchUserMoneyTask implements Runnable {
    private MainActivity activity;

    public FetchUserMoneyTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        activity.fetchUpdateMoney();
    }
}
