package com.ab.telugumoviequiz.common;

import com.ab.telugumoviequiz.chat.Chat;
import com.ab.telugumoviequiz.chat.ChatGameDetails;
import com.ab.telugumoviequiz.games.GameDetails;
import com.ab.telugumoviequiz.games.GameOperation;
import com.ab.telugumoviequiz.games.GameStatus;
import com.ab.telugumoviequiz.games.GameStatusHolder;
import com.ab.telugumoviequiz.games.GetGamesTask;
import com.ab.telugumoviequiz.games.PlayerAnswer;
import com.ab.telugumoviequiz.games.PlayerSummary;
import com.ab.telugumoviequiz.games.PrizeDetail;
import com.ab.telugumoviequiz.history.UserHistoryGameDetails;
import com.ab.telugumoviequiz.main.LoginData;
import com.ab.telugumoviequiz.main.UserMoney;
import com.ab.telugumoviequiz.main.UserProfile;
import com.ab.telugumoviequiz.referals.ReferalDetails;
import com.ab.telugumoviequiz.transactions.TransactionsHolder;
import com.ab.telugumoviequiz.withdraw.GetReceiptTask;
import com.ab.telugumoviequiz.withdraw.WithdrawRequestsHolder;

public class Request {
    public static String baseUri = null;
    public static final int GET_FUTURE_GAMES = 200;
    public static final int GET_ENROLLED_GAMES = 201;
    public static final int GET_FUTURE_GAMES_STATUS = 202;
    public static final int GET_ENROLLED_GAMES_STATUS = 203;
    public static final int GET_CELEBRITY_NAME = 204;
    public static final int CREATE_USER_PROFILE = 100;
    public static final int LOGIN_REQ = 101;
    public static final int SUBMIT_ANSWER_REQ = 102;
    public static final int UNJOIN_GAME = 103;
    public static final int SINGLE_GAME_STATUS = 104;
    public static final int PRIZE_DETAILS = 105;
    public static final int JOIN_GAME = 106;
    public static final int LEADER_BOARD = 107;
    public static final int USER_REFERRALS_LIST = 110;
    public static final int USER_TRANSACTIONS = 111;
    public static final int USER_HISTORY_GAMES = 112;
    public static final int USER_WITHDRAW_LIST = 113;
    public static final int WITHDRAW_CANCEL = 114;
    public static final int WITHDRAW_RECEIPT = 115;
    public static final int CHAT_BULK_FETCH = 120;
    public static final int POST_CHAT_MSG = 121;
    public static final int CHAT_BASIC_GAME_DETAILS_MIX_SET = 122;
    public static final int CHAT_BASIC_GAME_DETAILS_CELEBRITY_SET = 123;
    public static final int GET_USER_MONEY = 130;
    public static final int GAME_ENROLLED_STATUS = 131;

    public static final int SHOW_QUESTION = 1000;
    public static final int SHOW_USER_ANSWERS = 2000;
    public static final int SHOW_LEADER_BOARD = 2001;
    public static final int SHOW_READY_MSG = 2002;
    public static final int SHOW_WINNERS = 2003;
    public static final int LOCK_TIME_OVER = 3000;

    /* Games related Tasks */
    public static GetTask<String> getCelebrityNameTask() {
        String uri = baseUri + "/game/celebrity";
        return new GetTask<>(uri, GET_CELEBRITY_NAME, null,
                String.class, null);
    }


    public static GetTask<GameStatus> getSingleGameStatus(long gameId) {
        String uri = baseUri + "/game/" + gameId + "/status";
        return new GetTask<>(uri, SINGLE_GAME_STATUS, null,
                GameStatus.class, null);
    }
    public static GetTask<PrizeDetail[]> getPrizeDetails(long gameId) {
        String uri = baseUri + "/game/" + gameId + "/prize";
        return new GetTask<>(uri, PRIZE_DETAILS, null,
                PrizeDetail[].class, null);
    }
    public static GetTask<PlayerSummary[]> getLeaderBoard(long gameId, int completedQNo) {
        String uri = baseUri + "/game/" + gameId + "/leaderboard/" + completedQNo;
        return new GetTask<>(uri, LEADER_BOARD, null,
                PlayerSummary[].class, null);
    }
    public static GetGamesTask<GameDetails[]> getFutureGames(int gameType) {
        String uri = baseUri + "/game/" + gameType + "/future";
        return new GetGamesTask<>(uri, GET_FUTURE_GAMES,
                null, GameDetails[].class, null);
    }
    public static GetTask<GameStatusHolder> getFutureGamesStatusTask(int gameType) {
        String uri = baseUri + "/game/" + gameType + "/allstatus";
        return new GetTask<>(uri, GET_FUTURE_GAMES_STATUS, null,
                GameStatusHolder.class, null);
    }
    public static GetTask<GameDetails[]> getEnrolledGames(int gameType, long userProfileId) {
        String uri = baseUri + "/game/" + gameType + "/enrolled/" + userProfileId;
        return new GetTask<>(uri, GET_ENROLLED_GAMES, null,
                GameDetails[].class, null);
    }
    public static GetTask<GameStatusHolder> getEnrolledGamesStatus(int gameType, long userProfileId) {
        String uri = baseUri + "/game/" + gameType + "/enrolled/" + userProfileId + "/status";
        return new GetTask<>(uri, GET_ENROLLED_GAMES_STATUS, null,
                GameStatusHolder.class, null);
    }

