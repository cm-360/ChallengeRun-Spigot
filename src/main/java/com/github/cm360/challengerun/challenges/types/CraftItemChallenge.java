package com.github.cm360.challengerun.challenges.types;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;

import com.github.cm360.challengerun.challenges.Challenge;

public class CraftItemChallenge extends Challenge {

	protected Material itemMaterial;
	
	public CraftItemChallenge(Material itemMaterial) {
		this.itemMaterial = itemMaterial;
	}
	
	@EventHandler
	public void check(CraftItemEvent event) {
		HumanEntity who = event.getWhoClicked();
		if (!(who instanceof Player))
			return;
		
		if (itemMaterial == event.getRecipe().getResult().getType()) {
			this.completedBy((Player) who);
		}
	}

}
