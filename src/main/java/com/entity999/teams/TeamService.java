package com.entity999.teams;

import com.entity999.core.CustomConfig;
import com.entity999.core.events.SQLLoadEvent;

import com.entity999.core.services.Service;
import com.entity999.core.services.ServiceManager;
import com.entity999.core.utils.ColorUtils;
import com.entity999.core.utils.protocol.NBTUtils;
import com.entity999.teams.achievements.TeamAchievements;
import com.entity999.teams.coins.TeamCoinRegistry;
import com.entity999.teams.commands.teams.Command_givecoins;
import com.entity999.teams.events.*;
import com.entity999.teams.leaderboards.LeaderboardType;
import com.entity999.teams.leaderboards.TeamLeaderBoards;
import com.entity999.teams.leaderboards.TeamLeaderboardManager;

import com.entity999.teams.lang.TeamMessages;

import com.entity999.teams.leaderboards.TeamLeaderboardRank;
import com.entity999.teams.users.TeamUser;
import com.entity999.teams.villages.VillageService;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ru.xezard.glow.data.glow.Glow;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;


public class TeamService extends Service {
    public static double CREATE_COST;
    public static double MAX_CLAN_SIZE;
    public TeamCoinRegistry teamCoinRegistry = new TeamCoinRegistry();

    private HashMap<String, List<ServerTeam>> pendingInvites;

    private HashMap<UUID, ServerTeam> cachedTeams;
    private HashMap<UUID, ServerTeam> cachedActiveTeams;


    //this is used to access modifiable data
    private HashMap<UUID, ServerTeam> onlineTeams;

    private HashMap<String, ServerTeam> onlineTeamsByName;

    private TeamLeaderboardManager teamLeaderboardManager;

    private HashMap<String, Glow> teamGlows;

    public TeamService(){
        super( (ColorUtils.fadeString(TeamMessages.CLANS, ColorUtils.hex2Rgb("#02811b"), ColorUtils.hex2Rgb("#50c878"))), true);
        pendingInvites = new HashMap<>();
        teamGlows = new HashMap<>();
        cachedTeams = new HashMap<>();
        onlineTeams = new HashMap<>();
        cachedActiveTeams = new HashMap<>();
        onlineTeamsByName = new HashMap<>();
        teamLeaderboardManager = new TeamLeaderboardManager();
    }

    public ServerTeam getOnlineTeam(UUID id){
        return onlineTeams.get(id);
    }

    public ServerTeam getOnlineTeam(String name){
        return onlineTeamsByName.get(name);
    }

    public static ServerTeam getPlayerTeam(Player player){
        return TeamMain.getUserManager().getVoxelUser(player.getUniqueId()).getOnlineTeam();
    }

    public static ServerTeam getPlayerTeam(UUID uuid){
        return TeamMain.getUserManager().getVoxelUser(uuid).getOnlineTeam();
    }

    public CompletableFuture<OfflineTeam> getOfflineTeam(UUID id){
        if(onlineTeams.containsKey(id)){
            return CompletableFuture.supplyAsync(()->onlineTeams.get(id));
        }
        return TeamMain.getMainStorage().getOfflineTeam(id);
    }

    public CompletableFuture<OfflineTeam> getOfflineTeam(String name){
        if(onlineTeamsByName.containsKey(name)){
            return CompletableFuture.supplyAsync(()->onlineTeamsByName.get(name));
        }
        return TeamMain.getMainStorage().getOfflineTeam(name);
    }

    @Override
    public void onRegister() throws Exception {
        TeamAchievements.init();
        loadConfig();
        teamExperienceFromPlayTask();
    }

    //called in async prelogin
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPreJoin(TeamUserInitEvent event){
        TeamUser user = event.getUser();
        if(user.getTeam() == null) return;
        ServerTeam team = onlineTeams.get(user.getTeam());
        if(team == null){
            team = TeamMain.getMainStorage().getTeamSync(user.getTeam());
        }
        event.getUser().setOnlineTeam(team);
        onlineTeams.put(team.getUuid(), team);
        onlineTeamsByName.put(team.getName(), team);
    }


