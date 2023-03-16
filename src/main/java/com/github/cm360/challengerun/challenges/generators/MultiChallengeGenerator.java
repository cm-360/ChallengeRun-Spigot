package com.github.cm360.challengerun.challenges.generators;

import java.util.HashMap;
import java.util.Map;

import com.github.cm360.challengerun.challenges.Challenge;
import com.github.cm360.challengerun.challenges.ChallengeGenerator;

public class MultiChallengeGenerator extends ChallengeGenerator {

	private Map<ChallengeGenerator, Integer> generators = new HashMap<ChallengeGenerator, Integer>();
	
	public void addGenerator(ChallengeGenerator generator, int weight) {
		generators.put(generator, weight);
	}
	
	@Override
	public Challenge generateChallenge() {
		// TODO Auto-generated method stub
		return null;
	}

}
