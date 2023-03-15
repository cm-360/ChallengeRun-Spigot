package com.github.cm360.challengerun.challenges;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.github.cm360.challengerun.events.ChallengeCompletedEvent;
import com.github.cm360.challengerun.main.ChallengeRunPlugin;

public abstract class Challenge implements Listener {
	
	public void checkPreCompletion(Player player) { };
	
	public void completedBy(Player player) {
		Bukkit.getPluginManager().callEvent(new ChallengeCompletedEvent(player, this));
	}
	
	public void start() {
		Bukkit.getPluginManager().registerEvents(this, ChallengeRunPlugin.instance);
	}
	
	public void end() {
		HandlerList.unregisterAll(this);
	}
	
	public abstract String getDescription();
	
	public String toString() {
		return getDescription();
	}

}
