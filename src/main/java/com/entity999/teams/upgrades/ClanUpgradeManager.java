package com.entity999.teams.upgrades;

import com.entity999.core.services.ServiceManager;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamService;

import java.util.LinkedHashMap;

public class ClanUpgradeManager {
    private static LinkedHashMap<Integer, ClanUpgrades> clanUpgrades;

    static{
        clanUpgrades = new LinkedHashMap<>();
        //-1 is def value
        clanUpgrades.put(0, new ClanUpgrades());
        clanUpgrades.put(2, new ClanUpgrades(1, 0, 1, 1));
        clanUpgrades.put(3, new ClanUpgrades(2, 1, 2, 1, 4000));
        clanUpgrades.put(4, new ClanUpgrades(2, 1, 3,2, 5000));
        clanUpgrades.put(5, new ClanUpgrades(2, 1, 4,2, 6000));
        clanUpgrades.put(6, new ClanUpgrades(2, 1, 5,3, 8000));
        clanUpgrades.put(7, new ClanUpgrades(2, 1, 6, 3,10000));
        clanUpgrades.put(8, new ClanUpgrades(3, 1, 7, 4,12000));
        clanUpgrades.put(9, new ClanUpgrades(3, 1, 8, 4,14000));
        clanUpgrades.put(10, new ClanUpgrades(3, 1, 9, 5,16000));
        clanUpgrades.put(11, new ClanUpgrades(4, 1, 10,5,18000));
        clanUpgrades.put(12, new ClanUpgrades(5, 1, 11, 6,20000));
        clanUpgrades.put(13, new ClanUpgrades(5, 1, 12,6,22000));
        clanUpgrades.put(14, new ClanUpgrades(5, 1, 13,6,25000));
        clanUpgrades.put(15, new ClanUpgrades(5, 1, 14,6,28000));
        clanUpgrades.put(16, new ClanUpgrades(5, 1, 15,6,31000));
        clanUpgrades.put(17, new ClanUpgrades(6, 1, 16,6,34000));
        clanUpgrades.put(18, new ClanUpgrades(6, 1, 17,6,37000));
        clanUpgrades.put(19, new ClanUpgrades(6, 1, 18,6,40000));
        clanUpgrades.put(20, new ClanUpgrades(6, 1, 19,6,43000));
        clanUpgrades.put(21, new ClanUpgrades(6, 1, 20,6,46000));
        clanUpgrades.put(22, new ClanUpgrades(6, 1, 21,6,50000));
    }

    public static void announceAboutNewPerks(ServerTeam team, int oldLevel, int newLevel){
        TeamService teamService = ServiceManager.getService(TeamService.class);
        ClanUpgrades oldPerks = getAvailablePerks(oldLevel);
        ClanUpgrades newPerks = getAvailablePerks(newLevel);
        for(ClanUpgradeType type : ClanUpgradeType.values()){
            int oldT = oldPerks.getUpgradeLevel(type);
            int newT = newPerks.getUpgradeLevel(type);
            if(oldT < newT){
                if(oldT == type.defValue){
                    teamService.messageMembers(team, TeamMessages.format("§6Team upgrade unlocked: §a{0}", type.getString(newT)) , true);
                }
                else {
                    teamService.messageMembers(team, TeamMessages.format("§6Team upgrade level increased: §a{0}", type.getString(newT)) , true);
                }
            }
        }
    }

    public static ClanUpgrades getAvailablePerks(int level){
        int closestLevel = 0;
        for (Integer i : clanUpgrades.keySet()){
            int c = i.compareTo(level);
            if(c <= 0){
                closestLevel = i;
            }
        }
        return clanUpgrades.get(closestLevel);
    }
}
