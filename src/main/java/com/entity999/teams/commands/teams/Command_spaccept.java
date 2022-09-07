package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CustomCommand;
import com.entity999.core.services.RequestManager;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.services.SpectatorService;

import com.entity999.teams.TeamService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_spaccept extends CustomCommand {
    public Command_spaccept() {
        super("spaccept", "/spaccept <player<","Accept a spectate request");
        setPermission("core.team");
    }

    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args)  {
        if(args.length < 1) return false;
        Player player = (Player)commandSender;
        Player target = Bukkit.getPlayer(args[0]);
        if(target == null || !player.isOnline()) {
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.PLAYER_OFFLINE);
            return true;
        }
        if(!ServiceManager.getService(RequestManager.class).acceptRequest("spectate", player, target)){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_SPECTATE_NO_REQUEST);
            return true;
        }
        ServiceManager.getService(SpectatorService.class).addSpectator(target, player);
        ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.format(TeamMessages.CLAN_NOW_SPECTATING_INFORM, target.getName()));
        ServiceManager.getService(TeamService.class).messagePlayer(target, TeamMessages.format(TeamMessages.CLAN_NOW_SPECTATING_PLAYER, player.getName()));
        target.sendTitle(TeamMessages.CLAN_NOW_SPECTATE_ENTER_TITLE, TeamMessages.CLAN_NOW_SPECTATE_ENTER_SUBTITLE, 5, 100, 20);
        return true;
    }
}
