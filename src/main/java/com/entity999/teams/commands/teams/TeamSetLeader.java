package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamRank;
import com.entity999.teams.lang.TeamMessages;

import com.entity999.teams.TeamService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamSetLeader extends CustomCommand {
    public TeamSetLeader() {
        super("setleader", "/c setleader <member>", "Set your clan member as the clan leader");
        setPermission("core.teams");
    }

    @CommandParams(minArgs = 1, Sender = Sender.PLAYER)
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        String targetName = args[0];
        if(targetName.equalsIgnoreCase(commandSender.getName())){
            ServiceManager.getService(TeamService.class).messagePlayer(commandSender, TeamMessages.CLAN_CANT_PROMOTE_SELF);
            return true;
        }
        Player player = (Player) commandSender;
        ServerTeam team = TeamService.getPlayerTeam(player);
        if(team == null){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.JOIN_CLAN_FIRST);
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if(target == null || !target.hasPlayedBefore()){
            ServiceManager.getService(TeamService.class).messagePlayer(commandSender, TeamMessages.PLAYER_NOT_JOINED);
            return true;
        }
        if(!team.getMemberList().contains(target.getUniqueId())){
            ServiceManager.getService(TeamService.class).messagePlayer(commandSender, TeamMessages.PLAYER_NOT_ON_CLAN);
            return true;
        }
        if(!team.getOwner().equals(player.getUniqueId())){
            ServiceManager.getService(TeamService.class).messagePlayer(commandSender, TeamMessages.CLAN_OWNER_ONLY_COMMAND);
            return true;
        }
        team.setPlayerRank(target.getUniqueId(), TeamRank.LEADER);
        team.setOwner(target.getUniqueId());
        ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.format(TeamMessages.CLAN_PLAYER_SET_AS_OWNER, targetName));
        if(target.isOnline()){
            ServiceManager.getService(TeamService.class).messagePlayer(target.getPlayer(), TeamMessages.CLAN_PLAYER_SET_AS_OWNER_MSG);
        }
        return true;
    }
}
