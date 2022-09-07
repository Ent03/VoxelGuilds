package com.entity999.teams.papiextensions;

import com.entity999.core.services.ServiceManager;
import com.entity999.teams.ServerTeam;
import com.entity999.teams.TeamService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class TeamNamePlaceholder extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "teamname";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Entity422";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.2";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        ServerTeam team = TeamService.getPlayerTeam(player.getUniqueId());
        if(team == null) return "-";
        return team.getName();
    }
}
