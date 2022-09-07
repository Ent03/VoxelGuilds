package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.services.RequestManager;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.lang.TeamMessages;

import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamPermission;
import com.entity999.teams.TeamService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TeamAllianceCreate extends CustomCommand {
    public TeamAllianceCreate() {
        super("create", "/c alliance create <clan>", "Request an alliance with a clan");
        setPermission("core.team");
    }
    @CommandParams(Sender = Sender.PLAYER, minArgs = 1)
    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        Player p = (Player)commandSender;
        ServerTeam team = TeamService.getPlayerTeam(p);
        if(team == null){
            ServiceManager.getService(TeamService.class).messagePlayer(commandSender, TeamMessages.JOIN_CLAN_FIRST);
            return true;
        }
        if(!team.getMember(p.getUniqueId()).checkPermission(p, TeamPermission.MANAGE_ALLIANCES)){
            return true;
        }
        ServerTeam targetTeam = ServiceManager.getService(TeamService.class).getTeamByNameFast(args[0]);
        if(targetTeam == null){
            ServiceManager.getService(TeamService.class).messagePlayer(commandSender, TeamMessages.TARGET_CLAN_NOT_EXIST);
            return true;
        }
        if(targetTeam.getUuid().equals(team.getUuid())) {
            ServiceManager.getService(TeamService.class).messagePlayer(commandSender, TeamMessages.TARGET_CLAN_OWN_TEAM);
            return true;
        }
        if(targetTeam.isAlly(team.getUuid())){
            ServiceManager.getService(TeamService.class).messagePlayer(commandSender, TeamMessages.CLAN_ALREADY_ALLIED);
            return true;
        }
        List<Player> onlineManagers = targetTeam.getOnlineManagers();
        ServiceManager.getService(RequestManager.class).addNewRequest("team-ally", team.getUuid().toString(), targetTeam.getUuid().toString(), team.getUuid().toString(), p, 60, TeamMessages.format(TeamMessages.CLAN_ALLY_REQ_TIMEOUT, args[0]));
        for(Player op : onlineManagers){
            ServiceManager.getService(TeamService.class).messagePlayer(op, TeamMessages.format(TeamMessages.CLAN_ALLIANCE_REQUEST_INFO, team.getName(), team.getName()));
        }
        ServiceManager.getService(TeamService.class).messageMembers(team, TeamMessages.format(TeamMessages.CLAN_ALLIANCE_REQUEST_INFORM, p.getName(), targetTeam.getName()), true);
        return true;
    }
}
