package com.entity999.teams.services;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.entity999.core.VoxelPlugin;
import com.entity999.core.commands.CommandManager;
import com.entity999.core.services.Service;
import com.entity999.core.services.ServiceManager;


import com.entity999.npcs.npcs.NPC;
import com.entity999.npcs.npcs.NPCHandler;
import com.entity999.teams.TeamService;
import com.entity999.teams.commands.teams.TeamSpectateMember;
import com.entity999.teams.lang.TeamMessages;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;

import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class SpectatorService extends Service {
    private HashMap<UUID, UUID> spectating;
    private HashMap<UUID, GameMode> lastGamemodes;

    public SpectatorService() {
        super("", true);
        spectating = new HashMap<>();
        lastGamemodes = new HashMap<>();
    }

    @Override
    public void onRegister() throws Exception {
        if(!Bukkit.getPluginManager().isPluginEnabled("VoxelNPCs")) {
            throw new IllegalStateException("VoxelNPCs missing");
        }
        onInteract();
    }

    public boolean isSpectator(Player player){
        return spectating.containsKey(player.getUniqueId());
    }
    public void addTPTask(Player player, Player target){
        new BukkitRunnable() {

            @Override
            public void run() {
                if(!spectating.containsKey(player.getUniqueId())){
                    this.cancel();
                    return;
                }
                Location l = target.getLocation();
                player.teleport(target.getWorld().getHighestBlockAt(l.getBlockX(), l.getBlockZ()).getLocation().clone().add(0.5, 5, 0.5));
            }

        }.runTaskTimer(main, 0, 20);
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        for(UUID uuid : spectating.keySet()){
            event.getPlayer().hidePlayer(main, Bukkit.getPlayer(uuid));
        }
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event){
        for(UUID uuid : spectating.keySet()){
            removeSpectator(Bukkit.getPlayer(uuid));
        }
    }


    public boolean playerCanSpectate(Player player){
        if(player.hasMetadata("time-last-damaged")){
            EntityDamageEvent event = player.getLastDamageCause();
            int cooldown = 0;
            switch (event.getCause()){
                case FALL:
                    cooldown = 1;
                    break;
                case FALLING_BLOCK:
                    cooldown = 3;
                    break;
                case POISON:
                case FIRE:
                    cooldown = 20;
                    break;
                case MAGIC:
                case DROWNING:
                    cooldown = 10;
                    break;
                case SUFFOCATION:
                case WITHER:
                case BLOCK_EXPLOSION:
                case VOID:
                    cooldown = 5;
                    break;
                case PROJECTILE:
                    cooldown = 60;
                    break;
                case ENTITY_SWEEP_ATTACK:
                case ENTITY_ATTACK:
                    cooldown = 120;
                    break;
                case STARVATION:
                case LAVA:
                    cooldown = 30;
                    break;
            }
            long time = player.getMetadata("time-last-damaged").get(0).asLong();
            long timeNow = System.currentTimeMillis();
            long diff = timeNow - time;
            if(diff <= cooldown * 1000){
                ServiceManager.getService(TeamService.class).messagePlayer(player, TeamMessages.format(TeamMessages.CLAN_SPECTATE_UNABLE, (int) (cooldown - (diff / 1000))));
                return false;
            }
        }
        return true;
    }

    @EventHandler
    public void onPortalEnter(PlayerTeleportEvent event){
        if(spectating.containsKey(event.getPlayer().getUniqueId())){
            if(event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerSwitchWorlds(PlayerChangedWorldEvent event){
        if(spectating.containsKey(event.getPlayer().getUniqueId())){
            Bukkit.getScheduler().runTaskLaterAsynchronously(main, ()->{
                Player target = Bukkit.getPlayer(spectating.get(event.getPlayer().getUniqueId()));
                ClientboundSetCameraPacket camera = new ClientboundSetCameraPacket(((CraftPlayer)target).getHandle());
                ((CraftPlayer)event.getPlayer()).getHandle().connection.send(camera);
                ClientboundGameEventPacket gm = new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, 3);
                ((CraftPlayer)event.getPlayer()).getHandle().connection.send(gm);
            }, 5);
        }
    }

    public void addSpectator(Player player, Player target){
        spectating.put(player.getUniqueId(),target.getUniqueId());
        lastGamemodes.put(player.getUniqueId(), player.getGameMode());

        ClientboundSetCameraPacket camera = new ClientboundSetCameraPacket(((CraftPlayer)target).getHandle());
        player.setInvulnerable(true);
        ClientboundGameEventPacket gm = new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, 3);
        ServiceManager.getService(NPCHandler.class).addTempNPC(player, player.getLocation(), "temp_"+player.getName(), player.getName(), "spectator", "", player.getName() + "/n§b§lSpectating");
        for(Player p : Bukkit.getOnlinePlayers()){
            p.hidePlayer(main, player);
            ClientboundPlayerInfoPacket pack = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, ((CraftPlayer)player).getHandle());
            ((CraftPlayer)p).getHandle().connection.send(pack);
        }
        addTPTask(player,target);
        Bukkit.getScheduler().runTaskLater(main, ()->{
            ((CraftPlayer)player).getHandle().connection.send(camera);
            ((CraftPlayer)player).getHandle().connection.send(gm);
        },10);
    }
    public void removeSpectator(Player player){
        spectating.remove(player.getUniqueId());
        lastGamemodes.put(player.getUniqueId(), player.getGameMode());
        player.setInvulnerable(false);
        ClientboundSetCameraPacket camera = new ClientboundSetCameraPacket(((CraftPlayer)player).getHandle());
        ((CraftPlayer)player).getHandle().connection.send(camera);
        GameMode lastGm = lastGamemodes.get(player.getUniqueId());
        int gmValue = switch (lastGm) {
            case CREATIVE -> 1;
            case ADVENTURE -> 2;
            default -> 0;
        };
        NPC npc = ServiceManager.getService(NPCHandler.class).getNPCbyMetadata("spectator", player.getUniqueId().toString());
        ServiceManager.getService(NPCHandler.class).removeTempNPC(npc);
        player.teleport(npc.getLocation());
        for(Player p : Bukkit.getOnlinePlayers()){
            p.showPlayer(main, player);
        }
        ClientboundGameEventPacket gm = new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, gmValue);
        ((CraftPlayer)player).getHandle().connection.send(gm);
    }
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event){
        if(spectating.containsKey(event.getPlayer().getUniqueId())){
           removeSpectator(event.getPlayer());
        }
    }
    @EventHandler
    public void onInvOpen(InventoryOpenEvent event){
        if(spectating.containsKey(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }

    public void onInteract(){
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(getPlugin(), ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY){
            public void onPacketReceiving(PacketEvent e){
                try {
                    Player p = e.getPlayer();
                    if(spectating.containsKey(p.getUniqueId())){
                        e.setCancelled(true);
                    }
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    @EventHandler
    public void onItemInteract(InventoryClickEvent event){
        if(spectating.containsKey(event.getWhoClicked().getUniqueId())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        if(spectating.containsKey(event.getPlayer().getUniqueId())){
            removeSpectator(event.getPlayer());
        }
        if(spectating.containsValue(event.getPlayer().getUniqueId())){
            for(UUID uuid : spectating.keySet()){
                if(spectating.get(uuid).equals(event.getPlayer().getUniqueId())){
                    removeSpectator(Bukkit.getPlayer(spectating.get(uuid)));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCommand(PlayerCommandPreprocessEvent event){
        String cSpectate = CommandManager.getCustomCommandExecString(TeamSpectateMember.class);
        if(spectating.containsKey(event.getPlayer().getUniqueId()) && !event.getMessage().contains(cSpectate)){
            event.getPlayer().sendMessage(TeamMessages.CLAN_NOW_SPECTATE_ENTER_SUBTITLE);
            event.setMessage(cSpectate);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemInteract(PlayerDropItemEvent event){
        if(spectating.containsKey(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerAttack(EntityDamageByEntityEvent event){
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if(spectating.containsKey(player.getUniqueId())){
            removeSpectator(player);
        }
    }
}
