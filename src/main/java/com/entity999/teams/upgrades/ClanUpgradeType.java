package com.entity999.teams.upgrades;

public enum ClanUpgradeType{
    STORAGE_ROWS(0, 6,"Vault Level"),
    TOWNS(0, 1,"Towns"),
    BANK_CAPACITY(0, 1000,"Guild Bank Capacity Level"),
    MAX_BASES(1, 5, "Max Bases Amount"),
    TOWN_CLAIM_BLOCKS(4000, 100000, "Town Claim Blocks Amount");
    int defValue;
    int maxValue;
    String name;
    ClanUpgradeType(int defValue, int maxValue, String name){
        this.defValue = defValue;
        this.name = name;
        this.maxValue = maxValue;
    }

    public String getString(int level){
        if(maxValue == 1) return name;
        return name + " " + level;
    }
}

