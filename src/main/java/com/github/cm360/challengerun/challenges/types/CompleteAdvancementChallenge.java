package com.github.cm360.challengerun.challenges.types;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import com.github.cm360.challengerun.challenges.Challenge;

public class CompleteAdvancementChallenge extends Challenge {

	protected NamespacedKey advancementKey;
	protected AdvancementDisplay advancementDisplay;
	
	public CompleteAdvancementChallenge(Advancement advancement) {
		advancementKey = advancement.getKey();
		advancementDisplay = advancement.getDisplay();
	}
	
	@Override
	public void checkPreCompletion(Player player) {
		Advancement advancement = Bukkit.getServer().getAdvancement(advancementKey);
		if (player.getAdvancementProgress(advancement).isDone()) {
			this.completedBy(player);
		}
	}
	
	@EventHandler
	public void check(PlayerAdvancementDoneEvent event) {
		if (advancementKey.equals(event.getAdvancement().getKey())) {
			this.completedBy(event.getPlayer());
		}
	}

	@Override
	public String getDescription() {
		return String.format("Complete the advancement: ", advancementDisplay.getTitle());
	}

}
