package com.entity999.teams;


import com.entity999.core.LibsPlugin;
import com.entity999.core.SQL.serilization.*;
import com.entity999.core.utils.BukkitSerialization;

import com.entity999.teams.achievements.TeamAchievement;
import com.entity999.teams.achievements.TeamAchievementData;
import com.entity999.teams.achievements.TeamAchievementType;
import com.entity999.teams.upgrades.ClanUpgradeManager;
import com.entity999.teams.upgrades.ClanUpgradeType;

import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;


public class ServerTeam extends OfflineTeam {
    @DataProperty("settings")
    private TeamSettings teamSettings;

    @DataIgnore
    private boolean hasVillage = false;

    @DataIgnore
    private boolean hasClanVillagers = false;

    @DataProperty(value = "banner", parser = ItemStack.class)
    private ItemStack banner;

    private int coins;

    @DataProperty("bases")
    private final HashMap<String, Location> teamBases = new HashMap<>();

    @DataProperty(value = "storage", parser = Inventory.class)
    private Inventory storage;

    @DataIgnore
    private final HashSet<UUID> onlineMembers = new HashSet<>();


    private int experience = 0;

    /*
    Aliases, if the name was changed by an administrator and then someone tried to join the team with the old name.
    Aliases are not saved.
    */

    @DataIgnore
    private ArrayList<String> aliases = new ArrayList<>();

    private HashMap<TeamAchievementType, TeamAchievementData> achievements = new HashMap<>();

    private static final LinkedHashMap<Integer, Integer> experienceToLevelMap = new LinkedHashMap<>();

    static {
        experienceToLevelMap.put(0, 1);
        experienceToLevelMap.put(50, 2);
        experienceToLevelMap.put(100, 3);
        experienceToLevelMap.put(175, 4);
        experienceToLevelMap.put(300, 5);
        experienceToLevelMap.put(500, 6);
        experienceToLevelMap.put(1000, 7);
        experienceToLevelMap.put(1500, 8);
        experienceToLevelMap.put(2000, 9);
        experienceToLevelMap.put(3000, 10);
        experienceToLevelMap.put(4000, 11);
        experienceToLevelMap.put(5000, 12);
        experienceToLevelMap.put(6000, 13);
        experienceToLevelMap.put(7000, 13);
        experienceToLevelMap.put(8000, 14);
        experienceToLevelMap.put(9000, 15);
        experienceToLevelMap.put(10000, 16);
        experienceToLevelMap.put(11000, 17);
        experienceToLevelMap.put(12000, 18);
        experienceToLevelMap.put(13000, 19);
        experienceToLevelMap.put(15000, 20);
        experienceToLevelMap.put(17500, 21);
        experienceToLevelMap.put(20000, 22);
    }

    public ServerTeam(UUID uuid, String name, UUID owner, TeamSettings teamSettings, int coins) {
        this();
        this.uuid = uuid;
        this.owner = owner;
        this.coins = coins;
        this.aliases = new ArrayList<>();
        this.teamSettings = teamSettings;
        setName(name);
    }

    public ServerTeam(){
        setOnlineTeam(this);
    }

    public void addOnlineMember(UUID uuid){
        onlineMembers.add(uuid);
    }
    public void removeOnlineMember(UUID uuid){
        onlineMembers.remove(uuid);
    }


    public void setExperience(int experience){
        this.experience = experience;
    }

    public double addExperience(int amount){
        int oldLevel = getLevel();
        new TeamXPBar(this, oldLevel, amount).runTaskTimerAsynchronously(TeamMain.instance, 1, 1);
        this.experience += amount;
        return this.experience;
    }

    //Active == not afk
    public int getOnlineActiveMembers(){
        int size = 0;
        for(UUID uuid : onlineMembers){
//            User user = Main.getInstance().essentialsAPI.getUser(uuid);
//            if(user == null) continue; //went offline?
//            if(user.isAfk()) continue;
            size++;
        }
        return size;
    }

    public double getBankCapacity(){
        return ClanUpgradeManager.getAvailablePerks(getLevel()).getUpgradeLevel(ClanUpgradeType.BANK_CAPACITY) * (40000 * getLevel() / 3f);
    }

