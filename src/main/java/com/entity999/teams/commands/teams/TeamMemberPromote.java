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

public class TeamMemberPromote extends CustomCommand {

    public TeamMemberPromote() {
        super("promote", "/c promote <clan member>", "Promote your clan member to a manager.");
        setPermission("core.team");
    }
    @CommandParams(Sender = Sender.PLAYER, minArgs = 1)
    @Override
    public boolean onCommand(CommandSender commandSender, String s, String[] args) {
        Player player = (Player)commandSender;
        Bukkit.getScheduler().runTaskAsynchronously(main, ()->{
            OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[0]);
            if(target == null){
                ServiceManager.getService(TeamService.class).messagePlayer(player, VoxelMessages.PLAYER_NOT_JOINED);
            }
            if(commandSender.getName().equalsIgnoreCase(args[0])){
                ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_CANT_PROMOTE_SELF);
                return;
            }
            ServerTeam team = TeamService.getPlayerTeam(player.getUniqueId());
            if(team == null){
                ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.JOIN_CLAN_FIRST);
                return;
            }
            if(!team.getMember(player.getUniqueId()).checkPermission(player, TeamPermission.PROMOTE_MEMBERS)){
                return;
            }
            ServerTeam targetTeam = TeamService.getPlayerTeam(target.getUniqueId());
            if(!team.equals(targetTeam)){
                ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.PLAYER_NOT_ON_CLAN);
                return;
            }

            if(team.isAtLeastManager(target.getUniqueId())){
                ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_ALREADY_PROMOTED);
                return;
            }
            team.promotePlayer(target.getUniqueId());
            ServiceManager.getService(TeamService.class).messagePlayer(player,  TeamMessages.format(TeamMessages.CLAN_MEMBER_PROMOTED, args[0]));
            if(target.isOnline()){
                ServiceManager.getService(TeamService.class).messagePlayer(target.getPlayer(), TeamMessages.CLAN_MEMBER_PROMOTED_MSG);
            }
        });
        return true;
    }
}
