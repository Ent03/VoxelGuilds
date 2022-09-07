package com.entity999.teams.commands.teams;

import com.entity999.teams.TeamMain;
import com.entity999.teams.TeamSQLStorage;
import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.services.ServiceManager;

import com.entity999.teams.ServerTeam;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.TeamService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamRename extends CustomCommand {
    public TeamRename() {
        super("rename", "/c rename <clan> <new name>", "Rename a clan if the name is inappropriate.");
        setPermission("core.team.rename");
    }
    @CommandParams(minArgs = 2)
    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) {
        Player player = (Player) commandSender;
        String teamName = args[0];
        String newName = args[1];
        if(TeamMain.getMainStorage().getValue("name", "teams", "name", teamName) == null){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_NOT_EXIST);
            return true;
        }
        ServerTeam team = ServiceManager.getService(TeamService.class).getTeamByNameFast(teamName);
        if(team != null){
            for(UUID uuid : team.getMemberList()){
                Player member = Bukkit.getPlayer(uuid);
                if(member == null || !member.isOnline()) continue;
                ServiceManager.getService(TeamService.class).messagePlayer(player, ChatColor.RED + "Your clan was renamed to " + newName);
            }
        }
        TeamMain.getMainStorage().updateValue("teams", "name", "name", teamName, newName);
        ServiceManager.getService(TeamService.class).messagePlayer(player, ChatColor.RED + "Clan renamed.");
        return true;
    }
}
