package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CustomCommand;
import com.entity999.core.gui.Chapter;
import com.entity999.core.gui.ChapterGUI;
import com.entity999.core.gui.events.GUIButtonClickEvent;
import com.entity999.core.lang.VoxelMessages;
import com.entity999.core.services.ServiceManager;
import com.entity999.core.utils.ItemUtils;
import com.entity999.teams.TeamPublicity;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.ServerTeam;

import com.entity999.teams.TeamService;

import com.entity999.teams.leaderboards.LeaderboardType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_clans extends CustomCommand {
    public Command_clans() {
        super("clans", "/clans", "Displays all clans");
        setPermission("core.team");
    }

    public void onButtonClick(GUIButtonClickEvent event){
        if(event.getButtonName().startsWith("team_")){
            String name = event.getButtonName().split("team_")[1];
            event.getPlayer().performCommand("c info " + name);
        }
    }

    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        ChapterGUI chapterGUI = new ChapterGUI("asd", null, true);
        chapterGUI.getGuiEventBus().subscribe(GUIButtonClickEvent.class, this::onButtonClick);
        Chapter clans = chapterGUI.addChapter(54, TeamMessages.ACTIVE_CLANS, true, true);
        chapterGUI.openPageFromChapter(0, (Player)commandSender, clans);
        for(ServerTeam team : ServiceManager.getService(TeamService.class).getCachedActiveTeams()){
            ServerTeam targetTeam;
            ServerTeam onlineTeam = ServiceManager.getService(TeamService.class).getTeamFast(team.getUuid());
            targetTeam = onlineTeam != null ? onlineTeam : team;

            String publicity = targetTeam.getTeamSettings().getTeamPublicity().equals(TeamPublicity.PUBLIC) ? "§a"+ VoxelMessages.TRUE_WORD : "§c"+ VoxelMessages.FALSE_WORD;
            String online = targetTeam.isOnline() ? VoxelMessages.ONLINE + ": " + targetTeam.getOnlineMemberCount() : VoxelMessages.OFFLINE;
            String lore = TeamMessages.format(TeamMessages.CLANS_LIST_LORE,
                    ServiceManager.getService(TeamService.class).getRankString(LeaderboardType.EXPERIENCE, targetTeam.getUuid().toString()),
                    ServiceManager.getService(TeamService.class).getRankString(LeaderboardType.MONEY, targetTeam.getUuid().toString()), targetTeam.getMemberList().size(),
                    targetTeam.getLevel(), publicity, online);
            chapterGUI.addButton("team_"+targetTeam.getName(), ItemUtils.modifyItem(targetTeam.getBanner().clone(),
                    TeamMessages.format(TeamMessages.format(TeamMessages.CLAN, targetTeam.getName()), "§b"+targetTeam.getName()),
                    lore), clans);
        }
        return true;
    }
}
