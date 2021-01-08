package com.ab.telugumoviequiz.games;

import android.os.Parcel;
import android.os.Parcelable;

public class PrizeDetail implements Parcelable {
	private int rank;
	private int prizeMoney;

	public PrizeDetail() {
	}

	public PrizeDetail(Parcel in){
		this.rank = in.readInt();
		this.prizeMoney = in.readInt();
	}
	
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getPrizeMoney() {
		return prizeMoney;
	}
	public void setPrizeMoney(int prizeMoney) {
		this.prizeMoney = prizeMoney;
	}

	@Override
	public String toString() {
		return "PrizeDetail [rank=" + rank + ", prizeMoney=" + prizeMoney + "]";
	}

	public static final Creator CREATOR = new Creator() {
		public PrizeDetail createFromParcel(Parcel in) {
			return new PrizeDetail(in);
		}

		public PrizeDetail[] newArray(int size) {
			return new PrizeDetail[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(getRank());
		parcel.writeInt(getPrizeMoney());
	}
}