    public static PostTask<GameOperation, Boolean> gameJoinTask(long gameId) {
        String uri = baseUri + "/game/" + gameId + "/join";
        return new PostTask<>(uri, JOIN_GAME,
                null, null, Boolean.class);
    }

    public static PostTask<GameOperation, Boolean> gameUnjoinTask(long gameId) {
        String uri = baseUri + "/game/" + gameId + "/unjoin";
        return new PostTask<>(uri, UNJOIN_GAME,
                null, null, Boolean.class);
    }

    // This API tells whether the given user id is enrolled in the given gameId.
    public static GetTask<String> getEnrolledStatus(long gameId, long userProfileId) {
        String uri = baseUri + "/game/" + gameId + "/" + userProfileId + "/enrolledstatus";
        return new GetTask<>(uri, GAME_ENROLLED_STATUS, null,
                String.class, null);
    }
    public static PostTask<PlayerAnswer, String> submitAnswerTask(long gameId) {
        String uri = baseUri + "/game/" + gameId + "/submit";
        return new PostTask<>(uri, SUBMIT_ANSWER_REQ,
                null, null, String.class);
    }

    /* Login Screen related */
    public static PostTask<LoginData, UserProfile> getLogin() {
        String uri = baseUri + "/user/login";
        return new PostTask<>(uri, LOGIN_REQ,
                null, null, UserProfile.class);
    }
    public static PostTask<UserProfile, UserProfile> getCreateUserProfile() {
        String uri = baseUri + "/user";
        return new PostTask<>(uri, CREATE_USER_PROFILE,
                null, null, UserProfile.class);
    }

    /* My Referals related */
    public static GetTask<ReferalDetails> getUserReferalDetails(String referalCode, int startRowNo) {
        String uri = baseUri + "/user/mreferal/" + referalCode + "/" + startRowNo;
        return new GetTask<>(uri, USER_REFERRALS_LIST, null,
                ReferalDetails.class, null);
    }

    /* Transaction Related */
    public static GetTask<TransactionsHolder> getUserTransactions(long userProfileId, int startRowNo, int accType) {
        String uri = baseUri + "/user/transaction/" + userProfileId + "/" + startRowNo + "/" + accType;
        return new GetTask<>(uri, USER_TRANSACTIONS, null,
                TransactionsHolder.class, null);
    }

    /* User History Games Related */
    public static GetTask<UserHistoryGameDetails> getUserHistoryGames(long userProfileId, int startRowNo) {
        String uri = baseUri + "/game/past/" + userProfileId + "/" + startRowNo;
        return new GetTask<>(uri, USER_HISTORY_GAMES, null,
                UserHistoryGameDetails.class, null);
    }

    /* Withdraw Operations Related */
    public static GetTask<WithdrawRequestsHolder> getWDReqs(long userProfileId, int startRowNo, int status) {
        String uri = baseUri + "/wd/" + userProfileId + "/" + startRowNo + "/" + status;
        return new GetTask<>(uri, USER_WITHDRAW_LIST, null,
                WithdrawRequestsHolder.class, null);
    }

    public static GetTask<Boolean> getCancelReq(long userProfileId, String refId) {
        String uri = baseUri + "/wd/cancel/" + userProfileId + "/" + refId;
        return new GetTask<>(uri, WITHDRAW_CANCEL, null, Boolean.class, null);
    }

    public static GetTask<byte[]> getReceiptTask(long receiptId, int requestType) {
        String uri = baseUri + "/wd/receipt/" + receiptId;
        return new GetReceiptTask<>(uri, WITHDRAW_RECEIPT, null, byte[].class, null);
    }

    /* Chat feature related...*/
    public static GetTask<Chat[]> getChatMessages(long statTime, long endTime) {
        String uri = baseUri + "/chat/" + statTime + "/" + endTime;
        return new GetTask<>(uri, CHAT_BULK_FETCH, null, Chat[].class, null);
    }
    public static GetTask<ChatGameDetails[]> getMixedGameChatBasicGameDetails(int gameType) {
        String uri = baseUri + "/game/chat/" + gameType;
        return new GetTask<>(uri, CHAT_BASIC_GAME_DETAILS_MIX_SET, null,
                ChatGameDetails[].class, null);
    }
    public static GetTask<ChatGameDetails[]> getCelebrityGameChatBasicGameDetails(int gameType) {
        String uri = baseUri + "/game/chat/" + gameType;
        return new GetTask<>(uri, CHAT_BASIC_GAME_DETAILS_CELEBRITY_SET, null,
                ChatGameDetails[].class, null);
    }
    public static PostTask<Chat, Boolean> postChatMsgTask() {
        String uri = baseUri + "/chat/new";
        return new PostTask<>(uri, POST_CHAT_MSG,
                null, null, Boolean.class);
    }

    // Money related...
    public static GetTask<UserMoney> getMoneyTask(long userProfileId) {
        String uri = baseUri + "/money/" + userProfileId;
        return new GetTask<>(uri, GET_USER_MONEY, null,
                UserMoney.class, null);
    }

}