    public int getXPRemainingForNextLevel(){
        return getXPNeededForNextLevel() - experience;
    }

    public int getXPNeededForNextLevel(){
        int xp = Integer.MAX_VALUE;
        for (Map.Entry<Integer, Integer> entry : experienceToLevelMap.entrySet()){
            int c = entry.getKey().compareTo(experience);
            if(c > 0){
                xp = entry.getKey();
                break;
            }
        }
        return xp;
    }

    public int getExperience() {
        return experience;
    }

    public int getOnlineMemberCount() {
        return onlineMembers.size();
    }

    public void setStorage(Inventory storage) {
        this.storage = storage;
    }

    public Inventory getStorage() {
        return storage;
    }

    public void setBase(String name, Location location){
        this.teamBases.put(name, location);
    }

    public int getAmountOfBases(){
        return teamBases.size();
    }

    public List<String> getBaseNames(){
        return new ArrayList<>(teamBases.keySet());
    }

    public Location deleteBase(String name){
        return teamBases.remove(name);
    }
    public boolean hasBase(String name){
        return teamBases.containsKey(name);
    }

    public Location getTeamBase(String name) {
        return teamBases.get(name);
    }

    @DataGetter("bases")
    public String serializeBases(){
        JSONObject jsonObject = new JSONObject();
        for(Map.Entry<String, Location> base : teamBases.entrySet()){
            jsonObject.put(base.getKey(), BukkitSerialization.getStringFromLocation(base.getValue()));
        }
        return jsonObject.toJSONString();
    }

