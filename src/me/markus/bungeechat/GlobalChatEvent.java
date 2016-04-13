package me.markus.bungeechat;

import java.util.Collection;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class GlobalChatEvent extends Event implements Cancellable{

	private final TextComponent message;
	private final Collection<ProxiedPlayer> players;
	private final ProxiedPlayer sender;
	private boolean isCancelled;
	
	public GlobalChatEvent(ProxiedPlayer sender, Collection<ProxiedPlayer> players, TextComponent message){
		this.sender = sender;
		this.message = message;
		this.players = players;
		this.isCancelled = false;
		
	}
	
	public String getMessage(){
		return message.toPlainText();
	}
	
	public ProxiedPlayer getSender(){
		return this.sender;
	}

	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}

	@Override
	public void setCancelled(boolean status) {
		this.isCancelled = status;
		
	}
	
	public void sendMessage(){
		for (ProxiedPlayer pl : this.players) {
			if (!(pl instanceof ProxiedPlayer))
				continue;
			PlayerInfo pi = BungeeChat.instance.getPlayerInfo(pl.getName());
			if (pi.isRegistering == true)
				continue;
			pl.sendMessage(this.message);
		}
	}
}
