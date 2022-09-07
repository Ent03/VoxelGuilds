package com.entity999.teams.papi;

import com.entity999.VoxelEssentials;
import com.entity999.teams.TeamMain;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class TeamExtension extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "voxelguilds";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Entity422";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        var user = TeamMain.getUserManager().getVoxelUser(player.getUniqueId());
        if(user == null) return "User not online";
        if(user.getOnlineTeam() == null) return  "";
        if(params.equals("name")) return user.getOnlineTeam().getName();
        return "";
    }
}
