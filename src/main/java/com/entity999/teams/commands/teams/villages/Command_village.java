package com.entity999.teams.commands.teams.villages;

import com.entity999.core.commands.CustomCommand;
import org.bukkit.command.CommandSender;

public class Command_village extends CustomCommand {
    public Command_village() {
        super("village", "/village", "Main village command");
        setPermission("core.team");
        addSubCommand(new ClaimVillage());
        addSubCommand(new ClaimBlocks());
        addSubCommand(new DeleteVillage());
        addSubCommand(new ManagePClaims());
        addSubCommand(new ExpandVillage());
    }

    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {

        return true;
    }
}
