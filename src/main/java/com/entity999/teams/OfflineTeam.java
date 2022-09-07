package com.entity999.teams;

import com.entity999.core.SQL.SQLUtils;
import com.entity999.core.SQL.serilization.*;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.Serializable;
import java.util.*;

@Data
public class OfflineTeam implements SaveableBody {
    @DataProperty(value = "owner", parser = UUID.class)
    protected UUID owner;

    @DataProperty(value = "uuid", parser = UUID.class)
    protected UUID uuid;

    protected String name;

    @DataProperty("members")
    protected final HashMap<UUID, TeamMember> members = new HashMap<>();

    @DataProperty(value = "allies", parser = Serializable.class)
    protected HashSet<UUID> allies = new HashSet<>();

    @DataIgnore
    private ServerTeam onlineTeam;

    public OfflineTeam(UUID uuid, String name, UUID owner) {
        this.uuid = uuid;
        this.owner = owner;
        this.name = name;
    }

    public void setOwner(UUID owner){
        this.owner = owner;
    }

    public void setOnlineTeam(ServerTeam onlineTeam) {
        this.onlineTeam = onlineTeam;
    }

    public ServerTeam getOnlineTeam() {
        return onlineTeam;
    }

    public boolean isOnline(){
        return onlineTeam != null;
    }

    public void addAlly(UUID ally){
        allies.add(ally);
    }

    public void setPlayerRank(UUID player, TeamRank rank){
        if(!members.containsKey(player)) return;
        members.get(player).setRank(rank);
    }


    public void removeAlly(UUID ally){
        allies.add(ally);
    }
    public boolean isAlly(UUID team){
        return allies.contains(team);
    }


    public void addMember(UUID uuid){
        if(members.containsKey(uuid)) return;
        members.put(uuid, new TeamMember(TeamRank.MEMBER));
    }

    public OfflineTeam(){

    }

    public List<OfflinePlayer> getMembers(){
        List<OfflinePlayer> players = new ArrayList<>();
        for(UUID uuid : members.keySet()){
            OfflinePlayer p  = Bukkit.getOfflinePlayer(uuid);
            players.add(p);
        }
        return players;
    }


    public void removePlayer(UUID uuid){
        members.remove(uuid);
    }

    @DataGetter("members")
    public String getMemberData(){
        JSONObject jsonObject = new JSONObject();
        for(UUID member : members.keySet()){
            TeamMember teamMember = members.get(member);
            JSONObject memberObj = new JSONObject();
            memberObj.put("rank", teamMember.getRank().name());
            JSONObject permissionObj = new JSONObject();
            for(TeamPermission permission : TeamPermission.values()){
                boolean has = teamMember.hasPermission(permission);
                if(has) permissionObj.put(permission.getKey(), true);
            }
            memberObj.put("permissions", permissionObj.toJSONString());
            jsonObject.put(member.toString(), memberObj.toJSONString());
        }
        return jsonObject.toJSONString();
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


    public Set<UUID> getMemberList(){
        return members.keySet();
    }

    public int getTotalMemberCount(){
        return members.size();
    }

}
