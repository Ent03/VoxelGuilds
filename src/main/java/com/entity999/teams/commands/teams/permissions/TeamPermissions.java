package com.entity999.teams.commands.teams.permissions;

import com.entity999.core.commands.CustomCommand;
import org.bukkit.command.CommandSender;

public class TeamPermissions extends CustomCommand {
    public TeamPermissions() {
        super("permission", "/c permission <give | remove> <member> <permission>", "Manage permissions for your clan members");
        setPermission("core.team");
        addSubCommand(new TeamPermissionGive());
        addSubCommand(new TeamPermissionRemove());
    }

    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        return true;
    }
}
