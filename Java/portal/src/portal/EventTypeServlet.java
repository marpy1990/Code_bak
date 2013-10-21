package portal;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import monitoring.manager.CepRuleConfigWithSource;
import monitoring.manager.EventTypeDAO;
import monitoring.manager.Manager;
import monitoring.manager.Node;

import com.google.gson.Gson;

public class EventTypeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream out = response.getOutputStream();
		EventTypeDAO dao = Manager.getInstance().getEventTypeDao();
		out.print(new Gson().toJson(dao.findAll()));
	}
}
