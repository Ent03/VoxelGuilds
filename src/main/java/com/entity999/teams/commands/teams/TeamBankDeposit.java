package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.services.ServiceManager;
import com.entity999.core.utils.protocol.NBTUtils;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.lang.TeamMessages;
import com.entity999.teams.TeamService;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;


class CoinWrapper{
    private final String id;
    private final ItemStack itemStack;
    public CoinWrapper(ItemStack itemStack, String id){
        this.id = id;
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

public class TeamBankDeposit extends CustomCommand {
    public TeamBankDeposit() {
        super("deposit", "/clan deposit <amount>", "Deposit coins to your clan's bank");
        setPermission("core.team");
    }

    @CommandParams(minArgs = 1)
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        Player player = (Player) commandSender;

        ServerTeam team = TeamService.getPlayerTeam(player);
        if(team == null){
            player.sendMessage(TeamMessages.JOIN_CLAN_FIRST);
            return false;
        }

        int amount = Integer.parseInt(args[0]);

        //int maxAmount = ClanUpgradeManager.getAvailablePerks(team.getLevel()).getUpgradeLevel(ClanUpgradeType.BANK_CAPACITY);
        int maxAmount = 0;

        int alreadyInBank = ServiceManager.getService(TeamService.class).teamCoinRegistry.getCoinsPerTeam(team.getUuid());
        int total = alreadyInBank + amount;

        if(total > maxAmount){
            ServiceManager.getService(TeamService.class).messagePlayer(commandSender, TeamMessages.format(TeamMessages.CLAN_BANK_MAX_REACHED, maxAmount));
            return false;
        }

        int amountLeft = amount;

        int foundAmount = 0;

        HashMap<CoinWrapper, String> allCoinsInInv = new HashMap<>();

        for(ItemStack itemStack : player.getInventory().getContents()){
            if(itemStack == null) continue;
            if(itemStack.getType() != Material.GOLD_NUGGET) continue;
            String id = NBTUtils.getItemTag("clan-coin-id", itemStack);
            if(id != null){
                allCoinsInInv.put(new CoinWrapper(itemStack, id), id);
                foundAmount += itemStack.getAmount();
            }
        }
        if(foundAmount < amount){
            ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.format(TeamMessages.CLAN_BANK_NOT_ENOUGH_COINS, amount, foundAmount));
            return false;
        }
        for(CoinWrapper coinWrapper : allCoinsInInv.keySet()) {
            ItemStack itemStack = coinWrapper.getItemStack();
            if (itemStack == null) continue;
            if (itemStack.getType() != Material.GOLD_NUGGET) continue;
            int iAmount = itemStack.getAmount();
            int code = ServiceManager.getService(TeamService.class).teamCoinRegistry.removeAmount(itemStack, Math.min(iAmount, amount));
            if (code == 1) {
                Command_givecoins.dupeAlert(player);
                return false;
            }
            if (itemStack.getAmount() <= amountLeft) {
                itemStack.setAmount(0);
            }
            else if (itemStack.getAmount() > amountLeft) {
                ItemStack c = itemStack.clone();
                c.setAmount(amountLeft);
                player.getInventory().removeItem(c);
            }
            amountLeft -= iAmount;
            if (amountLeft <= 0) break;
        }
        ServiceManager.getService(TeamService.class).teamCoinRegistry.depositForTeam(team.getUuid(), amount);
        ServiceManager.getService(TeamService.class).messageMembers(team, TeamMessages.format(TeamMessages.CLAN_BANK_DEPOSIT, player.getName(), amount), true);
        return true;
    }
}
