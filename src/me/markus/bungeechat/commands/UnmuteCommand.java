package me.markus.bungeechat.commands;

import me.markus.bungeechat.BungeeChat;
import me.markus.bungeechat.PlayerInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class UnmuteCommand extends TabCompleteCommand {
	public UnmuteCommand() {
		super("unmute");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission("bungeechat.mute")){
			return;
		}
		
		if (args.length == 0){
			sender.sendMessage(new TextComponent("/unmute <spielername>"));
			return;
		}
		
		String playername = args[0];
		
		ProxiedPlayer player = BungeeChat.instance.getProxy().getPlayer(playername);
		if (player == null) {
			sender.sendMessage(new TextComponent("Kein Spieler mit diesem Namen online!"));
			return;
		}
		
		PlayerInfo pi = BungeeChat.instance.getPlayerInfo(playername);
		pi.isMuted = false;
		player.sendMessage(new TextComponent("Du wurdest entmuted!"));
		sender.sendMessage(new TextComponent("Du hast den Spieler '"+playername+"' entmuted!"));
		BungeeChat.instance.getLogger().info("Spieler " + playername + " wurde von " + sender.getName() + " entmuted!");
	}
	
	@Override
	public boolean canUseTabCompletion(int argsposition) {
		if (argsposition == 1)
			return true;
		return false;
	}
}
