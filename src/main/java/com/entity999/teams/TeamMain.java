package com.entity999.teams;

import com.entity999.core.VoxelPlugin;
import com.entity999.core.commands.CommandManager;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.papi.TeamExtension;
import com.entity999.teams.users.TeamUserManager;

public class TeamMain extends VoxelPlugin {
    public static TeamMain instance;

    public static TeamConfigManager getMainConfig(){
        return instance.getConfigManager(TeamConfigManager.class);
    }

    public static TeamSQLStorage getMainStorage(){
        return instance.getStorage(TeamSQLStorage.class);
    }

    public static TeamUserManager getUserManager(){return ServiceManager.getService(TeamUserManager.class); }

    @Override
    public void onLoad() {
        TeamMain.instance = this;
        try {
            super.initLanguage(getConfig().getString("lang-file"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        TeamConfigManager configManager = new TeamConfigManager();
        configManager.loadConfigs();
        setConfigManager(configManager);
    }

    @Override
    public void onEnable() {
        TeamSQLStorage storage = new TeamSQLStorage(this, VoxelPlugin.mainConfigManager.sqlConfig);
        setMainStorage(storage);
        storage.connectToDatabase();
        ServiceManager.initAllServices(this);
        setCommandManager(new CommandManager(this));
        getCommandManager().registerAllCommands();
        new TeamExtension().register();
    }
}
