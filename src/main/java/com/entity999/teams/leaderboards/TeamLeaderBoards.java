package com.entity999.teams.leaderboards;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TeamLeaderBoards {
    private LeaderboardType type;
    private HashMap<String, TeamLeaderboardRank> leaderboards;
    public TeamLeaderBoards(LeaderboardType type){
        this.type = type;
        this.leaderboards = new LinkedHashMap<>();
    }

    public void setRank(String team, TeamLeaderboardRank rank){
        leaderboards.put(team, rank);
    }

    public TeamLeaderboardRank getRank(String uuid){
        return leaderboards.get(uuid);
    }

    public Set<Map.Entry<String, TeamLeaderboardRank>> entrySet(){
        return leaderboards.entrySet();
    }

}
