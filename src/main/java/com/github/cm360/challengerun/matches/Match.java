package com.github.cm360.challengerun.matches;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.cm360.challengerun.challenges.Challenge;
import com.github.cm360.challengerun.main.ChallengeRunPlugin;

/**
 * The Class Match.
 */
public class Match {

	/** The players in this match. */
	private List<UUID> players = new ArrayList<UUID>();
	
	/** The current challenge. */
	private Challenge currentChallenge;
	
	/** The number of minutes to put on the prep timer. */
	private int prepTimerMins;
	
	/** The number of minutes to put on the challenge timer. */
	private int challengeTimerMins;
	
	/** The task ID of the currently running timer. */
	private int timerTaskId;
	
	/**
	 * Adds the player.
	 *
	 * @param player the player
	 */
	public void addPlayer(Player player) {
		players.add(player.getUniqueId());
	}
	
	/**
	 * Removes the player.
	 *
	 * @param player the player
	 */
	public void removePlayer(Player player) {
		players.remove(player.getUniqueId());
	}
	
	/**
	 * Start.
	 */
	public void start() {
		announce("Welcome to Challenge Run! Your objective is to complete as many challenges as you can.");
		announce(String.format("You will have %d minutes to prepare before being given your first task, good luck!", prepTimerMins));
		startPrepTimer();
	}
	
	/**
	 * End.
	 */
	public void end() {
		cancelTimer();
		endChallenge();
		announce("The game is over! Lets see how everyone did!");
	}
	
	/**
	 * Skip the currently running challenge. This will cancel any currently running
	 * timer, end the current challenge, generate a new challenge, and restart the
	 * challenge timer.
	 */
	public void skipChallenge() {
		cancelTimer();
		endChallenge();
		announce("The current challenge was vote-skipped! Your next challenge is:");
		generateNewChallenge();
		startChallengeTimer();
	}
	
	/**
	 * TODO Generates a new challenge.
	 */
	private void generateNewChallenge() {
		currentChallenge = null;
		announce(currentChallenge.getDescription());
	}

	/**
	 * Called when the preparation period timer ends. This will generate the first
	 * task and start the challenge timer.
	 */
	private void prepTimerEnded() {
		announce("The preparation period is over! Your first challenge is:");
		generateNewChallenge();
		startChallengeTimer();
	}
	
	/**
	 * Called when the challenge timer ends. This will end the current challenge,
	 * generate a new challenge, and restart the challenge timer.
	 */
	private void challengeTimerEnded() {
		endChallenge();
		announce("Times up! Your next challenge is:");
		generateNewChallenge();
		startChallengeTimer();
	}
	
	private void startPrepTimer() {
		schedule(this::prepTimerEnded, prepTimerMins);
	}
	
	private void startChallengeTimer() {
		schedule(this::challengeTimerEnded, challengeTimerMins);
	}
	
	private void cancelTimer() {
		Bukkit.getScheduler().cancelTask(timerTaskId);
	}
	
	private void endChallenge() {
		currentChallenge.end();
	}
	
	/**
	 * Announce a message to all players in this match.
	 *
	 * @param message Message to announce
	 */
	private void announce(String message) {
		players.forEach(id -> Bukkit.getPlayer(id).sendMessage(message));
	}
	
	/**
	 * Schedule a task to be run after a specified number of minutes.
	 *
	 * @param runnable The task to run
	 * @param delayMins The number of minutes to wait
	 */
	private void schedule(Runnable runnable, long delayMins) {
		timerTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(ChallengeRunPlugin.instance, runnable, delayMins * 60 * 20 );
	}

}
