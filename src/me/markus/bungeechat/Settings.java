package me.markus.bungeechat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;


public class Settings {

	public static String getMySQLHost;
	public static String getMySQLPort;
	public static String getMySQLUsername;
	public static String getMySQLDatabase;
	public static String getMySQLPassword;
	public static boolean isStopEnabled;
	public static ColoredRanks rankColors;
	public static ArrayList<String> nicknameRanks;
	
	

	public static void loadSettings() {

		// Default settings
		getMySQLHost = "foo.server.com";
		getMySQLPort = "1234";
		getMySQLUsername = "sqlAdmin";
		getMySQLDatabase = "forumDB";
		getMySQLPassword = "foobar";
		isStopEnabled = true;
		rankColors = new ColoredRanks();
		rankColors.put("Admin", ChatColor.RED);
		rankColors.put("Trusted", ChatColor.GREEN);
		nicknameRanks = new ArrayList<String>();
		nicknameRanks.add("Admin");

		File file = new File(BungeeChat.instance.getDataFolder(), "config.yml");
		if (!file.exists())
			saveSettings();
		
		try {
			Configuration yaml = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			
			getMySQLHost = yaml.getString("Datasource.mySQLHost");
			getMySQLPort = yaml.getString("Datasource.mySQLPort");
			getMySQLUsername = yaml.getString("Datasource.mySQLUsername");
			getMySQLDatabase = yaml.getString("Datasource.mySQLDatabase");
			getMySQLPassword = yaml.getString("Datasource.mySQLPassword");

			rankColors = new ColoredRanks();
			for (String s : yaml.getStringList("RankColors")){
				String[] split = s.split(":");
				rankColors.put(split[0], ChatColor.valueOf(split[1]));
			}
			nicknameRanks = new ArrayList<String>();
			for (String rank : yaml.getStringList("NicknameRanks")){
				nicknameRanks.add(rank);
			}
			
			isStopEnabled = yaml.getBoolean("Security.SQLProblem.stopServer");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		saveSettings();
	}

	public static void saveSettings() {
		try {
			File file = new File(BungeeChat.instance.getDataFolder(), "config.yml");
			if (!file.exists()){
				file.createNewFile();
			}
			Configuration yaml = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
	
			yaml.set("Datasource.mySQLHost", getMySQLHost);
			yaml.set("Datasource.mySQLPort", getMySQLPort);
			yaml.set("Datasource.mySQLUsername", getMySQLUsername);
			yaml.set("Datasource.mySQLPassword", getMySQLPassword);
			yaml.set("Datasource.mySQLDatabase", getMySQLDatabase);
			
			yaml.set("Security.SQLProblem.stopServer", isStopEnabled);
			
			String[] rankColorsList = new String[rankColors.size()];
			int i = 0;
			for (String rank : rankColors.ranks()){
				rankColorsList[i] = rank + ":" + rankColors.get(rank).name();
				i++;
			}
			yaml.set("RankColors", rankColorsList);
			
			yaml.set("NicknameRanks",nicknameRanks);

			ConfigurationProvider.getProvider(YamlConfiguration.class).save(yaml, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
