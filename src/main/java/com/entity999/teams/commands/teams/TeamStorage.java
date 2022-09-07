package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.TeamService;

import com.entity999.teams.upgrades.ClanUpgradeManager;
import com.entity999.teams.upgrades.ClanUpgradeType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TeamStorage extends CustomCommand {
    public TeamStorage() {
        super("vault", "Opens your clan vault", "/c vault", Arrays.asList("chest","storage"));
        setPermission("core.team.storage");
    }
    @CommandParams(Sender = Sender.PLAYER)
    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) {
        Player p = (Player) commandSender;
        ServerTeam team = TeamService.getPlayerTeam(p);
        if(team == null){
            ServiceManager.getService(TeamService.class).messagePlayer(p, TeamMessages.JOIN_CLAN_FIRST);
            return true;
        }
        int clanLevel = team.getLevel();
        int size = ClanUpgradeManager.getAvailablePerks(clanLevel).getUpgradeLevel(ClanUpgradeType.STORAGE_ROWS);
        size *= 9;

        if(size == 0){
            ServiceManager.getService(TeamService.class).messagePlayer(p, TeamMessages.CLAN_FEATURE_NOT_UNLOCKED);
            return true;
        }
        ServiceManager.getService(TeamService.class).openTeamChest(p, team.getUuid(), size, clanLevel);
        return true;
    }
}
