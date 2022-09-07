package com.entity999.teams.commands.teams.villages;

import com.entity999.core.commands.CustomCommand;
import com.entity999.core.lang.VoxelMessages;
import com.entity999.core.services.ServiceManager;

import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamService;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.villages.ClanVillageClaim;
import com.entity999.teams.villages.VillageService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteVillage extends CustomCommand {
    public DeleteVillage() {
        super("delete", "/village delete", "Delete your clan's village. Note: Achievements will not be deleted and");
        setPermission("core.team");
    }

    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        Player player = (Player) commandSender;
        ServerTeam team = TeamService.getPlayerTeam(player);
        if(team == null){
            ServiceManager.getService(VillageService.class).messagePlayer(player, TeamMessages.JOIN_CLAN_FIRST);
            return true;
        }
        if(!team.getOwner().equals(player.getUniqueId())){
            ServiceManager.getService(VillageService.class).messagePlayer(player, TeamMessages.CLAN_OWNER_ONLY_COMMAND);
            return true;
        }
        ClanVillageClaim claim = (ClanVillageClaim) ServiceManager.getService(VillageService.class).getRegionManager().getByID(team.getUuid());
        if(claim == null){
            ServiceManager.getService(VillageService.class).messagePlayer(player, TeamMessages.CLAN_NO_VILLAGE_CREATED);
            return true;
        }
        if(args.length == 0){
            ServiceManager.getService(VillageService.class).messagePlayer(player, TeamMessages.CLAN_VILLAGE_DELETION_CONFIRM);
            return true;
        }
        else if(args.length == 1 && args[0].equalsIgnoreCase(VoxelMessages.CONFIRM)){
            ServiceManager.getService(VillageService.class).deleteVillage(player, team);
        }
        return true;
    }
}
