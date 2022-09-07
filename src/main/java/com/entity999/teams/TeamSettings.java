package com.entity999.teams;

import org.json.simple.JSONObject;

public class TeamSettings {
    private boolean friendlyFire;
    private TeamPublicity teamPublicity;

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public void setTeamPublicity(TeamPublicity teamPublicity) {
        this.teamPublicity = teamPublicity;
    }

    public TeamPublicity getTeamPublicity() {
        return teamPublicity;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public static TeamSettings fromJsonObject(JSONObject jsonObject){
        TeamSettings teamSettings = new TeamSettings();
        teamSettings.setFriendlyFire((Boolean) jsonObject.get("friendlyfire"));
        if(!jsonObject.containsKey("teamPublicity")){
            teamSettings.setTeamPublicity(TeamPublicity.INVITE_ONLY);
        }
        else {
            teamSettings.setTeamPublicity(TeamPublicity.valueOf((String) jsonObject.get("teamPublicity")));
        }
        return teamSettings;
    }

    public static String serialize(TeamSettings teamSettings){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("friendlyfire", teamSettings.isFriendlyFire());
        jsonObject.put("teamPublicity", teamSettings.getTeamPublicity().toString());
        return jsonObject.toJSONString();
    }

    public static TeamSettings getDefaultInstance(){
        TeamSettings teamSettings = new TeamSettings();
        teamSettings.setFriendlyFire(false);
        teamSettings.setTeamPublicity(TeamPublicity.INVITE_ONLY);
        return teamSettings;
    }
}
