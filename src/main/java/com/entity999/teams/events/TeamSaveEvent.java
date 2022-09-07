package com.entity999.teams.events;

import com.entity999.teams.ServerTeam;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamSaveEvent extends Event {
    private ServerTeam serverTeam;

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    public TeamSaveEvent(ServerTeam serverTeam){
        this.serverTeam = serverTeam;
    }

    public boolean isCancelled() {
        return cancelled;
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
