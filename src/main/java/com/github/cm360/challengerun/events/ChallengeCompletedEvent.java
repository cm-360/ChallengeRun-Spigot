package com.github.cm360.challengerun.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.github.cm360.challengerun.challenges.Challenge;

public class ChallengeCompletedEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	protected Challenge challenge;

	public ChallengeCompletedEvent(Player player, Challenge challenge) {
		super(player);
		this.challenge = challenge;
	}
	
	public Challenge getChallenge() {
		return challenge;
	}
	
	public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
