package portal;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import monitoring.manager.Manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class EventServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EventServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int lastId = ServletUtils.parseInt(request, "id", -1);
		int start = ServletUtils.parseInt(request, "start", 0);
		int limit = ServletUtils.parseInt(request, "limit", 20);
		long timeBegin = 0;
		String type = "%";
		String source = "%";
		String filter = request.getParameter("filter");
		if (filter != null) {
			try {
				JsonArray array = new JsonParser().parse(filter).getAsJsonArray();
				for (JsonElement e : array) {
					if (e instanceof JsonObject) {
						JsonObject o = (JsonObject) e;
						String property = o.get("property").getAsString();
						String value = o.get("value").getAsString();
						if (property.equals("source")) {
							source = value;
						} else if (property.equals("type")) {
							type = value;
						} else if (property.equals("timeBegin")) {
							timeBegin = Long.valueOf(value);
						}
					}
				}
			} catch (RuntimeException e) {
				
			}
		}
		response.setContentType("text");
		PrintWriter out = response.getWriter();
		out.println(Manager.getInstance().getDAO().queryEvent(lastId, type, source, timeBegin, start, limit));
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
