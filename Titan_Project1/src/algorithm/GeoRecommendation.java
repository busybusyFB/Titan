package algorithm;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.MySQLConnection;
import entity.Item;


public class GeoRecommendation {
	public List<Item> recommendationItems(String userId, double lat, double lon) {
		List<Item> items = new ArrayList<>();
		MySQLConnection connection = new MySQLConnection();
		Set<String> itemIds = connection.getFavoriteItemIds(userId);
		for (String itemId : itemIds) {
			Set<String> categories = connection.getCategories(itemId);
			for(String category : categories) {
				items = connection.searchItems(userId, lat, lon, category);
			}
		}
		return items;
	}
	
}
