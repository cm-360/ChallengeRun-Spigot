package com.github.cm360.challengerun.challenges.generators;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Material;

import com.github.cm360.challengerun.challenges.ChallengeGenerator;
import com.github.cm360.challengerun.challenges.types.BreakBlockChallenge;

public class BreakBlockChallengeGenerator extends ChallengeGenerator {

	public BreakBlockChallenge generateChallenge() {
		List<Material> blockMaterials = Stream.of(Material.values())
				.filter(Material::isBlock)
				.filter(m -> m.getHardness() > 0)
				.collect(Collectors.toList());
		Material blockMaterial = blockMaterials.get(rand.nextInt(blockMaterials.size()));
		return new BreakBlockChallenge(blockMaterial);
	}

}