    private void teamExperienceFromPlayTask(){
        Bukkit.getScheduler().runTaskTimerAsynchronously(main, ()->{
           for(ServerTeam team : onlineTeams.values()){
               int amount = team.getOnlineActiveMembers() * 5;
               team.addExperience(amount);
               messageMembers(team, TeamMessages.format(TeamMessages.CLAN_XP_RECEIVE_FROM_PLAY,
                       amount), true);
           }
        },0, 20*60*10);
    }

    private void loadConfig(){
        CREATE_COST = getDefaultConfig().getDouble("create-cost");
        MAX_CLAN_SIZE = getDefaultConfig().getInt("max-clan-size");
    }

    @Override
    public CustomConfig getDefaultConfig() {
        return TeamMain.instance.getConfigManager(TeamConfigManager.class).teams;
    }

    @Override
    public boolean reloadService() {
        loadConfig();
        return true;
    }

    public ServerTeam createTeam(String name, Player player){
        UUID uuid = UUID.randomUUID();
        var user = TeamMain.getUserManager().getVoxelUser(player.getUniqueId());
        ServerTeam team = new ServerTeam(uuid, name, player.getUniqueId(), TeamSettings.getDefaultInstance(), 0);
        TeamCreateEvent teamCreateEvent = new TeamCreateEvent(player, team);
        Bukkit.getScheduler().runTask(main, ()->Bukkit.getPluginManager().callEvent(teamCreateEvent));
        if(teamCreateEvent.isCancelled()) return null;
        loadTeam(team);
        team.addMember(player.getUniqueId());
        team.setPlayerRank(player.getUniqueId(), TeamRank.LEADER);
        addMemberToTeam(uuid, player);
        TeamMain.getMainStorage().saveTeam(team);
        return team;
    }

    private boolean loadTeam(ServerTeam team){
        if(team == null) return false;
        onlineTeams.put(team.getUuid(), team);
        onlineTeamsByName.put(team.getName().toLowerCase(), team);
        cachedActiveTeams.put(team.getUuid(), team);
        cachedTeams.put(team.getUuid(), team);
        Bukkit.getScheduler().runTask(main, ()->Bukkit.getPluginManager().callEvent(new TeamLoadEvent(team)));
        return true;
    }

    public CompletableFuture<ServerTeam> getTeam(UUID teamUUID){
        if(teamUUID == null) return null;
        if(!onlineTeams.containsKey(teamUUID)){
            return TeamMain.getMainStorage().getTeam(teamUUID).whenComplete((team, e) -> {
                TeamMain.getMainStorage().saveTeam(team);
            });
        }
        else {
            return CompletableFuture.supplyAsync(()->onlineTeams.get(teamUUID));
        }
    }

    public CompletableFuture<ServerTeam> getTeam(String name){
        if(!onlineTeamsByName.containsKey(name)){
            return TeamMain.getMainStorage().getTeam(name).whenComplete((team, e) -> {
                TeamMain.getMainStorage().saveTeam(team);
            });
        }
        else {
            return CompletableFuture.supplyAsync(()->onlineTeamsByName.get(name));
        }
    }


    public ServerTeam getTeamFast(UUID teamUUID){
        return onlineTeams.get(teamUUID);
    }

    public ServerTeam getTeamByNameFast(String name){
        return onlineTeamsByName.get(name.toLowerCase());
    }

    public void addMemberToTeam(UUID team, Player player){
        if(team == null) return;
        ServerTeam targetTeam = getOnlineTeam(team);
        Bukkit.getPluginManager().callEvent(new PlayerJoinTeamEvent(player, targetTeam));
        targetTeam.addMember(player.getUniqueId());
        targetTeam.addOnlineMember(player.getUniqueId());
        TeamMain.getUserManager().getVoxelUser(player.getUniqueId()).setTeam(targetTeam);
    }

