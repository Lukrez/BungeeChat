package me.markus.bungeechat.commands;

import java.util.HashSet;

import me.markus.bungeechat.BungeeChat;
import me.markus.bungeechat.PlayerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SetNickCommand extends TabCompleteCommand {
	
	public SetNickCommand() {
	      super("setnick");
	  }

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (!(sender instanceof ProxiedPlayer)){
			return;
		}
		
		ProxiedPlayer player = (ProxiedPlayer) sender;
		
		if (!BungeeChat.instance.checkSpenderPermission(player)){
			TextComponent message = new TextComponent("Du bist leider kein Spender!");
			message.setColor(ChatColor.DARK_RED);
			player.sendMessage(message);
			return;
		}
			

		
		if (args.length == 0){
			player.sendMessage(new TextComponent(ChatColor.AQUA + "/setnick [clear]- setzt deinen Nickname zurück"));
			player.sendMessage(new TextComponent(ChatColor.AQUA + "/setnick <nick> - setzt deinen Nickname"));
			if (player.hasPermission("bungeechat.setnick.other")) {
				player.sendMessage(new TextComponent(ChatColor.AQUA + "/setnick <nick/clear> <spielername>- setzt den Nickname eines Spielers"));
			}
			return;
		}
		if (args.length == 1){
			if (args[0].toLowerCase().equals("help")){
				player.sendMessage(new TextComponent(ChatColor.AQUA + "/setnick [clear]- setzt deinen Nickname zurück"));
				player.sendMessage(new TextComponent(ChatColor.AQUA + "/setnick <nick> - setzt deinen Nickname"));
				if (player.hasPermission("bungeechat.setnick.other")) {
					player.sendMessage(new TextComponent(ChatColor.AQUA + "/setnick <nick/clear> <spielername>- setzt den Nickname eines Spielers"));
				}
				return;
			}
			if (args[0].toLowerCase().equals("clear")){
				String uuid = player.getUniqueId().toString();
				PlayerData.nicknames.remove(uuid);
				player.sendMessage(new TextComponent(ChatColor.GREEN + "Dein Nickname wurde entfernt!"));
				PlayerData.save();
				return;
			}
			String uuid = player.getUniqueId().toString();
			PlayerData.nicknames.put(uuid,args[0]);
			player.sendMessage(new TextComponent(ChatColor.GREEN + "Dein Nickname wurde neu gesetzt!"));
			PlayerData.save();
			return;
		}
		if (args.length > 1 && player.hasPermission("bungeechat.setnick")) {
			ProxiedPlayer other = BungeeChat.instance.getProxy().getPlayer(args[1]);
			if (other == null){
				player.sendMessage(new TextComponent(ChatColor.DARK_RED + "Der Spieler konnte nicht gefunden werden!"));
				PlayerData.save();
				return;
			}
			String uuidOther = other.getUniqueId().toString();
			if (args[0].toLowerCase().equals("clear")){
				PlayerData.nicknames.remove(uuidOther);
				player.sendMessage(new TextComponent(ChatColor.GREEN + "Der Nickname von "+other.getName()+" wurde entfernt!"));
				other.sendMessage(new TextComponent(ChatColor.GREEN + "Dein Nickname wurde von "+player.getName()+" entfernt!"));
				PlayerData.save();
				return;
			}
			PlayerData.nicknames.put(uuidOther,args[0]);
			player.sendMessage(new TextComponent(ChatColor.GREEN + "Der Nickname von "+other.getName()+" wurde gesetzt!"));
			other.sendMessage(new TextComponent(ChatColor.GREEN + "Dein Nickname wurde von "+player.getName()+"neu gesetzt!"));
			PlayerData.save();
			return;
		}
		

	}

	@Override
	public boolean canUseTabCompletion(int argsposition) {
		if (argsposition == 1 || argsposition == 2)
			return true;
		return false;
	}
	
	@Override
	public HashSet<String> getCompletionPossibilities(int argsposition){
		HashSet<String> possibilites = new HashSet<>();
		if (argsposition == 1) {
			possibilites.add("clear");
			possibilites.add("help");
			return possibilites;
		}
		if (argsposition == 2) {
			for (ProxiedPlayer player : BungeeChat.instance.getProxy().getPlayers() ) {
				possibilites.add(player.getName());
			}
		}
		return possibilites;
	}
	
}