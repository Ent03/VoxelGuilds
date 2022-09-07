package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CustomCommand;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamPermission;
import com.entity999.teams.TeamService;

import com.entity999.teams.upgrades.ClanUpgradeManager;
import com.entity999.teams.upgrades.ClanUpgradeType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TeamDelBase extends CustomCommand {
    public TeamDelBase() {
        super("delbase", "/g delbase <name>", "Delete a guild base.");
        setPermission("core.team");
    }

    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        Player player = (Player) commandSender;
        ServerTeam team = TeamService.getPlayerTeam(player);
        if (team == null) {
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.JOIN_CLAN_FIRST);
            return true;
        }
        if (!team.getMember(player.getUniqueId()).checkPermission(player, TeamPermission.SET_BASE)) {
            return true;
        }
        String baseName;
        if (args.length == 0 || ClanUpgradeManager.getAvailablePerks(team.getLevel()).getUpgradeLevel(ClanUpgradeType.MAX_BASES) <= 1) {
            baseName = "base";
        } else {
            baseName = args[0];
        }
        if(!team.hasBase(baseName)){
            ServiceManager.getService(TeamService.class).messagePlayer(player, "§cYou don't have a base by this name!");
            return true;
        }
        team.deleteBase(baseName);
        ServiceManager.getService(TeamService.class).messageMembers(team, TeamMessages.format("§c{0} deleted the base {1}", player.getName(), baseName), true);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Player player = (Player) sender;
        ServerTeam team = TeamService.getPlayerTeam(player.getUniqueId());
        if(team == null) return super.tabComplete(sender,alias,args);
        return team.getBaseNames();
    }
}
