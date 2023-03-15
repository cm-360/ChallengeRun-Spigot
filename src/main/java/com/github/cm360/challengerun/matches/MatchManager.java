package com.github.cm360.challengerun.matches;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.cm360.challengerun.events.MatchCompletedEvent;

public class MatchManager implements Listener {

	private Map<String, Match> matches = new HashMap<String, Match>();
	
	public Match createMatch() {
		Match match = new Match();
		matches.put(match.getCode(), match);
		return match;
	}
	
	public Match getMatchByCode(String joinCode) {
		return matches.get(joinCode);
	}
	
	public Match getMatchForPlayer(Player player) {
		for (Match match : matches.values())
			if (match.containsPlayer(player))
				return match;
		return null;
	}
	
	public boolean isPlayerInMatch(Player player) {
		return getMatchForPlayer(player) == null;
	}
	
	@EventHandler
	public void onMatchCompleted(MatchCompletedEvent event) {
		matches.remove(event.getMatch().getCode());
	}

}
