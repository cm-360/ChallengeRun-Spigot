package com.github.cm360.challengerun.challenges.types;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;

public class ObtainItemChallenge extends CraftItemChallenge {

	public ObtainItemChallenge(Material itemMaterial) {
		super(itemMaterial);
	}

	@Override
	public void checkPreCompletion(Player player) {
		if (player.getInventory().contains(itemMaterial))
			this.completedBy(player);
	}
	
	@EventHandler
	public void checkPickup(EntityPickupItemEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Player))
			return;
		
		if (this.itemMaterial == event.getItem().getItemStack().getType())
			this.completedBy((Player) entity);
	}
	
	@EventHandler
	public void checkInventory(InventoryInteractEvent event) {
		HumanEntity who = event.getWhoClicked();
		if (!(who instanceof Player))
			return;
		
		Player player = (Player) who;
		if (player.getInventory().contains(itemMaterial))
			this.completedBy(player);
	}

}
