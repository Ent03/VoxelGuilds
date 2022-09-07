package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamPublicity;
import com.entity999.teams.lang.TeamMessages;

import com.entity999.teams.TeamService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamChangePublicity extends CustomCommand {
    private HashMap<String, TeamPublicity> keys;
    public TeamChangePublicity() {
        super("publicity", "/c publicity <public | invite-only>",
                "Change your clan's publicity. ");
        setPermission("core.team");
        keys = new HashMap<>();
        keys.put("invite-only", TeamPublicity.INVITE_ONLY);
        keys.put("public", TeamPublicity.PUBLIC);
    }
    @CommandParams(minArgs = 1, Sender = Sender.PLAYER)
    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) {
        Player player = (Player) commandSender;
        String publicity = args[0];

        TeamPublicity teamPublicity = keys.get(publicity.toLowerCase());
        if (teamPublicity == null) {
            return false;
        }
        ServerTeam team = TeamService.getPlayerTeam(player);
        if(!team.getOwner().equals(player.getUniqueId())){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_OWNER_ONLY_COMMAND);
            return true;
        }
        team.getTeamSettings().setTeamPublicity(teamPublicity);
        ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.format(TeamMessages.CLAN_PUBLICITY_CHANGED, args[0]));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return StringUtil.copyPartialMatches(args[0], keys.keySet(), new ArrayList<>());
    }
}
