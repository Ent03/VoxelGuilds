package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.TeamService;
import org.bukkit.command.CommandSender;

public class Command_addteamxp extends CustomCommand {
    public Command_addteamxp() {
        super("addteamxp", "/addteamxp", "Add xp for a team");
        setPermission("core.admin");
    }

    @CommandParams(minArgs = 2)
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        String name = args[0];
        int amount = Integer.parseInt(args[1]);
        ServiceManager.getService(TeamService.class).getTeamByNameFast(name).addExperience(amount);
        commandSender.sendMessage("xp added");
        return true;
    }
}
