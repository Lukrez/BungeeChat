package me.markus.bungeechat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;


public class PlayerData {

	public static HashMap<String,String> nicknames;
	public static ArrayList<String> localChat;
	
	public static void load() {

		// Default settings
		nicknames = new HashMap<String,String>();
		localChat = new ArrayList<String>();

		File file = new File(BungeeChat.instance.getDataFolder(), "playerdata.yml");
		if (!file.exists())
			save();
		
		try {
			Configuration yaml = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			
			for (String pair : yaml.getStringList("nicknames")){
				String[] split = pair.split(":");
				String uuid = split[0];
				String nickname = split[1];
				if (!nickname.equals(""))
					nicknames.put(uuid, nickname);
			}
			localChat = new ArrayList<String>(); 
			for (String uuid :  yaml.getStringList("localchat")){
				localChat.add(uuid);

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		save();
	}

	public static void save() {
		try {
			
			File file = new File(BungeeChat.instance.getDataFolder(), "playerdata.yml");
			if (!file.exists()){
				file.createNewFile();
			}
			
			Configuration yaml = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			ArrayList<String> pairs = new ArrayList<String>();
			for (String uuid : nicknames.keySet()){
				String nick = nicknames.get(uuid);
				if (!nick.equals("")){
					pairs.add(uuid+":"+nick);
					
				}
			}
			yaml.set("nicknames",pairs);
			yaml.set("localchat",localChat);

		
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(yaml, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
