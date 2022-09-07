package com.entity999.teams.villages;

import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.entity999.teams.OfflineTeam;
import com.entity999.teams.TeamMain;
import com.entity999.core.CustomConfig;
import com.entity999.core.events.regions.RegionEnterEvent;
import com.entity999.core.events.regions.RegionLeaveEvent;
import com.entity999.core.regions.RegionManager;
import com.entity999.core.services.Service;
import com.entity999.core.services.ServiceManager;

import com.entity999.core.user.VoxelNetworkUser;
import com.entity999.core.user.VoxelNetworkUserManager;
import com.entity999.core.utils.ColorUtils;
import com.entity999.core.utils.CommonMapUtils;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamService;
import com.entity999.teams.events.TeamsCachedEvent;
import com.entity999.teams.events.VillageCreateEvent;
import com.entity999.teams.lang.TeamMessages;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.events.ClaimCreatedEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Switch;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


class ActiveSelection{
    public World world;
    public Vector cornerTop;
    public Vector cornerBottom;
    public boolean resizing;
    public ClaimBarTask claimBarTask;
    public int maxBlocks;
}

public class VillageService extends Service {

    private boolean initialLoadDone = false;
    private HashMap<UUID, ClanVillageClaim> playersInsideRegion;
    private HashMap<UUID, Claim> playersInsideLandClaims;

    private HashMap<UUID, ActiveSelection> activeSelections;
    private RegionManager regionManager;

    private final Material claimItem = Material.GOLDEN_HOE;

    public static int DEFAULT_CLAIM_BLOCKS;
    public static int MIN_CLAIM_AREA;
    public static int MAX_CLAIM_BLOCKS;


    public VillageService() {
        super(ColorUtils.fadeString(TeamMessages.VILLAGES, ColorUtils.hex2Rgb("#02811b"), ColorUtils.hex2Rgb("#50c878")), true);
        this.activeSelections = new HashMap<>();
        this.playersInsideRegion = new HashMap<>();
        this.playersInsideLandClaims = new HashMap<>();
        //villageScanTask();
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }



    public void villageScanTask(){
//        World world = main.getServer().getWorld("world");
//        long timeToNight = world.getTime() <= 12000 ? 12000 - world.getTime() : 36000-world.getTime();
//        new VillageScanTask(main, villageClaimStorage).runTaskTimer(main, 0, 20 * 30);
    }



    public void addVillageToMap(ClanVillageClaim area){
        String clanName = ServiceManager.getService(TeamService.class).getCachedTeamByUUID(area.getOwningClan()).getName();
        String markerid = area.getOwningClan().toString();
        CommonMapUtils.addRectMarker("Guild_Towns", markerid, TeamMessages.format(TeamMessages.VILLAGE_DYNMAP_DESC, area.getVillageName(), clanName),
                area.getWorld().getName(), (int) area.getMinX(), (int) area.getMinZ(), (int) area.getMaxX(),(int)  area.getMaxZ());
    }

    public void updateAreaMarker(ClanVillageClaim area, boolean delete){
        if(area == null) return;
        String markerid = area.getOwningClan().toString();

        if(delete){
            CommonMapUtils.removeMarker(area.getWorld().getName(), "Guild_Towns", markerid);
            return;
        }

        CommonMapUtils.updateRectMarkerDimensions("Guild_Towns", markerid, area.getWorld().getName(),
                (int) area.getMinX(), (int) area.getMinZ(), (int) area.getMaxX(), (int) area.getMaxZ());

    }

    public ClanVillageClaim getVillageClaimAt(Location location){
        return regionManager.getTypeRegionAt(location, ClanVillageClaim.class);
    }


    @EventHandler
    public void loadVillageClaims(TeamsCachedEvent event){
        if(initialLoadDone) return;
        TeamMain.getMainStorage().getAllVillages(ServiceManager.getService(TeamService.class).getCachedTeams(), claimAreas -> {
            for(ClanVillageClaim area : claimAreas){
                area.setID(area.getOwningClan());
                regionManager.addNewRegion(area, false);
                addVillageToMap(area);
            }
            initialLoadDone = true;
        });
    }

    @Override
    public void onRegister() throws Exception {
        this.regionManager = ServiceManager.getService(RegionManager.class);
        reloadService();
    }

    @Override
    public boolean reloadService() {
        DEFAULT_CLAIM_BLOCKS = getDefaultConfig().getInt("defaultClaimBlocks");
        MIN_CLAIM_AREA =  getDefaultConfig().getInt("minClaimArea");
        MAX_CLAIM_BLOCKS =  getDefaultConfig().getInt("maxClaimBlocks");
        return true;
    }

