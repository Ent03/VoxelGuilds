package com.entity999.teams.achievements;


import com.entity999.teams.events.PlayerJoinTeamEvent;
import org.bukkit.event.EventHandler;

public class PlayerJoinAchievement extends TeamAchievement{
    private int required;
    public TeamAchievementType achievementFirst;
    public PlayerJoinAchievement(TeamAchievementType type, String name, String desc, int required, TeamAchievementType achievementFirst) {
        super(type, name, desc);
        this.required = required;
        this.achievementFirst = achievementFirst;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinTeamEvent event){
        if(event.isCancelled() || event.getServerTeam().hasCompletedAchievement(getType())) return;
        if(achievementFirst != null && !event.getServerTeam().hasCompletedAchievement(achievementFirst)) return;
        if(required != 1) showProgression(event.getServerTeam(), 1, required-(event.getServerTeam().getTotalMemberCount()-1), required);
        if(event.getServerTeam().getTotalMemberCount() == required+1){
            setCompleted(event.getServerTeam());
        }
    }
}
