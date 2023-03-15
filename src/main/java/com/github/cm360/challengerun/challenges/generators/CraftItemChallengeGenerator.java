package com.github.cm360.challengerun.challenges.generators;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;

import com.github.cm360.challengerun.challenges.ChallengeGenerator;
import com.github.cm360.challengerun.challenges.types.CraftItemChallenge;

public class CraftItemChallengeGenerator extends ChallengeGenerator {

	@Override
	public CraftItemChallenge generateChallenge() {
		List<Recipe> recipes = new ArrayList<Recipe>();
		Bukkit.recipeIterator().forEachRemaining(r -> recipes.add(r));
		Recipe recipe = recipes.get(rand.nextInt(recipes.size()));
		return new CraftItemChallenge(recipe.getResult().getType());
	}

}
