package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.services.ServiceManager;
import com.entity999.shaded.nbtapi.NBTContainer;
import com.entity999.shaded.nbtapi.NBTItem;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.TeamPermission;
import com.entity999.teams.TeamService;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeamBannerImport extends CustomCommand {
    public TeamBannerImport() {
        super("import", "/c banner import <NBT>", "Import a banner from NBT tags, use a generator such as §bhttps://www.gamergeeks.nz/apps/minecraft/banner-maker. §7E.g /c banner import minecraft:white_banner{BlockEntityTag:{Patterns:[{Pattern:glb,Color:15}]}}");
        setPermission("core.team.banner");
    }

    @CommandParams(minArgs = 1)
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
        String[] typeSplit = args[0].split("\\{");
        Material material;
        String type;
        if(typeSplit.length == 0){
            type = args[0];
            material = Material.getMaterial(args[0]);
        }
        else {
            type = typeSplit[0].replace("minecraft:", "");
            material = Material.getMaterial(type.toUpperCase());
        }
        if(material == null){
            ServiceManager.getService(TeamService.class).messagePlayer(commandSender, TeamMessages.CLAN_BANNER_INVALID_NBT_VERSION);
            return true;
        }
        if(!material.name().toLowerCase().contains("banner")){
            ServiceManager.getService(TeamService.class).messagePlayer(commandSender, TeamMessages.CLAN_BANNER_INVALID_ITEM);
            return true;
        }
        ItemStack banner = new ItemStack(material);
        String nbt = args[0].split(type)[1];
        NBTContainer tags = new NBTContainer(nbt);
        for(String key : tags.getKeys()){
            if(!key.equalsIgnoreCase("BlockEntityTag")){
                ServiceManager.getService(TeamService.class).messagePlayer(commandSender, TeamMessages.CLAN_BANNER_INVALID_NBT);
                return true;
            }
        }
        NBTItem nbtItem = new NBTItem(banner);
        nbtItem.mergeCompound(tags);
        banner = nbtItem.getItem();
        banner.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        ItemMeta itemMeta = banner.getItemMeta();
        itemMeta.setDisplayName(TeamMessages.format(TeamMessages.CLAN_BANNER_OF, team.getName()));
        banner.setItemMeta(itemMeta);
        team.setBanner(banner);

        ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_BANNER_UPDATED);
        return true;
    }
}
