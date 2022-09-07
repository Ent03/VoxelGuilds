package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.services.ServiceManager;
import com.entity999.core.utils.ChatUtils;
import com.entity999.core.utils.PlayerUtils;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.TeamPermission;
import com.entity999.teams.TeamService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeamInvite extends CustomCommand {
    protected TeamInvite() {
        super("invite","/c invite <player name>", "Invite a player to your clan.");
        setPermission("core.team");
    }
    @CommandParams(Sender = Sender.PLAYER, minArgs = 1)
    @Override
    public boolean onCommand(CommandSender commandSender, String s, String[] args) {
        Player player = (Player) commandSender;
        Player targetPlayer = Bukkit.getPlayerExact(args[0]);
        if(args[0].equalsIgnoreCase(commandSender.getName()) && !player.hasPermission("core.debug")){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_CANT_INVITE_SELF);
            return true;
        }
        if(!PlayerUtils.isOnline(targetPlayer, player)){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.PLAYER_OFFLINE);
            return true;
        }
        ServerTeam team = TeamService.getPlayerTeam(player);
        if(team == null){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.JOIN_CLAN_FIRST);
            return true;
        }
        if(!team.getMember(player.getUniqueId()).checkPermission(player, TeamPermission.INVITE_MEMBERS)){
            return true;
        }
        targetPlayer.spigot().sendMessage(ChatUtils.getClickableText(ServiceManager.getService(TeamService.class).getPrefix(),
                TeamMessages.format(TeamMessages.CLAN_INVITE_MSG_LINE1, ChatColor.AQUA + team.getName() + ChatColor.YELLOW),
                TeamMessages.format(TeamMessages.CLAN_INVITE_MSG_LINE2, team.getName()),
                TeamMessages.CLAN_INVITE_CLICK_TO_JOIN, TeamMessages.format(TeamMessages.CLAN_INVITE_JOIN_LINK, team.getName())));

        ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_PLAYER_INVITED);
        ServiceManager.getService(TeamService.class).addInvite(team, targetPlayer.getUniqueId().toString());
        return true;
    }
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> players = new ArrayList<>();
        for(Player p : PlayerUtils.getOnlinePlayers((Player)sender)) players.add(p.getName());
        return players;
    }
}
