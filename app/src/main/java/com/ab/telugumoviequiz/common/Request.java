package com.ab.telugumoviequiz.common;

import com.ab.telugumoviequiz.chat.Chat;
import com.ab.telugumoviequiz.customercare.CCTicketsHolder;
import com.ab.telugumoviequiz.customercare.CustomerTicket;
import com.ab.telugumoviequiz.customercare.PostPictureTask;
import com.ab.telugumoviequiz.games.CelebrityFullDetails;
import com.ab.telugumoviequiz.games.GameDetails;
import com.ab.telugumoviequiz.games.GameOperation;
import com.ab.telugumoviequiz.games.GameStatus;
import com.ab.telugumoviequiz.games.GameStatusHolder;
import com.ab.telugumoviequiz.games.GetGamesTask;
import com.ab.telugumoviequiz.games.PlayerAnswer;
import com.ab.telugumoviequiz.games.PlayerSummary;
import com.ab.telugumoviequiz.games.PrizeDetail;
import com.ab.telugumoviequiz.history.UserHistoryGameDetails;
import com.ab.telugumoviequiz.kyc.KYCEntry;
import com.ab.telugumoviequiz.main.LoginData;
import com.ab.telugumoviequiz.main.OTPDetails;
import com.ab.telugumoviequiz.main.UserMoney;
import com.ab.telugumoviequiz.main.UserProfile;
import com.ab.telugumoviequiz.money.TransferRequest;
import com.ab.telugumoviequiz.referals.ReferalDetails;
import com.ab.telugumoviequiz.transactions.TransactionsHolder;
import com.ab.telugumoviequiz.withdraw.GetReceiptTask;
import com.ab.telugumoviequiz.withdraw.WithdrawRequestInput;
import com.ab.telugumoviequiz.withdraw.WithdrawRequestsHolder;

public class Request {
    public static String baseUri = null;
    public static final int GET_FUTURE_GAMES = 200;
    public static final int GET_ENROLLED_GAMES = 201;
    public static final int GET_FUTURE_GAMES_STATUS = 202;
    public static final int GET_ENROLLED_GAMES_STATUS = 203;
    //public static final int GET_CELEBRITY_NAME = 204;
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
    public static final int CHAT_MSG_COUNT_FETCH = 125;
    /*public static final int CHAT_BASIC_GAME_DETAILS_MIX_SET = 122;
    public static final int CHAT_BASIC_GAME_DETAILS_CELEBRITY_SET = 123;*/
    public static final int GET_USER_MONEY = 130;
    public static final int GAME_ENROLLED_STATUS = 131;
    public static final int TIME_CHECK_ID = 135;
    public static final int UPCOMING_CELEBRITY_NAMES_ID = 136;
    public static final int SEND_OTP_CODE = 140;
    public static final int VERIFY_OTP_CODE = 141;

    public static final int USER_CC_LIST = 150;
    public static final int CC_CANCEL = 151;
    public static final int CC_RECEIPT = 152;

    public static final int SHOW_QUESTION = 1000;
    public static final int SHOW_USER_ANSWERS = 2000;
    public static final int SHOW_LEADER_BOARD = 2001;
    //public static final int SHOW_READY_MSG = 2002;
    public static final int SHOW_WINNERS = 2003;
    //public static final int LOCK_TIME_OVER = 3000;
    public static final int WIN_WD_MSGS = 4000;
    public static final int WIN_WD_SHOW_MSG = 4001;
    public static final int UPDATE_USER_PROFILE = 4010;
    public static final int FORGOT_PASSWORD = 4011;
    public static final int CELEBRITY_SCHEDULE_DETAIS = 4020;
    public static final int TRANSFER_MONEY_REQ = 4030;
    public static final int CREATE_NEW_WD_REQ = 4040;
    public static final int ADD_MONEY_REQ = 4050;
    public static final int GET_LOGGEG_IN_USER_COUNT = 4051;
    public static final int MONEY_TASK_STATUS = 5000;
    public static final int CREATE_CC_ISSUE = 6000;
    public static final int POST_PIC_ISSUE = 6001;
    public static final int KYC_GET_OBJECT = 7000;
    public static final int KYC_CREATE = 7001;
    public static final int KYC_POST_PIC = 7002;