    public void leaveFromTeam(UUID teamID, UUID member){
        if(teamID == null) return;
        getOfflineTeam(teamID).thenAccept(team -> leaveFromTeam(team, member));
    }

    public void leaveFromTeam(OfflineTeam team, UUID member){
        if(team.isOnline()){
            messageMembers(team.getOnlineTeam(), TeamMessages.format(TeamMessages.PLAYER_LEFT_CLAN_ANNOUNCE, Bukkit.getPlayer(member).getName()), true);
            team.getOnlineTeam().removeOnlineMember(member);
        }
        team.removePlayer(member);
        TeamMain.getUserManager().getOfflineUser(member).thenAccept(user -> {
            user.setTeam(null);
            if(user.isOnline()) user.getTeamUser().setOnlineTeam(null);
        });
    }

    public void deleteTeam(UUID team){
        getOfflineTeam(team).thenAccept(this::deleteTeam);
    }

    public void deleteTeam(OfflineTeam targetTeam){
        UUID team = targetTeam.getUuid();
        leaveFromTeam(targetTeam, targetTeam.getOwner());
        onlineTeams.remove(team);
        onlineTeamsByName.remove(targetTeam.getName().toLowerCase());
        cachedActiveTeams.remove(team);
        cachedTeams.remove(team);
        for(UUID ally : targetTeam.getAllies()){
            getOfflineTeam(ally).thenAccept(offlineTeam -> offlineTeam.removeAlly(team));
        }
        TeamMain.getMainStorage().deleteTeam(targetTeam);
        ServiceManager.getService(VillageService.class).deleteVillage(null, targetTeam);
    }

    public void updateCachedTeams(){
        Bukkit.getScheduler().runTaskTimerAsynchronously(main, ()->{
            HashMap<UUID, ServerTeam> newTeams = new HashMap<>();
            HashMap<UUID, ServerTeam> newActiveTeams = new HashMap<>();
            for(ServerTeam team : TeamMain.getMainStorage().getAllTeamsSet()){
                cachedTeams.put(team.getUuid(), team);
                newTeams.put(team.getUuid(), team);
                for(OfflinePlayer p : team.getMembers()){
                    boolean pOnline = p.isOnline();
                    if(pOnline || (((System.currentTimeMillis() - p.getLastSeen()) / 1000) / 60) / 60 < 7 * 24){
                        if(!newActiveTeams.containsKey(team.getUuid())){
                            newActiveTeams.put(team.getUuid(), team);
                            if(pOnline){
                                ServerTeam onlineTeam = getPlayerTeam(p.getUniqueId());
                                newActiveTeams.put(onlineTeam.getUuid(), onlineTeam);
                            }
                        }
                        if(pOnline){
                            break;
                        }
                    }
                }
            }
            cachedTeams = newTeams;
            cachedActiveTeams = newActiveTeams;
            Bukkit.getScheduler().runTask(main, ()->Bukkit.getPluginManager().callEvent(new TeamsCachedEvent()));
        }, 0, 20 * 30);
    }

    @EventHandler
    public void onTeamCache(TeamsCachedEvent event){
        teamCoinRegistry.initTeams(main, getCachedTeams());
    }

    public Collection<ServerTeam> getCachedTeams() {
        return cachedTeams.values();
    }

    public Collection<ServerTeam> getCachedActiveTeams() {
        return cachedActiveTeams.values();
    }

    public void addNewCachedTeam(ServerTeam team){
        cachedTeams.put(team.getUuid(), team);
    }


    public ServerTeam getCachedTeamByUUID(UUID uuid){
        return cachedTeams.get(uuid);
    }

    public ServerTeam getCachedActiveTeamByUUID(UUID uuid){
        return cachedActiveTeams.get(uuid);
    }

