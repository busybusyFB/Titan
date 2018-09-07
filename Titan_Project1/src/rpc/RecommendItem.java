package rpc;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class RecommendItem
 */
@WebServlet("/recommendation")
public class RecommendItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public RecommendItem() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// test codes: eg1
//		response.setContentType("application/json");
//		PrintWriter out = response.getWriter();
//		
//		JSONArray array = new JSONArray();
//		try {
//			array.put(new JSONObject().put("username", "abcd").put("address", "SF").put("Time", "01/01/2017"));
//			array.put(new JSONObject().put("username", "wwwd").put("address", "NY").put("Time", "01/01/2018"));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		out.print(array);
//		out.close();
		
		
		//test codes: eg2
		JSONArray array = new JSONArray();
		try {
			array.put(new JSONObject().put("username", "abcd"));
			array.put(new JSONObject().put("username", "www"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RpcHelper.writeJsonArray(response,array);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
