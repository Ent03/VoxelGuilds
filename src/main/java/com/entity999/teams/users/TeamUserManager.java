package com.entity999.teams.users;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.entity999.core.SQL.SQLTableManager;
import com.entity999.core.events.DatabaseSavePlayerEvent;
import com.entity999.core.user.VoxelUserManager;
import com.entity999.teams.TeamMain;
import com.entity999.teams.events.TeamUserInitEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TeamUserManager extends VoxelUserManager<TeamUser>{
    @Override
    public void createNewPlayer(PlayerProfile playerProfile) {

    }

    @Override
    @EventHandler
    public void preLogin(AsyncPlayerPreLoginEvent event) {
        TeamUser user = TeamMain.getMainStorage().getTableManager("teamusers").
                getBodyOrDefault(event.getUniqueId().toString(), TeamUser.class, wrapUser(event.getUniqueId()));
        user.initUser();
        addVoxelUser(user.getUuid(), user);
        new TeamUserInitEvent(user, this).callEvent();
    }

    public CompletableFuture<OfflineTeamUser> getOfflineUser(UUID uuid) {
        TeamUser onlineUser = getVoxelUser(uuid);
        if(onlineUser == null){
            SQLTableManager table = TeamMain.getMainStorage().getTableManager("teamusers");
            return CompletableFuture.supplyAsync(()->table
                            .getBody(uuid.toString(), OfflineTeamUser.class))
                            .whenComplete((user, e )-> table.saveBody(user)); //finally saving it
        }

        return CompletableFuture.supplyAsync(()->onlineUser);
    }

    @Override
    @EventHandler(priority = EventPriority.LOW)
    public void onLogin(PlayerJoinEvent event) {
//        var user  = getVoxelUser(event.getPlayer().getUniqueId());
//        user.setPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        TeamMain.getMainStorage().saveBody("teamusers", "user",
                TeamMain.getUserManager().getVoxelUser(event.getPlayer().getUniqueId()));
    }

    @Override
    public TeamUser wrapUser(UUID uuid) {
        return new TeamUser(uuid);
    }
}
