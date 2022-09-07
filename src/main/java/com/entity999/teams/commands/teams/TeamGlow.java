package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.TeamService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class TeamGlow extends CustomCommand {
    public TeamGlow() {
        super("glow", "/c glow", "Make your clan members glow. Note: Only you will be able to see your clan members glow.");
        setPermission("core.teamglow");
        setShowRequiredRankInHelpMsg(true);
    }

    @CommandParams(Sender = Sender.PLAYER)
    @Override
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) {
        Player p = (Player)commandSender;
//        ServerTeam team = TeamService.getPlayerTeam(player);
//            if(team == null){
//                ServiceManager.getService(TeamService.class).messagePlayer(p, TeamMessages.JOIN_CLAN_FIRST);
//                return;
//            }
//            if(!p.hasMetadata("team-glow")){
//                ServiceManager.getService(TeamService.class).setTeamMatesGlowing(team, p, true);
//                p.setMetadata("team-glow", new FixedMetadataValue(main, true));
//                ServiceManager.getService(TeamService.class).messagePlayer(p, TeamMessages.CLAN_GLOW_ON);
//            }
//            else {
//                ServiceManager.getService(TeamService.class).setTeamMatesGlowing(team, p, false);
//                p.removeMetadata("team-glow", main);
//                ServiceManager.getService(TeamService.class).messagePlayer(p, TeamMessages.CLAN_GLOW_OFF);
//            }
//        });
        return true;
    }
}
