package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CustomCommand;
import com.entity999.core.services.ServiceManager;

import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamBank extends CustomCommand {
    public TeamBank() {
        super("bank", "/clan bank", "Your clan's bank");
        setPermission("core.team.bank");

    }

    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        Player player = (Player)commandSender;
        ServerTeam team = TeamService.getPlayerTeam(player.getUniqueId());
        if(team == null){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.JOIN_CLAN_FIRST);
            return true;
        }
        /*
        TODO
        new GuildBankGUI(team, player).openBank(null);
        */
        return true;
    }
}
