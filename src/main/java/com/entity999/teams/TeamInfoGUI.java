package com.entity999.teams;

import com.entity999.core.LibsPlugin;
import com.entity999.core.commands.CommandManager;
import com.entity999.core.gui.Chapter;
import com.entity999.core.gui.ChapterGUI;
import com.entity999.core.gui.events.GUIButtonClickEvent;
import com.entity999.core.services.ServiceManager;
import com.entity999.core.utils.ItemUtils;
import com.entity999.core.utils.Utils;

import com.entity999.teams.commands.teams.*;
import com.entity999.teams.lang.TeamMessages;

import com.entity999.teams.leaderboards.LeaderboardType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Set;
import java.util.UUID;

public class TeamInfoGUI extends ChapterGUI {
    private ServerTeam team;
    private Player player;
    private boolean otherTeamMode;
    public TeamInfoGUI(ServerTeam team, Player player) {
        super("none", null, true);
        getGuiEventBus().subscribe(GUIButtonClickEvent.class, this::buttonClicked);
        this.team = team;
        this.player = player;
        this.otherTeamMode = !team.getMemberList().contains(player.getUniqueId());
    }

    @Override
    public void onPageCreate(Inventory page, Chapter chapter) {
        super.onPageCreate(page, chapter);
        if(!chapter.getTitle().equals("members")) fillViewWithPanes(page);
    }

    private void openSettingsGUI(){
        int size = 27;
        Chapter sett = addChapter(size, "settings", "Clan Settings", true,true, player.getOpenInventory().getTopInventory());
        Inventory m = sett.getPage(0).gui;
        addButton("ff_enable", 11, Utils.createItem(Material.SHIELD, TeamMessages.CLAN_ENABLE_FF_GUI_NAME, TeamMessages.CLAN_ENABLE_FF_GUI_LORE), m);
        addButton("ff_disable", 13, Utils.createItem(Material.NETHERITE_AXE, TeamMessages.CLAN_DISABLE_FF_GUI_NAME, TeamMessages.CLAN_DISABLE_FF_GUI_LORE), m);
        addButton("togglechat", 15, Utils.createItem(Material.PAPER, TeamMessages.CLAN_TOGGLE_CHAT_GUI_NAME, TeamMessages.CLAN_TOGGLE_CHAT_GUI_LORE), m);
//        if(size == 36){
//            pageGUI.addButton("tglow", 21, com.entity999.core.utils.Utils.createItem(Material.BLAZE_POWDER, TeamMessages.CLAN_ENABLE_GLOW_GUI_NAME, TeamMessages.CLAN_ENABLE_GLOW_GUI_LORE), m);
//        }
        player.openInventory(sett.getPage(0).gui);
    }

    private void openManageGUI(Player player, String target){
        int size = player.hasPermission("core.team.spectate") ? 36 : 27;
        Chapter manage = addChapter(size, target, target, true, true, player.getOpenInventory().getTopInventory());
        Inventory m = manage.getPage(0).gui;
        addButton("kick_"+target, 11, Utils.createItem(Material.RED_WOOL, TeamMessages.CLAN_KICK_PLAYER_GUI_NAME, TeamMessages.CLAN_KICK_PLAYER_GUI_LORE), m);
        addButton("demote_"+target, 13, Utils.createItem(Material.WOODEN_SWORD, TeamMessages.CLAN_DEMOTE_PLAYER_GUI_NAME, TeamMessages.CLAN_DEMOTE_PLAYER_GUI_LORE), m);
        addButton("promote_"+target, 15, Utils.createItem(Material.SPECTRAL_ARROW, TeamMessages.CLAN_PROMOTE_PLAYER_GUI_NAME, TeamMessages.CLAN_PROMOTE_PLAYER_GUI_LORE), m);
        if(size == 36){
            addButton("spectate_"+target, 21, Utils.createItem(Material.TOTEM_OF_UNDYING, TeamMessages.CLAN_SPECTATE_PLAYER_GUI_NAME, TeamMessages.CLAN_SPECTATE_PLAYER_GUI_LORE), m);
        }
        player.openInventory(manage.getPage(0).gui);
    }

