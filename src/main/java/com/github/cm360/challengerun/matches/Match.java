package com.github.cm360.challengerun.matches;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;

import com.github.cm360.challengerun.challenges.Challenge;
import com.github.cm360.challengerun.challenges.generators.BreakBlockChallengeGenerator;
import com.github.cm360.challengerun.challenges.generators.CompleteAdvancementChallengeGenerator;
import com.github.cm360.challengerun.challenges.generators.CraftItemChallengeGenerator;
import com.github.cm360.challengerun.challenges.generators.DeathMessageChallengeGenerator;
import com.github.cm360.challengerun.challenges.generators.FindAnyPlayerChallengeGenerator;
import com.github.cm360.challengerun.challenges.generators.FindBiomeChallengeGenerator;
import com.github.cm360.challengerun.challenges.generators.KillEntityChallengeGenerator;
import com.github.cm360.challengerun.challenges.generators.KillPlayerChallengeGenerator;
import com.github.cm360.challengerun.challenges.generators.MultiChallengeGenerator;
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

	/** The scoreboard for displaying this match's players' points **/
	private Scoreboard scoreboard;

	/** The bossbar for displaying the time remaining in this match. **/
	private BossBar bossbar;

	/** The players who have voted to skip the current challenge. **/
	private Set<UUID> votes = new HashSet<UUID>();

	/** The players who have completed the current challenge **/
	private Set<UUID> completed = new HashSet<UUID>();

	/** Determines if players are allowed to vote-skip the current challenge. **/
	private boolean votingAllowed = false;

	/** Generator to randomly select challenges. **/
	private MultiChallengeGenerator challengeGenerator = new MultiChallengeGenerator();

	/** The current challenge. */
	private Challenge currentChallenge;

	/** The number of minutes to put on the prep timer. */
	private int prepTimerMins = 5;

	/** The number of minutes to put on the voting timer. */
	private int votingTimerMins = 2;

	/** The number of minutes to put on the challenge timer. */
	private int challengeTimerMins = 10;

	/** The length of this match in minutes **/
	private int matchDuration = 120;

	/** The number of seconds between scoreboard and bossbar updates. **/
	private int displayIntervalSecs = 1;

	/** The task ID of the currently running main timer. */
	private int mainTimerTaskId;

	/** The task ID of the voting timer. */
	private int votingTimerTaskId;

	/** The task ID of the display updater. **/
	private int displayTimerTaskId;

	/** Provides the absolute in-game time of world as a reference. **/
	private Supplier<Long> tickReference;

	/** The absolute in-game time this match was started at. **/
	private long startTime;

	public Match() {
		challengeGenerator.addGenerator(
				new BreakBlockChallengeGenerator(),
				5);
		challengeGenerator.addGenerator(
				new CompleteAdvancementChallengeGenerator(),
				3);
		challengeGenerator.addGenerator(
				new CraftItemChallengeGenerator(),
				4);
		challengeGenerator.addGenerator(
				new DeathMessageChallengeGenerator(),
				3);
		challengeGenerator.addGenerator(
				new FindAnyPlayerChallengeGenerator(p -> playersAndScores.containsKey(p.getUniqueId())),
				5);
		challengeGenerator.addGenerator(
				new FindBiomeChallengeGenerator(),
				2);
		challengeGenerator.addGenerator(
				new KillEntityChallengeGenerator(),
				3);
		challengeGenerator.addGenerator(
				new KillPlayerChallengeGenerator(playersAndScores.keySet()),
				2);
		challengeGenerator.addGenerator(
				new ObtainItemChallengeGenerator(),
				5);

		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		bossbar = Bukkit.createBossBar(
				NamespacedKey.fromString("match" + getCode(), ChallengeRunPlugin.instance),
				"Time Remaining for Challenge", BarColor.GREEN, BarStyle.SOLID);
	}

	/**
	 * Adds a player to this match.
	 *
	 * @param player The player to add
	 */
	public void addPlayer(Player player) {
		playersAndScores.put(player.getUniqueId(), 0);
		// I hope these persist across relogs :shrug:
		player.setScoreboard(scoreboard);
		bossbar.addPlayer(player);
	}

	/**
	 * Removes a player from this match.
	 *
	 * @param player The player to remove
	 */
	public void removePlayer(Player player) {
		playersAndScores.remove(player.getUniqueId());
		// maybe we need to remove the scoreboard?
		bossbar.removePlayer(player);
	}

	/**
	 * Determines if a player is in this match or not.
	 *
	 * @param player The player to look for
	 * @return true, If the given player is in this match
	 */
	public boolean containsPlayer(Player player) {
		return playersAndScores.containsKey(player.getUniqueId());
	}

	/**
	 * Returns the UUIDs of this match's players. The returned set is backed by the
	 * internal score map, so changes to the player list will be reflected
	 * immediately in the set, and vice-versa.
	 *
	 * @return The set of all UUIDs of players in this game
	 */
	public Set<UUID> getPlayerIds() {
		return playersAndScores.keySet();
	}

	/**
	 * Gets this match's unique code. This code is an 8-character hexadecimal
	 * representation of this object's hashCode value.
	 *
	 * @return This match's code
	 */
	public String getCode() {
		return String.format("%08X", this.hashCode());
	}

	/**
	 * Starts the match and registers its event handlers. In order to accurately
	 * track elapsed time, a world to use as a tick reference is required.
	 *
	 * @param tickReferenceWorld The world to use as a tick reference
	 */
	public void start(World tickReferenceWorld) {
		announce("Welcome to Challenge Run! Your objective is to complete as many challenges as you can.");
		announce(String.format("You will have %d minutes to prepare before being given your first task, good luck!",
				prepTimerMins));
		startPrepPeriod();
		Bukkit.getPluginManager().registerEvents(this, ChallengeRunPlugin.instance);
		Bukkit.getScheduler().runTaskTimer(ChallengeRunPlugin.instance, this::updateDisplay, 0,
				displayIntervalSecs * 20);

		this.tickReference = tickReferenceWorld::getFullTime;
		startTime = tickReference.get();
	}

	/**
	 * Ends the match and announces the scores. Calling will also unregister this
	 * match's event handlers.
	 */
	public void end() {
		endChallenge(true);
		announce("The game is over! Lets see how everyone did!");
		annouceScores();
		Bukkit.getScheduler().cancelTask(displayTimerTaskId);
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

	/**
	 * Fires when a challenge completed event is called. If the event is for a
	 * different challenge than {@code currentChallenge}, or if the player has
	 * already completed the current challenge, this method will return immediately
	 * and do nothing. For new completions, the player's score will be incremented
	 * by 1, and it will be announced.
	 * 
	 * If this player is the last completer, (@code allPlayersCompleted()} will be
	 * called. Otherwise, the challenge will continue and the
	 * {@code endVotingPeriod()} will be called.
	 *
	 * @param event The challenge completed event object
	 */
	@EventHandler
	public void onChallengeCompleted(ChallengeCompletedEvent event) {
		Challenge completedChallenge = event.getChallenge();
		if (completedChallenge != currentChallenge)
			return;
		// Check if already completed
		Player player = event.getPlayer();
		UUID playerId = player.getUniqueId();
		if (completed.contains(playerId))
			return;
		// Increment score
		playersAndScores.put(playerId, playersAndScores.get(playerId) + 1);
		announce(String.format("%s has completed the challenge and earned a point!", player.getDisplayName()));
		completed.add(playerId);
		// Check if everyone completed
		if (completed.containsAll(playersAndScores.keySet())) {
			allPlayersCompleted();
		} else {
			endVotingPeriod();
		}
	}

	/**
	 * Called when all players completed the current challenge. This will generate a
	 * new challenge and restart the challenge and voting period timers.
	 */
	private void allPlayersCompleted() {
		endChallenge(false);
		announce("Great job! Everyone completed the challenge before the time limit. Your next challenge is:");
		generateNewChallenge();
		startChallengePeriod();
		startVotingPeriod();
	}

	/**
	 * Skips the currently running challenge. This will cancel any currently running
	 * timers, end the current challenge, generate a new challenge, and restart the
	 * challenge and voting periods.
	 */
	public void skipChallenge() {
		endChallenge(false);
		announce("The current challenge was vote-skipped! Your next challenge is:");
		generateNewChallenge();
		startChallengePeriod();
		startVotingPeriod();
	}

	/**
	 * Generates a new challenge.
	 */
	private void generateNewChallenge() {
		completed.clear();
		currentChallenge = challengeGenerator.generateChallenge();
		currentChallenge.start();
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
		endChallenge(true);
		announce("Times up! Your next challenge is:");
		generateNewChallenge();
		startChallengePeriod();
		startVotingPeriod();
	}

	/**
	 * Starts the prep timer.
	 */
	private void startPrepPeriod() {
		mainTimerTaskId = schedule(this::prepTimerEnded, prepTimerMins);
	}

	/**
	 * Skips the prep period and starts giving challenges immediately. Do not call
	 * during the challenge portion of a game!
	 */
	public void skipPrepPeriod() {
		cancelTimers();
		prepTimerEnded();
	}

	/**
	 * Starts the voting timer.
	 */
	private void startVotingPeriod() {
		votingAllowed = true;
		votes.clear();
		votingTimerTaskId = schedule(this::endVotingPeriod, votingTimerMins);
	}

	/**
	 * Closes the voting period and announce it.
	 */
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
	 * Starts the challenge timer.
	 */
	private void startChallengePeriod() {
		mainTimerTaskId = schedule(this::challengeTimerEnded, challengeTimerMins);
	}

	/**
	 * Cancels all running timers.
	 */
	private void cancelTimers() {
		BukkitScheduler bs = Bukkit.getScheduler();
		if (mainTimerTaskId != -1 && bs.isQueued(mainTimerTaskId)) {
			bs.cancelTask(mainTimerTaskId);
			mainTimerTaskId = -1;
		}
		if (votingTimerTaskId != -1 && bs.isQueued(votingTimerTaskId)) {
			bs.cancelTask(votingTimerTaskId);
			votingTimerTaskId = -1;
		}
	}

	/**
	 * Ends the current challenge. If {@code allowAward} is true, the current
	 * challenge will also be allowed to award players right before ending, such as
	 * for surviving the timer.
	 *
	 * @param allowAward Whether to allow awarding
	 */
	private void endChallenge(boolean allowAward) {
		cancelTimers();
		closeVoting();
		if (currentChallenge != null)
			currentChallenge.end(allowAward);
	}

	/**
	 * Announces a message to all players in this match.
	 *
	 * @param message Message to announce
	 */
	private void announce(String message) {
		playersAndScores.keySet().forEach(id -> Bukkit.getPlayer(id).sendMessage(message));
	}

	/**
	 * Announces the scores of this match's players.
	 */
	private void annouceScores() {
		playersAndScores.entrySet().stream().sorted((e1, e2) -> e2.getValue() - e1.getValue()).forEach(e -> announce(
				String.format("%s: %d points", Bukkit.getPlayer(e.getKey()).getDisplayName(), e.getValue())));
	}

	/**
	 * Updates the scoreboard and bossbar for this match's players.
	 */
	private void updateDisplay() {
		double secondsElapsed = (tickReference.get() - startTime) / 20.0;
		bossbar.setProgress(secondsElapsed / (matchDuration * 60));
	}

	/**
	 * Schedules a task to be run after a specified number of minutes.
	 *
	 * @param runnable  The task to run
	 * @param delayMins The number of minutes to wait
	 */
	private int schedule(Runnable runnable, long delayMins) {
		return Bukkit.getScheduler().scheduleSyncDelayedTask(ChallengeRunPlugin.instance, runnable, delayMins * 1200);
	}

}
