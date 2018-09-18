package db;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

import entity.Item;
import entity.Item.ItemBuilder;
import external.YelpAPI;

import java.sql.*;
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
		if (conn == null) {
			return new HashSet<>();
		}
		Set<String> favoriteItemIds = new HashSet<>();
		String sql = "SELECT item_id from history WHERE user_id = ?";
		try {
	    	PreparedStatement ps = conn.prepareStatement(sql);
	    	ps.setString(1, userId);
	    	ResultSet rs = ps.executeQuery();
	    	while (rs.next()) {
	    		favoriteItemIds.add(rs.getString("item_id"));	
	    	}
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }
	    return favoriteItemIds;
	}

	public Set<Item> getFavoriteItems(String userId) {
		if (conn == null) {
			return new HashSet<>();
		}
		Set<String> itemIds = getFavoriteItemIds(userId);
	    Set<Item> favoriteItems = new HashSet<>();
	    
	    
	    String sql = "SELECT * from items WHERE item_id = ? ";
	    try {
	    	PreparedStatement ps = conn.prepareStatement(sql);
	    	for (String itemId : itemIds) {
	    		ps.setString(1, itemId);
	    		//need to read data from DB
	    		//ResultSet is also a table like item table;
	    		ResultSet rs = ps.executeQuery();
	    		//returned rs pointing to the -1th row of table
	    		ItemBuilder builder = new ItemBuilder();
	    		// Because itemId is unique and given one item id there should
	    		// have only one result returned.
	    		while (rs.next()) { // "if" is also fine, itemId only return one entry
	    			builder.setItemId(rs.getString("item_id"));
	    			builder.setName(rs.getString("name"));
	    			builder.setRating(rs.getDouble("rating"));
	    			builder.setAddress(rs.getString("address"));
	    			builder.setImageUrl(rs.getString("image_url"));
	    			builder.setUrl(rs.getString("url"));
	    			builder.setDistance(rs.getDouble("distance"));
	    			builder.setCategories(getCategories(itemId));
	    			
	    			favoriteItems.add(builder.build());
	    		}
	    	}
	    } catch (SQLException e) {
	      e.printStackTrace();
	    }
	    return favoriteItems;
	}

	public Set<String> getCategories(String itemId) {
		Set<String> categories = new HashSet<>();
		String sql = "SELECT category from categories WHERE item_id = ? ";
	    try {
	    	PreparedStatement ps = conn.prepareStatement(sql);
	    	ps.setString(1, itemId);
	    	ResultSet rs = ps.executeQuery();
	    	while (rs.next()) {
	    		categories.add(rs.getString("category"));
	    	}
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return categories;
	}

	public List<Item> searchItems(double lat, double lon, String term) {
		YelpAPI yelpAPI = new YelpAPI();
		List<Item> items = yelpAPI.search(lat, lon, term);
		for(Item item : items) {
			saveItem(item);
		}
		return items;
	}
	
	public void saveItem(Item item) { // save an restaurant item into DB
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
	
	public List<Item> searchItems(String userId, double lat, double lon, String category) {
		YelpAPI yelpAPI = new YelpAPI();
		List<Item> items = yelpAPI.search(lat, lon, category);
		for(Item item : items) {
			saveItem(item);
		}
		return items;
	}
	
	public String getPassword(String user_id) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return null;
		}
		
		String password = null;
		try {
			String sql = "SELECT password from users WHERE user_id = ? ";
	    	PreparedStatement ps = conn.prepareStatement(sql);
	    	ps.setString(1, user_id);
	    	ResultSet rs = ps.executeQuery();
	    	if (rs.next()) {
	    		password = rs.getString("password");
	    	}
		} catch (Exception e) {
			e.printStackTrace();			
		}
		return password;
	}
	
	public boolean saveUser(String user_id, String pwd, String firstName, String lastName) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		try {
			String oldPwd = getPassword(user_id);
			if (oldPwd != null) { //user_id already exists
				return false;
			} else {
				String sql = "INSERT INTO users VALUES (?, ?, ?, ?)";
		    	PreparedStatement ps = conn.prepareStatement(sql);
		    	ps.setString(1, user_id);
		    	ps.setString(2, pwd);
		    	ps.setString(3, firstName);
		    	ps.setString(4, lastName);
		    	ps.execute();
		    	return true;
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}
		return false;
	}
}
