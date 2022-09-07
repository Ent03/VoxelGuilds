package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.services.ServiceManager;
import com.entity999.core.utils.ChatUtils;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.TeamPermission;
import com.entity999.teams.TeamService;

import com.entity999.teams.upgrades.ClanUpgradeManager;
import com.entity999.teams.upgrades.ClanUpgradeType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.UUID;

public class TeamSetBase extends CustomCommand {
    protected TeamSetBase() {
        super("setbase", "Set your current clan base location for your members to teleport to.", "/c setbase <name>", Collections.singletonList("sb"));
        setPermission("core.team.setb");
    }
    @CommandParams(Sender = Sender.PLAYER)
    @Override
    public boolean onCommand(CommandSender commandSender, String s, String[] args) {
        Player player = (Player) commandSender;
        ServerTeam team = TeamService.getPlayerTeam(player);
         if(team == null){
             ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.JOIN_CLAN_FIRST);
             return true;
         }
         if(!team.getMember(player.getUniqueId()).checkPermission(player, TeamPermission.SET_BASE)){
             return true;
         }
        String baseName;
        if(args.length == 0 || ClanUpgradeManager.getAvailablePerks(team.getLevel()).getUpgradeLevel(ClanUpgradeType.MAX_BASES) <= 1){
            baseName = "base";
        }
        else {
            baseName = args[0];
        }
        int maxBases = ClanUpgradeManager.getAvailablePerks(team.getLevel()).getUpgradeLevel(ClanUpgradeType.MAX_BASES);
        if(!team.hasBase(baseName)
                && team.getAmountOfBases() >= maxBases){
            ServiceManager.getService(TeamService.class).messagePlayer(player,
                    TeamMessages.format(TeamMessages.TEAM_MAX_BASES_REACHED, maxBases));
            return true;
        }
        Location l = player.getLocation();
         int x = l.getBlockX();
         int z = l.getBlockZ();
         int y = 0;
         for(int py = l.getBlockY(); py > -64; py--){
             y = py;
             if(!player.getWorld().getBlockAt(x,py,z).getType().equals(Material.AIR)){
                 break;
             }
         }
         y += 1;
         Location newLoc = new Location(l.getWorld(), x,y,z, l.getYaw(), l.getPitch());
         newLoc.add(0.5,0,0.5);
         team.setBase(baseName, newLoc);
         ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.format("§aBase §7{0}§a set!", baseName));
         for(UUID member : team.getMemberList()){
             Player p = Bukkit.getPlayer(member);
             if(p != null && p.isOnline()){
                 p.spigot().sendMessage(ChatUtils.getClickableText(ServiceManager.getService(TeamService.class).getPrefix(),
                         TeamMessages.format(TeamMessages.CLAN_BASE_SET_LINE1, player.getName()),
                         TeamMessages.format(TeamMessages.CLAN_BASE_SET_LINE2, team.getName()),
                         TeamMessages.CLAN_BASE_SET_LINE3, TeamMessages.CLAN_BASE_SET_LINK +" " + baseName));
             }
         }
        return true;
    }
}
