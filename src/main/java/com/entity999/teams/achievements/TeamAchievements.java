package com.entity999.teams.achievements;

import com.entity999.teams.lang.TeamMessages;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.HashMap;

public abstract class TeamAchievements {
    public static HashMap<TeamAchievementType, TeamAchievement> typeMap = new HashMap<>();

    public static TeamAchievement CREATE_TEAM;
    public static TeamAchievement FIRST_JOIN;
    public static TeamAchievement FIVE_JOIN;
    public static TeamAchievement TEN_JOIN;
    public static TeamAchievement TWENTY_JOIN;

    public static TeamAchievement FIND_TEN_DIAMONDS;
    public static TeamAchievement FIND_STACK_DIAMONDS;
    public static TeamAchievement FIND_128_DIAMONDS;
    public static TeamAchievement FIND_512_DIAMONDS;
    public static TeamAchievement FIND_1024_DIAMONDS;

    public static TeamAchievement FIND_STACK_COPPER;
    public static TeamAchievement FIND_128_COPPER;
    public static TeamAchievement FIND_512_COPPER;
    public static TeamAchievement FIND_1024_COPPER;


    public static TeamAchievement FIND_STACK_GOLD;
    public static TeamAchievement FIND_128_GOLD;
    public static TeamAchievement FIND_512_GOLD;
    public static TeamAchievement FIND_1024_GOLD;

    public static TeamAchievement FIND_TEN_NETHERITE;
    public static TeamAchievement FIND_STACK_NETHERITE;

    public static TeamAchievement KILL_50_ZOMBIES;
    public static TeamAchievement KILL_100_ZOMBIES;
    public static TeamAchievement KILL_200_ZOMBIES;
    public static TeamAchievement KILL_600_ZOMBIES;
    public static TeamAchievement KILL_1200_ZOMBIES;


    public static TeamAchievement KILL_30_SKELETONS;
    public static TeamAchievement KILL_60_SKELETONS;
    public static TeamAchievement KILL_120_SKELETONS;
    public static TeamAchievement KILL_500_SKELETONS;
    public static TeamAchievement KILL_1000_SKELETONS;

    public static TeamAchievement KILL_30_SPIDERS;
    public static TeamAchievement KILL_60_SPIDERS;
    public static TeamAchievement KILL_120_SPIDERS;
    public static TeamAchievement KILL_500_SPIDERS;
    public static TeamAchievement KILL_1000_SPIDERS;

    public static TeamAchievement KILL_20_CREEPERS;
    public static TeamAchievement KILL_40_CREEPERS;
    public static TeamAchievement KILL_80_CREEPERS;
    public static TeamAchievement KILL_250_CREEPERS;
    public static TeamAchievement KILL_500_CREEPERS;
    public static TeamAchievement KILL_1000_CREEPERS;

    public static TeamAchievement CREATE_VILLAGE;
    public static TeamAchievement KILL_DRAGON;

    public static TeamAchievement EARN_50000;
    public static TeamAchievement EARN_100000;
    public static TeamAchievement EARN_1000000;
    public static TeamAchievement EARN_5000000;
    public static TeamAchievement EARN_10000000;

    public static TeamAchievement OPEN_10_GRAVES;

    public static TeamAchievement OPEN_10_CRATES;
    public static TeamAchievement OPEN_20_CRATES;
    public static TeamAchievement OPEN_50_CRATES;
    public static TeamAchievement OPEN_100_CRATES;

    public static TeamAchievement SELL_128_PSHOP_ITEMS;
    public static TeamAchievement SELL_256_PSHOP_ITEMS;

    public static TeamAchievement CREATE_5_PLAYERWARPS;

    public static TeamAchievement SELL_10_AUCTION_ITEMS;
    public static TeamAchievement SELL_20_AUCTION_ITEMS;

    public static TeamAchievement WIN_10_EVENTS;
    public static TeamAchievement WIN_20_EVENTS;
    public static TeamAchievement WIN_60_EVENTS;

