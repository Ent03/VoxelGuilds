package com.entity999.teams.commands.teams;

import com.entity999.core.commands.CommandParams;
import com.entity999.core.commands.CustomCommand;
import com.entity999.core.services.ServiceManager;
import com.entity999.core.utils.DateUtils;
import com.entity999.core.utils.protocol.NBTUtils;
import com.entity999.teams.coins.CoinData;

import com.entity999.teams.TeamService;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class Command_givecoins extends CustomCommand {
    public Command_givecoins() {
        super("givecoins", "/givecoins <amount>", "..");
        setPermission("core.admin");
    }

    public static ItemStack getCoins(Player player, int amount){
        return getCoins(player, amount, UUID.randomUUID().toString(), DateUtils.getCurrentDate());
    }

    public static ItemStack getCoins(Player player, int amount, String id, String date){
        ItemStack coins = new ItemStack(Material.GOLD_NUGGET, amount);
        coins.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        coins.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ItemMeta meta = coins.getItemMeta();
        meta.displayName(Component.text("§6Clan coin"));
        //meta.lore(Arrays.asList(Component.text(""),Component.text("§7§oUsed to upgrade clan villages and to"), Component.text("§7§otrade with clan villagers")));
        coins.setItemMeta(meta);

        coins = NBTUtils.addTagToItem("clan-coin-id", id, coins);
        coins = NBTUtils.addTagToItem("clan-coin-date", date, coins);
        String playerID = player == null ? "not a player" : player.getUniqueId().toString();
        ServiceManager.getService(TeamService.class).teamCoinRegistry.addNewID(new CoinData(id, playerID, amount,date));
        return coins;
    }

    /**
     * Coins are not naturally stackable due to their unique properties
     * this method will safely give coins to a player by searching already
     * existing coins and increasing their amount by the @param coins amount
     * if no existing coins were found, the itemstack is added to the player's inv
     */
    public static void giveCoinsToPlayer(Player player, ItemStack coins){
        addCoinsToInv(player.getInventory(), coins);
    }

    public static void addCoinsToInv(Inventory inventory, ItemStack coins){
        ItemStack[] contents = inventory.getStorageContents();
        boolean foundExisting = false;
        for(ItemStack itemStack : contents){
            if(itemStack == null) continue;
            if(itemStack.getType() != Material.GOLD_NUGGET) continue;
            if(itemStack.getAmount() == 64) continue;
            String targetID = NBTUtils.getItemTag("clan-coin-id", itemStack);
            if(targetID != null && !targetID.isEmpty()){
                foundExisting = true;
                int totalCount = coins.getAmount() + itemStack.getAmount();
                int toBeAdded = totalCount - itemStack.getAmount();
                int lostN = coins.getAmount();
                if(totalCount > 64){
                    toBeAdded = (64-itemStack.getAmount());
                    lostN = toBeAdded;
                    ServiceManager.getService(TeamService.class).teamCoinRegistry.removeAmount(coins, lostN); //Call before removing item!
                    coins.setAmount((coins.getAmount()-(64-itemStack.getAmount())));
                    totalCount = 64;
                    if(inventory.firstEmpty() != -1) {
                        inventory.setItem(inventory.firstEmpty(), coins);
                    }
                }
                else {
                    ServiceManager.getService(TeamService.class).teamCoinRegistry.removeAmount(coins, lostN); //Call before removing item!
                }
                itemStack.setAmount(totalCount);
                ServiceManager.getService(TeamService.class).teamCoinRegistry.addAmount(targetID, toBeAdded);
                break;
            }
        }
        if(!foundExisting){
            inventory.addItem(coins);
        }
    }


    public static void dupeAlert(Player player){
        ServiceManager.getService(TeamService.class).messagePlayer(player, "§cPlease contact an administrator. (Duplicated coins found)");
        String msg = "§f§l" +player.getName() + " has interacted with duplicated coins!";
        msg += "\n§cLocation: " + player.getLocation().toVector().toString();
        Bukkit.broadcast(Component.text(msg), "core.staff");
        Bukkit.getLogger().info(msg);
    }

    @CommandParams(minArgs = 1)
    protected boolean onCommand(CommandSender commandSender, String s, String[] args) throws Exception {
        if(!StringUtils.isNumeric(args[0])) return false;
        Player player = (Player) commandSender;
        int amount = Integer.parseInt(args[0]);
        String id = UUID.randomUUID().toString();
        String date = DateUtils.getCurrentDate();
        ItemStack coins = getCoins(player, amount, id,date);
        giveCoinsToPlayer(player, coins);
        return true;
    }
}



