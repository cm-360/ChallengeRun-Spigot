package com.github.cm360.challengerun.challenges.types;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.cm360.challengerun.challenges.Challenge;

public class FindAnyPlayerChallenge extends Challenge {

	protected Predicate<Entity> playerInMatchPredicate;
	
	protected double distance;
	
	public FindAnyPlayerChallenge(Predicate<Entity> playerInMatchPredicate, double distance) {
		this.playerInMatchPredicate = playerInMatchPredicate;
		this.distance = distance;
	}
	
	@EventHandler
	public void check(PlayerMoveEvent event) {
		Location loc = event.getTo();
		Collection<Entity> nearbyPlayers = loc.getWorld()
				.getNearbyEntities(loc, distance, distance, distance).stream()
						.filter((e) -> e instanceof Player)
						.filter(playerInMatchPredicate)
						.collect(Collectors.toList());
		for (Entity nearbyPlayer : nearbyPlayers) {
			if (event.getPlayer() != nearbyPlayer) {
				this.completedBy((Player) nearbyPlayer);
			}
		}
	}
	
	@Override
	public String getDescription() {
		return "Find another player!";
	}

}
