package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import org.bukkit.command.CommandSender;

public class TeamBanner extends CustomCommand {
    public TeamBanner() {
        super("banner", "/c banner <import | reset>", "Set a banner for your clan. Type '/c banner' for more info");
        setPermission("core.team.banner");
        addSubCommand(new TeamBannerImport());
        addSubCommand(new TeamBannerReset());
        addSubCommand(new TeamBannerItem());
    }

    @CommandParams(minArgs = 1)
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        return true;
    }
}
