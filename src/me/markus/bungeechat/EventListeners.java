package me.markus.bungeechat;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class EventListeners implements Listener{
	
	
	@EventHandler
	public void onPlayerChat(ChatEvent event) {
		if ((event.getSender() instanceof ProxiedPlayer)) {
			// check if message is a command
			ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
			if (event.isCommand()){
				String command = event.getMessage();
				if (!command.contains("/login ") && !command.contains("/l ")){
					BungeeChat.instance.storeChat("["+sender.getServer().getInfo().getName() + "] " + sender.getName() + " issued command "+ command);
				} else {
					BungeeChat.instance.storeChat("["+sender.getServer().getInfo().getName() + "] " + sender.getName() + " issued login command");
				}
				return;
			}
			
			String playername = sender.getName();
			
			PlayerInfo pi = BungeeChat.instance.getPlayerInfo(playername);
			if (pi.isRegistering == true) {
				// allow chat of registering guests, event should be canceled by EasyLogin
				return;
			}
			int chatSpam = pi.chatted();
			if (pi.isMuted == true) {
				event.setCancelled(true);
				sender.sendMessage(new TextComponent("Du bist momentan gemuted!"));
				return;
			}
			if ((chatSpam > -1) && (chatSpam < 5)) { // TODO: Set real Limit
				event.setCancelled(true);
				sender.sendMessage(new TextComponent("Bitte spamme nicht den Chat voll!"));
				return;
			}
			
			
			// check if player is in local chat
			if (PlayerData.localChat.contains(sender.getUniqueId().toString())){
				BungeeChat.instance.storeChat("["+sender.getServer().getInfo().getName() + "][" + sender.getName() + "] "+ event.getMessage());
				return;
			}
			
			event.setCancelled(true);
			// get player rank
			String rank = BungeeChat.instance.database.getPlayerRank(sender.getUniqueId().toString());
			
			// generate message
			TextComponent message = new TextComponent( "[" );
			message.setColor(ChatColor.GOLD);
			
			
			TextComponent rankString = new TextComponent(rank);
			ChatColor rankColor = Settings.rankColors.get(rank);
			if (rankColor == null){
				rankColor = ChatColor.WHITE;
			}
			rankString.setColor(rankColor);
			rankString.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Server: "+sender.getServer().getInfo().getName()).create() ) );
			message.addExtra(rankString);
			
			
			TextComponent p1 = new TextComponent("] ");
			p1.setColor(ChatColor.GOLD);
			message.addExtra(p1);
			
			// get playernick
			String nick = PlayerData.nicknames.get(sender.getUniqueId().toString());
			if (nick == null){
				nick = sender.getName();
			} else {
				nick = ChatColor.translateAlternateColorCodes('&', nick);
			}
			
			TextComponent name = new TextComponent(nick + ": ");
			name.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(sender.getName()).create() ) );
			name.setColor(ChatColor.GRAY);
			message.addExtra(name);
			
			// check if player is Spender and can write in color
			ProxiedPlayer player = (ProxiedPlayer) sender;
			
			String messageText = event.getMessage();
			if (BungeeChat.instance.checkSpenderPermission(player)){
				messageText = ChatColor.translateAlternateColorCodes('&',messageText);
			}

			TextComponent p2 = new TextComponent(messageText);
			p2.setColor(ChatColor.WHITE);
			message.addExtra(p2);
			

			GlobalChatEvent chatevent = new GlobalChatEvent(sender,
					BungeeChat.instance.getProxy().getPlayers(),
					message);
			BungeeChat.instance.getProxy().getPluginManager().callEvent(chatevent);
			if (!chatevent.isCancelled()){
				chatevent.sendMessage();
				BungeeChat.instance.storeChat("["+sender.getName()+"] "+ event.getMessage());
			}
			return;
		}
		return;
	}
	
	@EventHandler
	public void onDisconnect(PlayerDisconnectEvent event){
		ProxiedPlayer sender = event.getPlayer();
		String playername = sender.getName().toLowerCase();
		PlayerInfo pi = BungeeChat.instance.players.get(playername);
		if (pi == null)
			return;
		pi.isRegistering = false;
		if (pi.isMuted == false)
			BungeeChat.instance.players.remove(playername);
	}
	
	@EventHandler
    public void onPluginMessagePlayerRegister(PluginMessageEvent ev) {   	
        if (!ev.getTag().equals("Register")) {
            return;
        }
        
        if (!(ev.getSender() instanceof Server)) {
            return;
        }
        
        ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
        DataInputStream in = new DataInputStream(stream);
        try {
        	String message = in.readUTF();
        	boolean isRegistering;
        	if (message.matches("#start#.+#")) {
        		isRegistering = true;
        	} else if (message.matches("#exit#.+#")) {
        		isRegistering = false;
        	} else {
        		return;
        	}
        	String playername = message.split("#")[2];
        	ProxiedPlayer player = BungeeChat.instance.getProxy().getPlayer(playername);
        	if (player == null) {
        		return;
        	}
        	PlayerInfo pi = BungeeChat.instance.getPlayerInfo(playername);
        	if (pi == null)
        		return;
        	pi.isRegistering = isRegistering;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

