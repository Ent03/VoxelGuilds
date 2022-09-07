package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.lang.VoxelMessages;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.TeamService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamLeave extends CustomCommand {
    protected TeamLeave() {
        super("leave","/c leave","Leave your current clan. Note: Your clan will be disbanded if you are the last person to leave.");
        setPermission("core.team");
    }
    @CommandParams(Sender = Sender.PLAYER)
    @Override
    public boolean onCommand(CommandSender commandSender, String s, String[] args) {
        Player player = (Player) commandSender;
        ServerTeam team = TeamService.getPlayerTeam(player);
        var service = ServiceManager.getService(TeamService.class);
        if(team == null){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.JOIN_CLAN_FIRST);
            return false;
        }

        if(team.getMemberList().size() == 1){
            if(args.length > 0 && args[0].equalsIgnoreCase(VoxelMessages.CONFIRM)){
                service.messagePlayer(player, TeamMessages.CLAN_DISBANDED);
                service.deleteTeam(team);
            }
            else{
                service.messagePlayer(player, TeamMessages.CLAN_DISBAND_WARNING);
            }
        }
        else {
            if(team.getOwner().equals(player.getUniqueId())){
                service.messagePlayer(player, TeamMessages.CLAN_MUST_TRANSFER_OWNERSHIP);
                return false;
            }
            service.messagePlayer(player, TeamMessages.CLAN_LEFT_CLAN);
            service.leaveFromTeam(team.getUuid(), player.getUniqueId());
        }
        return true;
    }
}
