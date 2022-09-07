package com.entity999.teams;

import java.util.ArrayList;
import java.util.List;

public enum TeamPermission {
    ALL("ALL"),
    KICK_MEMBERS("kickMembers"),
    CHANGE_BANNER("changeBanner"),
    INVITE_MEMBERS("inviteMembers"),
    WITHDRAW_BANK("withdrawFromBank"),
    CHANGE_FRIENDLYFIRE("changeFriendlyFire"),
    MANAGE_ALLIANCES("manageAlliances"),
    DEMOTE_MEMBERS("demoteMembers"),
    PROMOTE_MEMBERS("promoteMembers"),
    SET_BASE("setBase");

    String key;

    TeamPermission(String key){
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static List<String> getKeys(){
        List<String> list = new ArrayList<>();
        for(TeamPermission permission : TeamPermission.values()) list.add(permission.getKey());
        return list;
    }

    public static TeamPermission getByKey(String key){
        for(TeamPermission permission : TeamPermission.values()){
            if(permission.getKey().equalsIgnoreCase(key)) return permission;
        }
        return null;
    }
}
