package com.entity999.teams.commands.teams.leaderboard;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import org.bukkit.command.CommandSender;

public class Command_teamleaderboards extends CustomCommand {
    public Command_teamleaderboards() {
        super("clantop", "/clantop", "View the clan leaderboard");
        setPermission("core.team");
//        addSubCommand(new TeamLeaderboardMoney());
//        addSubCommand(new TeamLeaderboardExperience());
    }

    @CommandParams(Sender = Sender.PLAYER)
    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        return new TeamLeaderboardExperience().onCommand(commandSender,s,args);
    }
}