    @EventHandler
    public void onSQL(SQLLoadEvent event){
        if(!event.getSqlStorage().getClass().equals(TeamSQLStorage.class)) return;
        teamCoinRegistry.init();
        teamLeaderboardManager.startTasks();
        updateCachedTeams();
    }



    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onItemPickUp(EntityPickupItemEvent event){
        if(event.getEntity().getType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        ItemStack item = event.getItem().getItemStack();
        if(item.getType() != Material.GOLD_NUGGET) return;
        String coinID = NBTUtils.getItemTag("clan-coin-id", item);
        if(coinID != null && !coinID.isEmpty()){
            ServiceManager.getService(TeamService.class).teamCoinRegistry.addAmount(coinID, item.getAmount());
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_CHAIN_PLACE, 0.7f, 1.5f);
            Command_givecoins.giveCoinsToPlayer(player, item);
            event.setCancelled(true);
            event.getItem().remove();
        }
    }

    @EventHandler
    public void onDeathWithCoins(PlayerDeathEvent event){
        Player player = event.getEntity();
        Iterator<ItemStack> dropItr = event.getDrops().iterator();
        while (dropItr.hasNext()){
            ItemStack itemStack = dropItr.next();
            if(itemStack.getType() != Material.GOLD_NUGGET) continue;
            String coinID = NBTUtils.getItemTag("clan-coin-id", itemStack);
            if(coinID != null && !coinID.isEmpty()){
                int code = ServiceManager.getService(TeamService.class).teamCoinRegistry.removeAmount(itemStack,itemStack.getAmount());
                if(code == 1){
                    player.getWorld().strikeLightning(player.getLocation());
                    player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1f,1f);
                    Command_givecoins.dupeAlert(player);
                    dropItr.remove();
                }
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        if(item.getType() != Material.GOLD_NUGGET) return;
        String coinID = NBTUtils.getItemTag("clan-coin-id", item);
        if(coinID != null && !coinID.isEmpty()){
            int code = ServiceManager.getService(TeamService.class).teamCoinRegistry.removeAmount(item,item.getAmount());
            if(code == 1){
                player.getWorld().strikeLightning(player.getLocation());
                player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1f,1f);
                event.getItemDrop().remove();
                Command_givecoins.dupeAlert(player);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeamCoinClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(event.getInventory().getType() == InventoryType.MERCHANT && event.getRawSlot() <= 2) return;
        if(event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY){
            Inventory source = event.getClickedInventory();
            Inventory target = source.equals(event.getView().getTopInventory()) ? event.getView().getBottomInventory() : event.getView().getTopInventory();

            if(source.getType() == InventoryType.PLAYER && target.getType() == InventoryType.CRAFTING){
                target = event.getView().getBottomInventory();
            }
            ItemStack[] contents = target.getContents();
            if(source instanceof PlayerInventory && target instanceof PlayerInventory){
                contents = IntStream.range(9, 35).boxed().map(target::getItem).toArray(ItemStack[]::new);
            }
            ItemStack clicked = event.getCurrentItem();
            if(clicked == null || clicked.getType() != Material.GOLD_NUGGET) return;
            if(clicked.getAmount() == 64) return;
            if(event.getClick() == ClickType.CREATIVE){
                player.sendMessage("§cPlease do not interact with the coins in creative mode.");
                return;
            }

            String clickedID = NBTUtils.getItemTag("clan-coin-id", clicked);
            if(clickedID == null || clickedID.isEmpty()) return;
            int clickedAmount = clicked.getAmount();
            for(ItemStack itemStack : contents){
                if(itemStack == null) continue;
                if(itemStack.getType() != Material.GOLD_NUGGET) continue;
                if(itemStack.getAmount() == 64) continue;
                String targetID = NBTUtils.getItemTag("clan-coin-id", itemStack);
                if(targetID == null || targetID.isEmpty()) continue;
                if(itemStack.equals(clicked)) continue;
                int totalCount = clickedAmount + itemStack.getAmount();
                int toBeAdded = totalCount - itemStack.getAmount();
                int lostN = clickedAmount;
                int code;
                if(totalCount > 64){
                    toBeAdded = (64-itemStack.getAmount());
                    lostN = toBeAdded;
                    code = ServiceManager.getService(TeamService.class).teamCoinRegistry.removeAmount(clicked, lostN); //Call before removing item!
                    clicked.setAmount((clickedAmount-(64-itemStack.getAmount())));
                    totalCount = 64;
                    if(target.firstEmpty() != -1) {
                        target.setItem(target.firstEmpty(), clicked);
                        source.setItem(source.first(clicked), new ItemStack(Material.AIR));
                    }
                }
                else {
                    code = ServiceManager.getService(TeamService.class).teamCoinRegistry.removeAmount(clicked, lostN); //Call before removing item!
                    clicked.setAmount(player.getItemOnCursor().getAmount() - lostN);
                }
                if(code == 1){
                    Command_givecoins.dupeAlert(player);
                    player.getItemOnCursor().setAmount(0);
                    itemStack.setAmount(0);
                    return;
                }
                event.setCancelled(true);
                itemStack.setAmount(totalCount);
                ServiceManager.getService(TeamService.class).teamCoinRegistry.addAmount(targetID, toBeAdded);
                break;
            }
        }
        else {
            if(event.getCurrentItem() == null || event.getCurrentItem().getType() != Material.GOLD_NUGGET) return;
            if(event.getCursor() == null || event.getCursor().getType() != Material.GOLD_NUGGET) return;

            ItemStack cursor = event.getCursor();
            ItemStack target = event.getCurrentItem();

            String coinIDCursor = NBTUtils.getItemTag("clan-coin-id", cursor);
            if(coinIDCursor == null || coinIDCursor.isEmpty()) return;
            String coinIDTarget = NBTUtils.getItemTag("clan-coin-id", target);
            if(coinIDTarget == null || coinIDTarget.isEmpty()) return;
            if(coinIDCursor.equals(coinIDTarget)) return;
            if(target.getAmount() == 64) return;

            if(event.getClick() == ClickType.CREATIVE){
                player.sendMessage("§cPlease do not interact with the coins in creative mode.");
                return;
            }

            int totalCount = cursor.getAmount() + target.getAmount();
            int lostN = cursor.getAmount();
            int cursorN = cursor.getAmount();
            int toBeAdded = totalCount - target.getAmount();
            int code;
            if(totalCount > 64){
                toBeAdded = (64-target.getAmount());
                lostN = cursorN - player.getItemOnCursor().getAmount();
                code = ServiceManager.getService(TeamService.class).teamCoinRegistry.removeAmount(cursor, lostN); //Call before removing item!
                player.getItemOnCursor().setAmount((cursorN-(64-target.getAmount())));
                totalCount = 64;
            }
            else {
                code = ServiceManager.getService(TeamService.class).teamCoinRegistry.removeAmount(cursor, lostN); //Call before removing item!
                player.getItemOnCursor().setAmount(player.getItemOnCursor().getAmount() - lostN);
            }
            if(code == 1){
                Command_givecoins.dupeAlert(player);
                player.getItemOnCursor().setAmount(0);
                target.setAmount(0);
                return;
            }
            event.setCancelled(true);
            target.setAmount(totalCount);
            ServiceManager.getService(TeamService.class).teamCoinRegistry.addAmount(coinIDTarget, toBeAdded);
        }
    }

    public String getRankString(LeaderboardType type, String uuid){
        TeamLeaderboardRank teamRank = getTeamRank(type, uuid);
        if(teamRank == null) return "N/A";
        int rank = teamRank.getRank();
        net.md_5.bungee.api.ChatColor chatColor;
        if(rank == 1)  chatColor =  net.md_5.bungee.api.ChatColor.of("#DAA520");
        else if(rank == 2) chatColor =  net.md_5.bungee.api.ChatColor.of("#c0c0c0");
        else if(rank == 3) chatColor = net.md_5.bungee.api.ChatColor.of("#cd7f32");
        else chatColor = net.md_5.bungee.api.ChatColor.WHITE;
        return chatColor + "#" + rank;
    }

    public TeamLeaderboardRank getTeamRank(LeaderboardType type, String uuid){
        return teamLeaderboardManager.getRank(type, uuid);
    }

    public TeamLeaderBoards getLeaderboard(LeaderboardType type){
        return teamLeaderboardManager.getLeaderboard(type);
    }

    public void openTeamChest(Player player, UUID teamUUID, int size, int level){
        ServerTeam team = getOnlineTeam(teamUUID);
        if(team.getStorage() == null){
            Inventory tInv = Bukkit.getServer().createInventory(null, size, "Clan vault (Level " + level+")");
            TeamMain.getMainStorage().getItemStackValue("storage", "teams", "uuid", teamUUID.toString(), itemStacks -> {
                if(itemStacks == null) return;
                if(itemStacks.length == 0) return;
                tInv.setContents(itemStacks);
            });
            team.setStorage(tInv);
        }
        Inventory inventory = team.getStorage();
        if(inventory.getSize() != size && inventory.getViewers().size() == 0){
            Inventory newInv = Bukkit.getServer().createInventory(null, size, "Clan vault (Level "+level+")");
            newInv.setContents(inventory.getContents());
            team.setStorage(newInv);
        }
        player.openInventory(inventory);
    }

    public void addInvite(ServerTeam team, String uuid){
        if(!pendingInvites.containsKey(uuid)){
            pendingInvites.put(uuid, new ArrayList<>());
        }
        pendingInvites.get(uuid).add(team);
    }
    public UUID playerInvited(String uuid, String teamName){
        if(pendingInvites.get(uuid) == null) return null;
        for(ServerTeam team : pendingInvites.get(uuid)){
            if(team.nameEquals(teamName)) {
                pendingInvites.get(uuid).remove(team);
                return team.getUuid();
            }
        }
        return null;
    }

    public void messageMembers(ServerTeam team, String message, boolean setPrefix){
        Bukkit.getScheduler().runTaskAsynchronously(main, ()->{
            for(UUID uuid : team.getMemberList()){
                Player player = Bukkit.getPlayer(uuid);
                if(player == null || !player.isOnline()) continue;
                if (setPrefix) {
                    messagePlayer(player, message);
                } else {
                    player.sendMessage(message);
                }
            }
        });
    }

    public void messageMembers(ServerTeam team, BaseComponent[] message, boolean setPrefix){
        Bukkit.getScheduler().runTaskAsynchronously(main, ()->{
            for(UUID uuid : team.getMemberList()){
                Player player = Bukkit.getPlayer(uuid);
                if(player == null || !player.isOnline()) continue;
                if (setPrefix) {
                    messagePlayer(player, message);
                } else {
                    player.sendMessage(message);
                }
            }
        });
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        if(event.getDamager() instanceof Projectile){
            Projectile projectile = (Projectile) event.getDamager();
            if(!(projectile.getShooter() instanceof Player)) return;
            damager = (Player) projectile.getShooter();
        }
        if(!(damager instanceof Player && event.getEntity() instanceof Player)) return;
        if(event.getDamager().equals(event.getEntity())) return;
        if(event.isCancelled()) return;
        ServerTeam t1 = TeamService.getPlayerTeam(damager.getUniqueId());
        ServerTeam t2 = TeamService.getPlayerTeam(event.getEntity().getUniqueId());
        if(t1 == null || t2 == null) return;
        if(t1.getName().equals(t2.getName())){
            if(!t1.getTeamSettings().isFriendlyFire()) event.setCancelled(true);
        }
        else if(t1.getAllies().contains(t2.getUuid())){
            if(!(t1.getTeamSettings().isFriendlyFire() || t2.getTeamSettings().isFriendlyFire())) {
                event.setCancelled(true);
                ServiceManager.getService(TeamService.class).messagePlayer(event.getDamager(), String.format("§e%s §7(%s§7)", event.getEntity().getName(), t2.getName()) + " §cis allied with your team.");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event){
        if(event.isCancelled()) return;
        if(event.getPlayer().hasMetadata("team-chat")){
            ServerTeam team = TeamService.getPlayerTeam(event.getPlayer().getUniqueId());

            String msg = event.getMessage();

            ServiceManager.getService(TeamService.class).messageMembers(team, ChatColor.YELLOW+"["+team.getName()+"] §7" + event.getPlayer().getDisplayName() +"§f: "+ msg, false);
            event.setCancelled(true);
            event.setMessage("");
            event.setFormat("");
            Bukkit.getLogger().info(event.getPlayer().getName() + " said in team chat: " + msg);
        }
        //cancelling the player from receiving the message is handled in MainListener
    }

//    @EventHandler
//    public void onJoin(PlayerJoinEvent event){
//        getPlayerTeam(event.getPlayer().getUniqueId(), team -> {
//            if(team == null) return;
//            team.addOnlineMember(event.getPlayer().getUniqueId());
//            if(!teamGlows.containsKey(team.getUuid())) return;
//            var user = ServiceManager.getService(VoxelNetworkUserManager.class).getVoxelUser(event.getPlayer().getUniqueId());
//            for(Player p : team.getOnlineMembers(event.getPlayer())){
//                if(p.hasMetadata("team-glow")){
//                    Bukkit.getScheduler().runTaskLater(main, ()->{
//                        teamGlows.get(team.getUuid()).addHolders(event.getPlayer());
//                        var pUser = ServiceManager.getService(VoxelNetworkUserManager.class).getVoxelUser(event.getPlayer().getUniqueId());
//                        ServiceManager.getService(ScoreboardService.class).addPlayerToTeam(p, pUser);
//                    }, 40);
//                    break;
//                }
//            }
//            if(event.getPlayer().hasMetadata("team-glow")){
//                Bukkit.getScheduler().runTaskLater(main, ()->{
//                    teamGlows.get(team.getUuid()).display(event.getPlayer());
//                    ServiceManager.getService(ScoreboardService.class).addPlayerToTeam(event.getPlayer(), user);
//                }, 40);
//            }
//        });
//    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        ServerTeam team = getPlayerTeam(event.getPlayer().getUniqueId());
        if(team == null) return;
        if(teamGlows.containsKey(team.getUuid())){
            Glow glow = teamGlows.get(team.getUuid());
            if(glow.getViewers().size() == 0 && glow.getHolders().size() == 0){
                teamGlows.remove(team.getUuid()).destroy();
            }
        }
        int online = team.getOnlineMemberCount();
        team.removeOnlineMember(event.getPlayer().getUniqueId());
        if(online-1 <= 0){
            onlineTeams.remove(team.getUuid());
            onlineTeamsByName.remove(team.getName().toLowerCase());
            TeamMain.getMainStorage().saveTeam(team);
            Bukkit.getPluginManager().callEvent(new TeamSaveEvent(team));
        }
        //Check how many online members in team and if 0 after this event remove team from map
    }

//    public void setTeamMatesGlowing(ServerTeam team, Player player, boolean glow){
//        if(!teamGlows.containsKey(team.getUuid())){
//            teamGlows.put(team.getUuid(), Glow.builder().animatedColor(ChatColor.WHITE).name("tglow").build());
//        }
//        for(Player member : team.getOnlineMembers(player)){
//            if(glow) teamGlows.get(team.getUuid()).addHolders(member);
//            else teamGlows.get(team.getUuid()).removeHolders(member);
//        }
//        if(glow) teamGlows.get(team.getUuid()).display(player);
//        else teamGlows.get(team.getUuid()).hideFrom(player);
//        ServiceManager.getService(ScoreboardService.class).addPlayerToTeam(player, ServiceManager.getService(VoxeliaUserManager.class).getVoxelUser(player.getUniqueId()));
//    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        if(event.getDeathMessage() == null || event.getDeathMessage().isEmpty()) return;
        Player p = event.getEntity();
        ServerTeam team = TeamService.getPlayerTeam(p);
        if(team == null) return;
        messageMembers(team, ChatColor.GRAY + event.getDeathMessage().replace(p.getName(), event.getPlayer().getName()+"§r"), true);
    }
}
