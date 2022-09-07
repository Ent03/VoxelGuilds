package com.entity999.teams.achievements;

import org.json.simple.JSONObject;

public class TeamAchievementData extends JSONObject {

    public TeamAchievementData(JSONObject object){
        super(object);

    }


    public boolean isCompleted(){
        return (boolean) super.getOrDefault("completed", false);
    }

    public void setCompleted(){
        super.put("completed", true);
    }
}
