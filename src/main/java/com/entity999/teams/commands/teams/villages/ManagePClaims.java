package com.entity999.teams.commands.teams.villages;

import com.entity999.core.commands.CustomCommand;
import com.entity999.core.services.ServiceManager;

import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamService;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.villages.ClanVillageClaim;
import com.entity999.teams.villages.PlayerManagerGUI;
import com.entity999.teams.villages.VillageService;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ManagePClaims extends CustomCommand {
    public ManagePClaims() {
        super("managepclaims", "/village managepclaims", "Manage the player created claims inside your village");
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
        ArrayList<Claim> claims = claim.getPlayerClaimsInside();
        PlayerManagerGUI playerManagerGUI = new PlayerManagerGUI(team);
        playerManagerGUI.openClaimsMenu(player, claims);
        return true;
    }
}