    public void openMembersMenu(){
        Chapter memberC = addChapter(54, "members", TeamMessages.CLAN_MEMBERS, true, true, player.getOpenInventory().getTopInventory());
        Set<UUID> members = team.getMemberList();


        for(UUID playerUUID : members){
            OfflinePlayer of = Bukkit.getOfflinePlayer(playerUUID);
            TeamRank rank = team.getPlayerRank(playerUUID);
            String rankString;
            if(rank == TeamRank.LEADER) rankString = TeamMessages.CLAN_LEADER;
            else if(rank == TeamRank.MANAGER) rankString = TeamMessages.CLAN_MANAGER;
            else rankString = TeamMessages.CLAN_MEMBER;
            String permString = TeamMessages.format(TeamMessages.CLAN_PERMISSIONS_LORE, String.join("/n§7- §b", team.getMember(of.getUniqueId()).getAllPermissionKeys()));
            LibsPlugin.globalStorage.getSkullFromDatabase(playerUUID.toString(), head -> {
                ItemUtils.modifyItem(head, of.getName() == null ? "Unknown name" : of.getName(), rankString+"/n"+permString);
                addButton("player_"+of.getName(), head, memberC);
            });
        }
        openPageFromChapter(0, player, memberC);
    }

    public void open(){
        Chapter chapter = addChapter(54, "main", TeamMessages.CLAN_INFO, true, true);
        Inventory mainInv = chapter.getPage(0).gui;

        if(!otherTeamMode){
            addButton("settings", 19, Utils.createItem(Material.WRITABLE_BOOK, TeamMessages.CLAN_SETTINGS, TeamMessages.CLAN_SETTINGS_GUI_LORE),
                    mainInv);
        }

        addButton("members", 21, Utils.createItem(Material.PLAYER_HEAD,TeamMessages.CLAN_MEMBERS, TeamMessages.CLAN_MEMBERS_GUI_LORE ), mainInv);

        addButton("xp", 23, Utils.createItem(Material.EXPERIENCE_BOTTLE, TeamMessages.CLAN_EXPERIENCE_INFO_NAME,
                TeamMessages.format(TeamMessages.CLAN_EXPERIENCE_INFO_LORE, team.getExperience(), team.getXPRemainingForNextLevel(), team.getLevel())), mainInv);


        addButton("rank", 25, Utils.createItem(Material.ENCHANTED_BOOK, TeamMessages.format(TeamMessages.CLAN_RANK_GUI_NAME,
                ServiceManager.getService(TeamService.class).getRankString(LeaderboardType.EXPERIENCE,team.getUuid().toString()),
                ServiceManager.getService(TeamService.class).getRankString(LeaderboardType.MONEY,team.getUuid().toString())),
                TeamMessages.CLAN_RANK_GUI_LORE), mainInv);

        openPageFromChapter(0, player, chapter);
    }


    public void buttonClicked(GUIButtonClickEvent event){
        if(event.getButtonName().equals("members")){
            openMembersMenu();
        }
        else if(event.getButtonName().startsWith("player_")){
            String pName = event.getButtonName().split("player_")[1];
            openManageGUI(event.getPlayer(), pName);
        }
        else if(event.getButtonName().startsWith("settings")){
            openSettingsGUI();
        }
        else if(event.getButtonName().startsWith("kick_")){
            String pName = event.getButtonName().split("kick_")[1];
            event.getPlayer().performCommand(CommandManager.getCustomCommandExecString(TeamKick.class) + " " + pName);
        }
        else if(event.getButtonName().startsWith("demote_")){
            String pName = event.getButtonName().split("demote_")[1];
            event.getPlayer().performCommand(CommandManager.getCustomCommandExecString(TeamMemberDemote.class) + " " + pName);
        }
        else if(event.getButtonName().startsWith("promote_")){
            String pName = event.getButtonName().split("promote_")[1];
            event.getPlayer().performCommand(CommandManager.getCustomCommandExecString(TeamMemberPromote.class) + " " + pName);
        }
        else if(event.getButtonName().startsWith("spectate_")){
            String pName = event.getButtonName().split("spectate_")[1];
            event.getPlayer().performCommand(CommandManager.getCustomCommandExecString(TeamSpectateMember.class) + " " + pName);
        }
        else if(event.getButtonName().equalsIgnoreCase("ff_enable")){
            event.getPlayer().performCommand(CommandManager.getCustomCommandExecString(TeamToggleFriendlyFire.class) + " on");
        }
        else if(event.getButtonName().equalsIgnoreCase("ff_disable")){
            event.getPlayer().performCommand(CommandManager.getCustomCommandExecString(TeamToggleFriendlyFire.class) + " off");
        }
        else if(event.getButtonName().equalsIgnoreCase("togglechat")){
            event.getPlayer().performCommand(CommandManager.getCustomCommandExecString(ToggleChat.class));
        }
        else if(event.getButtonName().equalsIgnoreCase("tglow")){
            event.getPlayer().performCommand(CommandManager.getCustomCommandExecString(TeamGlow.class));
        }
    }
}
