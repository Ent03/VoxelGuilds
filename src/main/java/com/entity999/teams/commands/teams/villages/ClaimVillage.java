package com.entity999.teams.commands.teams.villages;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.services.ServiceManager;

import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.villages.VillageService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClaimVillage extends CustomCommand {
    private final Pattern colorPattern = Pattern.compile("^[a-zA-Z0-9 ,._&]*$");
    public ClaimVillage() {
        super("create", "/village create <name of village>", "Create a village");
        setPermission("core.team");
    }

    public boolean checkName(String teamName){
        Matcher m = colorPattern.matcher(teamName);
        return m.matches();
    }

    @CommandParams(minArgs = 1)
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        Player player = (Player)commandSender;
        StringBuilder stringBuilder = new StringBuilder();
        for(String str : args){
            stringBuilder.append(str);
            stringBuilder.append(" ");
        }
        String name = stringBuilder.toString().trim();
        if(!checkName(name)){
            ServiceManager.getService(VillageService.class).messagePlayer(player, TeamMessages.VILLAGE_NAME_INVALID_CHARS);
            return true;
        }
        if(name.length() > 35){
            ServiceManager.getService(VillageService.class).messagePlayer(player, TeamMessages.VILLAGE_NAME_TOO_LONG);
            return true;
        }
        if(name.length() < 3){
            ServiceManager.getService(VillageService.class).messagePlayer(player, TeamMessages.VILLAGE_NAME_TOO_SHORT);
            return true;
        }
        ServiceManager.getService(VillageService.class).createClaim((Player)commandSender, name);
        return true;
    }
}