    @DataSetter("bases")
    public void deserializeBases(String data){
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(data);
            for(Object baseName : jsonObject.keySet()){
                teamBases.put((String) baseName, BukkitSerialization.getLocationFromString((String) jsonObject.get(baseName)));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Just data!
     * Does NOT get updated during the server is on! Get an updated value from TeamCoinRegistry
     */
    public int getCoins(){
        return coins;
    }

    public void setCoins(int amount){
        this.coins = amount;
    }

    public void setHasVillage(boolean hasVillage) {
        this.hasVillage = hasVillage;
    }

    public void setHasClanVillagers(boolean hasClanVillagers) {
        this.hasClanVillagers = hasClanVillagers;
    }

    public boolean hasClanVillagers() {
        return hasClanVillagers;
    }

    public boolean hasVillage() {
        return hasVillage;
    }

    public void setBanner(ItemStack itemStack){
        this.banner = itemStack;
    }

    public ItemStack getBanner() {
        return banner != null ? banner : new ItemStack(Material.GREEN_BANNER);
    }

    public boolean hasBanner(){
        return banner != null;
    }


    public boolean isOnline() {
        return getOnlineMemberCount() > 0;
    }

    public boolean playerInTeam(UUID uuid){
        return members.containsKey(uuid);
    }


    public void promotePlayer(UUID uuid){
        members.get(uuid).setRank(members.get(uuid).getRank() == TeamRank.MEMBER ? TeamRank.MANAGER : TeamRank.LEADER);
    }

    public void demotePlayer(UUID uuid){
        members.get(uuid).setRank(members.get(uuid).getRank() == TeamRank.MANAGER ? TeamRank.MEMBER : TeamRank.MANAGER);
    }

    public int getMaxVillageClaimBlocks(){
        return ClanUpgradeManager.getAvailablePerks(getLevel()).getUpgradeLevel(ClanUpgradeType.TOWN_CLAIM_BLOCKS);
    }

    public int getLevelXP(int experience){
        int exp = 0;
        for (Integer i : experienceToLevelMap.keySet()){
            int c = i.compareTo(experience);
            if(c <= 0){
                exp = i;
            }
        }
        return exp;
    }

    public int getLevel(){
        return experienceToLevelMap.getOrDefault(getLevelXP(experience), Collections.max(experienceToLevelMap.keySet()));
    }

    public TeamSettings getTeamSettings() {
        return teamSettings;
    }

    @DataGetter("settings")
    public String getSettinsSerialized(){
        return TeamSettings.serialize(getTeamSettings());
    }

    @DataSetter("settings")
    public void deserializeSettings(String data) throws ParseException {
        this.teamSettings = TeamSettings.fromJsonObject((JSONObject) new JSONParser().parse(data));
    }

    public TeamRank getPlayerRank(UUID player){
        return members.get(player).getRank();
    }

    public TeamMember getMember(UUID member){
        return members.get(member);
    }

    public boolean isAtLeastManager(UUID player){
        return members.get(player).getRank().ordinal() > 0;
    }

    @DataSetter("members")
    public void setPlayerRanks(String data) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(data);
        for(Object uuid : object.keySet()){
            JSONObject settings = (JSONObject) parser.parse((String) object.get(uuid));
            TeamRank teamRank;
            //Legacy check
            if(settings.containsKey("manager")){
                teamRank = (boolean) settings.get("manager") ? TeamRank.MANAGER : TeamRank.MEMBER;
            }
            else {
                teamRank = TeamRank.valueOf(settings.get("rank").toString().toUpperCase());
            }
            TeamMember member = new TeamMember(teamRank);
            if(settings.containsKey("permissions")){
                JSONObject permissionObj = (JSONObject) parser.parse(settings.get("permissions").toString());
                Set<TeamPermission> permissions = new HashSet<>();
                if((boolean) permissionObj.getOrDefault(TeamPermission.ALL.getKey(), false)){
                    permissions.add(TeamPermission.ALL);
                }
                else {
                    for(TeamPermission permission : TeamPermission.values()){
                        if((boolean) permissionObj.getOrDefault(permission.getKey(), false)) {
                            permissions.add(permission);
                        }
                    }
                }
                member.setExplicitPermissions(permissions);
            }
            members.put(UUID.fromString(uuid.toString()), member);
        }
        members.get(owner).setRank(TeamRank.LEADER);
    }



    public TeamAchievementData getAchievementData(TeamAchievementType type){
        if(!achievements.containsKey(type)){
            TeamAchievementData data = new TeamAchievementData(new JSONObject());
            achievements.put(type, data);
            return data;
        }
        return achievements.get(type);
    }

    @DataGetter("achievements")
    public String serializeAchievements(){
        JSONObject jsonObject = new JSONObject();
        for(Map.Entry<TeamAchievementType, TeamAchievementData> entry : achievements.entrySet()){
            jsonObject.put(entry.getKey().name(), entry.getValue().toJSONString());
        }
        return jsonObject.toJSONString();
    }

    @DataSetter("achievements")
    public void setAchievements(String data) throws ParseException {
        TeamAchievement.setAchievementsForTeam(this, data);
    }

    public void addAchievement(TeamAchievementType type, TeamAchievementData data){
        achievements.put(type, data);
    }

    public boolean hasCompletedAchievement(TeamAchievementType type){
        return achievements.containsKey(type) && achievements.get(type).isCompleted();
    }

    public void playSoundForMembers(Sound sound, float volume, float pitch){
        for(Player player : getOnlineMembers()){
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public List<Player> getOnlineManagers(){
        ArrayList<Player> list = new ArrayList<>();
        for(UUID uuid : members.keySet()){
            if(!isAtLeastManager(uuid)) continue;
            Player op = Bukkit.getPlayer(uuid);
            if(op != null) list.add(op);
        }
        return list;
    }

    public List<UUID> getManagers(){
        ArrayList<UUID> list = new ArrayList<>();
        for(UUID uuid : members.keySet()) {
            if (!isAtLeastManager(uuid)) continue;
            list.add(uuid);
        }
        return list;
    }

    public List<Player> getOnlineMembers(){
        List<Player> players = new ArrayList<>();
        for(UUID uuid : members.keySet()){
            Player p = Bukkit.getPlayer(uuid);
            if(p != null){
                players.add(p.getPlayer());
            }
        }
        return players;
    }

    public List<Player> getOnlineMembers(Player player){
        List<Player> players = getOnlineMembers();
        players.remove(player);
        return players;
    }

    public double getWealth(){
        double b = 0;
        for(OfflinePlayer player : getMembers()){
            b += LibsPlugin.economy.getBalance(player);
        }
        return b;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        aliases.add(name);
    }


    public boolean nameEquals(String name){
        return aliases.contains(name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerTeam team = (ServerTeam) o;
        return uuid.equals(team.uuid) &&
                name.equals(team.name) &&
                aliases.equals(team.aliases);
    }
}
