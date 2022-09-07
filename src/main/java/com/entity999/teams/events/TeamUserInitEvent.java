package com.entity999.teams.events;

import com.entity999.core.events.VoxelUserInitEvent;
import com.entity999.core.user.VoxelUserManager;
import com.entity999.teams.users.TeamUser;

public class TeamUserInitEvent extends VoxelUserInitEvent<TeamUser> {
    public TeamUserInitEvent(TeamUser user, VoxelUserManager<TeamUser> manager) {
        super(user, manager);
    }
}
