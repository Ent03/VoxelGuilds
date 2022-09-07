package com.entity999.teams.events;


import com.entity999.teams.ServerTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class VillageCreateEvent extends Event {
    private ServerTeam serverTeam;
    private Player player;
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    public VillageCreateEvent(Player player, ServerTeam serverTeam){
        this.serverTeam = serverTeam;
        this.player = player;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public Player getPlayer() {
        return player;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public ServerTeam getServerTeam() {
        return serverTeam;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
