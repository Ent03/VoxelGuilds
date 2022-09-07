package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CustomCommand;
import com.entity999.core.services.ServiceManager;
import com.entity999.core.user.VoxelNetworkUser;
import com.entity999.core.user.VoxelNetworkUserManager;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.TeamService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Command_TeamMessage extends CustomCommand {
    public Command_TeamMessage() {
        super("clanmsg", "Sends a message to your clan", "/clanmsg <message>", Arrays.asList("cm", "partychat"));
        setPermission("core.team");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String s, String[] args) {
        Player player = (Player) commandSender;
        if(args.length < 1){
            return false;
        }
        ServerTeam team = TeamService.getPlayerTeam(player);
        if(team == null){
            player.sendMessage(TeamMessages.JOIN_CLAN_FIRST);
            return true;
        }
        StringBuilder msg = new StringBuilder();
        for (String str : Arrays.copyOfRange(args, 0, args.length)) {
            String arg = str + " ";
            msg.append(arg);
        }

        ServiceManager.getService(TeamService.class).messageMembers(team, ChatColor.YELLOW+"["+team.getName()+"] §7" + player.getName() +"§f: "+ msg.toString(), false);
        return true;
    }
}
