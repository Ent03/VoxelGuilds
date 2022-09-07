package com.entity999.teams.achievements;


import com.entity999.teams.events.TeamCreateEvent;
import com.entity999.teams.lang.TeamMessages;
import org.bukkit.event.EventHandler;

public class CreateTeamAchievement extends TeamAchievement{
    public CreateTeamAchievement() {
        super(TeamAchievementType.CREATE_TEAM, TeamMessages.CLAN_TEAM_CREATE_ACHIEVEMENT_NAME, TeamMessages.CLAN_TEAM_CREATE_ACHIEVEMENT_DESC);
    }


    @EventHandler
    public void onTeamCreated(TeamCreateEvent event){
        setCompleted(event.getServerTeam());
    }
}
