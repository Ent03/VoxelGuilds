package com.entity999.teams.commands.teams.villages;

import com.entity999.core.commands.CustomCommand;
import com.entity999.core.services.ServiceManager;

import com.entity999.core.user.VoxelNetworkUser;
import com.entity999.core.user.VoxelNetworkUserManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_ignorevillages extends CustomCommand {
    public Command_ignorevillages() {
        super("ignorevillages", "/ignorevillages", "Ignore all villages");
        setPermission("core.admin");
    }

    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        VoxelNetworkUser user = ServiceManager.getService(VoxelNetworkUserManager.class).getVoxelUser(((Player)commandSender).getUniqueId());
        if(!user.getKeyStorage().getBoolean("ignoring_villages")){
            commandSender.sendMessage("§cNow ignoring villages.");
        }
        else {
            commandSender.sendMessage("§aNow respecting villages.");
        }
        user.getKeyStorage().put("ignoring_villages", true);
        return true;
    }
}
