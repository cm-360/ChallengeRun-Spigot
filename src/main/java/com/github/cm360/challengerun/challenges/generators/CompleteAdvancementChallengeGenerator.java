package com.github.cm360.challengerun.challenges.generators;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;

import com.github.cm360.challengerun.challenges.ChallengeGenerator;
import com.github.cm360.challengerun.challenges.types.CompleteAdvancementChallenge;

public class CompleteAdvancementChallengeGenerator extends ChallengeGenerator {

	@Override
	public CompleteAdvancementChallenge generateChallenge() {
		List<Advancement> advancements = new ArrayList<Advancement>();
		Bukkit.advancementIterator().forEachRemaining(a -> advancements.add(a));
		Advancement advancement = advancements.get(rand.nextInt(advancements.size()));
		return new CompleteAdvancementChallenge(advancement);
	}

}
