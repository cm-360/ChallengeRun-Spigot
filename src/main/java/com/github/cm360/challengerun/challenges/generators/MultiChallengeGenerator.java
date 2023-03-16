package com.github.cm360.challengerun.challenges.generators;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.github.cm360.challengerun.challenges.Challenge;
import com.github.cm360.challengerun.challenges.ChallengeGenerator;

public class MultiChallengeGenerator extends ChallengeGenerator {

	private Map<ChallengeGenerator, Integer> generators = new HashMap<ChallengeGenerator, Integer>();
	
	public void addGenerator(ChallengeGenerator generator, int weight) {
		generators.put(generator, weight);
	}
	
	@Override
	public Challenge generateChallenge() {
		// Calculate total weight
		int weightSum = 0;
		for (int w : generators.values())
			weightSum += w;
		// 
		Iterator<Entry<ChallengeGenerator, Integer>> genIterator = generators.entrySet().iterator();
		ChallengeGenerator gen = null;
		int r = rand.nextInt(weightSum);
		while (r >= 0 && genIterator.hasNext()) {
			Entry<ChallengeGenerator, Integer> entry = genIterator.next();
			gen = entry.getKey();
			r -= entry.getValue();
		}
		return gen.generateChallenge();
	}

}
