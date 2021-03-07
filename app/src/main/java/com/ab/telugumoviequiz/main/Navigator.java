package com.ab.telugumoviequiz.main;

import android.os.Bundle;

public interface Navigator {
    String MIXED_GAMES_VIEW = "MixedGamesView";
    String MIXED_ENROLLED_GAMES_VIEW = "MixedEnrolledGamesView";
    String CELEBRITY_GAMES_VIEW = "CelebrityGamesView";
    String CELEBRITY_ENROLLED_GAMES_VIEW = "CelebrityEnrolledGames";
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
