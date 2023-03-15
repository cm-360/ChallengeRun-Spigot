package com.github.cm360.challengerun.matches;

import java.util.ArrayList;
import java.util.List;

public class MatchManager {

	private List<Match> matches = new ArrayList<Match>();

	public MatchManager() {
		
	}
	
	public Match createMatch() {
		Match match = new Match();
		matches.add(match);
		return match;
	}

}