    public static void init(){
        CREATE_TEAM = new CreateTeamAchievement();
        FIRST_JOIN = new PlayerJoinAchievement(TeamAchievementType.FIRST_PLAYER_JOIN, TeamMessages.TEAM_ACHIEVEMENT_FIRST_MEMBER_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_FIRST_MEMBER_INFO, 1, null);
        FIVE_JOIN = new PlayerJoinAchievement(TeamAchievementType.FIVE_PLAYERS_JOIN, TeamMessages.TEAM_ACHIEVEMENT_FIVE_MEMBERS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_FIVE_MEMBERS_INFO, 5, TeamAchievementType.FIRST_PLAYER_JOIN);
        TEN_JOIN = new PlayerJoinAchievement(TeamAchievementType.TEN_PLAYERS_JOIN, TeamMessages.TEAM_ACHIEVEMENT_TEN_MEMBERS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_TEN_MEMBERS_INFO, 10, TeamAchievementType.FIVE_PLAYERS_JOIN);
        TWENTY_JOIN = new PlayerJoinAchievement(TeamAchievementType.TWENTY_PLAYERS_JOIN, TeamMessages.TEAM_ACHIEVEMENT_TWENTY_MEMBERS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_TWENTY_MEMBERS_INFO, 20, TeamAchievementType.TEN_PLAYERS_JOIN);


        FIND_TEN_DIAMONDS  = new CommonFindBlockAchievement(TeamAchievementType.FIND_TEN_DIAMONDS, TeamMessages.TEAM_ACHIEVEMENT_10_DIAMONDS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_10_DIAMONDS_INFO, 10,0,
                null,
                Material.DIAMOND_ORE,
                Material.DEEPSLATE_DIAMOND_ORE);
        FIND_STACK_DIAMONDS = new CommonFindBlockAchievement(TeamAchievementType.FIND_STACK_DIAMONDS, TeamMessages.TEAM_ACHIEVEMENT_STACK_DIAMONDS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_STACK_DIAMONDS_INFO, 64,10,
                TeamAchievementType.FIND_TEN_DIAMONDS,
                Material.DIAMOND_ORE,
                Material.DEEPSLATE_DIAMOND_ORE);
        FIND_128_DIAMONDS = new CommonFindBlockAchievement(TeamAchievementType.FIND_128_DIAMONDS, TeamMessages.TEAM_ACHIEVEMENT_128_DIAMONDS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_128_DIAMONDS_INFO, 128,64,
                TeamAchievementType.FIND_STACK_DIAMONDS,
                Material.DIAMOND_ORE,
                Material.DEEPSLATE_DIAMOND_ORE);
        FIND_512_DIAMONDS = new CommonFindBlockAchievement(TeamAchievementType.FIND_512_DIAMONDS, "Diamonds IV",
                TeamMessages.TEAM_ACHIEVEMENT_128_DIAMONDS_INFO, 512,128,
                TeamAchievementType.FIND_128_DIAMONDS,
                Material.DIAMOND_ORE,
                Material.DEEPSLATE_DIAMOND_ORE);
        FIND_1024_DIAMONDS = new CommonFindBlockAchievement(TeamAchievementType.FIND_1024_DIAMONDS, "Diamonds V",
                TeamMessages.TEAM_ACHIEVEMENT_128_DIAMONDS_INFO, 1024,512,
                TeamAchievementType.FIND_512_DIAMONDS,
                Material.DIAMOND_ORE,
                Material.DEEPSLATE_DIAMOND_ORE);


        FIND_STACK_GOLD = new CommonFindBlockAchievement(TeamAchievementType.FIND_STACK_GOLD, "Gold I",
                "Find 64 Gold ore as a team", 64,0,
                null,
                Material.GOLD_ORE,
                Material.DEEPSLATE_GOLD_ORE);
        FIND_128_GOLD = new CommonFindBlockAchievement(TeamAchievementType.FIND_128_GOLD, "Gold II",
                "Find 128 Gold ore as a team", 128,64,
                TeamAchievementType.FIND_STACK_GOLD,
                Material.GOLD_ORE,
                Material.DEEPSLATE_GOLD_ORE);
        FIND_512_GOLD = new CommonFindBlockAchievement(TeamAchievementType.FIND_512_GOLD, "Gold III",
                "Find 512 Gold ore as a team", 512,128,
                TeamAchievementType.FIND_128_GOLD,
                Material.GOLD_ORE,
                Material.DEEPSLATE_GOLD_ORE);
        FIND_1024_GOLD = new CommonFindBlockAchievement(TeamAchievementType.FIND_1024_GOLD, "Gold IV",
                "Find 1024 Gold ore as a team", 1024,512,
                TeamAchievementType.FIND_512_GOLD,
                Material.GOLD_ORE,
                Material.DEEPSLATE_GOLD_ORE);

        FIND_STACK_COPPER = new CommonFindBlockAchievement(TeamAchievementType.FIND_STACK_COPPER, "Copper I",
                "Find 64 Copper ore as a team", 64,0,
                null,
                Material.COPPER_ORE,
                Material.DEEPSLATE_COPPER_ORE);
        FIND_128_COPPER = new CommonFindBlockAchievement(TeamAchievementType.FIND_128_COPPER, "Copper II",
                "Find 128 Copper ore as a team", 128,64,
                TeamAchievementType.FIND_STACK_COPPER,
                Material.COPPER_ORE,
                Material.DEEPSLATE_COPPER_ORE);
        FIND_512_COPPER = new CommonFindBlockAchievement(TeamAchievementType.FIND_512_COPPER, "Copper III",
                "Find 512 Copper ore as a team", 512,128,
                TeamAchievementType.FIND_128_COPPER,
                Material.COPPER_ORE,
                Material.DEEPSLATE_COPPER_ORE);
        FIND_1024_COPPER = new CommonFindBlockAchievement(TeamAchievementType.FIND_1024_COPPER, "Copper IV",
                "Find 1024 Copper ore as a team", 1024,512,
                TeamAchievementType.FIND_512_COPPER,
                Material.COPPER_ORE,
                Material.DEEPSLATE_COPPER_ORE);


        FIND_TEN_NETHERITE = new CommonFindBlockAchievement(TeamAchievementType.FIND_TEN_NETHERITE, TeamMessages.TEAM_ACHIEVEMENT_10_NETHERITE_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_10_NETHERITE_INFO, 10,0,
                null,
                Material.ANCIENT_DEBRIS);
        FIND_STACK_NETHERITE = new CommonFindBlockAchievement(TeamAchievementType.FIND_32_NETHERITE, TeamMessages.TEAM_ACHIEVEMENT_32_NETHERITE_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_32_NETHERITE_INFO, 32,10,
                TeamAchievementType.FIND_TEN_NETHERITE,
                Material.ANCIENT_DEBRIS);
        FIND_STACK_NETHERITE = new CommonFindBlockAchievement(TeamAchievementType.FIND_STACK_NETHERITE, TeamMessages.TEAM_ACHIEVEMENT_64_NETHERITE_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_64_NETHERITE_INFO, 64,32,
                TeamAchievementType.FIND_32_NETHERITE,
                Material.ANCIENT_DEBRIS);

        KILL_50_ZOMBIES = new CommonMobKillAchievement(TeamAchievementType.KILL_50_ZOMBIES, TeamMessages.TEAM_ACHIEVEMENT_50_ZOMBIES_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_50_ZOMBIES_INFO, 50, 0,
                null,
                EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.HUSK, EntityType.ZOMBIFIED_PIGLIN);

        KILL_100_ZOMBIES = new CommonMobKillAchievement(TeamAchievementType.KILL_100_ZOMBIES, TeamMessages.TEAM_ACHIEVEMENT_100_ZOMBIES_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_100_ZOMBIES_INFO, 100, 50,
                TeamAchievementType.KILL_50_ZOMBIES,
                EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.HUSK,EntityType.ZOMBIFIED_PIGLIN);

        KILL_200_ZOMBIES = new CommonMobKillAchievement(TeamAchievementType.KILL_200_ZOMBIES, TeamMessages.TEAM_ACHIEVEMENT_200_ZOMBIES_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_200_ZOMBIES_INFO, 200, 100,
                TeamAchievementType.KILL_100_ZOMBIES,
                EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.HUSK,EntityType.ZOMBIFIED_PIGLIN);
        KILL_600_ZOMBIES = new CommonMobKillAchievement(TeamAchievementType.KILL_600_ZOMBIES, TeamMessages.TEAM_ACHIEVEMENT_600_ZOMBIES_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_600_ZOMBIES_INFO, 600, 200,
                TeamAchievementType.KILL_200_ZOMBIES,
                EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.HUSK,EntityType.ZOMBIFIED_PIGLIN);

        KILL_1200_ZOMBIES = new CommonMobKillAchievement(TeamAchievementType.KILL_1200_ZOMBIES, TeamMessages.TEAM_ACHIEVEMENT_1200_ZOMBIES_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_1200_ZOMBIES_INFO, 1200, 600,
                TeamAchievementType.KILL_600_ZOMBIES,
                EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.HUSK,EntityType.ZOMBIFIED_PIGLIN);

        KILL_20_CREEPERS = new CommonMobKillAchievement(TeamAchievementType.KILL_20_CREEPERS, TeamMessages.TEAM_ACHIEVEMENT_20_CREEPERS,
                TeamMessages.TEAM_ACHIEVEMENT_20_CREEPERS_INFO, 20, 0,
                null,
                EntityType.CREEPER);
        KILL_40_CREEPERS = new CommonMobKillAchievement(TeamAchievementType.KILL_40_CREEPERS, TeamMessages.TEAM_ACHIEVEMENT_40_CREEPERS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_40_CREEPERS_INFO, 40, 20,
                TeamAchievementType.KILL_20_CREEPERS,
                EntityType.CREEPER);
        KILL_80_CREEPERS = new CommonMobKillAchievement(TeamAchievementType.KILL_80_CREEPERS, TeamMessages.TEAM_ACHIEVEMENT_80_CREEPERS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_80_CREEPERS_INFO, 80, 40,
                TeamAchievementType.KILL_40_CREEPERS,
                EntityType.CREEPER);
        KILL_250_CREEPERS = new CommonMobKillAchievement(TeamAchievementType.KILL_250_CREEPERS, TeamMessages.TEAM_ACHIEVEMENT_250_CREEPERS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_250_CREEPERS_INFO, 250, 80,
                TeamAchievementType.KILL_80_CREEPERS,
                EntityType.CREEPER);
        KILL_500_CREEPERS = new CommonMobKillAchievement(TeamAchievementType.KILL_500_CREEPERS, TeamMessages.TEAM_ACHIEVEMENT_500_CREEPERS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_500_CREEPERS_INFO, 500, 250,
                TeamAchievementType.KILL_250_CREEPERS,
                EntityType.CREEPER);
        KILL_1000_CREEPERS = new CommonMobKillAchievement(TeamAchievementType.KILL_1000_CREEPERS, TeamMessages.TEAM_ACHIEVEMENT_1000_CREEPERS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_1000_CREEPERS_INFO, 1000, 500,
                TeamAchievementType.KILL_500_CREEPERS,
                EntityType.CREEPER);

        KILL_30_SPIDERS = new CommonMobKillAchievement(TeamAchievementType.KILL_30_SPIDERS, TeamMessages.TEAM_ACHIEVEMENT_30_SPIDERS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_30_SPIDERS_INFO, 30, 0,
                null,
                EntityType.SPIDER, EntityType.CAVE_SPIDER);
        KILL_60_SPIDERS = new CommonMobKillAchievement(TeamAchievementType.KILL_60_SPIDERS, TeamMessages.TEAM_ACHIEVEMENT_60_SPIDERS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_60_SPIDERS_LORE, 60, 30,
                TeamAchievementType.KILL_30_SPIDERS,
                EntityType.SPIDER, EntityType.CAVE_SPIDER);
        KILL_120_SPIDERS = new CommonMobKillAchievement(TeamAchievementType.KILL_120_SPIDERS, TeamMessages.TEAM_ACHIEVEMENT_120_SPIDERS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_120_SPIDERS_LORE, 120, 60,
                TeamAchievementType.KILL_60_SPIDERS,
                EntityType.SPIDER, EntityType.CAVE_SPIDER);
        KILL_500_SPIDERS = new CommonMobKillAchievement(TeamAchievementType.KILL_500_SPIDERS, TeamMessages.TEAM_ACHIEVEMENT_500_SPIDERS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_500_SPIDERS_LORE, 500, 120,
                TeamAchievementType.KILL_120_SPIDERS,
                EntityType.SPIDER, EntityType.CAVE_SPIDER);
        KILL_1000_SPIDERS = new CommonMobKillAchievement(TeamAchievementType.KILL_1000_SPIDERS, TeamMessages.TEAM_ACHIEVEMENT_1000_SPIDERS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_1000_SPIDERS_LORE, 1000, 500,
                TeamAchievementType.KILL_500_SPIDERS,
                EntityType.SPIDER, EntityType.CAVE_SPIDER);

        KILL_30_SKELETONS = new CommonMobKillAchievement(TeamAchievementType.KILL_30_SKELETONS, TeamMessages.TEAM_ACHIEVEMENT_30_SKELETONS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_30_SKELETONS_LORE, 30, 0,
                null,
                EntityType.SKELETON, EntityType.WITHER_SKELETON);

        KILL_60_SKELETONS = new CommonMobKillAchievement(TeamAchievementType.KILL_60_SKELETONS, TeamMessages.TEAM_ACHIEVEMENT_60_SKELETONS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_60_SKELETONS_LORE, 60, 30,
                TeamAchievementType.KILL_30_SKELETONS,
                EntityType.SKELETON, EntityType.WITHER_SKELETON);

        KILL_120_SKELETONS = new CommonMobKillAchievement(TeamAchievementType.KILL_120_SKELETONS, TeamMessages.TEAM_ACHIEVEMENT_120_SKELETONS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_120_SKELETONS_LORE, 120, 60,
                TeamAchievementType.KILL_60_SKELETONS,
                EntityType.SKELETON, EntityType.WITHER_SKELETON);

        KILL_500_SKELETONS = new CommonMobKillAchievement(TeamAchievementType.KILL_500_SKELETONS, TeamMessages.TEAM_ACHIEVEMENT_500_SKELETONS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_500_SKELETONS_LORE, 500, 120,
                TeamAchievementType.KILL_120_SKELETONS,
                EntityType.SKELETON, EntityType.WITHER_SKELETON);
        KILL_1000_SKELETONS = new CommonMobKillAchievement(TeamAchievementType.KILL_1000_SKELETONS, TeamMessages.TEAM_ACHIEVEMENT_1000_SKELETONS_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_1000_SKELETONS_LORE, 1000, 500,
                TeamAchievementType.KILL_500_SKELETONS,
                EntityType.SKELETON, EntityType.WITHER_SKELETON);
        KILL_DRAGON = new CommonMobKillAchievement(TeamAchievementType.KILL_DRAGON, TeamMessages.TEAM_ACHIEVEMENT_ENDER_DRAGON_1_NAME,
                TeamMessages.TEAM_ACHIEVEMENT_ENDER_DRAGON_1_LORE, 1, 0,
                null,
                EntityType.ENDER_DRAGON);


//        EARN_50000 = new CommonMoneyEarnAchievement(TeamAchievementType.EARN_50000, TeamMessages.TEAM_ACHIEVEMENT_MONEY_1_NAME, TeamMessages.TEAM_ACHIEVEMENT_MONEY_1_INFO, 50000, 0
//        ,null);
//
//        EARN_100000 = new CommonMoneyEarnAchievement(TeamAchievementType.EARN_100000, TeamMessages.TEAM_ACHIEVEMENT_MONEY_2_NAME, TeamMessages.TEAM_ACHIEVEMENT_MONEY_2_INFO, 100000, 50000
//                ,TeamAchievementType.EARN_50000);
//
//        EARN_1000000 = new CommonMoneyEarnAchievement(TeamAchievementType.EARN_1000000, TeamMessages.TEAM_ACHIEVEMENT_MONEY_3_NAME, TeamMessages.TEAM_ACHIEVEMENT_MONEY_3_LORE, 1000000, 100000
//                ,TeamAchievementType.EARN_100000);
//
//        EARN_5000000 = new CommonMoneyEarnAchievement(TeamAchievementType.EARN_5000000, TeamMessages.TEAM_ACHIEVEMENT_MONEY_4_NAME, TeamMessages.TEAM_ACHIEVEMENT_MONEY_4_LORE, 5000000, 1000000
//                ,TeamAchievementType.EARN_1000000);
//
//        EARN_10000000 = new CommonMoneyEarnAchievement(TeamAchievementType.EARN_10000000, TeamMessages.TEAM_ACHIEVEMENT_MONEY_5_LORE, TeamMessages.TEAM_ACHIEVEMENT_MONEY_5_LORE, 10000000, 5000000
//                ,TeamAchievementType.EARN_5000000);

        CREATE_VILLAGE = new VillageCreateAchievement();

//        OPEN_10_GRAVES = new GraveOpenAchievement(TeamAchievementType.OPEN_10_GRAVES, TeamMessages.TEAM_ACHIEVEMENT_GRAVES_1_NAME, TeamMessages.TEAM_ACHIEVEMENT_GRAVES_1_LORE,
//                10, 0, null);
//
//        OPEN_10_CRATES = new GraveOpenAchievement(TeamAchievementType.OPEN_10_CRATES, TeamMessages.TEAM_ACHIEVEMENT_CRATES_1_NAME, TeamMessages.TEAM_ACHIEVEMENT_CRATES_1_LORE,
//                10, 0, null);
//
//        OPEN_20_CRATES = new CrateOpenAchievement(TeamAchievementType.OPEN_20_CRATES, TeamMessages.TEAM_ACHIEVEMENT_CRATES_2_NAME, TeamMessages.TEAM_ACHIEVEMENT_CRATES_2_LORE,
//                20, 10, TeamAchievementType.OPEN_10_CRATES);
//        OPEN_50_CRATES = new CrateOpenAchievement(TeamAchievementType.OPEN_50_CRATES, TeamMessages.TEAM_ACHIEVEMENT_CRATES_3_NAME, TeamMessages.TEAM_ACHIEVEMENT_CRATES_3_LORE,
//                50, 20, TeamAchievementType.OPEN_20_CRATES);
//        OPEN_100_CRATES = new CrateOpenAchievement(TeamAchievementType.OPEN_100_CRATES, TeamMessages.TEAM_ACHIEVEMENT_CRATES_4_NAME, TeamMessages.TEAM_ACHIEVEMENT_CRATES_4_LORE,
//                100, 50, TeamAchievementType.OPEN_50_CRATES);
//
//
//        SELL_128_PSHOP_ITEMS = new SellShopItemsAchievement(TeamAchievementType.SELL_128_PSHOP_ITEMS, TeamMessages.TEAM_ACHIEVEMENT_PSHOP_SELL_1_NAME, TeamMessages.TEAM_ACHIEVEMENT_PSHOP_SELL_1_LORE,
//                128, 0, null);
//
//        SELL_256_PSHOP_ITEMS = new SellShopItemsAchievement(TeamAchievementType.SELL_256_PSHOP_ITEMS, TeamMessages.TEAM_ACHIEVEMENT_PSHOP_SELL_2_NAME, TeamMessages.TEAM_ACHIEVEMENT_PSHOP_SELL_2_LORE,
//                256, 128, TeamAchievementType.SELL_128_PSHOP_ITEMS);
//
//        CREATE_5_PLAYERWARPS = new CreatePlayerWarpAchievement(TeamAchievementType.CREATE_5_PLAYERWARPS, TeamMessages.TEAM_ACHIEVEMENT_PWARP_CREATE_1_NAME, TeamMessages.TEAM_ACHIEVEMENT_PWARP_CREATE_1_LORE,
//                5, 0, null);
//
//        SELL_10_AUCTION_ITEMS = new SellAuctionItemAchievement(TeamAchievementType.SELL_10_AUCTION_ITEMS, TeamMessages.TEAM_ACHIEVEMENT_AUCTION_SELL_1_NAME, TeamMessages.TEAM_ACHIEVEMENT_AUCTION_SELL_1_LORE,
//                10, 0, null);
//
//        SELL_20_AUCTION_ITEMS = new SellAuctionItemAchievement(TeamAchievementType.SELL_20_AUCTION_ITEMS, TeamMessages.TEAM_ACHIEVEMENT_AUCTION_SELL_2_NAME, TeamMessages.TEAM_ACHIEVEMENT_AUCTION_SELL_2_LORE,
//                20, 10, TeamAchievementType.SELL_10_AUCTION_ITEMS);
//
//
//        WIN_10_EVENTS = new EventWinAchievement(TeamAchievementType.WIN_10_EVENTS, TeamMessages.TEAM_ACHIEVEMENT_EVENTS_1_NAME, TeamMessages.TEAM_ACHIEVEMENT_EVENTS_1_LORE,
//                10, 0, null);
//
//        WIN_20_EVENTS = new EventWinAchievement(TeamAchievementType.WIN_20_EVENTS, TeamMessages.TEAM_ACHIEVEMENT_EVENTS_2_NAME, TeamMessages.TEAM_ACHIEVEMENT_EVENTS_2_LORE,
//                20, 10, TeamAchievementType.WIN_10_EVENTS);
//
//        WIN_60_EVENTS = new EventWinAchievement(TeamAchievementType.WIN_60_EVENTS, TeamMessages.TEAM_ACHIEVEMENT_EVENTS_3_NAME, TeamMessages.TEAM_ACHIEVEMENT_EVENTS_3_LORE,
//                60, 20, TeamAchievementType.WIN_20_EVENTS);

    }

    public static void addToMap(TeamAchievement achievement){
        typeMap.put(achievement.getType(), achievement);
    }
}
