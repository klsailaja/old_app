package com.ab.telugumoviequiz.money;

public class CoinPurchase {
    private int cost;
    private int numberOfCoins;

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getNumberOfCoins() {
        return numberOfCoins;
    }

    public void setNumberOfCoins(int numberOfCoins) {
        this.numberOfCoins = numberOfCoins;
    }

    public CoinPurchase(int cost, int numberOfCoins) {
        this.cost = cost;
        this.numberOfCoins = numberOfCoins;
    }
}
