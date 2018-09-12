package db;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import entity.Item;
import external.YelpAPI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class MySQLConnection {
	private Connection conn;
	
	public MySQLConnection() { //constructor
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close() { //Destructor
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setFavoriteItems(String userId, List<String> itemIds) {
		if (conn == null) {
			System.err.println("DS connection failed");
			return;
		}
		try {
			String sql = "INSERT IGNORE INTO history(user_id, item_id) VALUES(?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			for (String item : itemIds) {
				ps.setString(2, item);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		if (conn == null) {
			System.err.println("DS connection failed");
			return;
		}
		try {
			String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			for (String item : itemIds) {
				ps.setString(2, item);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public Set<String> getFavoriteItemIds(String userId) {
		return null;
	}
	
	public Set<Item> getFavoriteItems(String userId) {
		return null;
	}
	
	public Set<String> getCategories(String itemId) {
		return null;
	}
	
	public List<Item> searchItems(double lat, double lon, String term) {
		YelpAPI yelpAPI = new YelpAPI();
		List<Item> items = yelpAPI.search(lat, lon, term);
		for(Item item : items) {
			saveItem(item);
		}
		return items;
	}
	
	public void saveItem(Item item) { // save an restuarant item into DB
		if (conn == null) {
			System.err.println("DB connection failed");
		}
		try {
			String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, item.getItemId());
			ps.setString(2, item.getName());
			ps.setDouble(3, item.getRating());
			ps.setString(4, item.getAddress());
			ps.setString(5, item.getUrl());
			ps.setString(6, item.getImageUrl());
			ps.setDouble(7, item.getDistance());
			ps.execute();

			sql = "INSERT IGNORE INTO categories VALUES(?, ?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, item.getItemId());
			for (String category : item.getCategories()) {
				ps.setString(2, category);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
