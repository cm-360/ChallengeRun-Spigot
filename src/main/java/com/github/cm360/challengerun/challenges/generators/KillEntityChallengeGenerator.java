package com.github.cm360.challengerun.challenges.generators;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.entity.EntityType;

import com.github.cm360.challengerun.challenges.ChallengeGenerator;
import com.github.cm360.challengerun.challenges.types.KillEntityChallenge;

public class KillEntityChallengeGenerator extends ChallengeGenerator {

	private EntityType[] invalidEntities = {
			EntityType.ENDER_DRAGON,
			EntityType.WITHER
		};
	
	@Override
	public KillEntityChallenge generateChallenge() {
		List<EntityType> entityTypes = Stream.of(EntityType.values())
				.filter(EntityType::isAlive)
				.filter(t -> {
					for (EntityType invalid : invalidEntities)
						if (t == invalid)
							return false;
					return true;
				})
				.collect(Collectors.toList());
		EntityType entityType = entityTypes.get(rand.nextInt(entityTypes.size()));
		return new KillEntityChallenge(entityType);
	}

}
