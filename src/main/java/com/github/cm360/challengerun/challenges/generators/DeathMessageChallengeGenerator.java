package com.github.cm360.challengerun.challenges.generators;

import org.bukkit.entity.EntityType;

import com.github.cm360.challengerun.challenges.ChallengeGenerator;
import com.github.cm360.challengerun.challenges.types.DeathMessageChallenge;
import com.github.cm360.challengerun.utils.NameUtils;

public class DeathMessageChallengeGenerator extends ChallengeGenerator {
	
	private String[] deathMessages = {
			"{player} fell off a ladder",
			"{player} fell off some vines",
			"{player} fell off some weeping vines",
			"{player} fell off some twisting vines",
			"{player} fell off scaffolding",
			"{player} fell from a high place",
			"{player} went up in flames",
			"{player} walked into fire whilst fighting {mob}",
			"{player} burned to death",
			"{player} was burnt to a crisp whilst fighting {mob}",
			"{player} tried to swim in lava",
			"{player} tried to swim in lava to escape {mob}",
			"{player} discovered the floor was lava",
			"{player} walked into danger zone due to {mob}",
			"{player} suffocated in a wall",
			"{player} was squished too much",
			"{player} drowned",
			"{player} starved to death",
			"{player} was pricked to death",
			"{player} walked into a cactus whilst trying to escape {mob}",
			"{player} blew up",
			"{player} was killed by magic",
			"{player} was squashed by a falling anvil",
			"{player} was squashed by a falling anvil whilst fighting {mob}",
			"{player} was squashed by a falling block",
			"{player} was squashed by a falling block whilst fighting {mob}",
			"{player} was impaled on a stalagmite",
			"{player} was impaled on a stalagmite whilst fighting {mob}",
			"{player} was skewered by a falling stalactite",
			"{player} was skewered by a falling stalactite whilst fighting {mob}",
			"{player} was obliterated by a sonically-charged shriek",
			"{player} was slain by {mob}",
			"{player} was shot by Skeleton",
			"{player} was shot by Pillager",
			"{player} was fireballed by Blaze",
			"{player} was fireballed by Ghast",
			"{player} was impaled by Drowned",
			"{player} hit the ground too hard",
			"{player} hit the ground too hard whilst trying to escape {mob}",
			"{player} went off with a bang",
			"{player} was killed by [Intentional Game Design]",
			"{player} was poked to death by a sweet berry bush",
			"{player} was poked to death by a sweet berry bush whilst trying to escape {mob}",
			"{player} was stung to death",
			"{player} froze to death"
	};
	
	private EntityType[] assistMobsEasy = {
			EntityType.ZOMBIE,
			EntityType.SPIDER,
			EntityType.SKELETON,
			EntityType.ENDERMAN
	};
	
	private EntityType[] assistMobsHard = {
			EntityType.HOGLIN,
			EntityType.PIGLIN,
			EntityType.ZOMBIFIED_PIGLIN
	};

	@Override
	public DeathMessageChallenge generateChallenge() {
		String deathMessage = deathMessages[rand.nextInt(deathMessages.length)];
		
		deathMessage = deathMessage.replace("{mob}", chooseAssistMob());
		return new DeathMessageChallenge(deathMessage);
	}
	
	protected String chooseAssistMob() {
		EntityType[] assistMobs;
		if (rand.nextInt(10) >= 2)
			assistMobs = assistMobsEasy;
		else
			assistMobs = assistMobsHard;
		EntityType assistMob = assistMobs[rand.nextInt(assistMobs.length)];
		return NameUtils.enumToTitleCase(assistMob);
	}

}
