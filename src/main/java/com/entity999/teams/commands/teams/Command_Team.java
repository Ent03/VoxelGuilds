package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandManager;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.HelpChapter;
import com.entity999.teams.commands.teams.permissions.TeamPermissions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Command_Team extends CustomCommand {
    public Command_Team() {
        super("clan", "Manage your clan", "Type /clan for help", Arrays.asList("t", "c", "f", "party", "team"));
        this.setPermission("core.team");
        HelpChapter general = new HelpChapter("general", "General");
        HelpChapter permissions = new HelpChapter("general", "Permissions");
        HelpChapter settings = new HelpChapter("general", "Settings");
        HelpChapter other = new HelpChapter("other", "Other");
        addSubCommand(new TeamCreate(), general);
        addSubCommand(new TeamJoin(),general);
        addSubCommand(new TeamSetBase(),general);
        addSubCommand(new TeamDelBase(), general);
        addSubCommand(new TeamTeleportBase(),general);
        addSubCommand(new TeamLeave(),general);
        addSubCommand(new TeamInvite(),general);
        addSubCommand(new TeamMemberPromote(),permissions);
        addSubCommand(new TeamMemberDemote(),permissions);
        addSubCommand(new TeamKick(), general);
        addSubCommand(new TeamSpectateMember(), other);
        addSubCommand(new TeamStorage(), other);
        addSubCommand(new TeamRename(), other);
        addSubCommand(new TeamInfo(), general);
        addSubCommand(new TeamToggleFriendlyFire(), settings);
        //addSubCommand(new TeamGlow(), other);
        addSubCommand(new ToggleChat(), general);
        addSubCommand(new TeamAlliance(), general);
        addSubCommand(new TeamSetLeader(), settings);
        addSubCommand(new TeamChangePublicity(), settings);
        addSubCommand(new TeamBanner(), other);
        addSubCommand(new TeamBank(), other);
        addSubCommand(new TeamPermissions(),permissions);
        zeroArgCommand = true;
    }

    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) {
        Player player = (Player) commandSender;
        player.performCommand(
                CommandManager.getCustomCommandExecString(TeamInfo.class));
        return true;
    }
}
