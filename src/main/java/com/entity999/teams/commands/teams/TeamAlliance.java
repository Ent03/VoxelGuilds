package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import org.bukkit.command.CommandSender;

public class TeamAlliance extends CustomCommand {
    public TeamAlliance() {
        super("alliance", "/c alliance", "Manage your clan's allies");
        setPermission("core.team");
        addSubCommand(new TeamAllianceCreate());
        addSubCommand(new TeamRemoveAlly());
    }

    @CommandParams(Sender = Sender.PLAYER)
    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        return true;
    }
}
