package com.entity999.teams;

import com.entity999.core.CustomConfig;
import com.entity999.core.SQL.SQLStorage;
import com.entity999.core.SQL.SQLTableManager;

import com.entity999.core.SQL.SQLUtils;
import com.entity999.teams.villages.ClanVillageClaim;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TeamSQLStorage extends SQLStorage {
    public TeamSQLStorage(Plugin main, CustomConfig sqlSettings) {
        super(main, sqlSettings);
    }

    @Override
    public void createTables(Plugin plugin) throws SQLException {
        Connection connection = getConnection();
        addTable(connection, new SQLTableManager("teams", "PRIMARY KEY (uuid)","name text", "storage MEDIUMTEXT",
                "bases text", "members longtext", "allies binary", "owner text",
                "uuid VARCHAR(36)", "datecreated text", "settings longtext", "villagename text DEFAULT ''",
                "villagecorners text DEFAULT ''", "villageworld text DEFAULT ''", "banner text DEFAULT ''",
                "clanvillagers longtext DEFAULT ''", "coins int(11) DEFAULT 0", "experience int(11) DEFAULT 1", "achievements longtext"));
        addTable(connection, new SQLTableManager("teamusers", "PRIMARY KEY(user)", "user VARCHAR(36)", "team VARCHAR(36)"));
        closeConnection(connection);
    }

    public void saveVillage(ClanVillageClaim area, boolean delete){
        Bukkit.getScheduler().runTaskAsynchronously(main, ()->{
            Connection connection = null;
            try {
                connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE teams SET villagename = ?,villagecorners = ?,villageworld = ? WHERE uuid = ?");
                if(delete){
                    statement.setString(1, "");
                    statement.setString(3, "");
                    statement.setString(2, "");
                    statement.setString(4, area.getOwningClan().toString());
                    statement.executeUpdate();
                    return;
                }
                statement.setString(1, area.getVillageName());
//                String hashes = String.join(",", area.getChunkHashes());
//                statement.setString(2, corners);
                String corners =
                        area.getCornerTop().getBlockX()+","+area.getCornerTop().getBlockY()+","+area.getCornerTop().getBlockZ()+";"+
                                area.getCornerBottom().getBlockX()+","+area.getCornerBottom().getBlockY()+","+area.getCornerBottom().getBlockZ();
                statement.setString(2, corners);
                statement.setString(3, area.getWorld().getName());
                statement.setString(4, area.getOwningClan().toString());
                statement.executeUpdate();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                try { if(connection != null) closeConnection(connection); } catch (SQLException e){ e.printStackTrace(); }
            }
        });
    }

    public void getAllVillages(Collection<ServerTeam> teams, Consumer<HashSet<ClanVillageClaim>> areas){
        Bukkit.getScheduler().runTaskAsynchronously(main, ()->{
            HashSet<ClanVillageClaim> clanVillageClaims = new HashSet<>();
            Connection connection = null;
            try {
                connection = getConnection();
                for(ServerTeam team : teams){
                    PreparedStatement statement = connection.prepareStatement("SELECT villagename,villagecorners,villageworld FROM teams WHERE uuid = ?");
                    statement.setString(1, team.getUuid().toString());
                    ResultSet rs = statement.executeQuery();
                    rs.next();
                    String vWorld = rs.getString("villageworld");
                    if(vWorld == null || vWorld.isEmpty()) continue;
                    World world = Bukkit.getWorld(rs.getString("villageworld"));
                    String[] corners = rs.getString("villagecorners").split(";");
                    String[] corner1Split = corners[0].split(",");
                    String[] corner2Split = corners[1].split(",");
                    org.bukkit.util.Vector corner1 = new org.bukkit.util.Vector(Integer.parseInt(corner1Split[0]),
                            Integer.parseInt(corner1Split[1]),Integer.parseInt(corner1Split[2]));
                    org.bukkit.util.Vector corner2 = new Vector(Integer.parseInt(corner2Split[0]),
                            Integer.parseInt(corner2Split[1]),Integer.parseInt(corner2Split[2]));
                    ClanVillageClaim clanVillageClaim = new ClanVillageClaim(corner2,corner1,world,team.getUuid(), rs.getString("villagename"));
                    clanVillageClaims.add(clanVillageClaim);
                }
            }
            catch (Exception e){
                e.printStackTrace();

            }
            finally {
                areas.accept(clanVillageClaims);
                try { if(connection != null) closeConnection(connection); } catch (SQLException e){ e.printStackTrace(); }
            }
        });
    }

    public ArrayList<ServerTeam> getAllTeamsList(){
        Connection connection = null;
        ArrayList<ServerTeam> teamSet = new ArrayList<>();
        try {
            connection = getConnection();
            PreparedStatement teamStatement = connection.prepareStatement("SELECT * FROM teams");
            ResultSet teamResults = teamStatement.executeQuery();
            while (teamResults.next()){
                teamSet.add(getTeamSync(UUID.fromString(teamResults.getString("uuid"))));
            }
            return teamSet;
        }
        catch (Exception e){
            e.printStackTrace();
            return teamSet;
        }
        finally {
            try { if(connection != null) closeConnection(connection); } catch (SQLException e){ e.printStackTrace(); }
        }
    }

    public HashSet<ServerTeam> getAllTeamsSet(){
        Connection connection = null;
        HashSet<ServerTeam> teamSet = new HashSet<>();
        try {
            connection = getConnection();
            PreparedStatement teamStatement = connection.prepareStatement("SELECT * FROM teams");
            ResultSet teamResults = teamStatement.executeQuery();
            while (teamResults.next()){
                teamSet.add(getTeamSync(UUID.fromString(teamResults.getString("uuid"))));
            }
            return teamSet;
        }
        catch (Exception e){
            e.printStackTrace();
            return teamSet;
        }
        finally {
            try { if(connection != null) closeConnection(connection); } catch (SQLException e){ e.printStackTrace(); }
        }
    }

    public CompletableFuture<ServerTeam> getTeam(String name){
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement teamStatement = connection.prepareStatement("SELECT * FROM teams WHERE name = ?");
            teamStatement.setString(1, name);
            ResultSet teamResults = teamStatement.executeQuery();
            if(!teamResults.next()){
                return null;
            }
            return getTeam(UUID.fromString(teamResults.getString("uuid")));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try { if(connection != null) closeConnection(connection); } catch (SQLException e){ e.printStackTrace(); }
        }
        return null;
    }

    public void getPlayerTeamUUID(UUID playerUUID, Consumer<UUID> teamUUID){
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT team FROM teamusers WHERE uuid = ?");
            statement.setString(1, playerUUID.toString());
            ResultSet results = statement.executeQuery();
            if(!results.next()){
                teamUUID.accept(null);
                return;
            }
            String uuid = results.getString("team");
            if(uuid.isEmpty()){
                teamUUID.accept(null);
                return;
            }
            teamUUID.accept(UUID.fromString(uuid));
        }
        catch (Exception e){
            e.printStackTrace();
            teamUUID.accept(null);
        }
        finally {
            try { if(connection != null) closeConnection(connection); } catch (SQLException e){ e.printStackTrace(); }
        }
    }


    public void deleteTeam(OfflineTeam team){
        getTableManager("teams").deleteBody(team);
    }

    private void removeAllyFromTeamAllies(Connection connection, String team, List<String> targets){
        try {
            for(String target : targets){
                PreparedStatement teamStatement = connection.prepareStatement("SELECT allies FROM teams WHERE uuid = ?");
                teamStatement.setString(1, target);
                ResultSet team1 = teamStatement.executeQuery();
                if(!team1.next()) return;
                List<String> team1Allies = SQLUtils.getStringListOfString(team1.getString("allies"));
                team1Allies.remove(team);

                PreparedStatement update1 = connection.prepareStatement("UPDATE teams SET allies = ? WHERE uuid = ?");
                update1.setString(1, SQLUtils.createStringOf(team1Allies));
                update1.setString(2, target);
                update1.executeUpdate();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public void saveTeam(ServerTeam team) {
        asyncThread.newTask(()->{
            TeamMain.instance.getLogger().info("TEAM SAVED " + team.getName());
            getTableManager("teams").saveBody(team);
//            List<String> insertKeys = Arrays.asList("name", "storage", "base", "members", "owner", "uuid", "datecreated", "settings");
//            List<String> updateKeys = Arrays.asList("storage", "base", "members", "owner", "settings", "allies", "banner",
//                    "coins", "experience", "achievements");
//
//            String memberData = team.getMemberData();
//            String owner = team.getOwner().toString();
//            String settings = TeamSettings.serialize(team.getTeamSettings());
//            String bases = team.serializeBases();
//
//            List<Object> insertValues = Arrays.asList(team.getName(), bases,
//                    memberData, owner, team.getUuid(),DateUtils.getCurrentDate(), settings);
//
//            String banner = team.hasBanner() ? BukkitSerialization.itemToBase64(team.getBanner()) : "";
//
//            List<Object> updateValues = Arrays.asList(BukkitSerialization.itemStackArrayToBase64(team.getStorageContents()),
//                    bases, memberData, owner, settings, String.join(",", team.getAllies()),banner, team.getCoins(), team.getExperience(), team.serializeAchievements());
//
//            insertOrUpdate("teams", "id", insertKeys, insertValues, updateKeys, updateValues);
        });
    }

    public CompletableFuture<ServerTeam> getTeam(UUID id){
        CompletableFuture<ServerTeam> future = new CompletableFuture<>();
        asyncThread.newTask(()->{
           future.complete(getTeamSync(id));
        });
        return future;
    }

    public CompletableFuture<OfflineTeam> getOfflineTeam(UUID id){
        return CompletableFuture.supplyAsync(()-> getTableManager("teams").getBody(id.toString(), OfflineTeam.class));
    }

    public CompletableFuture<OfflineTeam> getOfflineTeam(String name){
        return CompletableFuture.supplyAsync(()-> {
            String id = (String) getValue("uuid", "teams", "name", name);
            if(id == null){
                return null;
            }
            return getTableManager("teams").getBody(id, OfflineTeam.class);
        });
    }

    public ServerTeam getTeamSync(UUID id) {
        return getTableManager("teams").getBody(id.toString(), ServerTeam.class);
//        JSONParser parser = new JSONParser();
//        JSONObject settings = (JSONObject) parser.parse(resultSet.getString("settings"));
//        TeamSettings teamSettings = TeamSettings.fromJsonObject(settings);
//        JSONObject object = (JSONObject) parser.parse(resultSet.getString("members"));
//        ArrayList<String> allies = new ArrayList(SQLUtils.getStringListOfString(resultSet.getString("allies")));
//        allies.removeIf(String::isEmpty);
//        ServerTeam team = new ServerTeam(UUID.fromString(resultSet.getString("uuid")),resultSet.getString("name"),
//                UUID.fromString(resultSet.getString("owner")), teamSettings, resultSet.getInt("coins"));
//        String bannerString = resultSet.getString("banner");
//        if(!bannerString.isEmpty()) team.setBanner(com.entity999.core.utils.BukkitSerialization.itemFromBase64(bannerString));
//        team.setHasVillage(!resultSet.getString("villagename").isEmpty());
//        team.setHasClanVillagers(!resultSet.getString("clanvillagers").isEmpty());
//        team.setAllies(allies);
//
//        team.setExperience(resultSet.getInt("experience"));
//        team.deserializeBases(resultSet.getString("base"));
//
//        TeamAchievement.setAchievementsForTeam(team, resultSet.getString("achievements"));
//        return team;
    }
}
