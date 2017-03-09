package wasdev.sample.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;

import wasdev.sample.redis.RedisClientMgr;

/**
 * Servlet implementation class HSetServlet
 */
@WebServlet("/HSetServlet")
public class HSetServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HSetServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String key = "myhash";
		String field = request.getParameter("field");
		String value = request.getParameter("value");
		
		StatefulRedisConnection<String, String> conn = RedisClientMgr.getConnection();
		
		// Get sync commands
		RedisCommands<String, String> commands = conn.sync();
		Boolean result = commands.hset(key, field, value);
		
		response.getWriter().append(result ? "created":"updated");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}	
	
}
