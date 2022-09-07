package com.entity999.teams.commands.teams.leaderboard;

import com.entity999.core.books.BookManager;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.services.ServiceManager;
import com.entity999.core.utils.CurrencyFormat;

import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamService;

import com.entity999.teams.leaderboards.LeaderboardType;
import com.entity999.teams.leaderboards.TeamLeaderBoards;
import com.entity999.teams.leaderboards.TeamLeaderboardRank;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeamLeaderboardMoney extends CustomCommand {
    public TeamLeaderboardMoney(){
        super("money", "/teamtop money", "Team leaderboards by money");
        setPermission("core.team");
    }

    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        Player p = (Player) commandSender;
        ServerTeam team = TeamService.getPlayerTeam(p.getUniqueId());
        List<String> lines = new ArrayList<>();
        lines.add(TeamMessages.CLAN_LEADERBOARDS);
        TeamLeaderBoards leaderboard = ServiceManager.getService(TeamService.class).getLeaderboard(LeaderboardType.MONEY);
        lines.add("");
        if(team != null) {
            lines.add(TeamMessages.format(TeamMessages.CLAN_RANK, ServiceManager.getService(TeamService.class).getRankString(LeaderboardType.MONEY, team.getUuid().toString())));
            lines.add("§0");
        }
        for(Map.Entry<String, TeamLeaderboardRank> entry : leaderboard.entrySet()){
            TeamLeaderboardRank rank = entry.getValue();
            if(rank.getRank() > 50) break;
            lines.add(TeamMessages.format(TeamMessages.TEAM_LEADERBOARDS_MONEY_LINE, rank.getName(), ServiceManager.getService(TeamService.class).getRankString(LeaderboardType.MONEY, entry.getKey()), CurrencyFormat.format((long) rank.getValue())));
        }
        ServiceManager.getService(BookManager.class).openBookRaw(p, p, Lists.partition(lines, 13), "");
        return true;
    }
}
