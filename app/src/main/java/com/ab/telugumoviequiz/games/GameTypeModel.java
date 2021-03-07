package com.ab.telugumoviequiz.games;

public class GameTypeModel {
    private String gameTypeName;
    private String celebrityName;

    public String getGameTypeName() {
        return gameTypeName;
    }
    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }

    public String getCelebrityName() {
        return celebrityName;
    }
    public void setCelebrityName(String celebrityName) {
        this.celebrityName = celebrityName;
    }

    @Override
    public String toString() {
        return "GameTypeModel{" +
                "gameTypeName='" + gameTypeName + '\'' +
                ", celebrityName='" + celebrityName + '\'' +
                '}';
    }
}
