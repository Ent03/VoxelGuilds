package com.entity999.teams;

import com.entity999.core.services.ServiceManager;
import com.entity999.teams.lang.TeamMessages;
import org.bukkit.entity.Player;

import java.util.*;


public class TeamMember {
    private static HashMap<TeamRank, List<TeamPermission>> definedPermissions = new HashMap<>();
    static {
        definedPermissions.put(TeamRank.MEMBER, Collections.emptyList());
        definedPermissions.put(TeamRank.MANAGER, Arrays.asList(TeamPermission.KICK_MEMBERS, TeamPermission.INVITE_MEMBERS,
                TeamPermission.MANAGE_ALLIANCES, TeamPermission.SET_BASE, TeamPermission.WITHDRAW_BANK));
        definedPermissions.put(TeamRank.LEADER, Collections.singletonList(TeamPermission.ALL));
    }

    private TeamRank rank;
    private Set<TeamPermission> explicitPermissions = new HashSet<>();

    public TeamMember(TeamRank rank){
        this.rank = rank;
    }

    public boolean checkPermission(Player player, TeamPermission permission){
        if(!hasPermission(permission)){
            ServiceManager.getService(TeamService.class).messagePlayer(player,
                    TeamMessages.format(TeamMessages.CLAN_NEED_SPECIFIC_PERMISSION, permission.getKey()));
            return false;
        }
        return true;
    }

    public Set<TeamPermission> getExplicitPermissions() {
        return explicitPermissions;
    }

    public Set<String> getAllPermissionKeys() {
        HashSet<String> keys = new HashSet<>();
        for(TeamPermission permission : explicitPermissions) keys.add(permission.getKey());
        return keys;
    }

    public void setExplicitPermissions(Set<TeamPermission> permissions){
        this.explicitPermissions = permissions;
    }

    public TeamRank getRank() {
        return rank;
    }

    public void setRank(TeamRank rank){
        if(this.rank.ordinal() < rank.ordinal()){
            for(TeamPermission definedPerm : definedPermissions.get(rank)){
                addExplicitPermission(definedPerm);
            }
        }
        this.rank = rank;
    }

    public boolean hasPermission(TeamPermission permission){
        if(explicitPermissions.contains(TeamPermission.ALL)) return true;
        return explicitPermissions.contains(permission);
    }

    public boolean addExplicitPermission(TeamPermission permission){
        return explicitPermissions.add(permission);
    }

    public boolean removeExplicitPermission(TeamPermission permission){
        return explicitPermissions.remove(permission);
    }
}
