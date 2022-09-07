package com.entity999.teams.users;

import com.entity999.core.SQL.serilization.*;
import com.entity999.core.user.VoxelUser;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamMain;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class TeamUser extends OfflineTeamUser implements VoxelUser {
    @DataIgnore
    private Player player;

    @DataIgnore
    private ServerTeam onlineTeam;

    public TeamUser(UUID uuid){
        this();
        this.uuid = uuid;
        this.teamUser = this;
    }

    public void setOnlineTeam(ServerTeam onlineTeam) {
        this.onlineTeam = onlineTeam;
    }

    public ServerTeam getOnlineTeam() {
        return onlineTeam;
    }

    public void setTeam(ServerTeam team) {
        super.setTeam(team.getUuid());
        setOnlineTeam(team);
    }

    public TeamUser(){
        setTeamUser(this);
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public String getPlayerName() {
        return getPlayer().getName();
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void initUser() {

    }
}
