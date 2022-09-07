package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.TeamPublicity;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.ServerTeam;

import com.entity999.teams.TeamService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamJoin extends CustomCommand {
    protected TeamJoin() {
        super("join","/c join <clan name>","Join a clan");
        setPermission("core.team");
    }
    @CommandParams(Sender = Sender.PLAYER, minArgs = 1)
    @Override
    public boolean onCommand(CommandSender commandSender, String s, String[] args) {
        Player player = (Player) commandSender;
        ServerTeam team = TeamService.getPlayerTeam(player);
        if(team != null){
            ServiceManager.getService(TeamService.class).messagePlayer(commandSender, TeamMessages.LEAVE_CLAN_FIRST);
            return true;
        }
        String teamName = args[0];
        ServerTeam serverTeam = ServiceManager.getService(TeamService.class).getTeamByNameFast(teamName);
        if(serverTeam == null){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_NOT_EXIST);
            return true;
        }
        if(serverTeam.getTotalMemberCount() >= TeamService.MAX_CLAN_SIZE){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_FULL);
            return true;
        }
        TeamPublicity teamPublicity = serverTeam.getTeamSettings().getTeamPublicity();
        if(teamPublicity.equals(TeamPublicity.INVITE_ONLY)){
            UUID targetUUID = ServiceManager.getService(TeamService.class).playerInvited(player.getUniqueId().toString(), teamName);
            if(targetUUID == null){
                ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_PLAYER_NOT_INVITED);
                return true;
            }
        }
        ServiceManager.getService(TeamService.class).addMemberToTeam(serverTeam.getUuid(), player);
        ServiceManager.getService(TeamService.class).messageMembers(serverTeam, TeamMessages.format(TeamMessages.PLAYER_JOINED_CLAN, player.getName()), true);
        ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.format(TeamMessages.CLAN_PLAYER_JOINED_MSG, serverTeam.getName()));
        return true;
    }
}
