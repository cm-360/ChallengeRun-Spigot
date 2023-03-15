package com.github.cm360.challengerun.challenges.generators;

import java.util.List;

import org.bukkit.entity.Player;

import com.github.cm360.challengerun.challenges.ChallengeGenerator;
import com.github.cm360.challengerun.challenges.types.KillPlayerChallenge;

public class KillPlayerChallengeGenerator extends ChallengeGenerator {

	protected List<Player> matchPlayerList;
	
	@Override
	public KillPlayerChallenge generateChallenge() {
		Player targetPlayer = matchPlayerList.get(rand.nextInt(matchPlayerList.size()));
		return new KillPlayerChallenge(targetPlayer);
	}

}
