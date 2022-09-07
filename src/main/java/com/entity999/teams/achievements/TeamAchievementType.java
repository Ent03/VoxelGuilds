package com.entity999.teams.achievements;

public enum TeamAchievementType {
    CREATE_TEAM(10),
    FIRST_PLAYER_JOIN(20),
    FIVE_PLAYERS_JOIN(200),
    TEN_PLAYERS_JOIN(400),
    TWENTY_PLAYERS_JOIN(800),

    FIND_TEN_DIAMONDS(10),
    FIND_STACK_DIAMONDS(50),
    FIND_128_DIAMONDS(75),
    FIND_512_DIAMONDS(200),
    FIND_1024_DIAMONDS(300),

    FIND_STACK_COPPER(10),
    FIND_128_COPPER(20),
    FIND_512_COPPER(60),
    FIND_1024_COPPER(130),

    FIND_STACK_GOLD(20),
    FIND_128_GOLD(40),
    FIND_512_GOLD(80),
    FIND_1024_GOLD(150),

    FIND_TEN_NETHERITE(100),
    FIND_32_NETHERITE(250),
    FIND_STACK_NETHERITE(500),
    KILL_50_ZOMBIES(50),
    KILL_100_ZOMBIES(100),
    KILL_200_ZOMBIES(200),
    KILL_600_ZOMBIES(300),
    KILL_1200_ZOMBIES(600),
    KILL_30_SKELETONS(30),
    KILL_60_SKELETONS(60),
    KILL_120_SKELETONS(120),
    KILL_500_SKELETONS(200),
    KILL_1000_SKELETONS(400),
    KILL_30_SPIDERS(30),
    KILL_60_SPIDERS(60),
    KILL_120_SPIDERS(120),
    KILL_500_SPIDERS(200),
    KILL_1000_SPIDERS(400),
    KILL_20_CREEPERS(30),
    KILL_40_CREEPERS(70),
    KILL_80_CREEPERS(140),
    KILL_250_CREEPERS(300),
    KILL_500_CREEPERS(500),
    KILL_1000_CREEPERS(1000),
    CREATE_VILLAGE(100),
    KILL_DRAGON(1000),
    SELL_128_PSHOP_ITEMS(100),
    SELL_256_PSHOP_ITEMS(200),
    SELL_10_AUCTION_ITEMS(100),
    SELL_20_AUCTION_ITEMS(200),
    WIN_10_EVENTS(200),
    WIN_20_EVENTS(400),
    WIN_60_EVENTS(600),
    OPEN_10_CRATES(50),
    OPEN_20_CRATES(100),
    OPEN_50_CRATES(250),
    OPEN_100_CRATES(500),
    OPEN_10_GRAVES(200),
    CREATE_5_PLAYERWARPS(100),
    COMPLETE_10_TRADES(100),
    CREATE_5_ALLIES(300),
    CREATE_1_ALLY(100),
    EARN_50000(100),
    EARN_100000(200),
    EARN_1000000(500),
    EARN_5000000(1000),
    EARN_10000000(2000);



    private final int expReward;
    TeamAchievementType(int expReward){
        this.expReward = expReward;
    }

    public int getExpReward() {
        return expReward;
    }
}
