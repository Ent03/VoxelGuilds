package com.entity999.teams.events;

import com.entity999.teams.ServerTeam;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AllyCreateEvent extends Event {
    private ServerTeam target;
    private ServerTeam asker;
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    public AllyCreateEvent(ServerTeam target, ServerTeam asker){
        this.target = target;
        this.asker = asker;
    }

    public boolean isCancelled() {
        return cancelled;
    }



    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public ServerTeam getTarget() {
        return target;
    }

    public ServerTeam getAsker() {
        return asker;
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
