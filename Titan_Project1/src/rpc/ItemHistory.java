package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.MySQLConnection;
import entity.Item;

@WebServlet("/history")
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public ItemHistory() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MySQLConnection conn = new MySQLConnection();
		String userId = request.getParameter("user_id");
		JSONArray array = new JSONArray();
		
	    Set<Item> items = conn.getFavoriteItems(userId);
	    
	    for (Item item : items) {
	      JSONObject obj = item.toJSONObject();
	      try {
	        obj.append("favorite", true);
	      } catch (JSONException e) {
	        e.printStackTrace();
	      }
	      array.put(obj);
	    }
	    RpcHelper.writeJsonArray(response, array);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MySQLConnection conn = new MySQLConnection();
		try {
			JSONObject input = RpcHelper.readJSONObject(request); // input contains user_id, favorite infor
			String userId = input.getString("user_id");
			JSONArray favorArr = input.getJSONArray("favorite");
			List<String> itemIds = new ArrayList<>();
			for (int i = 0; i < favorArr.length(); i++) {
				itemIds.add(favorArr.getString(i));
			}
			conn.setFavoriteItems(userId, itemIds);
			RpcHelper.writeJsonObject(response, new JSONObject().put("result","SUCCESS"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}	
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MySQLConnection conn = new MySQLConnection();
		try {
			JSONObject input = RpcHelper.readJSONObject(request); // input contains user_id, favorite infor
			String userId = input.getString("user_id");
			JSONArray favorArr = input.getJSONArray("favorite");
			List<String> itemIds = new ArrayList<>();
			for (int i = 0; i < favorArr.length(); i++) {
				itemIds.add(favorArr.getString(i));
			}
			conn.unsetFavoriteItems(userId, itemIds);
			RpcHelper.writeJsonObject(response, new JSONObject().put("result","SUCCESS"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

}
