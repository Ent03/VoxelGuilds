package com.entity999.teams.achievements;

import com.entity999.core.services.ServiceManager;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamService;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.List;

public class CommonMobKillAchievement extends TeamAchievement{
    private int required;
    private int remainingOffset;
    private List<EntityType> types;
    private TeamAchievementType achieveFirst;
    public CommonMobKillAchievement(TeamAchievementType type, String name, String desc, int required, int remainingOffset,
                                    TeamAchievementType achieveFirst,
                                    EntityType... types) {
        super(type, name, desc);
        this.required = required;
        this.remainingOffset = remainingOffset;
        this.types = Arrays.asList(types);
        this.achieveFirst = achieveFirst;
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event){
        if(event.getEntity().getKiller() == null) return;
        Player dmger = event.getEntity().getKiller();
        EntityType mobType = event.getEntityType();
        if(types.contains(mobType)){
            ServerTeam team = TeamService.getPlayerTeam(dmger.getUniqueId());
            if(team == null) return;
            if(achieveFirst != null && !team.hasCompletedAchievement(achieveFirst)) return;
            if(team.hasCompletedAchievement(getType())) return;
            JSONObject object = team.getAchievementData(type);
            int found = ((Number) object.getOrDefault("killed", 0)).intValue();
            showProgression(team, 1, required-found-remainingOffset, required);
            found++;
            if(found >= required-remainingOffset){
                setCompleted(team);
            }
            else {
                object.put("killed", found);
            }
        }
    }
}
