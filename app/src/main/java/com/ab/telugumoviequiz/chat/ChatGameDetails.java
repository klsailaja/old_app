package com.ab.telugumoviequiz.chat;

public class ChatGameDetails {
	private int tempGameId;
	private int ticketRate;
	private int currentCount;
	private int gameType;
	private String gameTime;
	private String celebrityName;
	private long gameTimeInMillis;
	
	public long getGameTimeInMillis() {
		return gameTimeInMillis;
	}
	public void setGameTimeInMillis(long gameTimeInMillis) {
		this.gameTimeInMillis = gameTimeInMillis;
	}
	
	public int getTempGameId() {
		return tempGameId;
	}
	public void setTempGameId(int tempGameId) {
		this.tempGameId = tempGameId;
	}
	public int getTicketRate() {
		return ticketRate;
	}
	public void setTicketRate(int ticketRate) {
		this.ticketRate = ticketRate;
	}
	public int getCurrentCount() {
		return currentCount;
	}
	public void setCurrentCount(int currentCount) {
		this.currentCount = currentCount;
	}
	public int getGameType() {
		return gameType;
	}
	public void setGameType(int gameType) {
		this.gameType = gameType;
	}
	public String getGameTime() {
		return gameTime;
	}
	public void setGameTime(String gameTime) {
		this.gameTime = gameTime;
	}
	public String getCelebrityName() {
		return celebrityName;
	}
	public void setCelebrityName(String celebrityName) {
		this.celebrityName = celebrityName;
	}
}
