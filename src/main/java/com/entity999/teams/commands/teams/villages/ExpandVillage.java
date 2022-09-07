package com.entity999.teams.commands.teams.villages;

import com.entity999.core.commands.CustomCommand;
import com.entity999.core.services.ServiceManager;

import com.entity999.teams.villages.VillageService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpandVillage extends CustomCommand {
    public ExpandVillage() {
        super("expand", "/village expand", "Expand your village");
        setPermission("core.team");
    }

    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        ServiceManager.getService(VillageService.class).resizeClaim((Player)commandSender);
        return true;
    }
}
