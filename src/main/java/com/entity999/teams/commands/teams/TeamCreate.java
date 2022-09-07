package com.entity999.teams.commands.teams;

import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamMain;
import com.entity999.teams.TeamSQLStorage;
import com.entity999.core.LibsPlugin;
import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.commands.Sender;
import com.entity999.core.lang.VoxelMessages;
import com.entity999.core.services.ServiceManager;
import com.entity999.core.utils.RegexUtils;

import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.TeamService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TeamCreate extends CustomCommand {
    private static int MAX_NAME_LENGTH = 16;
    private static int MIN_NAME_LENGTH = 3;
    protected TeamCreate() {
        super("create","Create a new clan", "/c create <clan name>", Collections.singletonList("c"));
        setPermission("core.team");
    }


    @CommandParams(Sender = Sender.PLAYER, minArgs = 1)
    @Override
    public boolean onCommand(CommandSender commandSender, String s, String[] args) {
        Player player = (Player) commandSender;
        if(args[0].length() > MAX_NAME_LENGTH){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.format(TeamMessages.CLAN_MAX_CHARS, MAX_NAME_LENGTH));
            return true;
        }
        else if(args[0].length() < 3){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.format(TeamMessages.CLAN_MIN_CHARS, MIN_NAME_LENGTH));
            return true;
        }
        if(!RegexUtils.containsOnlyLettersAndNumbers(args[0]) || (args.length > 1)){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.INVALID_CLAN_NAME);
            return true;
        }
        if(TeamMain.getMainStorage().getValue("name", "teams", "name", args[0]) != null){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_ALREADY_EXISTS);
            return true;
        }
        if(LibsPlugin.economy.getBalance(player) < TeamService.CREATE_COST){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.format(TeamMessages.CLAN_CREATE_NO_FUNDS, LibsPlugin.economy.format(TeamService.CREATE_COST)));
            return true;
        }
        ServerTeam team = TeamService.getPlayerTeam(player);
        if(team != null){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.LEAVE_CLAN_FIRST);
            return true;
        }
        ServiceManager.getService(TeamService.class).createTeam(args[0], player);
        ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.format(TeamMessages.CLAN_CREATED, args[0]));
        LibsPlugin.economy.withdrawPlayer(player, TeamService.CREATE_COST);
        ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.format(VoxelMessages.MONEY_WITHDRAW, LibsPlugin.economy.format(TeamService.CREATE_COST)));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
