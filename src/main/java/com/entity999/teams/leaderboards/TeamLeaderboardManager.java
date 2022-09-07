package com.entity999.teams.leaderboards;

import com.entity999.teams.TeamMain;
import com.entity999.teams.TeamSQLStorage;

import com.entity999.teams.ServerTeam;
import org.bukkit.Bukkit;

import java.util.*;

public class TeamLeaderboardManager {
    private HashMap<LeaderboardType, TeamLeaderBoards> leaderboards;
    public TeamLeaderboardManager(){
        this.leaderboards = new HashMap<>();
        leaderboards.put(LeaderboardType.MONEY, new TeamLeaderBoards(LeaderboardType.MONEY));
        leaderboards.put(LeaderboardType.EXPERIENCE, new TeamLeaderBoards(LeaderboardType.EXPERIENCE));
    }

    public TeamLeaderboardRank getRank(LeaderboardType type, String uuid){
        return leaderboards.get(type).getRank(uuid);
    }

    public TeamLeaderBoards getLeaderboard(LeaderboardType type){
        return leaderboards.get(type);
    }

    public void startTasks(){
        leaderboardTaskMoney();
        leaderboardTaskExp();
    }

    public void leaderboardTaskMoney(){
        Bukkit.getScheduler().runTaskTimerAsynchronously(TeamMain.instance, ()->{
            ArrayList<ServerTeam> map = TeamMain.getMainStorage().getAllTeamsList();
            HashMap<String, Double> wealths = new HashMap<>();
            for(ServerTeam t : map){
                wealths.put(t.getUuid().toString(), t.getWealth());
            }
            Iterator<Map.Entry<String, TeamLeaderboardRank>> rankIterator = leaderboards.get(LeaderboardType.MONEY).entrySet().iterator();
            while (rankIterator.hasNext()){
                Map.Entry<String, TeamLeaderboardRank> rank = rankIterator.next();
                boolean foundTeam = false;
                for(ServerTeam t : map){
                    if (t.getUuid().equals(rank.getValue().getUuid())) {
                        foundTeam = true;
                        break;
                    }
                }
                if(!foundTeam){
                    rankIterator.remove();
                }
            }
            Comparator<ServerTeam> comparator = Comparator.comparingDouble(t -> wealths.get(t.getUuid()));
            map.sort(comparator.reversed());
            int rank = 1;
            for(ServerTeam team : map){
                leaderboards.get(LeaderboardType.MONEY).setRank(team.getUuid().toString(),
                        new TeamLeaderboardRank(rank++, wealths.get(team.getUuid()), team.getName(), team.getUuid().toString(), team.getLevel(), team.getExperience()));
            }
        }, 0, 20 * 60);
    }


    public void leaderboardTaskExp(){
        Bukkit.getScheduler().runTaskTimerAsynchronously(TeamMain.instance, ()->{
            ArrayList<ServerTeam> map = TeamMain.getMainStorage().getAllTeamsList();

            Iterator<Map.Entry<String, TeamLeaderboardRank>> rankIterator = leaderboards.get(LeaderboardType.EXPERIENCE).entrySet().iterator();
            while (rankIterator.hasNext()){
                Map.Entry<String, TeamLeaderboardRank> rank = rankIterator.next();
                boolean foundTeam = false;
                for(ServerTeam t : map){
                    if (t.getUuid().equals(rank.getValue().getUuid())) {
                        foundTeam = true;
                        break;
                    }
                }
                if(!foundTeam){
                    rankIterator.remove();
                }
            }
            Comparator<ServerTeam> comparator = Comparator.comparingInt(ServerTeam::getExperience);
            map.sort(comparator.reversed());
            int rank = 1;
            for(ServerTeam team : map){
                leaderboards.get(LeaderboardType.EXPERIENCE).setRank(team.getUuid().toString(),
                        new TeamLeaderboardRank(rank++, team.getExperience(), team.getName(), team.getUuid().toString(), team.getLevel(), team.getExperience()));
            }
        }, 0, 20 * 60);
    }
}
