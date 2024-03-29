package com.ab.telugumoviequiz.main;

public class UserMoney {
	private long id;
	private long amount;
	private long amtLocked;
	private long winAmount;
	private long referAmount;
	private long addedAmount;
	private long withdrawnAmount;

	public UserMoney() {
	}

	public UserMoney(long id, long amount, long amtLocked, long winAmount, long referAmount) {
		super();
		this.id = id;
		this.amount = amount;
		this.amtLocked = amtLocked;
		this.winAmount = winAmount;
		this.referAmount = referAmount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public long getAmtLocked() {
		return amtLocked;
	}

	public void setAmtLocked(long amtLocked) {
		this.amtLocked = amtLocked;
	}

	public long getWinAmount() {
		return winAmount;
	}

	public void setWinAmount(long winAmount) {
		this.winAmount = winAmount;
	}

	public long getReferAmount() {
		return referAmount;
	}

	public void setReferAmount(long referAmount) {
		this.referAmount = referAmount;
	}

	public long getAddedAmount() {
		return addedAmount;
	}
	public void setAddedAmount(long addedAmount) {
		this.addedAmount = addedAmount;
	}
	public long getWithdrawnAmount() {
		return withdrawnAmount;
	}
	public void setWithdrawnAmount(long withdrawnAmount) {
		this.withdrawnAmount = withdrawnAmount;
	}
}


