package com.entity999.teams.upgrades;

import java.util.HashMap;

public class ClanUpgrades{
    private HashMap<ClanUpgradeType, Integer> upgrades = new HashMap<>();

    public ClanUpgrades(int... levels){
        ClanUpgradeType[] types = ClanUpgradeType.values();
        for(int i = 0; i < types.length; i++){
            if(levels.length-1 < i || levels[i] == -1) continue;
            upgrades.put(types[i], levels[i]);
        }
    }

    public int getUpgradeLevel(ClanUpgradeType type){
        return upgrades.getOrDefault(type, type.defValue);
    }
}
