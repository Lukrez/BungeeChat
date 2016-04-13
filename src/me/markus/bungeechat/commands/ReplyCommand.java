package me.markus.bungeechat.commands;

import me.markus.bungeechat.BungeeChat;
import me.markus.bungeechat.PlayerInfo;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReplyCommand extends TabCompleteCommand {
	public ReplyCommand() {
		//super("reply", "bungeechat.chat", new String[]{"r"});
		super("r");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		
		
		String playername = sender.getName();
		
		if (args.length < 1) {
			sender.sendMessage(new TextComponent("/reply oder /r <nachricht>"));
			return;
		}
		
		PlayerInfo piSender = BungeeChat.instance.getPlayerInfo(playername);
		
		String recPlayername = piSender.lastCommunicationParter;
		if (recPlayername == null) {
			sender.sendMessage(new TextComponent("Kein Spieler mit diesem Namen online!"));
			return;
		}
		
		ProxiedPlayer recPlayer = BungeeChat.instance.getProxy().getPlayer(recPlayername);
		if (recPlayer == null) {
			sender.sendMessage(new TextComponent("Kein Spieler mit diesem Namen online!"));
			return;
		}
		
		PlayerInfo piRec = BungeeChat.instance.getPlayerInfo(recPlayername);
		
		int chatSpam = piSender.chatted();
		if (piSender.isMuted == true) {
			sender.sendMessage(new TextComponent("Du bist momentan gemuted!"));
			return;
		}
		if (piRec.isRegistering == true) {
			sender.sendMessage(new TextComponent("Der Empfänger registriert sich gerade!"));
			return;
		}
		if ((chatSpam > -1) && (chatSpam < 5)) { // TODO: Set real Limit
			sender.sendMessage(new TextComponent("Bitte spamme nicht den Chat voll!"));
			return;
		}
		piSender.lastCommunicationParter = recPlayername;
		piRec.lastCommunicationParter = playername;
		
		
		// get message
		String message = args[0];
		for (int i=1; i < args.length; i++) {
			message += " " + args[i];
		}
		// send message to reciever
		TextComponent msgA = new TextComponent(playername+" flüstert: "+ message);
		msgA.setColor(ChatColor.GRAY);
		recPlayer.sendMessage(msgA);
		
		// send message to sender
		TextComponent msgB = new TextComponent("["+playername+"->"+recPlayername+"]: "+ message);
		msgB.setColor(ChatColor.GRAY);
		sender.sendMessage(msgB);
		
		
	}



	@Override
	public boolean canUseTabCompletion(int argsposition) {
		if (argsposition > 0){
			return true;
		}
		return false;
	}
}
