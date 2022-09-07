package com.entity999.teams.users;

import com.entity999.core.SQL.serilization.DataIgnore;
import com.entity999.core.SQL.serilization.DataProperty;
import com.entity999.core.SQL.serilization.SaveableBody;
import com.entity999.core.user.VoxelUser;
import lombok.Data;

import java.util.UUID;

@Data
public class OfflineTeamUser implements SaveableBody {

    @DataProperty(value = "user", parser = UUID.class)
    protected UUID uuid;

    @DataProperty(value = "team", parser = UUID.class)
    protected UUID team;

    @DataIgnore
    protected TeamUser teamUser;

    public boolean isOnline(){
        return teamUser != null;
    }
}
