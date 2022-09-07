package com.entity999.teams.villages;

import com.entity999.core.gui.Chapter;
import com.entity999.core.gui.PageGUI;
import com.entity999.core.gui.events.GUIButtonClickEvent;
import com.entity999.core.services.ServiceManager;
import com.entity999.core.utils.Utils;

import com.entity999.teams.ServerTeam;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerManagerGUI extends PageGUI {
    private ServerTeam clan;
    private HashMap<String, Claim> claimHashMap;
    public PlayerManagerGUI(ServerTeam clan) {
        super("Manage player claims", 6, true, "you.dont.have", true);
        this.clan = clan;
        this.claimHashMap = new HashMap<>();
        guiEventBus.subscribe(GUIButtonClickEvent.class, this::onButtonClick);
    }

    public void openClaimManageGUI(Player player, Claim claim){
        Chapter manageC = addChapter(54, "manage_"+claim.getOwnerName(), player.getName()+"'s claim", true, true, player.getOpenInventory().getTopInventory());
        addButton("corners", 13, Utils.createItem(Material.DIAMOND_BLOCK, "§bCorners",
                "§eCorner 1: " + claim.getGreaterBoundaryCorner().toVector().toString() +
                        "/n§6Corner 2: " + claim.getLesserBoundaryCorner().toVector().toString()), manageC.getPage(0).gui);

        addButton("area", 21, Utils.createItem(Material.BLUE_CARPET, "§bSize (Area) = §7" + claim.getArea(),
                ""), manageC.getPage(0).gui);

        ArrayList<String> trusted = new ArrayList<>();
        claim.getPermissions(trusted, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        trusted = trusted.stream().map(p -> "§7"+Bukkit.getOfflinePlayer(UUID.fromString(p)).getName()).collect(Collectors.toCollection(ArrayList::new));
        addButton("trusted", 23, Utils.createItem(Material.PLAYER_HEAD, "§bTrusted players",
                String.join("/n", trusted)), manageC.getPage(0).gui);

        addButton("delete_"+claim.getOwnerName(), 22, Utils.createItem(Material.TNT, "§4Delete claim",
                "§7Click to delete this claim"), manageC.getPage(0).gui);
        openPageFromChapter(0, player, manageC);
    }

    public void openConfirmationMenu(Player player, String target){
        Chapter confirm = addChapter(9, "confirm", "Confirm deletion", true, false);
        addButton("confirm_del_"+target, 3, Utils.createItem(Material.RED_WOOL, "§4Confirm", "§7Click to permanently delete this claim"), confirm.getPage(0).gui);
        addButton("cancel_del_"+target, 5, Utils.createItem(Material.GREEN_WOOL, "§aCancel", "§7Click to cancel"), confirm.getPage(0).gui);
        openPageFromChapter(0, player, confirm);
    }

    public void onButtonClick(GUIButtonClickEvent event){
        if(event.getButtonName().startsWith("claim_")){
            String player = event.getButtonName().split("claim_")[1];
            Claim claim = claimHashMap.get(player);
            openClaimManageGUI(event.getPlayer(), claim);
        }
        else if(event.getButtonName().startsWith("delete_")){
            String target = event.getButtonName().split("delete_")[1];
            openConfirmationMenu(event.getPlayer(), target);
        }
        else if(event.getButtonName().startsWith("cancel_del_")){
            String player = event.getButtonName().split("cancel_del_")[1];
            openPageFromChapter(0, event.getPlayer(), "manage_"+player);
        }
        else if(event.getButtonName().startsWith("confirm_del_")){
            String player = event.getButtonName().split("confirm_del_")[1];
            GriefPrevention.instance.dataStore.deleteClaim(claimHashMap.get(player));
            ServiceManager.getService(VillageService.class).messagePlayer(event.getPlayer(), "§cClaim deleted");
            event.getPlayer().closeInventory();
            Player targetPlayer = Bukkit.getPlayer(player);
            if(targetPlayer != null){
                ServiceManager.getService(VillageService.class).messagePlayer(targetPlayer, String.format("§e%s§c deleted your claim at %s", event.getPlayer().getName(), claimHashMap.get(player).getLesserBoundaryCorner().toVector().toBlockVector()));
            }
        }
    }

    public void openClaimsMenu(Player player, ArrayList<Claim> claims){
        Chapter claimC = addChapter(54, "claims", "Manage player claims", true, true);
        for(Claim claim : claims){
            claimHashMap.put(claim.getOwnerName(), claim);
            addButton("claim_"+claim.getOwnerName(),
                    Utils.createItem(Material.GOLD_BLOCK, "§b" + claim.getOwnerName() + "'s claim", "§7Click to view more info"),claimC);
        }
        openPageFromChapter(0, player, claimC);
    }
}

