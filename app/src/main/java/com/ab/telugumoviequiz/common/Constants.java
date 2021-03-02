package com.ab.telugumoviequiz.common;

public class Constants {
    public static final long GAME_BEFORE_LOCK_PERIOD_IN_SECS = 10; // In Secs
    public static final long GAME_BEFORE_LOCK_PERIOD_IN_MILLIS = GAME_BEFORE_LOCK_PERIOD_IN_SECS * 1000;

    public static final long SCHEDULER_OFFSET_IN_SECS = 0;
    public static final long SCHEDULER_OFFSET_IN_MILLIS = SCHEDULER_OFFSET_IN_SECS * 1000;

    public static final long QUESTION_MAX_TIME_IN_SEC = 15;
    public static final long QUESTION_MAX_TIME_IN_MILLIS = QUESTION_MAX_TIME_IN_SEC * 1000 - SCHEDULER_OFFSET_IN_MILLIS;

    public static final long USER_ANSWERS_VIEW_MAX_TIME_IN_SEC = 5;
    public static final long USER_ANSWERS_VIEW_MAX_TIME_IN_MILLIS = USER_ANSWERS_VIEW_MAX_TIME_IN_SEC * 1000;
    public static final long USER_ANSWERS_VIEW_START_TIME_IN_MILLIS = QUESTION_MAX_TIME_IN_MILLIS - SCHEDULER_OFFSET_IN_MILLIS;

    public static final long LEADERBOARD_VIEW_MAX_TIME_IN_SEC = 10;
    public static final long LEADERBOARD_VIEW_MAX_TIME_IN_MILLIS = LEADERBOARD_VIEW_MAX_TIME_IN_SEC * 1000;
    public static final long LEADERBOARD_VIEW_START_TIME_IN_MILLIS = USER_ANSWERS_VIEW_START_TIME_IN_MILLIS +
            USER_ANSWERS_VIEW_MAX_TIME_IN_MILLIS - SCHEDULER_OFFSET_IN_MILLIS;

    public static int CHAT_MAX_ENTRIES = 100;
    public static final long CHAT_MAX_DURATION_IN_MINS = 30;
    public static final long CHAT_MAX_DURATION_IN_MILLIS = CHAT_MAX_DURATION_IN_MINS * 60 * 1000;

    public static final int TEST_MODE = 1;
}
