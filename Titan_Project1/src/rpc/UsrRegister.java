package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import db.MySQLConnection;

/**
 * Servlet implementation class UsrRegister
 */
@WebServlet("/register")
public class UsrRegister extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public UsrRegister() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userFirstName = request.getParameter("firstname");
		String userLastName = request.getParameter("lastname");
		String user_id = request.getParameter("user_id");
		String newPwd = request.getParameter("pwd");
		System.out.println(userFirstName + " " + userLastName + " " + user_id + " " + newPwd);
		MySQLConnection conn = new MySQLConnection();
		String pwd = conn.getPassword(user_id);
		JSONObject result = new JSONObject();
		try {
			result.put("SUCCESS", conn.saveUser(user_id, newPwd, userFirstName, userLastName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		RpcHelper.writeJsonObject(response, result);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
