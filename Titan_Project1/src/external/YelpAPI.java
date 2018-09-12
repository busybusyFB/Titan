package external;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder; //function encode " " -->%20
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.*;
import entity.Item;
import entity.Item.ItemBuilder;


public class YelpAPI {
	private static final String HOST = "https://api.yelp.com";
	private static final String ENDPOINT = "/v3/businesses/search";
	private static final String DEFAULT_TERM = ""; // default key word
	private static final int SEARCH_LIMIT = 20; // up limit

	private static final String TOKEN_TYPE = "Bearer";
	private static final String API_KEY = "TccgYcIcyzqpHm7zz0SF6vzuscWlE9BSRlbCNMNONeP8KV1HAPIqBf0alGRAtwxMw487DhYF1f7oN4VsveGONVeRZUk-CCpe2Y04BZvJawdXyqNPsxaf6LvAw_2BW3Yx";
	
	public List<Item> search(double lat, double lon, String term) {
		if (term == null || term.isEmpty()) {
			term = DEFAULT_TERM;
		}
		//replace white space with 20%
		try {
			term = URLEncoder.encode(term, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String query = String.format("term=%s&latitude=%s&longitude=%s&limit=%s", term, lat, lon, SEARCH_LIMIT);
		String url = HOST + ENDPOINT + "?" + query;
		System.out.println(url);
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization",TOKEN_TYPE + " " + API_KEY);
			// send a request, get code
			int responseCode = connection.getResponseCode(); // two steps: 1 send request; 2 get response code
			//should be 200 (successful)
			System.out.println("Sending request to URL: " + url);
			System.out.println("Response Code: " + responseCode);
			if (responseCode != 200) {
				return new ArrayList<>();
			}
			//input stream is from the server (Yelp API)
			//Java uses BufferReader and StreamReader to handle streams
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine = ""; // short string of each line
			StringBuilder response = new StringBuilder(); //a long string composed of short strings
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			JSONObject obj = new JSONObject(response.toString()); // string --> key value pairs json obj
			//we want business
			if(!obj.isNull("businesses")) {
				//return obj.getJSONArray("businesses");
				return getItemList(obj.getJSONArray("businesses"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	private void queryAPI(double lat, double lon) {
		//v2
		/*
		JSONArray items = search(lat,lon,null);
		try {
			for (int i = 0; i < items.length(); i++) {
				JSONObject item = items.getJSONObject(i);
				System.out.println(item);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
		//class 6
		List<Item> itemList = search(lat, lon, null);
		for (Item item : itemList) {
			JSONObject jsonObject = item.toJSONObject();
			System.out.println(jsonObject);
		}
	}
	//only for testing
	public static void main(String[] args) {
		YelpAPI tmApi = new YelpAPI();
		tmApi.queryAPI(37.38, -122.08);
	}
	/**
	 * Helper methods
	 */
	// Convert JSONArray to a list of item objects.
	private List<Item> getItemList(JSONArray restaurants) throws JSONException {
		List<Item> list = new ArrayList<>();
		
		for (int i = 0; i < restaurants.length(); i++) {
			JSONObject restaurant = restaurants.getJSONObject(i);
			ItemBuilder builder = new ItemBuilder();
			if (!restaurant.isNull("id")) {
				builder.setItemId(restaurant.getString("id"));
			}
			if (!restaurant.isNull("name")) {
				builder.setName(restaurant.getString("name"));
			}
			if (!restaurant.isNull("url")) {
				builder.setUrl(restaurant.getString("url"));
			}
			if (!restaurant.isNull("image_url")) {
				builder.setImageUrl(restaurant.getString("image_url"));
			}
			if (!restaurant.isNull("rating")) {
				builder.setRating(restaurant.getDouble("rating"));
			}
			if (!restaurant.isNull("distance")) {
				builder.setDistance(restaurant.getDouble("distance"));
			}
			builder.setAddress(getAddress(restaurant));
			builder.setCategories(getCategories(restaurant));
			
			list.add(builder.build()); //forgot
		}
		return list;
	}
	
	private Set<String> getCategories(JSONObject restaurant) throws JSONException {
		Set<String> categories = new HashSet<>();
		//class 6
		if (!restaurant.isNull("categories")) {
			JSONArray array = restaurant.getJSONArray("categories");
			for (int i = 0; i < array.length(); ++i) {
				JSONObject category = array.getJSONObject(i);
				if (!category.isNull("alias")) {
					categories.add(category.getString("alias"));
				}
			}
		}
		// end of class 6
		return categories;
	}

	private String getAddress(JSONObject restaurant) throws JSONException {
		String address = "";
		//class 6
		if (!restaurant.isNull("location")) {
			JSONObject location = restaurant.getJSONObject("location");
			if (!location.isNull("display_address")) {
				JSONArray array = location.getJSONArray("display_address");
				address = array.join(",");
			}
		}		
		return address;

	}

}
