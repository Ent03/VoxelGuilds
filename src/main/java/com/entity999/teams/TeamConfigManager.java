package com.entity999.teams;

import com.entity999.core.ConfigManager;
import com.entity999.core.CustomConfig;

public class TeamConfigManager extends ConfigManager {
    public CustomConfig teams = new CustomConfig("teams.yml");
    public CustomConfig villages = new CustomConfig("villages.yml");

    @Override
    public void loadConfigs() {
        try {
            teams.load(TeamMain.instance);
            villages.load(TeamMain.instance);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveConfigs(){

    }
}
