package me.markus.bungeechat;

import java.util.Date;
import java.util.LinkedList;

public class PlayerInfo {

	public String playername;
	public boolean isMuted;
	public boolean isRegistering;
	public LinkedList<Long> chatHistory;
	public String lastCommunicationParter;
	
	public PlayerInfo(String playername) {
		this.playername = playername;
		this.isMuted = false;
		this.isRegistering = false;
		this.chatHistory = new LinkedList<>();
		this.lastCommunicationParter = null;
		
	}
	
	public int chatted() {
		long time = new Date().getTime();
		chatHistory.addLast(time);
		while (chatHistory.size() > 5) {
			chatHistory.removeFirst();
		}
		if (chatHistory.size() == 5){
			long timeOld = chatHistory.getFirst();
			return (int)((time - timeOld)/1000);
		}
		return -1;
		
	}
	

}
