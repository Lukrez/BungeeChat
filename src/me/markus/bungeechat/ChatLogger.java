package me.markus.bungeechat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class ChatLogger {

	private FileWriter fw;
	private BufferedWriter bw;
	private File folder;
	private int lineCount = 0;
	private int flushCount = 0;
	
	
	public ChatLogger() {
		this.folder = new File(BungeeChat.instance.getDataFolder(), "chatdata");
		
		try {
			this.makeNewLogFile();
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
	}
	
	private void makeNewLogFile() throws IOException {
		
		this.close();
		if (!this.folder.exists()) {
			this.folder.mkdir();
		}
		GregorianCalendar now = new GregorianCalendar();
		String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH-mm-ss").format(now.getTime());
		File file = new File(folder, timeStamp + ".txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		this.fw = new FileWriter(file.getAbsoluteFile());
		this.bw = new BufferedWriter(fw);
		this.lineCount = 0;
		this.flushCount = 0;
		
	}
	
	public void close() {
		// close filewriters if they exist
		try {
			if (bw != null)
				bw.close();
			if (fw != null)
				fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fw = null;
		bw = null;
	}
	
	public void storeChat(String message) {
		if (fw == null || bw == null)
			return;
		try {
			if (this.flushCount > 10) {
				bw.flush();
				this.flushCount = 0;
			}
			if (this.lineCount > 1000) {
				makeNewLogFile();
			}
			GregorianCalendar now = new GregorianCalendar();
			String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(now.getTime());
			bw.write(timeStamp + ": " + message + "\n");
			this.lineCount++;
			this.flushCount++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			close();
			e.printStackTrace();
				
			
		}
		
		
	}
}
