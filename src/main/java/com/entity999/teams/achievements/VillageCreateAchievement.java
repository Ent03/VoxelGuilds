package com.entity999.teams.achievements;

import com.entity999.teams.events.VillageCreateEvent;
import com.entity999.teams.lang.TeamMessages;
import org.bukkit.event.EventHandler;

public class VillageCreateAchievement extends TeamAchievement{
    public VillageCreateAchievement() {
        super(TeamAchievementType.CREATE_VILLAGE, TeamMessages.TEAM_ACHIEVEMENT_VILLAGES, TeamMessages.TEAM_ACHIEVEMENT_VILLAGES_LORE);
    }
    @EventHandler
    public void onCreation(VillageCreateEvent event){
        if(event.getServerTeam().hasCompletedAchievement(getType())) return;
        setCompleted(event.getServerTeam());
    }
}
