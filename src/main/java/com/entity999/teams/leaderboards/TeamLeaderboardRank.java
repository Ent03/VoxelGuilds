package com.entity999.teams.leaderboards;

public class TeamLeaderboardRank{
    int rank, level, exp;
    String name, uuid;
    double value;

    public TeamLeaderboardRank(int rank, double value, String name, String uuid, int level, int exp) {
        this.rank = rank;
        this.value = value;
        this.name = name;
        this.level = level;
        this.exp = exp;
        this.uuid = uuid;
    }

    public int getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    public String getUuid() {
        return uuid;
    }

    public double getValue() {
        return value;
    }
    public int getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }
}
