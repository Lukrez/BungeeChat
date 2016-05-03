package me.markus.bungeechat;

import java.util.HashMap;

import me.markus.bungeechat.commands.MuteCommand;
import me.markus.bungeechat.commands.ReplyCommand;
import me.markus.bungeechat.commands.SetNickCommand;
import me.markus.bungeechat.commands.ToggleGlobalChatCommand;
import me.markus.bungeechat.commands.UnmuteCommand;
import me.markus.bungeechat.commands.TellCommand;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;


public class BungeeChat extends Plugin  {
	
	public static BungeeChat instance;
	public MySQLDataSource database;
	public HashMap<String, PlayerInfo> players;
	private ChatLogger chatlog;
	
    @Override
    public void onEnable() {
    	instance = this;
    	// setup pluginfolder
    	if (!this.getDataFolder().exists())
			this.getDataFolder().mkdir();
    	
    	// Load setting
    	Settings.loadSettings();
    	
    	// Load playerdata
    	PlayerData.load();
    	
    	// link commands
    	this.getProxy().getPluginManager().registerCommand(this, new ToggleGlobalChatCommand());
    	this.getProxy().getPluginManager().registerCommand(this, new SetNickCommand());
    	this.getProxy().getPluginManager().registerCommand(this, new MuteCommand());
    	this.getProxy().getPluginManager().registerCommand(this, new UnmuteCommand());
    	this.getProxy().getPluginManager().registerCommand(this, new TellCommand());
    	this.getProxy().getPluginManager().registerCommand(this, new ReplyCommand());
    	
    	// link listeners
    	this.getProxy().getPluginManager().registerListener(this, new EventListeners());
    	
    	// register PluginChannels
    	this.getProxy().registerChannel("Register");
    	
    	try {
    		database = new MySQLDataSource();
		} catch (Exception ex) {
			this.getLogger().severe(ex.getMessage());
			this.getLogger().severe("Can't use MySQL... Please input correct MySQL informations ! SHUTDOWN...");
			this.shutdown();
		}
    	
    	// set up chatlogger
    	this.chatlog = new ChatLogger();
    	
    	this.players = new HashMap<String, PlayerInfo>();
        getLogger().info("Finished setup!");
    	
    }
    
    @Override
    public void onDisable() {
    	this.chatlog.close();
    	PlayerData.save();
    	database.close();
    }
    
    public void shutdown(){
    	if (Settings.isStopEnabled) {
			this.getProxy().stop();
		}
    }
    
    public void storeChat(String message) {
    	this.chatlog.storeChat(message);
    }
    
    
    public boolean checkSpenderPermission(ProxiedPlayer player){
    
		String rank = BungeeChat.instance.database.getPlayerRank(player.getUniqueId().toString());
		if (!Settings.nicknameRanks.contains(rank)){
			return false;
		}
		return true;
    }
    
    public PlayerInfo getPlayerInfo(String playername) {
    	String lwcplayername = playername.toLowerCase();
    	PlayerInfo pi = this.players.get(lwcplayername);
    	if (pi == null){
			pi = new PlayerInfo(playername);
			this.players.put(lwcplayername, pi);
		}
    	return pi;
    }
}



