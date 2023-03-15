package com.github.cm360.challengerun.challenges.types;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.github.cm360.challengerun.challenges.Challenge;

public class DeathMessageChallenge extends Challenge {

	public String deathMessage;
	
	public DeathMessageChallenge(String deathMessage) {
		this.deathMessage = deathMessage;
	}
	
	@EventHandler
	public void check(PlayerDeathEvent event) {
		String formattedMessage = deathMessage.replace("{player}", event.getEntity().getName());
		if (event.getDeathMessage().equals(formattedMessage)) {
			this.completedBy(event.getEntity());
		}
	}
	
	@Override
	public String getDescription() {
		return String.format("Die with the death message: \"%s\".", deathMessage);
	}

}
