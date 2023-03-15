package com.github.cm360.challengerun.challenges.types;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import com.github.cm360.challengerun.challenges.Challenge;

public class BreakBlockChallenge extends Challenge {

	protected Material blockMaterial;
	
	public BreakBlockChallenge(Material blockMaterial) {
		this.blockMaterial = blockMaterial;
	}
	
	@EventHandler
	public void check(BlockBreakEvent event) {
		if (blockMaterial == event.getBlock().getBlockData().getMaterial())
			this.completedBy(event.getPlayer());
	}

}
