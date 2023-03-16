package com.github.cm360.challengerun.matches;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.cm360.challengerun.challenges.Challenge;
import com.github.cm360.challengerun.challenges.generators.ObtainItemChallengeGenerator;
import com.github.cm360.challengerun.events.ChallengeCompletedEvent;
import com.github.cm360.challengerun.events.MatchCompletedEvent;
import com.github.cm360.challengerun.main.ChallengeRunPlugin;

/**
 * The Class Match.
 */
public class Match implements Listener {

	/** The players in this match. */
	private Map<UUID, Integer> playersAndScores = new HashMap<UUID, Integer>();
	
	/** The players that have voted to skip the current challenge. **/
	private Set<UUID> votes = new HashSet<UUID>();
	
	private boolean votingAllowed = false;
	
	/** The current challenge. */
	private Challenge currentChallenge;
	
	/** The number of minutes to put on the prep timer. */
	private int prepTimerMins = 5;
	
	/** The number of minutes to put on the voting timer. */
	private int votingTimerMins = 2;
	
	/** The number of minutes to put on the challenge timer. */
	private int challengeTimerMins = 10;
	
	/** The task ID of the currently running main timer. */
	private int mainTimerTaskId;
	
	/** The task ID of the voting timer. */
	private int votingTimerTaskId;
	
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
	
	public Set<UUID> getPlayerIds() {
		return playersAndScores.keySet();
	}
	
	public String getCode() {
		return String.format("%08X", this.hashCode());
	}
	
	/**
	 * Starts the match and registers its event handlers.
	 */
	public void start() {
		announce("Welcome to Challenge Run! Your objective is to complete as many challenges as you can.");
		announce(String.format("You will have %d minutes to prepare before being given your first task, good luck!", prepTimerMins));
		startPrepPeriod();
		Bukkit.getPluginManager().registerEvents(this, ChallengeRunPlugin.instance);
	}
	
	/**
	 * Ends the match and announces the scores. Calling will also unregister this
	 * match's event handlers.
	 */
	public void end() {
		endChallenge();
		announce("The game is over! Lets see how everyone did!");
		annouceScores();
		Bukkit.getPluginManager().callEvent(new MatchCompletedEvent(this));
		HandlerList.unregisterAll(this);
	}
	
	
	/**
	 * Signifies that a player wants to skip the current challenge. It will be
	 * skipped once 75% of this match's players have voted.
	 *
	 * @param player The player who voted
	 */
	public void voteToSkip(Player player) {
		// Check if voting is open
		if (!votingAllowed) {
			player.sendMessage("The vote-skip period for this challenge is closed!");
			return;
		}
		// Check if player already voted
		if (votes.add(player.getUniqueId()))
			announce(String.format("%s has voted to skip this challenge!", player.getDisplayName()));
		else
			player.sendMessage("You already voted!");
		// Skip with large majority vote
		if ((double) votes.size() / ((double) playersAndScores.size()) >= 0.75)
			skipChallenge();
	}
	
	@EventHandler
	public void onChallengeCompleted(ChallengeCompletedEvent event) {
		Challenge completedChallenge = event.getChallenge();
		if (completedChallenge != currentChallenge)
			return;
		// Increment score
		Player player = event.getPlayer();
		UUID playerId = player.getUniqueId();
		playersAndScores.put(playerId, playersAndScores.get(playerId) + 1);
		announce(String.format("%s has completed the challenge and earned a point!", player.getDisplayName()));
		// Disable voting
		endVotingPeriod();
	}
	
	/**
	 * Skip the currently running challenge. This will cancel any currently running
	 * timers, end the current challenge, generate a new challenge, and restart the
	 * challenge and voting periods.
	 */
	public void skipChallenge() {
		endChallenge();
		announce("The current challenge was vote-skipped! Your next challenge is:");
		generateNewChallenge();
		startChallengePeriod();
		startVotingPeriod();
	}
	
	/**
	 * TODO Generates a new challenge.
	 */
	private void generateNewChallenge() {
		currentChallenge = new ObtainItemChallengeGenerator().generateChallenge();
		announce(currentChallenge.getDescription());
	}

	/**
	 * Called when the preparation period timer ends. This will generate the first
	 * task and start the challenge and voting periods.
	 */
	private void prepTimerEnded() {
		announce("The preparation period is over! Your first challenge is:");
		generateNewChallenge();
		startChallengePeriod();
		startVotingPeriod();
	}
	
	/**
	 * Called when the challenge timer ends. This will end the current challenge,
	 * generate a new challenge, and restart the challenge and voting periods.
	 */
	private void challengeTimerEnded() {
		endChallenge();
		announce("Times up! Your next challenge is:");
		generateNewChallenge();
		startChallengePeriod();
		startVotingPeriod();
	}
	
	/**
	 * Start the prep timer.
	 */
	private void startPrepPeriod() {
		mainTimerTaskId = schedule(this::prepTimerEnded, prepTimerMins);
	}
	
	/**
	 * Start the voting timer.
	 */
	private void startVotingPeriod() {
		votingAllowed = true;
		votes.clear();
		votingTimerTaskId = schedule(this::endVotingPeriod, votingTimerMins);
	}
	
	private void endVotingPeriod() {
		closeVoting();
		announce("The skip-vote period is now closed!");
	}
	
	/**
	 * Closes the voting period without announcing anything.
	 */
	private void closeVoting() {
		votingAllowed = false;
		votes.clear();
	}
	
	/**
	 * Start the challenge timer.
	 */
	private void startChallengePeriod() {
		mainTimerTaskId = schedule(this::challengeTimerEnded, challengeTimerMins);
	}
	
	/**
	 * Cancel all running timers.
	 */
	private void cancelTimers() {
		BukkitScheduler bs = Bukkit.getScheduler();
		bs.cancelTask(mainTimerTaskId);
		bs.cancelTask(votingTimerTaskId);
	}
	
	/**
	 * End the current challenge.
	 */
	private void endChallenge() {
		cancelTimers();
		closeVoting();
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
	private int schedule(Runnable runnable, long delayMins) {
		return Bukkit.getScheduler().scheduleSyncDelayedTask(ChallengeRunPlugin.instance, runnable, delayMins * 60 * 20 );
	}

}
