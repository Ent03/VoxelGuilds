package com.entity999.teams.coins;

public class CoinData {
    public String id;
    public String originalHolder;
    public int amountLeft;
    public String date;

    public CoinData(String id, String originalHolder, int amountLeft, String date) {
        this.id = id;
        this.date = date;
        this.originalHolder = originalHolder;
        this.amountLeft = amountLeft;
    }
}
