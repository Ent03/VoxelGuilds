package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CustomCommand;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.TeamPermission;
import com.entity999.teams.TeamService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamBannerReset extends CustomCommand {
    public TeamBannerReset() {
        super("reset", "/c banner reset", "Reset your clan's banner");
        setPermission("core.team.banner");
    }

    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        Player player = (Player) commandSender;
        ServerTeam team = TeamService.getPlayerTeam(player);
        if(team == null){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.JOIN_CLAN_FIRST);
            return true;
        }
        if(!team.getMember(player.getUniqueId()).checkPermission(player, TeamPermission.CHANGE_BANNER)){
            return true;
        }
        team.setBanner(null);
        ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_BANNER_RESET);
        return true;
    }
}
