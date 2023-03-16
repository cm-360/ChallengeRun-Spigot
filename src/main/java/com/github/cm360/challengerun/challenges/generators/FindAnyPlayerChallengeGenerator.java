package com.github.cm360.challengerun.challenges.generators;

import java.util.function.Predicate;

import org.bukkit.entity.Entity;

import com.github.cm360.challengerun.challenges.ChallengeGenerator;
import com.github.cm360.challengerun.challenges.types.FindAnyPlayerChallenge;

public class FindAnyPlayerChallengeGenerator extends ChallengeGenerator {

	protected Predicate<Entity> playerInMatchPredicate;
	
	public FindAnyPlayerChallengeGenerator(Predicate<Entity> playerInMatchPredicate) {
		this.playerInMatchPredicate = playerInMatchPredicate;
	}

	@Override
	public FindAnyPlayerChallenge generateChallenge() {
		return new FindAnyPlayerChallenge(playerInMatchPredicate, 5.0);
	}

}
