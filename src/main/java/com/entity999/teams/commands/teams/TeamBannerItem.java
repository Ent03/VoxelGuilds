package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CustomCommand;
import com.entity999.core.lang.VoxelMessages;
import com.entity999.core.services.CooldownManager;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.lang.TeamMessages;

import com.entity999.teams.TeamService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class TeamBannerItem extends CustomCommand {
    public TeamBannerItem() {
        super("get", "/c banner get", "Get your clan's banner as an item.");
        setPermission("core.team.banner");
    }

    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        Player player = (Player) commandSender;
        int cdLeft = ServiceManager.getService(CooldownManager.class).getCooldown("banner", player.getUniqueId());
        if(cdLeft > 0){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.format(VoxelMessages.CMD_COOLDOWN, cdLeft));
            return true;
        }
        ServerTeam team = TeamService.getPlayerTeam(player);
        if(team == null){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.JOIN_CLAN_FIRST);
            return false;
        }
        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(team.getBanner());
        for(ItemStack left : leftover.values()){
            player.getWorld().dropItemNaturally(player.getLocation(), left);
        }
        ServiceManager.getService(CooldownManager.class).addNewCooldown("banner", player.getUniqueId(), 60 * 60);
        if(!leftover.isEmpty()) ServiceManager.getService(TeamService.class).messagePlayer(player, VoxelMessages.INV_FULL_DROPPING_ITEM);
        else ServiceManager.getService(TeamService.class).messagePlayer(player, VoxelMessages.ITEM_ADDED_TO_INV);
        return true;
    }
}
