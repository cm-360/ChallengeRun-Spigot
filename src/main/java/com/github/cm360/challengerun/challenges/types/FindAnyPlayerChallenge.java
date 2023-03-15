package com.github.cm360.challengerun.challenges.types;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.cm360.challengerun.challenges.Challenge;

public class FindAnyPlayerChallenge extends Challenge {

	protected double distance;
	
	public FindAnyPlayerChallenge(double distance) {
		this.distance = distance;
	}
	
	@EventHandler
	public void check(PlayerMoveEvent event) {
		Location loc = event.getTo();
		Collection<Entity> nearbyPlayers = loc.getWorld()
				.getNearbyEntities(loc, distance, distance, distance, (e) -> e instanceof Player);
		for (Entity nearbyPlayer : nearbyPlayers){
			this.completedBy((Player) nearbyPlayer);
		}
	}
	
	@Override
	public String getDescription() {
		return "Find another player!";
	}

}
