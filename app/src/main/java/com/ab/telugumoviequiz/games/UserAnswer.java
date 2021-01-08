package com.ab.telugumoviequiz.games;

public class UserAnswer {
    private int qNo;
    private boolean isCorrect;
    private long timeTaken;

    public UserAnswer(int qNo, boolean isCorrect, long timeTaken) {
        this.qNo = qNo;
        this.isCorrect = isCorrect;
        this.timeTaken = timeTaken;
    }

    public int getqNo() {
        return qNo;
    }
    public boolean isCorrect() {
        return isCorrect;
    }
    public long getTimeTaken() {
        return timeTaken;
    }
}
