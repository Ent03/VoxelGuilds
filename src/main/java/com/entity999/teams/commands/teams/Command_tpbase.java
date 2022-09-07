package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.lang.VoxelMessages;
import com.entity999.core.services.ServiceManager;
import com.entity999.core.services.TPService.TeleporterService;
import com.entity999.teams.lang.TeamMessages;

import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamService;

import com.entity999.teams.upgrades.ClanUpgradeManager;
import com.entity999.teams.upgrades.ClanUpgradeType;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Command_tpbase extends CustomCommand {
    public Command_tpbase() {
        super("tb", "/tb <base>", "Teleport to your clan base");
        setPermission("core.team.tb");
}
    @CommandParams(Sender = Sender.PLAYER)
    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) {
        Player player = (Player) commandSender;
        ServerTeam team = TeamService.getPlayerTeam(player);
        if(team == null){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.JOIN_CLAN_FIRST);
            return true;
        }
        String baseName;
        if(args.length == 0 || ClanUpgradeManager.getAvailablePerks(team.getLevel()).getUpgradeLevel(ClanUpgradeType.MAX_BASES) <= 1){
            baseName = "base";
        }
        else{
            baseName = args[0];
        }

        Location loc = team.getTeamBase(baseName);
        if(loc == null){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_BASE_NOT_SET);
            return true;
        }
        ServiceManager.getService(TeleporterService.class).dTeleport(player, loc);
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