    public static String getTermsConditionsURL() {
        return baseUri + "/terms";
    }

    public static PostTask<WithdrawRequestInput, Boolean> createNewWDRequest() {
        String uri = baseUri + "/wd";
        return new PostTask<>(uri, CREATE_NEW_WD_REQ,
                null, null, Boolean.class);
    }

    public static GetTask<String[]> getWinWdMessages(long userProfileId, int maxUserCount) {
        String uri = baseUri + "/wd/messages/" + userProfileId + "/" + maxUserCount;
        return new GetTask<>(uri, WIN_WD_MSGS, null,
                String[].class, null);
    }


    /* Games related Tasks */
    public static GetTask<Long> getLoggedInUserCount() {
        String uri = baseUri + "/loggedin/count";
        return new GetTask<>(uri, GET_LOGGEG_IN_USER_COUNT, null,
                Long.class, null);
    }
    public static GetTask<CelebrityFullDetails> getCelebrityScheduleTask() {
        String uri = baseUri + "/game/celebrityschedule";
        return new GetTask<>(uri, CELEBRITY_SCHEDULE_DETAIS, null,
                CelebrityFullDetails.class, null);
    }
    /*public static GetTask<String> getCelebrityNameTask() {
        String uri = baseUri + "/game/celebrity";
        return new GetTask<>(uri, GET_CELEBRITY_NAME, null,
                String.class, null);
    }*/


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
    public static String getFutureGamesURI(int gameType, long lastGameId, int slotsNeeded) {
        return baseUri + "/game/" + gameType + "/future/" + lastGameId + "/" + slotsNeeded;
    }
    public static GetGamesTask<GameDetails[]> getFutureGames() {
        return new GetGamesTask<>(null, GET_FUTURE_GAMES,
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

    /*
    // This API tells whether the given user id is enrolled in the given gameId.
    public static GetTask<String> getEnrolledStatus(long gameId, long userProfileId) {
        String uri = baseUri + "/game/" + gameId + "/" + userProfileId + "/enrolledstatus";
        return new GetTask<>(uri, GAME_ENROLLED_STATUS, null,
                String.class, null);
    }
    */

    public static PostTask<PlayerAnswer, String> submitAnswerTask(long gameId) {
        String uri = baseUri + "/game/" + gameId + "/submit";
        return new PostTask<>(uri, SUBMIT_ANSWER_REQ,
                null, null, String.class);
    }

    public static PostTask<TransferRequest, Boolean> getLoadMoneyRequest(int amt) {
        String uri = baseUri + "/money/" + UserDetails.getInstance().getUserProfile().getId()
                + "/load/" + amt;
        return new PostTask<>(uri, ADD_MONEY_REQ,
                null, null, Boolean.class);
    }
    /* Wallet View related */
    public static PostTask<TransferRequest, Boolean> getTransferRequest() {
        String uri = baseUri + "/money/" + UserDetails.getInstance().getUserProfile().getId() + "/transfer";
        return new PostTask<>(uri, TRANSFER_MONEY_REQ,
                null, null, Boolean.class);
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
    public static PostTask<UserProfile, UserProfile> getUpdateUserProfile() {
        String uri = baseUri + "/update";
        return new PostTask<>(uri, UPDATE_USER_PROFILE,
                null, null, UserProfile.class);
    }
    public static PostTask<LoginData, UserProfile> getForgotPassword() {
        String uri = baseUri + "/forgot";
        return new PostTask<>(uri, FORGOT_PASSWORD,
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

    /* Customer Care */
    public static GetTask<CCTicketsHolder> getCCReqs(long userProfileId, int startRowNo, int status) {
        String uri = baseUri + "/cc/" + userProfileId + "/" + startRowNo + "/" + status;
        System.out.println("uri is : " + uri);
        return new GetTask<>(uri, USER_CC_LIST, null,
                CCTicketsHolder.class, null);
    }
    public static GetTask<Boolean> getCCReqCancel(long userProfileId, String refId) {
        String uri = baseUri + "/cc/cancel/" + userProfileId + "/" + refId;
        return new GetTask<>(uri, CC_CANCEL, null, Boolean.class, null);
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

    public static GetTask<byte[]> getReceiptTask(long receiptId) {
        String uri = baseUri + "/wd/receipt/" + receiptId;
        return new GetReceiptTask<>(uri, WITHDRAW_RECEIPT, null, byte[].class, null);
    }

    /* Chat feature related...*/
    public static GetTask<Chat[]> getChatMessages(long statTime, long endTime) {
        String uri = baseUri + "/chat/" + statTime + "/" + endTime;
        return new GetTask<>(uri, CHAT_BULK_FETCH, null, Chat[].class, null);
    }

    public static GetTask<Integer> getChatMsgCount(long statTime, long endTime) {
        String uri = baseUri + "/chat/count/" + statTime + "/" + endTime;
        return new GetTask<>(uri, CHAT_MSG_COUNT_FETCH, null, Integer.class, null);
    }
    /*public static GetTask<ChatGameDetails[]> getMixedGameChatBasicGameDetails(int gameType) {
        String uri = baseUri + "/game/chat/" + gameType;
        return new GetTask<>(uri, CHAT_BASIC_GAME_DETAILS_MIX_SET, null,
                ChatGameDetails[].class, null);
    }
    public static GetTask<ChatGameDetails[]> getCelebrityGameChatBasicGameDetails(int gameType) {
        String uri = baseUri + "/game/chat/" + gameType;
        return new GetTask<>(uri, CHAT_BASIC_GAME_DETAILS_CELEBRITY_SET, null,
                ChatGameDetails[].class, null);
    }*/
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

    public static GetTask<String> getTimeCheckTask() {
        String uri = baseUri + "/user/time/" + System.currentTimeMillis();
        return new GetTask<>(uri, TIME_CHECK_ID, null,
                String.class, null);
    }

    public static GetTask<String[]> getUpcomingCelebrityNamesTask(int hour) {
        String uri = baseUri + "/game/upcoming/" + hour;
        return new GetTask<>(uri, UPCOMING_CELEBRITY_NAMES_ID, null,
                String[].class, null);
    }

    // New User Registration Screen
    public static PostTask<String, String> sendCodeTask() {
        String uri = baseUri + "/user/sendcode";
        return new PostTask<>(uri, SEND_OTP_CODE, null, null, String.class);
    }

    public static PostTask<OTPDetails, String> verifyCodeTask() {
        String uri = baseUri + "/user/verify";
        return new PostTask<>(uri, VERIFY_OTP_CODE,
                null, null, String.class);
    }

    public static GetTask<Integer> getMoneyStatusTask(long gameStartTime) {
        String uri = baseUri + "/money/update/" + gameStartTime;
        return new GetTask<>(uri, MONEY_TASK_STATUS, null, Integer.class, null);
    }

    public static PostTask<CustomerTicket, Long> getCreateCCTask() {
        String uri = baseUri + "/ccticket";
        return new PostTask<>(uri, CREATE_CC_ISSUE, null,
                null, Long.class);
    }

    public static PostPictureTask getPostPictureTask() {
        String uri = baseUri + "/ccimg";
        return new PostPictureTask(uri, POST_PIC_ISSUE, null);
    }

    public static GetTask<KYCEntry> getKYV(long uid) {
        String uri = baseUri + "/kyc/" + uid;
        return new GetTask<>(uri, KYC_GET_OBJECT, null,
                KYCEntry.class, null);
    }
    public static PostTask<KYCEntry, Long> getCreateKYCTask() {
        String uri = baseUri + "/kyc";
        return new PostTask<>(uri, KYC_CREATE, null,
                null, Long.class);
    }
    public static PostPictureTask getKYCPostPictureTask() {
        String uri = baseUri + "/kycimg";
        return new PostPictureTask(uri, KYC_POST_PIC, null);
    }
}
