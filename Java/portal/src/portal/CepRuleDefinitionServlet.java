package portal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import monitoring.manager.CepRuleConfigWithSource;
import monitoring.manager.CepRuleDefinitionDAO;
import monitoring.manager.CepRuleSubscriptionRequest;
import monitoring.manager.Manager;
import monitoring.manager.Node;
import cep.CepRuleConfig;

public class CepRuleDefinitionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CepRuleDefinitionServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream out = response.getOutputStream();
		CepRuleDefinitionDAO dao = Manager.getInstance().getCepRuleDefinitionDao();
		out.println(new Gson().toJson(dao.findAll()));
		out.close();
	}
}
