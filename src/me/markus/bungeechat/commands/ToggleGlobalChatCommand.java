package me.markus.bungeechat.commands;

import me.markus.bungeechat.PlayerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ToggleGlobalChatCommand extends Command{
	
	public ToggleGlobalChatCommand() {
	      super("chat");
	  }

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (!(sender instanceof ProxiedPlayer)){
			return;
		}
						
		ProxiedPlayer s = (ProxiedPlayer) sender;
		String uuid = s.getUniqueId().toString();
		String messageText;
		if (PlayerData.localChat.contains(uuid)){
			PlayerData.localChat.remove(uuid);
			messageText = "Du chattest nun im globalen Chat!";
		} else {
			PlayerData.localChat.add(uuid);
			messageText = "Du chattest nun nur auf dem " + s.getServer().getInfo().getName() + "-Server!";
		}
		TextComponent message = new TextComponent(messageText);
		message.setColor(ChatColor.AQUA);
		sender.sendMessage(message);
		PlayerData.save();
		
	}
}