package com.github.cm360.challengerun.main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.cm360.challengerun.matches.Match;
import com.github.cm360.challengerun.matches.MatchManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChallengeRunPlugin extends JavaPlugin {

	public static ChallengeRunPlugin instance;
	
	private MatchManager matchManager = new MatchManager();
	
	@Override
	public void onEnable() {
		instance = this;
		this.getCommand("challengerun").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(matchManager, this);
	}
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		List<String> argsList = new ArrayList<String>();
		for (String arg : args)
			argsList.add(arg);
		if (argsList.isEmpty())
			return false;
		
		Player player;
		Match match;
		
		String nextArg = argsList.remove(0);
		switch (nextArg) {
		case "create":
			match = matchManager.createMatch();
			sender.sendMessage("Created a new match! Code: " + match.getCode());
			broadcastNewMatch(sender.getName(), match.getCode());
			if (sender instanceof Player)
				match.addPlayer((Player) sender);
			break;
		case "join":
			// Validate player only
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use this command!");
				break;
			}
			player = (Player) sender;
			// Check argument
			nextArg = argsList.remove(0);
			if (nextArg == null) {
				sender.sendMessage("You must provide a match code!");
				break;
			}
			// Check if already in match
			match = matchManager.getMatchForPlayer(player);
			if (match != null) {
				sender.sendMessage("You are already in a match! Code: " + match.getCode());
				break;
			}
			// Check if match exists
			match = matchManager.getMatchByCode(nextArg);
			if (match == null) {
				sender.sendMessage("That match code is not valid!");
				break;
			}
			// Add player to match
			match.addPlayer(player);
			sender.sendMessage("You joined the match!");
			break;
		case "leave":
			// Validate player only
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use this command!");
				break;
			}
			player = (Player) sender;
			// Check if not in match
			match = matchManager.getMatchForPlayer(player);
			if (match == null) {
				sender.sendMessage("You are not in a match!");
				break;
			}
			// Remove player from match
			match.removePlayer(player);
			sender.sendMessage("You left the match!");
			break;
		case "start":
			// Validate player only
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use this command!");
				break;
			}
			player = (Player) sender;
			// Check if not in match
			match = matchManager.getMatchForPlayer(player);
			if (match == null) {
				sender.sendMessage("You are not in a match!");
				break;
			}
			// Remove player from match
			sender.sendMessage("Starting the match...");
			match.start();
			break;
		case "stop":
			break;
		case "info":
			// Validate player only
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use this command!");
				break;
			}
			player = (Player) sender;
			// Check argument
			nextArg = argsList.remove(0);
			if (nextArg == null) {
				// Check if already in match
				match = matchManager.getMatchForPlayer(player);
				if (match == null) {
					sender.sendMessage("You are not currently in a match.");
					break;
				}
				sendMatchInfo((Player) sender, match);
			} else {
				// Check if match exists
				match = matchManager.getMatchByCode(nextArg);
				if (match == null) {
					sender.sendMessage("That match code is not valid!");
					break;
				}
				sendMatchInfo((Player) sender, match);
			}
			break;
		case "skip":
			// Validate player only
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use this command!");
				break;
			}
			player = (Player) sender;
			// Check if not in match
			match = matchManager.getMatchForPlayer(player);
			if (match == null) {
				sender.sendMessage("You are not in a match!");
				break;
			}
			// Vote to skip the challenge
			match.voteToSkip(player);
			break;
		case "forceskip":
			break;
		default:
			return false;
		}
		return true;
	}
	
	public void sendMatchInfo(Player player, Match match) {
		player.sendMessage("Currently in match " + match.getCode());
		String playerList = String.join(", ", match.getPlayerIds().stream()
				.map(pid -> Bukkit.getPlayer(pid).getDisplayName())
				.collect(Collectors.toList()));
		player.sendMessage("  Players: " + playerList);
	}
	
	public void broadcastNewMatch(String ownerName, String code) {
		TextComponent clickable = new TextComponent("Click here to join!");
		clickable.setColor(ChatColor.GREEN);
		clickable.setBold(true);
		clickable.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/chr join %s", code)));
		TextComponent text = new TextComponent(String.format("%s has started a new match! ", ownerName));
		Bukkit.spigot().broadcast(text, clickable);
	}

}
