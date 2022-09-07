package com.entity999.teams.coins;

import com.entity999.core.services.ServiceManager;
import com.entity999.core.utils.DateUtils;

import com.entity999.core.utils.protocol.NBTUtils;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamService;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class TeamCoinRegistry {
    //private Main main = Main.getInstance();
    public boolean isLoaded = false;
    private static final int EXPIRY_TIME = 182;

    private HashMap<String, CoinData> coins = new HashMap<>();
    private HashMap<UUID, Integer> coinsPerTeam = new HashMap<>();

    public void init(){
//        TeamMain.instance.getStorage(TeamStorage.class).getClanCoins(coinDataSet -> {
//            for(CoinData coinData : coinDataSet){
//                coins.put(coinData.id, coinData);
//            }
//        });
    }

    public void initTeams(Plugin main, Collection<ServerTeam> teams){
        if(isLoaded) return;
        isLoaded = true;
        Bukkit.getScheduler().runTaskAsynchronously(main, ()->{
            for(ServerTeam team : teams){
                coinsPerTeam.put(team.getUuid(), team.getCoins());
            }
        });
    }

    public void depositForTeam(UUID team, int amount){
        int newAmount = coinsPerTeam.getOrDefault(team,0)+amount;
        coinsPerTeam.put(team, newAmount);
        ServiceManager.getService(TeamService.class).getTeam(team).thenAccept((team1) -> team1.setCoins(newAmount));
    }

    public int getCoinsPerTeam(UUID team){
        return coinsPerTeam.getOrDefault(team, 0);
    }

    public void withdrawFromTeam(UUID team, int amount){
        int newAmount = coinsPerTeam.getOrDefault(team,0)-amount;
        if(newAmount < 0) newAmount = 0;
        coinsPerTeam.put(team, newAmount);
        int finalNewAmount = newAmount;
        ServiceManager.getService(TeamService.class).getTeam(team).thenAccept((team1) -> team1.setCoins(finalNewAmount));
    }

    public void save(){
//        TeamMain.instance.getStorage(VoxeliaSQLStorage.class).updateClanCoins(coins);
//        TeamMain.instance.getStorage(VoxeliaSQLStorage.class).saveClanCoins(coinsPerTeam);
    }

    public void addNewID(CoinData coinData){
        if(coins.containsKey(coinData.id)){
            addAmount(coinData.id, coinData.amountLeft);
            return;
        }
        coins.put(coinData.id, coinData);
    }

    public void addAmount(String id, int amount){
        if(!coins.containsKey(id)) return;
        coins.get(id).amountLeft += amount;
    }

    public int removeAmount(ItemStack itemStack, int amount){
        String id = NBTUtils.getItemTag("clan-coin-id", itemStack);
        if(id == null) return -1; //Error: clan id not in item
        String date = NBTUtils.getItemTag("clan-coin-date", itemStack);
        boolean success = removeAmount(id, date, amount);
        if(success) return 0;
        else return 1;
    }

    public boolean removeAmount(String id, String dateInItem, int amount){
        if(!coins.containsKey(id)){
            long diffDays = DateUtils.getDiffDays(dateInItem);
            if(diffDays > EXPIRY_TIME) return true;
            return false;
        }
        CoinData coinData = coins.get(id);
        int amountInRegistry = coinData.amountLeft;
        if(amount > amountInRegistry) return false;
        coins.get(coinData.id).amountLeft = amountInRegistry-amount;
        return true;
    }
}
