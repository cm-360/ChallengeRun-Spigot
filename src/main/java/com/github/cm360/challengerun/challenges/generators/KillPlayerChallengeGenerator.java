package com.github.cm360.challengerun.challenges.generators;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.github.cm360.challengerun.challenges.ChallengeGenerator;
import com.github.cm360.challengerun.challenges.types.KillPlayerChallenge;

public class KillPlayerChallengeGenerator extends ChallengeGenerator {

	protected Collection<UUID> matchPlayers;
	
	public KillPlayerChallengeGenerator(Collection<UUID> matchPlayers) {
		this.matchPlayers = matchPlayers;
	}
	
	@Override
	public KillPlayerChallenge generateChallenge() {
		List<UUID> matchPlayerList = List.copyOf(matchPlayers);
		UUID targetPlayerId = matchPlayerList.get(rand.nextInt(matchPlayerList.size()));
		return new KillPlayerChallenge(Bukkit.getPlayer(targetPlayerId));
	}

}
