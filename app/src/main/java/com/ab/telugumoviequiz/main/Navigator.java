package com.ab.telugumoviequiz.main;

import android.os.Bundle;

public interface Navigator {
    String PUBLIC_GAMES_VIEW = "PublicGamesView1";
    String ENROLLED_GAMES_VIEW = "PublicGamesView2";
    String HOME_VIEW = "HomeView";
    String PROFILE_VIEW = "ProfileView";
    String CHAT_VIEW = "ChatView";
    String REFERALS_VIEW = "ReferalsView";
    String HISTORY_VIEW = "HistoryView";
    String TRANSACTIONS_VIEW = "TransactionsView";
    String WALLET_VIEW = "WalletView";
    String WITHDRAW_REQ_VIEW = "WithdrawReqsView";
    String WITHDRAW_HISTORY_VIEW = "WithdrawHistoryView";
    String QUESTION_VIEW = "QuestionView";

    void launchView(String viewId, Bundle params, boolean storeState);
}
