package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.lang.VoxelMessages;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.TeamMain;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamPermission;
import com.entity999.teams.TeamService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamKick extends CustomCommand {
    public TeamKick() {
        super("kick", "/c kick <clan member>","Kick a player from your clan. Note: Only managers and the clan leader can kick players.");
        setPermission("core.team");
    }
    @CommandParams(Sender = Sender.PLAYER, minArgs = 1)
    @Override
    public boolean onCommand(CommandSender commandSender, String s, String[] args) {
        Player player = (Player)commandSender;
        if(commandSender.getName().equalsIgnoreCase(args[0])){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_CANT_KICK_SELF);
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[0]);
        if(target == null){
            ServiceManager.getService(TeamService.class).messagePlayer(player, VoxelMessages.PLAYER_NOT_JOINED);
        }
        ServerTeam team = TeamService.getPlayerTeam(player.getUniqueId());
        if(team == null){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.JOIN_CLAN_FIRST);
            return true;
        }

        if(!team.playerInTeam(target.getUniqueId())){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.PLAYER_NOT_ON_CLAN);
            return true;
        }
        if(!team.getMember(player.getUniqueId()).checkPermission(player, TeamPermission.KICK_MEMBERS)){
            return true;
        }
        if(team.isAtLeastManager(target.getUniqueId()) && !team.getOwner().equals(player.getUniqueId())){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_CANT_KICK_MANAGER);
            return true;
        }
        ServiceManager.getService(TeamService.class).messageMembers(team, TeamMessages.format(TeamMessages.PLAYER_KICKED_FROM_CLAN, target.getName()), true);
        if(target.isOnline()){
            ServiceManager.getService(TeamService.class).messagePlayer(target.getPlayer(), TeamMessages.CLAN_PLAYER_KICKED_MSG);
        }
        ServiceManager.getService(TeamService.class).leaveFromTeam(team.getUuid(), target.getUniqueId());
        return true;
    }
}
