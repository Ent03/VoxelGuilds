package com.entity999.teams.villages;

import com.entity999.teams.lang.TeamMessages;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ClaimBarTask extends BukkitRunnable {
    private ActiveSelection activeSelection;
    private BossBar bossBar;
    private Player player;
    private int claimBlocks;
    public ClaimBarTask(ActiveSelection activeSelection, BossBar bossBar, Player player, int claimBlocks) {
        this.activeSelection = activeSelection;
        this.bossBar = bossBar;
        this.claimBlocks = claimBlocks;
        this.player = player;
    }

    @Override
    public void cancel(){
        super.cancel();
        bossBar.removeAll();
        bossBar.setVisible(false);
    }

    public int getClaimBlocks() {
        return claimBlocks;
    }

    @Override
    public void run() {
        int dimensionsX = activeSelection.cornerTop.getBlockX() - player.getLocation().getBlockX();
        int dimensionsZ = activeSelection.cornerTop.getBlockZ() - player.getLocation().getBlockZ();
        int area = Math.abs(dimensionsX * dimensionsZ);
        int blocksLeft = claimBlocks - area;
        if(blocksLeft < 0) blocksLeft = 0;
        bossBar.setTitle(TeamMessages.format(TeamMessages.VILLAGE_CLAIM_BAR_MESSAGE, blocksLeft, dimensionsX, dimensionsZ));
    }
}
