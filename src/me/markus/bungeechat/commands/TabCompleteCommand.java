package me.markus.bungeechat.commands;

import java.util.HashSet;
import java.util.Set;

import me.markus.bungeechat.BungeeChat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import com.google.common.collect.ImmutableSet;

public abstract class TabCompleteCommand extends Command implements TabExecutor {
	
	public TabCompleteCommand(String name) {
		super(name);
	}
	
	public TabCompleteCommand(String name, String permission, String... aliases) {
		super(name, name, aliases);
	}
	
	
	public abstract void execute(CommandSender sender, String[] args);
	
	public abstract boolean canUseTabCompletion(int argsposition);
	
	public HashSet<String> getCompletionPossibilities(int argsposition){
		HashSet<String> possibilites = new HashSet<>();
		for (ProxiedPlayer player : BungeeChat.instance.getProxy().getPlayers() ) {
			possibilites.add(player.getName());
		}
		return possibilites;
	}
	
	@Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (this.canUseTabCompletion(args.length) == false) {
			 return ImmutableSet.of();
		}
        
        Set<String> matches = new HashSet<>();
        String search = args[args.length-1].toLowerCase();
        for (String possibility : this.getCompletionPossibilities(args.length) ) {
            if (possibility.toLowerCase().startsWith(search)){
                matches.add(possibility );
            }
        }
        return matches;
    }

}
