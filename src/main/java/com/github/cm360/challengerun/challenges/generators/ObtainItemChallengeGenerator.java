package com.github.cm360.challengerun.challenges.generators;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Material;

import com.github.cm360.challengerun.challenges.ChallengeGenerator;
import com.github.cm360.challengerun.challenges.types.ObtainItemChallenge;

public class ObtainItemChallengeGenerator extends ChallengeGenerator {

	private String[] invalidMaterials = {
			"BARRIER",
			"BEDROCK",
			"COMMAND_BLOCK",
			"END_PORTAL_FRAME",
			"_ORE",
			"_SPAWN_EGG"
		};
	
	@SuppressWarnings("deprecation")
	@Override
	public ObtainItemChallenge generateChallenge() {
		List<Material> itemMaterials = Stream.of(Material.values())
				.filter(Material::isItem)
				.filter(Predicate.not(Material::isLegacy))
				.filter(m -> {
					String materialName = m.name();
					for (String s : invalidMaterials)
						if (materialName.contains(s))
							return false;
					return true;
				})
				.collect(Collectors.toList());
		Material itemMaterial = itemMaterials.get(rand.nextInt(itemMaterials.size()));
		return new ObtainItemChallenge(itemMaterial);
	}

}