    @Override
    public CustomConfig getDefaultConfig() {
        return TeamMain.getMainConfig().villages;
    }


    public void resetBlockChange(Location location, Player player){
        Material blockMat = location.getBlock().getType();
        Bukkit.getScheduler().runTaskLater(main, ()->player.sendBlockChange(location, blockMat
                .createBlockData()), 5);
    }


    private void createSelectionBossBar(Player player, ActiveSelection activeSelection, int maxBlocks){
        BossBar bossBar = Bukkit.getServer().createBossBar("", BarColor.GREEN, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.addPlayer(player);
        ClaimBarTask claimBarTask = new ClaimBarTask(activeSelection, bossBar, player, maxBlocks);
        activeSelection.claimBarTask = claimBarTask;
        activeSelection.maxBlocks = maxBlocks;
        claimBarTask.runTaskTimerAsynchronously(main, 0, 20);
    }

    public void setActiveSelectionCorner(Player player, Location location, boolean first, boolean resizing, ServerTeam team){


        if(!activeSelections.containsKey(player.getUniqueId())){
            Bukkit.getScheduler().runTaskLater(main, ()->player.sendBlockChange(location, Material.GLOWSTONE
                    .createBlockData()), 5);
            ActiveSelection activeSelection = new ActiveSelection();
            activeSelection.world = location.getWorld();
            activeSelection.resizing = resizing;
            if(first) activeSelection.cornerTop = location.toVector();
            else activeSelection.cornerBottom = location.toVector();
            activeSelections.put(player.getUniqueId(), activeSelection);
            if(first){
                createSelectionBossBar(player, activeSelection, team.getMaxVillageClaimBlocks());
            }
        }
        else {
            Bukkit.getScheduler().runTaskLater(main, ()->player.sendBlockChange(location, Material.GLOWSTONE
                    .createBlockData()), 5);
            ActiveSelection activeSelection = activeSelections.get(player.getUniqueId());
            if(first) {
                if(activeSelection.claimBarTask == null) createSelectionBossBar(player, activeSelection, team.getMaxVillageClaimBlocks());
                if(activeSelection.cornerTop != null){
                    Location blockLoc = activeSelection.cornerTop.toLocation(activeSelection.world);
                    resetBlockChange(blockLoc, player);
                }
                activeSelection.resizing = false;
                activeSelection.cornerTop = location.toVector();
            }
            else{
                if(activeSelection.cornerBottom != null){
                    Location blockLoc = activeSelection.cornerBottom.toLocation(activeSelection.world);
                    resetBlockChange(blockLoc, player);
                }
                activeSelection.cornerBottom = location.toVector();
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if(event.isCancelled()) return;
        Block block = event.getBlock();
        ClanVillageClaim area = getVillageClaimAt(block.getLocation());
        if(area != null){
            if(disallowPlayerInteract(event.getPlayer(), area, TeamMessages.CLAN_VILLAGE_NO_BUILD)) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if(event.isCancelled()) return;
        Block block = event.getBlock();
        ClanVillageClaim area = getVillageClaimAt(block.getLocation());
        if(area != null){
            //@TODO change format to include village name
            if(disallowPlayerInteract(event.getPlayer(), area, TeamMessages.format(TeamMessages.CLAN_VILLAGE_NO_BUILD, area.getVillageName()))) event.setCancelled(true);
        }
    }

    public boolean disallowPlayerInteract(Player player, ClanVillageClaim area, String msg){
        UUID uuid = area.getOwningClan();
        VoxelNetworkUser voxeliaServerUser = ServiceManager.getService(VoxelNetworkUserManager.class).getVoxelUser(player.getUniqueId());

        if(voxeliaServerUser.getKeyStorage().getBoolean("ignoring_villages")) return false;
        ServerTeam playerTeam = TeamService.getPlayerTeam(player.getUniqueId());
        ServerTeam teamName = ServiceManager.getService(TeamService.class).getCachedTeamByUUID(uuid);
        if(teamName == null){
            return true;
        }
        if(playerTeam == null || !playerTeam.getUuid().equals(uuid)){
            messagePlayer(player, String.format(msg, teamName.getName()));
            return true;
        }
        return false;
    }

    public void deleteVillage(Player player, OfflineTeam team){
        ClanVillageClaim area = (ClanVillageClaim) regionManager.getByID(team.getUuid());
        if(area == null) return;
        updateAreaMarker(area, true);
        regionManager.deleteRegion(team.getUuid());
        if(player != null && team.isOnline()){
            ServiceManager.getService(TeamService.class).messageMembers(team.getOnlineTeam(), TeamMessages.format(TeamMessages.CLAN_VILLAGE_DELETED, player.getName()), true);
        }
        TeamMain.getMainStorage().saveVillage(area, true);
    }

    @EventHandler
    public void preventMobLeavingVillage(EntityPathfindEvent event){
        if(event.getEntity() instanceof Tameable || event.getEntity() instanceof Villager){
            if(event.getEntity() instanceof Tameable){
                if(!((Tameable)event.getEntity()).isTamed()) return;
            }
            ClanVillageClaim sourceArea = getVillageClaimAt(event.getEntity().getLocation());
            if(sourceArea == null) return;
            ClanVillageClaim targetArea = getVillageClaimAt(event.getLoc());
            if(!sourceArea.equals(targetArea)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFluidFlow(BlockFromToEvent event){
        Block sourceBlock = event.getBlock();
        ClanVillageClaim sourceArea = getVillageClaimAt(sourceBlock.getLocation());
        Block block = event.getToBlock();
        ClanVillageClaim area = getVillageClaimAt(block.getLocation());
        if(area != null){
            if(area.equals(sourceArea)) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event){
        Block block = event.getBlockClicked();
        if(block == null) return;
        ClanVillageClaim area = getVillageClaimAt(block.getLocation());
        if(area != null){
            if(disallowPlayerInteract(event.getPlayer(), area, TeamMessages.CLAN_VILLAGE_NO_BUILD)) event.setCancelled(true);
        }
    }

    @EventHandler
    public void stopMobTarget(EntityTargetLivingEntityEvent event){
        if(!(event.getTarget() instanceof Player)) return;
        Player player = (Player) event.getTarget();
        ClanVillageClaim area = getVillageClaimAt(player.getLocation());
        ServerTeam team = TeamService.getPlayerTeam(player.getUniqueId());
        if(area == null){
            ClanVillageClaim mobArea = getVillageClaimAt(event.getEntity().getLocation());
            if(mobArea == null) return;
            if(team == null || !mobArea.getOwningClan().equals(team.getUuid())){
                event.setCancelled(true);
            }
            return;
        }
        if(team == null || !area.getOwningClan().equals(team.getUuid())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void preventFarmlandDestroy(PlayerInteractEvent event){
        if(!event.getAction().equals(Action.PHYSICAL)) return;
        if(event.getClickedBlock() == null || !event.getClickedBlock().getType().equals(Material.FARMLAND)) return;
        ClanVillageClaim area = getVillageClaimAt(event.getClickedBlock().getLocation());
        if(area != null){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void preventArmorstandDestroy(PlayerArmorStandManipulateEvent event){
        ClanVillageClaim area = getVillageClaimAt(event.getRightClicked().getLocation());
        if(area == null) return;
        ServerTeam team = TeamService.getPlayerTeam(event.getPlayer().getUniqueId());
        if(team == null || !area.getOwningClan().equals(team.getUuid())){
            if(disallowPlayerInteract(event.getPlayer(), area, TeamMessages.CLAN_VILLAGE_NO_USE_PERM)) event.setCancelled(true);
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() == null) return;
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        if(event.getClickedBlock() != null){
            if(event.getClickedBlock().getBlockData() instanceof Door) return;
        }
        if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;
        if(event.getInteractionPoint() == null) return;
        ClanVillageClaim area = getVillageClaimAt(event.getInteractionPoint());
        if(area != null){
            if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                Material type = event.getPlayer().getInventory().getItemInMainHand().getType();
                if(!type.equals(Material.AIR)){
                    if(type.equals(Material.FLINT_AND_STEEL)
                            || type.equals(Material.FIRE_CHARGE)
                            || type.name().contains("SPAWN_EGG")){
                        if(disallowPlayerInteract(event.getPlayer(), area, TeamMessages.CLAN_VILLAGE_NO_BUILD)) event.setCancelled(true);
                    }
                }
                if(event.getClickedBlock().getState() instanceof InventoryHolder){
                    if(disallowPlayerInteract(event.getPlayer(), area, TeamMessages.CLAN_VILLAGE_NO_OPEN)) event.setCancelled(true);
                }
                else if(event.getClickedBlock().getBlockData() instanceof Switch){
                    if(disallowPlayerInteract(event.getPlayer(), area, TeamMessages.CLAN_VILLAGE_NO_USE_PERM)) event.setCancelled(true);
                }
                else if(event.getClickedBlock().getType().equals(Material.ITEM_FRAME)){
                    if(disallowPlayerInteract(event.getPlayer(), area, TeamMessages.CLAN_VILLAGE_NO_USE_PERM)) event.setCancelled(true);
                }

                return;
            }
            if(disallowPlayerInteract(event.getPlayer(), area, TeamMessages.CLAN_VILLAGE_BELONGS)) event.setCancelled(true);
        }
    }

    @EventHandler
    public void preventHangingBreak(HangingBreakByEntityEvent event){
        if(!(event.getRemover() instanceof Player)) return;
        Player player = (Player) event.getRemover();
        ClanVillageClaim area = getVillageClaimAt(player.getLocation());
        if(area == null) return;
        ServerTeam team = TeamService.getPlayerTeam(player.getUniqueId());
        if(team == null || !area.getOwningClan().equals(team.getUuid())){
            if(disallowPlayerInteract(player, area, TeamMessages.CLAN_VILLAGE_NO_USE_PERM)) event.setCancelled(true);
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        if(event.getDamager() instanceof Projectile){
            Projectile projectile = (Projectile) event.getDamager();
            if(!(projectile.getShooter() instanceof Player)) return;
            damager = (Player) projectile.getShooter();
        }
        if(!(damager instanceof Player)) return;
        Player damagerPlayer = (Player)damager;
        if(!(event.getEntity() instanceof Player)){
            if(event.getEntity() instanceof Monster) return;
            ClanVillageClaim area1 = getVillageClaimAt(damager.getLocation());
            if(area1 != null){
                if(disallowPlayerInteract(damagerPlayer, area1, TeamMessages.CLAN_VILLAGE_BELONGS)) event.setCancelled(true);
                return;
            }
            else {
                ClanVillageClaim area2 = getVillageClaimAt(event.getEntity().getLocation());
                if(area2 != null){
                    if(disallowPlayerInteract(damagerPlayer, area2, TeamMessages.CLAN_VILLAGE_BELONGS)) event.setCancelled(true);
                }
            }
        }

        if(event.getDamager().equals(event.getEntity())) return;
        if(!(event.getEntity() instanceof Player)) return;
        if(event.isCancelled()) return;


        ClanVillageClaim area1 = getVillageClaimAt(damager.getLocation());
        if(area1 != null){
            messagePlayer(event.getDamager(), TeamMessages.format(TeamMessages.CLAN_VILLAGE_NO_PVP, ServiceManager.getService(TeamService.class).getCachedTeamByUUID(area1.getOwningClan()).getName()));
            event.setCancelled(true);

        }
        else{
            ClanVillageClaim area2 = getVillageClaimAt(event.getEntity().getLocation());
            if(area2 != null){
                messagePlayer(event.getDamager(), TeamMessages.format(TeamMessages.CLAN_VILLAGE_NO_PVP, ServiceManager.getService(TeamService.class).getCachedTeamByUUID(area2.getOwningClan()).getName()));
                event.setCancelled(true);
            }
        }
    }
    public void createClaimPattern(ClanVillageClaim area, Player player){
        Bukkit.getScheduler().runTaskAsynchronously(main, ()->{
            Vector top = area.getCornerTop();
            Vector bottom = area.getCornerBottom();

            boolean topIsLeftX = (top.getX() - bottom.getX()) < 0;
            boolean topIsUpZ = (top.getZ() - bottom.getZ()) < 0;

            Location dirIndicatorXTop = new Location(area.getWorld(), topIsLeftX ? top.getBlockX()+1 : top.getBlockX()-1, top.getBlockY(), top.getBlockZ());
            Location dirIndicatorZTop = new Location(area.getWorld(), top.getBlockX(), top.getBlockY(), topIsUpZ ? top.getBlockZ()+1 : top.getBlockZ()-1);

            Location dirIndicatorXBottom = new Location(area.getWorld(), topIsLeftX ? bottom.getBlockX()-1 : bottom.getBlockX()+1, bottom.getBlockY(), bottom.getBlockZ());
            Location dirIndicatorZBottom = new Location(area.getWorld(), bottom.getBlockX(), bottom.getBlockY(), topIsUpZ ? bottom.getBlockZ()-1 : bottom.getBlockZ()+1);

            Location topBlock = new Location(area.getWorld(), top.getBlockX(), top.getBlockY(), top.getBlockZ());
            Location bottomBlock = new Location(area.getWorld(), bottom.getBlockX(), bottom.getBlockY(), bottom.getBlockZ());
            Location corner3 =  new Location(area.getWorld(), bottom.getBlockX(), top.getBlockY(), top.getBlockZ());
            Location corner4 =  new Location(area.getWorld(), top.getBlockX(), top.getBlockY(), bottom.getBlockZ());

            Location dirIndicatorXCorner3 = new Location(area.getWorld(), topIsLeftX ? corner3.getBlockX()-1 : corner3.getBlockX()+1, corner3.getBlockY(), corner3.getBlockZ());
            Location dirIndicatorZCorner3 = new Location(area.getWorld(), corner3.getBlockX(), corner3.getBlockY(), topIsUpZ ? corner3.getBlockZ()+1 : corner3.getBlockZ()-1);

            Location dirIndicatorXCorner4 = new Location(area.getWorld(), topIsLeftX ? corner4.getBlockX()+1 : corner4.getBlockX()-1, corner4.getBlockY(),  corner4.getBlockZ());
            Location dirIndicatorZCorner4 = new Location(area.getWorld(), corner4.getBlockX(), corner4.getBlockY(), topIsUpZ ? corner4.getBlockZ()-1 : corner4.getBlockZ()+1);

            dirIndicatorXTop.setY(area.getWorld().getHighestBlockYAt(dirIndicatorXTop));
            dirIndicatorZTop.setY(area.getWorld().getHighestBlockYAt(dirIndicatorZTop));
            dirIndicatorXBottom.setY(area.getWorld().getHighestBlockYAt(dirIndicatorXBottom));
            dirIndicatorZBottom.setY(area.getWorld().getHighestBlockYAt(dirIndicatorZBottom));
            corner3.setY(area.getWorld().getHighestBlockYAt(corner3));
            corner4.setY(area.getWorld().getHighestBlockYAt(corner4));
            dirIndicatorXCorner3.setY(area.getWorld().getHighestBlockYAt(dirIndicatorXCorner3));
            dirIndicatorZCorner3.setY(area.getWorld().getHighestBlockYAt(dirIndicatorZCorner3));
            dirIndicatorXCorner4.setY(area.getWorld().getHighestBlockYAt(dirIndicatorXCorner4));
            dirIndicatorZCorner4.setY(area.getWorld().getHighestBlockYAt(dirIndicatorZCorner4));

            player.sendBlockChange(topBlock, Material.GLOWSTONE.createBlockData());
            player.sendBlockChange(bottomBlock, Material.GLOWSTONE.createBlockData());
            player.sendBlockChange(corner3, Material.GLOWSTONE.createBlockData());
            player.sendBlockChange(corner4, Material.GLOWSTONE.createBlockData());

            player.sendBlockChange(dirIndicatorXBottom, Material.SEA_LANTERN.createBlockData());
            player.sendBlockChange(dirIndicatorZBottom, Material.SEA_LANTERN.createBlockData());
            player.sendBlockChange(dirIndicatorXTop, Material.SEA_LANTERN.createBlockData());
            player.sendBlockChange(dirIndicatorZTop, Material.SEA_LANTERN.createBlockData());
            player.sendBlockChange(dirIndicatorXCorner3, Material.SEA_LANTERN.createBlockData());
            player.sendBlockChange(dirIndicatorZCorner3, Material.SEA_LANTERN.createBlockData());
            player.sendBlockChange(dirIndicatorXCorner4, Material.SEA_LANTERN.createBlockData());
            player.sendBlockChange(dirIndicatorZCorner4, Material.SEA_LANTERN.createBlockData());

            Bukkit.getScheduler().runTaskLaterAsynchronously(main, ()->{
                player.sendBlockChange(topBlock, topBlock.getBlock().getType().createBlockData());
                player.sendBlockChange(bottomBlock, bottomBlock.getBlock().getType().createBlockData());
                player.sendBlockChange(corner3, corner3.getBlock().getType().createBlockData());
                player.sendBlockChange(corner4, corner4.getBlock().getType().createBlockData());

                player.sendBlockChange(dirIndicatorXBottom, dirIndicatorXBottom.getBlock().getType().createBlockData());
                player.sendBlockChange(dirIndicatorZBottom, dirIndicatorZBottom.getBlock().getType().createBlockData());
                player.sendBlockChange(dirIndicatorXTop, dirIndicatorXTop.getBlock().getType().createBlockData());
                player.sendBlockChange(dirIndicatorZTop, dirIndicatorZTop.getBlock().getType().createBlockData());
                player.sendBlockChange(dirIndicatorXCorner3, dirIndicatorXCorner3.getBlock().getType().createBlockData());
                player.sendBlockChange(dirIndicatorZCorner3, dirIndicatorZCorner3.getBlock().getType().createBlockData());
                player.sendBlockChange(dirIndicatorXCorner4, dirIndicatorXCorner4.getBlock().getType().createBlockData());
                player.sendBlockChange(dirIndicatorZCorner4, dirIndicatorZCorner4.getBlock().getType().createBlockData());
            }, 20 * 30);
        });
    }


    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event){
        ItemStack itemHeld = event.getPlayer().getInventory().getItem(event.getNewSlot());
        if(itemHeld != null && itemHeld.getType().equals(claimItem)){
            ClanVillageClaim clanVillageClaim = getVillageClaimAt(event.getPlayer().getLocation());

            ServerTeam serverTeam =  TeamService.getPlayerTeam(event.getPlayer().getUniqueId());
            if(serverTeam == null) return;
            Claim playerClaim = GriefPrevention.instance.dataStore.getClaimAt(event.getPlayer().getLocation(), true, null);
            if(clanVillageClaim == null){
                if(playerClaim != null && !playerClaim.canSiege(event.getPlayer())){
                    event.getPlayer().sendMessage(TeamMessages.VILLAGES_CANT_MAKE_CLAIM_INSIDE_ANOTHER);
                    return;
                }
                event.getPlayer().sendMessage(TeamMessages.VILLAGE_CREATE_TIP);
            }
            else if(clanVillageClaim.getOwningClan().equals(serverTeam.getUuid())){
                event.getPlayer().sendMessage(TeamMessages.VILLAGE_EXPANSION_TIP);
                createClaimPattern(clanVillageClaim, event.getPlayer());
            }
            else {
                event.getPlayer().sendMessage(TeamMessages.VILLAGES_CANT_MAKE_CLAIM_INSIDE_ANOTHER);
            }
        }
        else {
            ActiveSelection activeSelection = activeSelections.get(event.getPlayer().getUniqueId());
            if(activeSelection == null) return;
            if(activeSelection.claimBarTask != null){
                //If you right click first it doesnt set a bar task
                activeSelection.claimBarTask.cancel();
            }

            activeSelections.remove(event.getPlayer().getUniqueId());
        }
    }

    public boolean vectorEquals(Vector vector1, Vector vector2){
        return vector1.getBlockX() == vector2.getBlockX() && vector1.getBlockZ() == vector2.getBlockZ();
    }

    @EventHandler
    public void preventBlockExplosions(BlockExplodeEvent event){
        for(Block block : event.blockList()){
            ClanVillageClaim claim = getVillageClaimAt(block.getLocation());
            if(claim != null){
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void preventEntityExplosions(EntityExplodeEvent event){
        for(Block block : event.blockList()){
            ClanVillageClaim claim = getVillageClaimAt(block.getLocation());
            if(claim != null){
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBlockClick(PlayerInteractEvent event){
        if(event.getHand() == null) return;
        if(event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        Block clickedBlock = event.getClickedBlock();
        if(clickedBlock == null) return;
        ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
        if(itemInHand == null) return;
        if(!itemInHand.getType().equals(claimItem)) return;
        ClanVillageClaim clanVillageClaimOnBlock = getVillageClaimAt(clickedBlock.getLocation());
        Location bLoc = clickedBlock.getLocation();
        ServerTeam serverTeam = TeamService.getPlayerTeam(event.getPlayer().getUniqueId());
        if(serverTeam == null) return;
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            boolean resizing = false;
            ActiveSelection activeSelection = activeSelections.get(event.getPlayer().getUniqueId());
            if(activeSelection == null){
                //First selection
                if(clanVillageClaimOnBlock != null && clanVillageClaimOnBlock.getOwningClan().equals(serverTeam.getUuid())){
                    if(!serverTeam.getOwner().equals(event.getPlayer().getUniqueId())){
                        messagePlayer(event.getPlayer(), TeamMessages.VILLAGE_LEADER_ONLY_RESIZE);
                        return;
                    }
                    Vector bLocV = bLoc.toVector();
                    if(vectorEquals(clanVillageClaimOnBlock.getCornerTop(), bLocV)){
                        messagePlayer(event.getPlayer(), TeamMessages.VILLAGE_RESIZING);
                        clickedBlock = clanVillageClaimOnBlock.getCornerBottom().toLocation(clanVillageClaimOnBlock.getWorld()).getBlock();
                        resizing = true;
                    }
                    else if(vectorEquals(clanVillageClaimOnBlock.getCornerBottom(), bLocV)){
                        messagePlayer(event.getPlayer(), TeamMessages.VILLAGE_RESIZING);
                        clickedBlock = clanVillageClaimOnBlock.getCornerTop().toLocation(clanVillageClaimOnBlock.getWorld()).getBlock();
                        resizing = true;
                    }
                    else if(vectorEquals(clanVillageClaimOnBlock.getCorner3(), bLocV)){
                        messagePlayer(event.getPlayer(), TeamMessages.VILLAGE_RESIZING);
                        clickedBlock = clanVillageClaimOnBlock.getCorner4().toLocation(clanVillageClaimOnBlock.getWorld()).getBlock();
                        resizing = true;
                    }
                    else if(vectorEquals(clanVillageClaimOnBlock.getCorner4(), bLocV)){
                        messagePlayer(event.getPlayer(), TeamMessages.VILLAGE_RESIZING);
                        clickedBlock = clanVillageClaimOnBlock.getCorner3().toLocation(clanVillageClaimOnBlock.getWorld()).getBlock();
                        resizing = true;
                    }
                }
                messagePlayer(event.getPlayer(), TeamMessages.VILLAGE_SET_FIRST_CORNER);
                setActiveSelectionCorner(event.getPlayer(), clickedBlock.getLocation(), true, resizing, serverTeam);
            }
            else {
                if(activeSelection.resizing){
                    messagePlayer(event.getPlayer(), TeamMessages.VILLAGE_SET_SECOND_CORNER_EXPAND);
                }
                else messagePlayer(event.getPlayer(), TeamMessages.VILLAGE_SET_SECOND_CORNER);
                setActiveSelectionCorner(event.getPlayer(), clickedBlock.getLocation(), false, false, null);
            }
            event.setCancelled(true);
        }
    }


    public void resizeClaim(Player player){
        ServerTeam team = TeamService.getPlayerTeam(player);
        if(team == null){
            messagePlayer(player, TeamMessages.JOIN_CLAN_FIRST);
            return;
        }
        ActiveSelection activeSelection = activeSelections.get(player.getUniqueId());
        if(activeSelection == null || activeSelection.cornerBottom == null || activeSelection.cornerTop == null){
            messagePlayer(player, TeamMessages.VILLAGE_NO_SELECTION_MADE);
            return;
        }
        if(!activeSelection.resizing){
            messagePlayer(player, TeamMessages.VILLAGE_RESIZING_SELECT_CORNER_FIRST);
            return;
        }
        ClanVillageClaim area = getVillageClaimAt(activeSelection.cornerTop.toLocation(activeSelection.world));
        if(area == null) return;
        ClanVillageClaim testingArea = new ClanVillageClaim(activeSelection.cornerBottom, activeSelection.cornerTop, activeSelection.world, team.getUuid(), "resizing");
        if(testingArea.testForPlayerClaims(this, player)){
            return;
        }
        else if(testingArea.testForVillageClaims(regionManager)){
            messagePlayer(player, TeamMessages.VILLAGE_OVERLAPS_ANOTHER_VILLAGE);
            return;
        }
        if(testingArea.getArea() < MIN_CLAIM_AREA){
            ServiceManager.getService(VillageService.class).messagePlayer(player, TeamMessages.format(TeamMessages.VILLAGE_MIN_BLOCKS, MIN_CLAIM_AREA));
            return;
        }
        if(testingArea.getArea() > activeSelection.claimBarTask.getClaimBlocks()){
            ServiceManager.getService(VillageService.class).messagePlayer(player, TeamMessages.VILLAGE_NOT_ENOUGH_BLOCKS);
            return;
        }
        regionManager.resizeRegion(area, activeSelection.cornerBottom, activeSelection.cornerTop);
        messagePlayer(player, TeamMessages.VILLAGE_RESIZED);
        createClaimPattern(area, player);
        activeSelection.claimBarTask.cancel();
        updateAreaMarker(area, false);
        activeSelections.remove(player.getUniqueId());
        TeamMain.getMainStorage().saveVillage(area, false);
    }

    public void createClaim(Player player, String name){
        ServerTeam team = TeamService.getPlayerTeam(player);
        if(team == null){
            messagePlayer(player, TeamMessages.JOIN_CLAN_FIRST);
            return;
        }
        if(!team.getOwner().equals(player.getUniqueId()) && !isDebugMode()){
            ServiceManager.getService(VillageService.class).messagePlayer(player, TeamMessages.CLAN_OWNER_ONLY_COMMAND);
            return;
        }
        if(team.getTotalMemberCount() < 3 && !isDebugMode()){
            ServiceManager.getService(VillageService.class).messagePlayer(player, TeamMessages.VILLAGE_NOT_ENOUGH_MEMBERS);
            return;
        }
        if(team.getLevel() < 3 && !isDebugMode()){
            ServiceManager.getService(VillageService.class).messagePlayer(player, TeamMessages.VILLAGE_NOT_ENOUGH_LEVELS);
            return;
        }
        ActiveSelection activeSelection = activeSelections.get(player.getUniqueId());
        if(activeSelection == null || activeSelection.cornerBottom == null || activeSelection.cornerTop == null){
            messagePlayer(player, TeamMessages.VILLAGE_NO_SELECTION_MADE);
            return;
        }
        ClanVillageClaim clanVillageClaim = new ClanVillageClaim(activeSelection.cornerBottom,activeSelection.cornerTop,activeSelection.world,team.getUuid(), name);
        clanVillageClaim.setID(team.getUuid());
        if(clanVillageClaim.testForPlayerClaims(this, player)){
            return;
        }
        else if(clanVillageClaim.testForVillageClaims(regionManager)){
            messagePlayer(player, TeamMessages.VILLAGE_OVERLAPS_ANOTHER_VILLAGE);
            return;
        }
        ClanVillageClaim village = (ClanVillageClaim) regionManager.getByID(team.getUuid());
        if(village != null){
            ServiceManager.getService(VillageService.class).messagePlayer(player, TeamMessages.format(TeamMessages.VILLAGE_CLAN_ALREADY_HAS_ONE, village.getCornerTop()));
            return;
        }
        if(clanVillageClaim.getArea() < MIN_CLAIM_AREA){
            ServiceManager.getService(VillageService.class).messagePlayer(player, TeamMessages.format(TeamMessages.VILLAGE_MIN_BLOCKS, MIN_CLAIM_AREA));
            return;
        }
        if(clanVillageClaim.getArea() > activeSelection.maxBlocks){
            ServiceManager.getService(VillageService.class).messagePlayer(player, TeamMessages.VILLAGE_NOT_ENOUGH_BLOCKS);
            return;
        }
        regionManager.addNewRegion(clanVillageClaim, false);
        messagePlayer(player, TeamMessages.VILLAGE_CREATED);
        messagePlayer(player, TeamMessages.format(TeamMessages.VILLAGE_MAP_LINK, clanVillageClaim.getWorld().getName(),
                clanVillageClaim.getCornerTop().getBlockX(), clanVillageClaim.getCornerTop().getBlockY(), clanVillageClaim.getCornerTop().getBlockZ()));
        createClaimPattern(clanVillageClaim, player);
        activeSelection.claimBarTask.cancel();
        activeSelections.remove(player.getUniqueId());
        TeamMain.getMainStorage().saveVillage(clanVillageClaim, false);
        Bukkit.getScheduler().runTask(main, ()->Bukkit.getPluginManager().callEvent(new VillageCreateEvent(player, team)));
        addVillageToMap(clanVillageClaim);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        playersInsideRegion.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onEnterClaim(RegionEnterEvent event){
        if(!(event.getRegion() instanceof ClanVillageClaim claim)) return;
        event.getPlayer().sendTitle(TeamMessages.VILLAGE_ENTER, "§6"+ claim.getVillageName(), 10, 30, 10);
    }

    @EventHandler
    public void onLeaveClaim(RegionLeaveEvent event){
        if(!(event.getClaim() instanceof ClanVillageClaim claim)) return;
        event.getPlayer().sendTitle(TeamMessages.VILLAGE_LEAVE, "§6"+ claim.getVillageName(), 10, 30, 10);
    }


    @EventHandler
    public void onQuitWhileInClaim(PlayerQuitEvent event){
        Claim claim = playersInsideLandClaims.remove(event.getPlayer().getUniqueId());
        ClanVillageClaim clanVillageClaim = playersInsideRegion.remove(event.getPlayer().getUniqueId());

    }

    @EventHandler
    public void preventCreatingPlayerClaims(ClaimCreatedEvent event){
        Claim claim = event.getClaim();

        ServerTeam team = TeamService.getPlayerTeam(((Player) event.getCreator()).getUniqueId());
        for(Chunk chunk : claim.getChunks()){
            ArrayList<ClanVillageClaim> villages = regionManager.getTypeRegionsFromChunk(chunk.getX(), chunk.getZ(), ClanVillageClaim.class);
            if(villages == null || villages.isEmpty()) continue;
            for(ClanVillageClaim clanVillageClaim : villages){
                if(regionManager.areasOverlap(clanVillageClaim, claim.getLesserBoundaryCorner().toVector(), claim.getGreaterBoundaryCorner().toVector())){
                    if(team == null || !team.getUuid().equals(clanVillageClaim.getOwningClan())) event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event){
        activeSelections.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        activeSelections.remove(event.getPlayer().getUniqueId());
    }

}
