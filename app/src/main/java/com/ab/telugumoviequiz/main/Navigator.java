package com.ab.telugumoviequiz.main;

import android.os.Bundle;

public interface Navigator {
    String CURRENT_GAMES = "CurrentGames";
    String ENROLLED_GAMES = "EnrolledGames";

    String MIXED_GAMES_VIEW = "MixedGamesView";
    String MIXED_ENROLLED_GAMES_VIEW = "MixedEnrolledGamesView";
    String CELEBRITY_GAMES_VIEW = "CelebrityGamesView";
    String CELEBRITY_ENROLLED_GAMES_VIEW = "CelebrityEnrolledGames";
    String QUESTION_VIEW = "QuestionView";

    String HISTORY_VIEW = "HistoryView";
    String ADD_MONEY_VIEW = "AddMoney";
    String CHAT_VIEW = "ChatView";
    String REFERRALS_VIEW = "ReferralsView";
    String TRANSACTIONS_VIEW = "TransactionsView";
    String WITHDRAW_REQ_VIEW = "WithdrawReqsView";
    String PROFILE_VIEW = "ProfileView";
    String NEW_WITHDRAW_REQUEST = "NewWithdrawReqView";
    String TRANSFER_TO_BANK_ACCOUNT = "TransferToBankAccount";
    String TRANSFER_TO_PHONEPE_ACCOUNT = "TransferToPhonePeAccount";
    String CC_REQ_VIEW = "CustomerCareView";
    String NEW_CC_REQUEST = "NewCustomerCareReqView";
    String ADDED_MONEY_NOT_UPDATED = "AddedMoneyNotUpdated";
    String WIN_MONEY_NOT_UPDATED = "WinMoneyNotUpdated";
    String WD_REQ_NOT_PROCESSED = "WithdrawRequestNotProcessed";
    String QUESTION_ANSWER_WRONG = "ReportQuestionAnswerWrong";
    String CC_OTHERS = "CC_OTHERS";
    String KYC_VIEW = "KYC_VIEW";
    String FAQ = "FAQS";
    String MORE_GAMES = "Similar More Games";

    void launchView(String viewId, Bundle params, boolean storeState);
}
