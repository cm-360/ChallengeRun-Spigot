package com.github.cm360.challengerun.challenges.types;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.cm360.challengerun.challenges.Challenge;

public class FindBiomeChallenge extends Challenge {

	protected Biome biome;

	public FindBiomeChallenge(Biome biome) {
		this.biome = biome;
	}

	@EventHandler
	public void check(PlayerMoveEvent event) {
		Location loc = event.getTo();
		if (biome == loc.getWorld().getBiome(loc)) {
			this.completedBy(event.getPlayer());
		}
	}

}
