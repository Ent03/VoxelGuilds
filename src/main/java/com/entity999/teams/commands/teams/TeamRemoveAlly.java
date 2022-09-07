package com.entity999.teams.commands.teams;

import com.entity999.teams.*;
import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.services.ServiceManager;

import com.entity999.teams.lang.TeamMessages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamRemoveAlly extends CustomCommand {
    public TeamRemoveAlly() {
        super("end", "/c alliance end <clan>", "End an alliance with a clan.");
        setPermission("core.team");
    }

    @CommandParams(Sender = Sender.PLAYER, minArgs = 1)
    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        Player p = (Player) commandSender;
        ServerTeam team = TeamService.getPlayerTeam(p);
        if(team == null){
            ServiceManager.getService(TeamService.class).messagePlayer(p, TeamMessages.JOIN_CLAN_FIRST);
            return true;
        }
        if(!team.getMember(p.getUniqueId()).checkPermission(p, TeamPermission.MANAGE_ALLIANCES)){
            return true;
        }
        ServiceManager.getService(TeamService.class).getOfflineTeam(args[0]).thenAccept(target -> {
            if(target == null) {
                ServiceManager.getService(TeamService.class).messagePlayer(p, TeamMessages.TARGET_CLAN_NOT_EXIST);
                return;
            }
            target.removeAlly(team.getUuid());
            team.removeAlly(target.getUuid());
            if(!team.getAllies().contains(target.getUuid())){
                ServiceManager.getService(TeamService.class).messagePlayer(p, TeamMessages.CLAN_NOT_ALLIED);
                return;
            }

            ServiceManager.getService(TeamService.class).messageMembers(team, TeamMessages.format(TeamMessages.CLAN_NO_LONGER_ALLIED, args[0]), true);
            if(target.isOnline()){
                ServiceManager.getService(TeamService.class).messageMembers(target.getOnlineTeam(), TeamMessages.format(TeamMessages.CLAN_NO_LONGER_ALLIED, team.getName()), true);
            }
        });
        return true;
    }
}
