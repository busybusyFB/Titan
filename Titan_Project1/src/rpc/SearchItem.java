package rpc;

import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.*;

import entity.Item;
import external.YelpAPI;
import db.MySQLConnection;

@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public SearchItem() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//eg1
//		response.setContentType("application/json");
//		PrintWriter out = response.getWriter();
//		out.println("<html><body>");
//		out.println("<h1>hello world</h1>");
//		out.println("</body></html>");
//		out.close();
		
		//eg2
		//service testing
//		response.setContentType("application/json");
//		response.addHeader("Access-Control-Allow-Origin", "*");
//		PrintWriter out = response.getWriter();
//		if (request.getParameter("username") != null) {
//			JSONObject obj = new JSONObject();
//			String username = request.getParameter("username");
//			try {
//				obj.put("username",username);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			out.print(obj);
//		}
//		out.close();
		
		//real code
		//eg3
//		double lat = Double.parseDouble(request.getParameter("lat"));
//		double lon = Double.parseDouble(request.getParameter("lon"));
//		String term = request.getParameter("term");
//		
//		YelpAPI yelpAPI = new YelpAPI();
//		List<Item> items = yelpAPI.search(lat, lon, "");
//		
//		//DB operations
//		
//		JSONArray array = new JSONArray();
//		for (Item item : items) {
//			array.put(item.toJSONObject());
//		}
//		RpcHelper.writeJsonArray(response, array);

		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		String term = request.getParameter("term");
		String userId = request.getParameter("user_id");
		System.out.println(userId + " " + lat + " " + lon + " " + term);
		MySQLConnection conn = new MySQLConnection();
		try {
			List<Item> items = conn.searchItems(lat, lon, term);
			Set<Item> favoriteItems = conn.getFavoriteItems(userId);
			System.out.println("size is " + favoriteItems.size());
			//for (Item item : favoriteItems) {
				//System.out.println(item.getName());
			//}
			JSONArray array = new JSONArray();
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				if(favoriteItems.contains(item)) {
					obj.append("favorite", true);
				}
				array.put(obj);
			}
			RpcHelper.writeJsonArray(response, array);
		} catch (Exception e) {
				e.printStackTrace();
		} finally {
			conn.close(); // run no matter what
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
