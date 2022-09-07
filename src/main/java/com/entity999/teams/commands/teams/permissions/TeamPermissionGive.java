package com.entity999.teams.commands.teams.permissions;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.TeamMember;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.ServerTeam;

import com.entity999.teams.TeamPermission;
import com.entity999.teams.TeamService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TeamPermissionGive extends CustomCommand {
    public TeamPermissionGive() {
        super("give", "/c permission give <member> <permission>", "Give a permission for your clan member");
        setPermission("core.team");
    }

    @CommandParams(minArgs = 2)
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        Player player = (Player) commandSender;
        ServerTeam team = TeamService.getPlayerTeam(player);
        if(team == null){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.JOIN_CLAN_FIRST);
            return true;
        }
        if(!team.getOwner().equals(player.getUniqueId())){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_OWNER_ONLY_COMMAND);
            return true;
        }
        TeamPermission permission = TeamPermission.getByKey(args[1]);
        if(permission == null){
            ServiceManager.getService(TeamService.class).messagePlayer(player, "§cUnknown permission name.");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[0]);
        if(target == null){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.PLAYER_NOT_ON_CLAN);
            return true;
        }
        TeamMember member = team.getMember(target.getUniqueId());
        if(member.hasPermission(permission)){
            ServiceManager.getService(TeamService.class).messagePlayer(player, "§cMember already has this permission!");
            return true;
        }
        else {
            member.addExplicitPermission(permission);
            ServiceManager.getService(TeamService.class).messagePlayer(player, "§7Permission §e" + permission.getKey() + "§7 added for §a" + target.getName());
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Player player = (Player) sender;
        ServerTeam team = TeamService.getPlayerTeam(player.getUniqueId());
        if(!team.getOwner().equals(player.getUniqueId())) return super.tabComplete(sender,alias,args);
        if(args.length == 1){
            ArrayList<String> names = team.getMembers().stream().map(OfflinePlayer::getName).collect(Collectors.toCollection(ArrayList::new));
            return StringUtil.copyPartialMatches(args[0], names, new ArrayList<>());
        }
        else if(args.length == 2){
            return StringUtil.copyPartialMatches(args[1], TeamPermission.getKeys(), new ArrayList<>());
        }
        return super.tabComplete(sender,alias,args);
    }
}
