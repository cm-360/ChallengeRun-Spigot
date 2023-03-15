package com.github.cm360.challengerun.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.github.cm360.challengerun.matches.Match;

public class MatchCompletedEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	protected Match match;

	public MatchCompletedEvent(Match match) {
		this.match = match;
	}
	
	public Match getMatch() {
		return match;
	}
	
	public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
