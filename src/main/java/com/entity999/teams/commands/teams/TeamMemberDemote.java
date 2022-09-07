package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.lang.VoxelMessages;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamPermission;
import com.entity999.teams.TeamService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamMemberDemote extends CustomCommand {
    public TeamMemberDemote() {
        super("demote", "/c demote <player>", "Demote a member");
        setPermission("core.team");
    }

    @CommandParams(Sender = Sender.PLAYER, minArgs = 1)
    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        Player player = (Player)commandSender;
        if(commandSender.getName().equalsIgnoreCase(args[0])){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_CANT_DEMOTE_SELF);
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

        if(!team.getMember(player.getUniqueId()).checkPermission(player, TeamPermission.DEMOTE_MEMBERS)){
            return true;
        }
        if(!team.playerInTeam(target.getUniqueId())){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.PLAYER_NOT_ON_CLAN);
            return true;
        }
        if(!team.isAtLeastManager(target.getUniqueId())){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.PLAYER_NOT_ON_CLAN);
            return true;
        }
        team.demotePlayer(target.getUniqueId());
        ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.format(TeamMessages.CLAN_MEMBER_DEMOTED, args[0]));
        if(target.isOnline()) ServiceManager.getService(TeamService.class).messagePlayer(target.getPlayer(), TeamMessages.CLAN_MEMBER_DEMOTED_MSG);
        return true;
    }
}
