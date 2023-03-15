package com.github.cm360.challengerun.challenges.generators;

import org.bukkit.block.Biome;

import com.github.cm360.challengerun.challenges.ChallengeGenerator;
import com.github.cm360.challengerun.challenges.types.FindBiomeChallenge;

public class FindBiomeChallengeGenerator extends ChallengeGenerator {

	@Override
	public FindBiomeChallenge generateChallenge() {
		Biome[] biomes = Biome.values();
		Biome biome = biomes[rand.nextInt(biomes.length)];
		return new FindBiomeChallenge(biome);
	}

}
