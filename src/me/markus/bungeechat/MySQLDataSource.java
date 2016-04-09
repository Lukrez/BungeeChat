package me.markus.bungeechat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import me.markus.bungeechat.MiniConnectionPoolManager.TimeoutException;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

public class MySQLDataSource {

	private String host;
	private String port;
	private String username;
	private String password;
	private String database;
	
	private MiniConnectionPoolManager conPool;

	public MySQLDataSource() throws ClassNotFoundException, SQLException {
		this.host = Settings.getMySQLHost;
		this.port = Settings.getMySQLPort;
		this.username = Settings.getMySQLUsername;
		this.password = Settings.getMySQLPassword;
		this.database = Settings.getMySQLDatabase;


		connect();
	}

	private synchronized void connect() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		BungeeChat.instance.getLogger().info("MySQL driver loaded");
		MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
		dataSource.setDatabaseName(database);
		dataSource.setServerName(host);
		dataSource.setPort(Integer.parseInt(port));
		dataSource.setUser(username);
		dataSource.setPassword(password);
		conPool = new MiniConnectionPoolManager(dataSource, 20);
		BungeeChat.instance.getLogger().info("Connection pool ready");
		// check if database exists
		//this.setup();
	}
	
	
	public synchronized String getPlayerRank(String uuid) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = makeSureConnectionIsReady();
			pst = con.prepareStatement("SELECT world,parent FROM pex_inheritance WHERE child =?;");
			pst.setString(1, uuid);
			rs = pst.executeQuery();
			ArrayList<String> primaryRanks = new ArrayList<String>();
			while (rs.next()){
				String world = rs.getString("world");
				String rank = rs.getString("parent");
				if (world == null){
					primaryRanks.add(rank);
				}
			}
			close(rs);
			close(pst);
			close(con);
			if (!primaryRanks.isEmpty()){
				for (String rank : Settings.rankColors.ranks()){
					if (primaryRanks.contains(rank)){
						return rank;
					}
				}
				return primaryRanks.get(0);
			}
			return "Guest";
			
		} catch (SQLException ex) {
			BungeeChat.instance.getLogger().severe(ex.getMessage());
			return "Guest";
		} catch (TimeoutException ex) {
			BungeeChat.instance.getLogger().severe(ex.getMessage());
			return "Guest";
		} finally {
			close(rs);
			close(pst);
			close(con);
		}
	}
	

	public synchronized void close() {
		try {
			conPool.dispose();
		} catch (SQLException ex) {
			BungeeChat.instance.getLogger().severe(ex.getMessage());
		}
	}

	private void close(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException ex) {
				BungeeChat.instance.getLogger().severe(ex.getMessage());
			}
		}
	}

	private void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException ex) {
				BungeeChat.instance.getLogger().severe(ex.getMessage());
			}
		}
	}

	private void close(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException ex) {
				BungeeChat.instance.getLogger().severe(ex.getMessage());
			}
		}
	}

	private synchronized Connection makeSureConnectionIsReady() {
		Connection con = null;
		try {
			con = conPool.getValidConnection();
		} catch (Exception te) {
			try {
				con = null;
				reconnect();
			} catch (Exception e) {
				BungeeChat.instance.getLogger().severe(e.getMessage());
				BungeeChat.instance.getLogger().severe("Can't reconnect to MySQL database... Please check your MySQL informations ! SHUTDOWN...");
				BungeeChat.instance.shutdown();
			}
		} catch (AssertionError ae) {
			// Make sure assertionerror is caused by the connectionpoolmanager, else re-throw it
			if (!ae.getMessage().equalsIgnoreCase("BungeeDatabaseError"))
				throw new AssertionError(ae.getMessage());
			try {
				con = null;
				reconnect();
			} catch (Exception e) {
				BungeeChat.instance.getLogger().severe(e.getMessage());
				BungeeChat.instance.getLogger().severe("Can't reconnect to MySQL database... Please check your MySQL informations ! SHUTDOWN...");
				BungeeChat.instance.shutdown();
			}
		}
		if (con == null)
			con = conPool.getValidConnection();
		return con;
	}

	private synchronized void reconnect() throws ClassNotFoundException, SQLException, TimeoutException {
		conPool.dispose();
		Class.forName("com.mysql.jdbc.Driver");
		MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
		dataSource.setDatabaseName(database);
		dataSource.setServerName(host);
		dataSource.setPort(Integer.parseInt(port));
		dataSource.setUser(username);
		dataSource.setPassword(password);
		conPool = new MiniConnectionPoolManager(dataSource, 10);
		BungeeChat.instance.getLogger().info("ConnectionPool was unavailable... Reconnected!");
	}

}
