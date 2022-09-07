package com.entity999.teams.achievements;

import com.entity999.core.services.ServiceManager;
import com.entity999.core.utils.VoxelBlockLookUp;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamService;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.List;

public class CommonFindBlockAchievement extends TeamAchievement{
    private int requiredBlocks;
    private int remainingOffset;
    private List<Material> materials;
    private TeamAchievementType achieveFirst;
    public CommonFindBlockAchievement(TeamAchievementType type, String name, String desc,int required,int remainingOffset,
                                      TeamAchievementType achieveFirst,
                                      Material... blockTypes) {
        super(type, name, desc);
        this.requiredBlocks = required;
        this.remainingOffset = remainingOffset;
        this.achieveFirst = achieveFirst;
        this.materials = Arrays.asList(blockTypes);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Material blockType = event.getBlock().getType();
        if(!materials.contains(blockType)) return;
        VoxelBlockLookUp.blockIsNatural(event.getBlock(), natural -> {
            if(!natural) return;
            ServerTeam team = TeamService.getPlayerTeam(event.getPlayer().getUniqueId());
            if(team == null) return;
            if(achieveFirst != null && !team.hasCompletedAchievement(achieveFirst)) return;
            if(team.hasCompletedAchievement(getType())) return;
            JSONObject object = team.getAchievementData(type);
            int found = ((Number) object.getOrDefault("found", 0)).intValue();
            showProgression(team, 1, requiredBlocks-found-remainingOffset, requiredBlocks);
            found++;
            if(found >= requiredBlocks-remainingOffset){
                setCompleted(team);
            }
            else {
                object.put("found", found);
            }
        });
    }
}
