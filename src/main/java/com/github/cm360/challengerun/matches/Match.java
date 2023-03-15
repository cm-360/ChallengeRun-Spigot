package com.github.cm360.challengerun.matches;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
	private Map<UUID, Integer> playersAndScores = new HashMap<UUID, Integer>();
	
	/** The players that have voted to skip the current challenge. **/
	private Set<UUID> votes = new HashSet<UUID>();
	
	/** The current challenge. */
	private Challenge currentChallenge;
	
	/** The number of minutes to put on the prep timer. */
	private int prepTimerMins;
	
	/** The number of minutes to put on the challenge timer. */
	private int challengeTimerMins;
	
	/** The task ID of the currently running timer. */
	private int timerTaskId;
	
	/**
	 * Adds a player to this match.
	 *
	 * @param player The player to add
	 */
	public void addPlayer(Player player) {
		playersAndScores.put(player.getUniqueId(), 0);
	}
	
	/**
	 * Removes a player from this match.
	 *
	 * @param player The player to remove
	 */
	public void removePlayer(Player player) {
		playersAndScores.remove(player.getUniqueId());
	}
	
	public boolean containsPlayer(Player player) {
		return playersAndScores.containsKey(player.getUniqueId());
	}
	
	public String getCode() {
		return String.format("%08X", this.hashCode());
	}
	
	/**
	 * Starts the match.
	 */
	public void start() {
		announce("Welcome to Challenge Run! Your objective is to complete as many challenges as you can.");
		announce(String.format("You will have %d minutes to prepare before being given your first task, good luck!", prepTimerMins));
		startPrepTimer();
	}
	
	/**
	 * Ends the match and announces the scores.
	 */
	public void end() {
		cancelTimer();
		endChallenge();
		announce("The game is over! Lets see how everyone did!");
		annouceScores();
	}
	
	
	/**
	 * Signifies that a player wants to skip the current challenge. It will be
	 * skipped once 75% of this match's players have voted.
	 *
	 * @param player The player who voted
	 */
	public void voteToSkip(Player player) {
		if (votes.add(player.getUniqueId()))
			announce(String.format("%s has voted to skip this challenge!", player.getDisplayName()));
		else
			player.sendMessage("You already voted!");
		// Skip with large majority vote
		if ((double) votes.size() / ((double) playersAndScores.size()) >= 0.75)
			skipChallenge();
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
	
	/**
	 * Start prep timer.
	 */
	private void startPrepTimer() {
		schedule(this::prepTimerEnded, prepTimerMins);
	}
	
	/**
	 * Start challenge timer.
	 */
	private void startChallengeTimer() {
		schedule(this::challengeTimerEnded, challengeTimerMins);
	}
	
	/**
	 * Cancel timer.
	 */
	private void cancelTimer() {
		Bukkit.getScheduler().cancelTask(timerTaskId);
	}
	
	/**
	 * End challenge.
	 */
	private void endChallenge() {
		votes.clear();
		currentChallenge.end();
	}
	
	/**
	 * Announce a message to all players in this match.
	 *
	 * @param message Message to announce
	 */
	private void announce(String message) {
		playersAndScores.keySet().forEach(id -> Bukkit.getPlayer(id).sendMessage(message));
	}
	
	private void annouceScores() {
		playersAndScores.entrySet().stream()
				.sorted((e1, e2) -> e2.getValue() - e1.getValue())
				.forEach(e -> announce(String.format("%s: %d points",
						Bukkit.getPlayer(e.getKey()).getDisplayName(),
						e.getValue())
				));
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
