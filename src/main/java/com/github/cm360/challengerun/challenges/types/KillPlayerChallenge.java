package com.github.cm360.challengerun.challenges.types;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.github.cm360.challengerun.challenges.Challenge;

public class KillPlayerChallenge extends Challenge {

	protected UUID targetPlayerId;
	
	public KillPlayerChallenge(Player targetPlayer) {
		targetPlayerId = targetPlayer.getUniqueId();
	}
	
	@EventHandler
	public void check(PlayerDeathEvent event) {
		if (!targetPlayerId.equals(event.getEntity().getUniqueId()))
			return;
		
		EntityDamageEvent damageCause = event.getEntity().getLastDamageCause();
		if (!(damageCause instanceof EntityDamageByEntityEvent))
			return;
		
		Entity damager = ((EntityDamageByEntityEvent) damageCause).getDamager();
		if (!(damager instanceof Player))
			return;
		
		this.completedBy((Player) damager);
	}
	
	@Override
	public void end() {
		this.completedBy(Bukkit.getPlayer(targetPlayerId));
		super.end();
	}

}
