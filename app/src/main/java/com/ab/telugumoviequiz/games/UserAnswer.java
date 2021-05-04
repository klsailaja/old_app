package com.ab.telugumoviequiz.games;

import android.os.Parcel;
import android.os.Parcelable;

public class UserAnswer implements Parcelable {
    private int qNo;
    private boolean isCorrect;
    private long timeTaken;

    public UserAnswer() {
    }

    public UserAnswer(int qNo, boolean isCorrect, long timeTaken) {
        this.qNo = qNo;
        this.isCorrect = isCorrect;
        this.timeTaken = timeTaken;
    }

    public int getqNo() {
        return qNo;
    }
    public void setqNo(int qNo) {
        this.qNo = qNo;
    }
    public boolean isCorrect() {
        return isCorrect;
    }
    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
    public long getTimeTaken() {
        return timeTaken;
    }
    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(qNo);
        int booleanEq = 0;
        if (isCorrect) {
            booleanEq = 1;
        }
        out.writeInt(booleanEq);
        out.writeLong(timeTaken);
    }

    public static final Parcelable.Creator<UserAnswer> CREATOR
            = new Parcelable.Creator<UserAnswer>() {
        public UserAnswer createFromParcel(Parcel in) {
            return new UserAnswer(in);
        }

        public UserAnswer[] newArray(int size) {
            return new UserAnswer[size];
        }
    };

    private UserAnswer(Parcel in) {
        qNo = in.readInt();
        int booleanEq = in.readInt();
        isCorrect = booleanEq != 0;
        timeTaken = in.readLong();
    }
}
