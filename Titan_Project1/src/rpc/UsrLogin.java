package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.MySQLConnection;

@WebServlet("/login")
public class UsrLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public UsrLogin() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user_id = request.getParameter("user_id");
		String password = request.getParameter("pwd");
		MySQLConnection conn = new MySQLConnection();
		String correctPwd = conn.getPassword(user_id);
		JSONObject result = new JSONObject();
		try {
			if (correctPwd == null) {
				result.put("hasUsrName", false).put("isPwdCorrect", false);
			} else if (!correctPwd.equals(password)){
				result.put("hasUsrName", true).put("isPwdCorrect", false);
			} else {
				result.put("hasUsrName", true).put("isPwdCorrect", true);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RpcHelper.writeJsonObject(response, result);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
