package com.github.cm360.challengerun.challenges;

import java.util.Random;

public abstract class ChallengeGenerator {

	protected static Random rand = new Random();
	
	public abstract Challenge generateChallenge();

}
