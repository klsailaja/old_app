package com.ab.telugumoviequiz.common;

import com.ab.telugumoviequiz.games.GameDetails;
import com.ab.telugumoviequiz.games.GameOperation;
import com.ab.telugumoviequiz.games.GameStatus;
import com.ab.telugumoviequiz.games.GameStatusHolder;
import com.ab.telugumoviequiz.games.GetGamesTask;
import com.ab.telugumoviequiz.games.PlayerAnswer;
import com.ab.telugumoviequiz.games.PlayerSummary;
import com.ab.telugumoviequiz.games.PrizeDetail;
import com.ab.telugumoviequiz.main.LoginData;
import com.ab.telugumoviequiz.main.UserProfile;
import com.ab.telugumoviequiz.referals.ReferalDetails;
import com.ab.telugumoviequiz.transactions.MyTransaction;
import com.ab.telugumoviequiz.transactions.TransactionsHolder;

public class Request {
    public static String baseUri = null;
    public static final int GET_FUTURE_GAMES = 200;
    public static final int GET_ENROLLED_GAMES = 201;
    public static final int GET_FUTURE_GAMES_STATUS = 202;
    public static final int GET_ENROLLED_GAMES_STATUS = 203;
    public static final int CREATE_USER_PROFILE = 100;
    public static final int LOGIN_REQ = 101;
    public static final int SUBMIT_ANSWER_REQ = 102;
    public static final int UNJOIN_GAME = 103;
    public static final int SINGLE_GAME_STATUS = 104;
    public static final int PRIZE_DETAILS = 105;
    public static final int JOIN_GAME = 106;
    public static final int LEADER_BOARD = 107;
    public static final int USER_REFERALS_LIST = 110;
    public static final int USER_TRANSACTIONS = 111;

    public static final int SHOW_QUESTION = 1000;
    public static final int SHOW_USER_ANSWERS = 2000;
    public static final int SHOW_LEADER_BOARD = 2001;
    public static final int SHOW_READY_MSG = 2002;
    public static final int SHOW_WINNERS = 2003;
    public static final int LOCK_TIME_OVER = 3000;

    /* Games related Tasks */

    public static GetTask<GameStatus> getSingleGameStatus(long gameId) {
        String uri = baseUri + "/game/" + gameId + "/status";
        GetTask<GameStatus> getSingleGameStatusTask = new GetTask<>(uri, SINGLE_GAME_STATUS, null,
                GameStatus.class, null);
        return getSingleGameStatusTask;
    }
    public static GetTask<PrizeDetail[]> getPrizeDetails(long gameId) {
        String uri = baseUri + "/game/" + gameId + "/prize";
        GetTask<PrizeDetail[]> getPrizeDetailsTask = new GetTask<>(uri, PRIZE_DETAILS, null,
                PrizeDetail[].class, null);
        return getPrizeDetailsTask;
    }
    public static GetTask<PlayerSummary[]> getLeaderBoard(long gameId, int completedQNo) {
        String uri = baseUri + "/game/" + gameId + "/leaderboard/" + completedQNo;
        GetTask<PlayerSummary[]> getLeaderBoardTask = new GetTask<>(uri, LEADER_BOARD, null,
                PlayerSummary[].class, null);
        return getLeaderBoardTask;
    }
    public static GetGamesTask<GameDetails[]> getFutureGames(int gameType) {
        String uri = baseUri + "/game/" + gameType + "/future";
        GetGamesTask<GameDetails[]> getFutureGames = new GetGamesTask<>(uri, GET_FUTURE_GAMES,
                null, GameDetails[].class, null);
        return getFutureGames;
    }
    public static GetTask<GameStatusHolder> getFutureGamesStatusTask(int gameType) {
        String uri = baseUri + "/game/" + gameType + "/allstatus";
        GetTask<GameStatusHolder> getFutureStatusTask = new GetTask<>(uri, GET_FUTURE_GAMES_STATUS, null,
                GameStatusHolder.class, null);
        return getFutureStatusTask;
    }
    public static GetTask<GameDetails[]> getEnrolledGames(int gameType, long userProfileId) {
        String uri = baseUri + "/game/" + gameType + "/enrolled/" + userProfileId;
        GetTask<GameDetails[]> getFutureGames = new GetTask<>(uri, GET_ENROLLED_GAMES, null,
                GameDetails[].class, null);
        return getFutureGames;
    }
    public static GetTask<GameStatusHolder> getEnrolledGamesStatus(int gameType, long userProfileId) {
        String uri = baseUri + "/game/" + gameType + "/enrolled/" + userProfileId + "/status";
        GetTask<GameStatusHolder> getFutureStatusTask = new GetTask<>(uri, GET_ENROLLED_GAMES_STATUS, null,
                GameStatusHolder.class, null);
        return getFutureStatusTask;
    }

    public static PostTask<GameOperation, Boolean> gameJoinTask(long gameId) {
        String uri = baseUri + "/game/" + gameId + "/join";
        PostTask<GameOperation, Boolean> joinTask = new PostTask<>(uri, JOIN_GAME,
                null, null, Boolean.class);
        return joinTask;
    }
    public static PostTask<PlayerAnswer, String> submitAnswerTask(long gameId) {
        String uri = baseUri + "/game/" + gameId + "/submit";
        PostTask<PlayerAnswer, String> submitAnswer = new PostTask<>(uri, SUBMIT_ANSWER_REQ,
                null, null, String.class);
        return submitAnswer;
    }

    /* Login Screen related */
    public static PostTask<LoginData, UserProfile> getLogin() {
        String uri = baseUri + "/user/login";
        PostTask<LoginData, UserProfile> loginTask = new PostTask<>(uri, LOGIN_REQ,
                null, null, UserProfile.class);
        return loginTask;
    }
    public static PostTask<UserProfile, UserProfile> getCreateUserProfile() {
        String uri = baseUri + "/user";
        PostTask<UserProfile, UserProfile> createUserProfile = new PostTask<>(uri, CREATE_USER_PROFILE,
                null, null, UserProfile.class);
        return createUserProfile;
    }

    /* My Referals related */
    public static GetTask<ReferalDetails> getUserReferalDetails(String referalCode, int startRowNo) {
        String uri = baseUri + "/user/mreferal/" + referalCode + "/" + startRowNo;
        GetTask<ReferalDetails> getReferalsTask = new GetTask<>(uri, USER_REFERALS_LIST, null,
                ReferalDetails.class, null);
        return getReferalsTask;
    }

    /* Transaction Related */
    public static GetTask<TransactionsHolder> getUserTransactions(long userProfileId, int startRowNo, int accType) {
        String uri = baseUri + "/user/transaction/" + userProfileId + "/" + startRowNo + "/" + accType;
        GetTask<TransactionsHolder> getTransactionsTask = new GetTask<>(uri, USER_TRANSACTIONS, null,
                TransactionsHolder.class, null);
        return getTransactionsTask;
    }
}
