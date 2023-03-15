package com.github.cm360.challengerun.main;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.cm360.challengerun.matches.MatchManager;

public class ChallengeRunPlugin extends JavaPlugin {

	public static ChallengeRunPlugin instance;
	
	private MatchManager matchManager = new MatchManager();
	
	@Override
	public void onEnable() {
		instance = this;
	}
	
	@Override
	public void onDisable() {
		
	}

}
