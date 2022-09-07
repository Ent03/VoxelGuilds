package com.entity999.teams;

import com.entity999.core.services.ServiceManager;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.upgrades.ClanUpgradeManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeamXPBar extends BukkitRunnable {
    private ServerTeam team;
    private int ticks = 0;
    private BossBar bossBar;
    private int xpNeeded;
    private int xpNeededForCurrent;
    private int xpRemaining;

    private int toAdd;

    private final double animationTime = 20;
    private final int tickSpeed = 1;

    private double addVar = 0;

    private boolean levelUp = false;
    private int levelNow;
    private int oldLevel;

    public TeamXPBar(ServerTeam team, int oldLevel, int add){
        this.team = team;
        this.toAdd = add;
        this.oldLevel = oldLevel;
        this.xpNeededForCurrent = team.getLevelXP(team.getExperience());
        this.xpRemaining = team.getXPRemainingForNextLevel();
        this.levelNow = team.getLevel();
        this.xpNeeded = team.getXPNeededForNextLevel()-xpNeededForCurrent;
        this.bossBar = Bukkit.createBossBar(TeamMessages.format(TeamMessages.CLANS_XP_BAR, toAdd, (int) xpRemaining, levelNow), BarColor.GREEN, BarStyle.SEGMENTED_20);
        bossBar.setVisible(true);
        bossBar.setProgress(1f - (xpRemaining+0.0f) / xpNeeded);
        //
    }


    public static <T extends Comparable<T>> T clamp(T val, T min, T max) {
        if (val.compareTo(min) < 0) return min;
        else if (val.compareTo(max) > 0) return max;
        else return val;
    }


    @Override
    public void run() {
        ticks++;
        if(ticks == 1){
            for(Player player : team.getOnlineMembers()){
                bossBar.addPlayer(player);
            }
        }
        if(ticks <= animationTime && !levelUp){
            double stepSize = tickSpeed / animationTime;
            addVar += toAdd * stepSize;
            int xpRemainingTitle = (int) (xpRemaining+0.0f-addVar);
            xpRemainingTitle = clamp(xpRemainingTitle, 0, Integer.MAX_VALUE);
            bossBar.setTitle(TeamMessages.format(TeamMessages.CLANS_XP_BAR, toAdd, xpRemainingTitle, levelNow));
            double progress = 1f - (xpRemaining+0.0f-addVar) / xpNeeded;
            if(xpRemainingTitle == 0){
                bossBar.setProgress(1.0);
                int newLevel = team.getLevel();
                ServiceManager.getService(TeamService.class).messageMembers(team, TeamMessages.format(TeamMessages.CLAN_LEVEL_UP, newLevel), true);
                team.playSoundForMembers(Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0.5f);
                bossBar.setTitle(TeamMessages.CLAN_LEVEL_UP_BAR);
                progress = 1.0;
                levelUp = true;
                ClanUpgradeManager.announceAboutNewPerks(team, oldLevel, team.getLevel());
            }
            bossBar.setProgress(progress);
        }
        else if(ticks > 60){
            this.cancel();
            bossBar.removeAll();
        }
    }
}
