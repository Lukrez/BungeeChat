package me.markus.bungeechat;

import java.util.ArrayList;
import java.util.HashMap;

import net.md_5.bungee.api.ChatColor;


public class ColoredRanks {
	
	private ArrayList<String> ranks;
	private HashMap<String, ChatColor> colors;

	public ColoredRanks(){
		this.ranks = new ArrayList<String>();
		this.colors = new HashMap<String, ChatColor>();
	}
	
	public void put(String rank, ChatColor color){
		if (!this.ranks.contains(rank)){
			this.ranks.add(rank);
		}
		this.colors.put(rank,color);
	}
	
	public ChatColor get(String rank){
		return this.colors.get(rank);
	}
	
	public ArrayList<String> ranks(){
		return this.ranks;
	}
	
	public int size(){
		return this.ranks.size();
	}
	
	
}