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
		Set<String> favoriteItems = new HashSet<>();
		try {
	    	String sql = "SELECT item_id from history WHERE user_id = ?";
	    	PreparedStatement statement = conn.prepareStatement(sql);
	    	statement.setString(1, userId);
	    	ResultSet rs = statement.executeQuery();
	    	while (rs.next()) {
	    		String itemId = rs.getString("item_id");
	    		favoriteItems.add(itemId);	
	    	}
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }
	    return favoriteItems;
	  }

	  
	  public Set<Item> getFavoriteItems(String userId) {
		  System.out.println(userId);
		  Set<String> itemIds = getFavoriteItemIds(userId);
	    Set<Item> favoriteItems = new HashSet<>();
	    try {

	      for (String itemId : itemIds) {
	        String sql = "SELECT * from items WHERE item_id = ? ";
	        PreparedStatement statement = conn.prepareStatement(sql);
	        statement.setString(1, itemId);
	        ResultSet rs = statement.executeQuery();
	        ItemBuilder builder = new ItemBuilder();

	        // Because itemId is unique and given one item id there should
	        // have
	        // only one result returned.
	        if (rs.next()) {
	          builder.setItemId(rs.getString("item_id"));
	          builder.setName(rs.getString("name"));
//	          builder.setCity(rs.getString("city"));
//	          builder.setState(rs.getString("state"));
//	          builder.setCountry(rs.getString("country"));
//	          builder.setZipcode(rs.getString("zipcode"));
	          builder.setRating(rs.getDouble("rating"));
	          builder.setAddress(rs.getString("address"));
//	          builder.setLatitude(rs.getDouble("latitude"));
//	          builder.setLongitude(rs.getDouble("longitude"));
//	          builder.setDescription(rs.getString("description"));
//	          builder.setSnippet(rs.getString("snippet"));
//	          builder.setSnippetUrl(rs.getString("snippet_url"));
	          builder.setImageUrl(rs.getString("image_url"));
	          builder.setUrl(rs.getString("url"));
	        }
	        
	        // Join categories information into builder.
	        // But why we do not join in sql? Because it'll be difficult
	        // to set it in builder.
	        sql = "SELECT * from categories WHERE item_id = ?";
	        statement = conn.prepareStatement(sql);
	        statement.setString(1, itemId);
	        rs = statement.executeQuery();
	        Set<String> categories = new HashSet<>();
	        while (rs.next()) {
	          categories.add(rs.getString("category"));
	        }
	        builder.setCategories(categories);
	        favoriteItems.add(builder.build());
	      }
	    } catch (SQLException e) {
	      e.printStackTrace();
	    }
	    return favoriteItems;
	  }

	  public Set<String> getCategories(String itemId) {
	    Set<String> categories = new HashSet<>();
	    try {
	      String sql = "SELECT category from categories WHERE item_id = ? ";
	      PreparedStatement statement = conn.prepareStatement(sql);
	      statement.setString(1, itemId);
	      ResultSet rs = statement.executeQuery();
	      while (rs.next()) {
	        categories.add(rs.getString("category"));
	      }
	    } catch (Exception e) {
	      System.out.println(e.getMessage());
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
	
	public List<Item> searchItems(String userId, double lat, double lon, String category) {
		List<Item> items = new ArrayList<>();
		return items;
	}
}
