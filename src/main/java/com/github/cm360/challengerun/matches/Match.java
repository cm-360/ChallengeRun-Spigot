package com.github.cm360.challengerun.matches;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.cm360.challengerun.challenges.Challenge;

public class Match {

	private List<UUID> players = new ArrayList<UUID>();
	private Challenge currentChallenge;
	
	public void addPlayer(Player player) {
		players.add(player.getUniqueId());
	}
	
	public void removePlayer(Player player) {
		players.remove(player.getUniqueId());
	}
	
	public void start() {
		announce("");
		generateChallenge();
		announce("");
	}
	
	public void end() {
		endChallenge();
		announce("");
	}
	
	public void generateChallenge() {
		if (currentChallenge != null)
			currentChallenge.end();
		
		
	}
	
	public void endChallenge() {
		currentChallenge.end();
	}
	
	public void announce(String message) {
		players.forEach(id -> Bukkit.getPlayer(id).sendMessage(message));
	}

}
