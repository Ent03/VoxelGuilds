package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.lang.VoxelMessages;
import com.entity999.core.services.ServiceManager;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.TeamPermission;
import com.entity999.teams.TeamService;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class TeamBankWithdraw extends CustomCommand {
    public TeamBankWithdraw() {
        super("withdraw", "/c bank withdraw <amount>", "Withdraw coins for your clan's bank");
        setPermission("core.team");
    }

    @CommandParams(minArgs = 1)
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        Player player = (Player) commandSender;
        if(!StringUtils.isNumeric(args[0])) return false;
        int amount = Integer.parseInt(args[0]);
        ServerTeam team = TeamService.getPlayerTeam(player);
        if(team == null){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.JOIN_CLAN_FIRST);
            return true;
        }
        if(!team.getMember(player.getUniqueId()).checkPermission(player, TeamPermission.WITHDRAW_BANK)){
            return true;
        }
        if(player.getInventory().firstEmpty() == -1){
            ServiceManager.getService(TeamService.class).messagePlayer(player, VoxelMessages.INV_FULL);
            return true;
        }
        int coins = ServiceManager.getService(TeamService.class).teamCoinRegistry.getCoinsPerTeam(team.getUuid());
        if(coins < amount){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.CLAN_BANK_NOT_ENOUGH_COINS_IN_BANK);
            return true;
        }
        ItemStack coinItem = Command_givecoins.getCoins(player,amount);
        HashMap<Integer, ItemStack> left = player.getInventory().addItem(coinItem);
        int leftOver = 0;
        for(ItemStack i : left.values()) leftOver += i.getAmount();
        int finalAmount = amount - leftOver;
        ServiceManager.getService(TeamService.class).messageMembers(team, TeamMessages.format(TeamMessages.CLAN_BANK_WITHDRAW, player.getName(), finalAmount), true);
        if(leftOver > 0){
            ServiceManager.getService(TeamService.class).teamCoinRegistry.removeAmount(coinItem, leftOver);
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.format(TeamMessages.CLAN_BANK_EXCESS_COINS,leftOver));
        }
        ServiceManager.getService(TeamService.class).teamCoinRegistry.withdrawFromTeam(team.getUuid(), finalAmount);

        return true;
    }
}
