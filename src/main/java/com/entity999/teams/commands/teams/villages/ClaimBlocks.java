package com.entity999.teams.commands.teams.villages;

import com.entity999.core.commands.CustomCommand;
import com.entity999.core.services.ServiceManager;

import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamService;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.villages.ClanVillageClaim;
import com.entity999.teams.villages.VillageService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClaimBlocks extends CustomCommand {
    public ClaimBlocks() {
        super("claimblocks", "/village claimblocks", "Shows how many claim blocks your clan has left");
        setPermission("core.team");
    }

    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        Player player = (Player) commandSender;
        ServerTeam team = TeamService.getPlayerTeam(player);
        if(team == null){
            player.sendMessage(TeamMessages.JOIN_CLAN_FIRST);
            return true;
        }
        ClanVillageClaim village = (ClanVillageClaim) ServiceManager.getService(VillageService.class).getRegionManager().getByID(team.getUuid());
        int total = team.getMaxVillageClaimBlocks();
        int claimed = village != null ? village.getArea() : 0;
        int left = total - claimed;
        player.sendMessage(TeamMessages.format(TeamMessages.CLAN_VILLAGE_BLOCKS_LEFT,left));
        return true;
    }
}
