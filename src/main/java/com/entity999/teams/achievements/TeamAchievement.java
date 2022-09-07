package com.entity999.teams.achievements;

import com.entity999.core.services.ServiceManager;
import com.entity999.core.utils.ChatUtils;

import com.entity999.teams.TeamMain;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamService;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.Listener;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public abstract class TeamAchievement implements Listener {
    private final String name, desc;
    public TeamAchievementType type;
    protected TeamMain main;

    public TeamAchievement(TeamAchievementType type, String name, String desc) {
        this.name = name;
        this.main = TeamMain.instance;
        this.type = type;
        this.desc = desc;
        Bukkit.getPluginManager().registerEvents(this, main);
        TeamAchievements.addToMap(this);
    }

    public TeamAchievementType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }


    public TeamAchievementData addAchievementData(ServerTeam team){
        if(team.getAchievementData(type) == null){
            TeamAchievementData data = new TeamAchievementData(new JSONObject());
            team.addAchievement(getType(), data);
            return data;
        }
        return team.getAchievementData(type);
    }

    public void showProgression(ServerTeam team, double toAdd, double remaining, double needed){
        new TeamAchievementProgressBar(team, getName(), toAdd, remaining, needed, TeamAchievementProgressBarType.SHOW_ALL).runTaskTimerAsynchronously(main, 1,1);
    }
    public void showProgression(ServerTeam team, double toAdd, double remaining, double needed, TeamAchievementProgressBarType type){
        new TeamAchievementProgressBar(team, getName(), toAdd, remaining, needed, type).runTaskTimerAsynchronously(main, 1,1);
    }


    public void setCompleted(ServerTeam team){
        addAchievementData(team);
        BaseComponent[] text = ChatUtils.getHoverText(TeamMessages.format(TeamMessages.CLAN_ACHIEVEMENT_COMPLETED, getName()), "§6"+getDesc());
        ServiceManager.getService(TeamService.class).messageMembers(team, text, true);
        team.playSoundForMembers(Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.2f, 1f);
        team.addExperience(type.getExpReward());
        team.getAchievementData(type).setCompleted();
    }

    public static void setAchievementsForTeam(ServerTeam team, String data) throws ParseException {
        JSONParser parser = new JSONParser();
        if(data == null) return;
        JSONObject parsed = (JSONObject) parser.parse(data);
        for(Object key : parsed.keySet()){
            String typeName = (String) key;
            TeamAchievementType type = TeamAchievementType.valueOf(typeName);
            JSONObject jsonData = (JSONObject) parser.parse((String) parsed.get(key));
            team.addAchievement(type, new TeamAchievementData(jsonData));
        }
    }
}
