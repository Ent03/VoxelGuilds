package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.services.RequestManager;
import com.entity999.core.services.ServiceManager;

import com.entity999.teams.events.AllyCreateEvent;
import com.entity999.teams.lang.TeamMessages;

import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_allyaccept extends CustomCommand {
    public Command_allyaccept() {
        super("allyaccept", "/allyaccept <clan>", "Accept an alliance request.");
        setPermission("core.team");
    }

    @CommandParams(Sender = Sender.PLAYER, minArgs = 1)
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        Player p = (Player) commandSender;
        ServerTeam team = TeamService.getPlayerTeam(p);
        if(team == null){
            ServiceManager.getService(TeamService.class).messagePlayer(p, TeamMessages.JOIN_CLAN_FIRST);
            return true;
        }
        if(!team.isAtLeastManager(p.getUniqueId())){
            ServiceManager.getService(TeamService.class).messagePlayer(p, TeamMessages.CLAN_NO_PERMISSION);
            return true;
        }
        ServerTeam targetTeam = ServiceManager.getService(TeamService.class).getTeamByNameFast(args[0]);
        if(targetTeam == null){
            ServiceManager.getService(TeamService.class).messagePlayer(p, TeamMessages.TARGET_CLAN_NOT_EXIST);
            return true;
        }
        if(ServiceManager.getService(RequestManager.class).acceptRequest("team-ally", team.getUuid().toString(), targetTeam.getUuid().toString())){
            AllyCreateEvent event1 = new AllyCreateEvent(targetTeam, team);
            AllyCreateEvent event2 = new AllyCreateEvent(team, targetTeam);
            Bukkit.getPluginManager().callEvent(event1);
            Bukkit.getPluginManager().callEvent(event2);

            if(event1.isCancelled() || event2.isCancelled()){
                return true;
            }

            targetTeam.addAlly(team.getUuid());
            team.addAlly(targetTeam.getUuid());
            ServiceManager.getService(TeamService.class).messageMembers(team, TeamMessages.format(TeamMessages.CLANS_ALLIED, targetTeam.getName()), true);
            ServiceManager.getService(TeamService.class).messageMembers(targetTeam, TeamMessages.format(TeamMessages.CLANS_ALLIED, team.getName()), true);
        }
        else {
            ServiceManager.getService(TeamService.class).messagePlayer(p, TeamMessages.CLAN_NO_ALLY_REQUEST);
        }
        return true;
    }
}
