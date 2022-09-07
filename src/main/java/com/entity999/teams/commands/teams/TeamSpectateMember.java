package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.services.RequestManager;
import com.entity999.core.services.ServiceManager;
import com.entity999.core.utils.ChatUtils;
import com.entity999.core.utils.PlayerUtils;
import com.entity999.teams.lang.TeamMessages;

import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamService;
import com.entity999.teams.services.SpectatorService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamSpectateMember extends CustomCommand {
    public TeamSpectateMember() {
        super("spectate", "/c spectate <member>", "Spectate your clan member");
        setPermission("core.team.spectate");
        setShowRequiredRankInHelpMsg(true);
    }
    @CommandParams(Sender = Sender.PLAYER, minArgs = 1)
    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) {
        if(!ServiceManager.isServiceRegistered(SpectatorService.class)){
            commandSender.sendMessage("This feature is not enabled.");
            return true;
        }
        Player player = (Player)commandSender;
        Bukkit.getScheduler().runTaskAsynchronously(main, ()->{
            ServerTeam team = TeamService.getPlayerTeam(player.getUniqueId());
            if(team == null){
                ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.JOIN_CLAN_FIRST);
                return;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if(!PlayerUtils.isOnline(target)){
                ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.PLAYER_OFFLINE);
                return;
            }
            if(target.getUniqueId().equals(player.getUniqueId()) && !player.hasPermission("core.debug")){
                ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_CANT_SPECTATE_SELF);
                return;
            }
            ServerTeam targetTeam = TeamService.getPlayerTeam(player.getUniqueId());
            if(!team.equals(targetTeam)){
                ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.PLAYER_NOT_ON_CLAN);
                return;
            }
            if(ServiceManager.getService(SpectatorService.class).isSpectator(target)){
                ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_PLAYER_ALREADY_SPECTATING);
                return;
            }
            if(!ServiceManager.getService(SpectatorService.class).playerCanSpectate(player)){
                return;
            }
            ServiceManager.getService(RequestManager.class).addNewRequest("spectate", target, player, 60, ServiceManager.getService(TeamService.class).getPrefix() + TeamMessages.format(TeamMessages.CLAN_SPEC_REQ_TIMEOUT, target.getName()));
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_SPECTATE_REQUEST_SENT);

            target.spigot().sendMessage(ChatUtils.getClickableText(ServiceManager.getService(TeamService.class).getPrefix(),
                    TeamMessages.format(TeamMessages.CLAN_PLAYER_RECEIVE_SPEC_REQ_LINE1, player.getName()),
                    TeamMessages.format(TeamMessages.CLAN_PLAYER_RECEIVE_SPEC_REQ_LINE2, player.getName()),
                    TeamMessages.CLAN_PLAYER_RECEIVE_SPEC_REQ_LINE3, TeamMessages.format(TeamMessages.CLAN_PLAYER_RECEIVE_SPEC_REQ_LINK, player.getName())));
        });

        return true;
    }
}
