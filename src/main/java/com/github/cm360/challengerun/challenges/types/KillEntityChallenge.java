package com.github.cm360.challengerun.challenges.types;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import com.github.cm360.challengerun.challenges.Challenge;
import com.github.cm360.challengerun.utils.NameUtils;

public class KillEntityChallenge extends Challenge {

	protected EntityType entityType;
	
	public KillEntityChallenge(EntityType entityType) {
		this.entityType = entityType;
	}

	@EventHandler
	public void check(EntityDeathEvent event) {
		if (entityType != event.getEntityType())
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
	public String getDescription() {
		return String.format("Kill a %s.",NameUtils.enumToTitleCase(entityType));
	}

}
