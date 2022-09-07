package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamPermission;
import com.entity999.teams.TeamService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TeamToggleFriendlyFire extends CustomCommand {
    public TeamToggleFriendlyFire() {
        super("friendlyfire", "/c friendlyfire <on | off>", "Set your clan's friendly fire on or off");
        setPermission("core.team");
    }
    @CommandParams(Sender = Sender.PLAYER, minArgs = 1)
    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) {
        Player p = (Player) commandSender;
        if(!(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))) return false;
        ServerTeam team = TeamService.getPlayerTeam(p);
        if(team == null){
             ServiceManager.getService(TeamService.class).messagePlayer(p, TeamMessages.JOIN_CLAN_FIRST);
             return true;
        }
        if(!team.getMember(p.getUniqueId()).checkPermission(p, TeamPermission.CHANGE_FRIENDLYFIRE)){
            return true;
        }
        team.getTeamSettings().setFriendlyFire(args[0].equalsIgnoreCase("on"));
        boolean on = args[0].equalsIgnoreCase("on");
        String msg = on ? TeamMessages.CLAN_FF_ON : TeamMessages.CLAN_FF_OFF;
        ServiceManager.getService(TeamService.class).messageMembers(team, "§b"+p.getName() + "§f" + msg, true);
        for(UUID ally : team.getAllies()){
             ServerTeam allyT = ServiceManager.getService(TeamService.class).getTeamFast(ally);
             ServiceManager.getService(TeamService.class).messageMembers(allyT, TeamMessages.format(TeamMessages.CLAN_ALLY_HAS_TOGGLED_FF, team.getName(), msg), true);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return StringUtil.copyPartialMatches(args[0], Arrays.asList("on", "off"), new ArrayList<>());
    }
}
