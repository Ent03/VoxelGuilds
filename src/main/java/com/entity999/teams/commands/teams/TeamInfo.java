package com.entity999.teams.commands.teams;

import com.entity999.teams.TeamMain;
import com.entity999.teams.TeamSQLStorage;
import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.gui.PageGUI;
import com.entity999.core.services.ServiceManager;
import com.entity999.core.utils.Utils;

import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamInfoGUI;
import com.entity999.teams.TeamService;
import com.entity999.teams.lang.TeamMessages;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;


public class TeamInfo extends CustomCommand {
    public TeamInfo() {
        super("info", "/c info", "Shows your clan's stats / info.");
        setPermission("core.team");
    }

    @CommandParams(Sender = Sender.PLAYER)
    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) {
        Player player = (Player)commandSender;
        if(args.length == 0 || !player.hasPermission("core.team.seeothers")){
            ServerTeam team = TeamService.getPlayerTeam(player);
            if(team == null){
                PageGUI pageGUI = new PageGUI(TeamMessages.CLAN_INFO, 6, true, "u.dont.have", true);
                pageGUI.addBorders(pageGUI.getMenuInventory(), 6*9);
                pageGUI.openInventory(player);
                player.playSound(player.getLocation(), Sound.EVENT_RAID_HORN, 0.5f, 0.5f);
                ItemStack item = Utils.createItem(Material.GOLD_BLOCK, TeamMessages.ABOUT_CLANS, TeamMessages.ABOUT_CLANS_LORE);
                item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                pageGUI.addButton("info", 22, item, pageGUI.getMenuInventory());
                return true;
            }
            new TeamInfoGUI(team, player).open();
        }
        else if(args.length == 1){
            String teamName = args[0];
            ServiceManager.getService(TeamService.class).getTeam(teamName).thenAccept(team -> {
                if(team == null){
                    player.sendMessage(TeamMessages.CLAN_NOT_EXIST);
                    return;
                }
                new TeamInfoGUI(team, player).open();
            });
        }
        return true;
    }
}
