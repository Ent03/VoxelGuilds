package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.TeamService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class ToggleChat extends CustomCommand {
    public ToggleChat() {
        super("togglechat", "/c togglechat","Toggle clan-only chat on or off. Only clan messages and announcements will be visible.");
        setPermission("core.team");
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
        if(!p.hasMetadata("team-chat")){
            p.setMetadata("team-chat", new FixedMetadataValue(main, true));
            ServiceManager.getService(TeamService.class).messagePlayer(p, TeamMessages.CLAN_CHAT_ON);
        }
        else {
            p.removeMetadata("team-chat", main);
            ServiceManager.getService(TeamService.class).messagePlayer(p, TeamMessages.CLAN_CHAT_OFF);
        }
        return true;
    }
}
