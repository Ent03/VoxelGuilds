package com.entity999.teams.achievements;

import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.ServerTeam;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class TeamAchievementProgressBar extends BukkitRunnable {
    public static HashMap<UUID, TeamAchievementProgressBar> showingForTeams = new HashMap<>();
    private ServerTeam team;
    private int ticks = 0;
    private BossBar bossBar;
    private double needed;
    private double remaining;
    private double toAdd;

    private final double animationTime = 20;
    private final int tickSpeed = 1;
    private double addVar = 0;

    private TeamAchievementProgressBarType type;


    public TeamAchievementProgressBar(ServerTeam team, String achivementName, double toAdd, double remaining, double needed, TeamAchievementProgressBarType type){
        this.team = team;
        this.type = type;
        this.toAdd = toAdd;
        this.remaining = remaining;
        this.needed = needed;
        this.bossBar = Bukkit.createBossBar(TeamMessages.format(TeamMessages.CLAN_ACHIEVEMENT_PROGRESS, achivementName), BarColor.GREEN, BarStyle.SEGMENTED_20);
        bossBar.setVisible(true);
        bossBar.setProgress(1f - (remaining+0.0f) / needed);
        if(showingForTeams.containsKey(team.getUuid())) showingForTeams.get(team.getUuid()).finish();
        showingForTeams.put(team.getUuid(), this);
    }


    public static <T extends Comparable<T>> T clamp(T val, T min, T max) {
        if (val.compareTo(min) < 0) return min;
        else if (val.compareTo(max) > 0) return max;
        else return val;
    }

    public void finish(){
        this.cancel();
        bossBar.removeAll();
        showingForTeams.remove(team.getUuid());
    }

    public void setShowingForTeam(){
        for(Player player : team.getOnlineMembers()){
            bossBar.addPlayer(player);
        }
    }

    @Override
    public void run() {
        ticks++;
        if(ticks == 1){
            if(type == TeamAchievementProgressBarType.SHOW_ALL){
                setShowingForTeam();
            }
            else if(bossBar.getProgress() >= 0.25 && bossBar.getProgress() % 0.25 < 0.001){
                setShowingForTeam();
            }
        }
        if(ticks <= animationTime && type != TeamAchievementProgressBarType.MILESTONE){
            double stepSize = tickSpeed / animationTime;
            addVar += toAdd * stepSize;
            double progress = 1f - (remaining+0.0f-addVar) / needed;
            progress = clamp(progress, 0d, 1d);
            bossBar.setProgress(progress);
        }
        else if(ticks > 60){
            finish();
        }
    }
}
