package com.github.cm360.challengerun.challenges.generators;

import com.github.cm360.challengerun.challenges.ChallengeGenerator;
import com.github.cm360.challengerun.challenges.types.FindAnyPlayerChallenge;

public class FindAnyPlayerChallengeGenerator extends ChallengeGenerator {

	@Override
	public FindAnyPlayerChallenge generateChallenge() {
		return new FindAnyPlayerChallenge(5.0);
	}

}
