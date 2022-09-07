package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandManager;
import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TeamTeleportBase extends CustomCommand {
    protected TeamTeleportBase() {
        super("tb","Teleport to your clan base.", "/c tb", Collections.singletonList("base"));
        setPermission("core.team.tb");
    }
    @CommandParams(Sender = Sender.PLAYER)
    @Override
    public boolean onCommand(CommandSender commandSender, String s, String[] args) {
        Player player = (Player) commandSender;
        player.performCommand(CommandManager.getCustomCommandExecString(Command_tpbase.class) + " " + String.join(" ", args));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Player player = (Player) sender;
        ServerTeam team = TeamService.getPlayerTeam(player.getUniqueId());
        if(team == null) return super.tabComplete(sender,alias,args);
        return team.getBaseNames();
    }
}
