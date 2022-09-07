package com.entity999.teams.villages;

import com.entity999.core.regions.Region;
import com.entity999.core.regions.RegionManager;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class ClanVillageClaim extends Region {
    private String villageName;
    private UUID owningClan;

    public ClanVillageClaim(Vector cornerBottom, Vector cornerTop, World world, UUID owningClan, String villageName) {
        super(cornerBottom, cornerTop, world);
        setPersistent(false);
        this.owningClan = owningClan;
        this.villageName = villageName;
    }


    public String getVillageName() {
        return villageName;
    }

    public UUID getOwningClan() {
        return owningClan;
    }

    public boolean testForPlayerClaims(VillageService service, Player player){
        for(long hash : chunkHashes){
            int[] c = regionManager.getChunkCoordsFromHash(hash);
            Collection<Claim> claims = GriefPrevention.instance.dataStore.getClaims(c[0], c[1]);
            if(claims == null) continue;
            for(Claim claim : GriefPrevention.instance.dataStore.getClaims(c[0], c[1])){
                if(!claim.getLesserBoundaryCorner().getWorld().equals(this.getWorld())) continue;
                if(claim.canSiege(player)) continue;
                if(claim.ownerID.equals(player.getUniqueId())) continue;
                if(regionManager.areasOverlap(this, claim.getLesserBoundaryCorner().toVector(), claim.getGreaterBoundaryCorner().toVector())) {
                    service.messagePlayer(player, "§cThe selected area overlaps a player claim owned by §e" + claim.getOwnerName());
                    service.messagePlayer(player, "§cAsk them to trust you or try making the selection elsewhere.");
                    return true;
                }
            }
        }
        return false;
    }


    public boolean testForVillageClaims(RegionManager regionManager){
        for(long hash : chunkHashes){
            int[] c = regionManager.getChunkCoordsFromHash(hash);
            Collection<ClanVillageClaim> claims = regionManager.getTypeRegionsFromChunk(c[0], c[1], ClanVillageClaim.class);
            if(claims == null) continue;
            for(ClanVillageClaim claim : claims){
                if(!claim.getWorld().equals(this.getWorld())) continue;
                if(claim.getOwningClan().equals(this.getOwningClan())) continue;
                if(regionManager.areasOverlap(this, claim)) return true;
            }
        }
        return false;
    }

    public HashSet<Chunk> getChunks() {
        return chunks;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanVillageClaim area = (ClanVillageClaim) o;
        return Objects.equals(owningClan, area.owningClan) &&
                Objects.equals(cornerBottom, area.cornerBottom) &&
                Objects.equals(cornerTop, area.cornerTop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owningClan, cornerBottom, cornerTop);
    }
}
