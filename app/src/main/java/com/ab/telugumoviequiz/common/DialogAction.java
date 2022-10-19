package com.ab.telugumoviequiz.common;

public interface DialogAction {
    int SHARE_CONFIRM = 2000;
    int GAME_CANCELLED_MONEY_NOT_CREDITED = 9000;
    void doAction(int calledId, Object userObject);
}
