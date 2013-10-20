package portal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import monitoring.manager.Manager;
import monitoring.manager.PortalSubscriptionBean;
import monitoring.manager.PortalSubscriptionDAO;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class PortalSubscriptionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String type = request.getParameter("type");
		ServletOutputStream out = response.getOutputStream();
		PortalSubscriptionDAO dao = Manager.getInstance().getPortalSubscriptionDao();

		
		JsonObject o = new JsonObject();
		
		if (type == null) {
			out.print(new Gson().toJson(dao.findAll()));
		} else if (type.equals("add")) {
			PortalSubscriptionBean bean = new PortalSubscriptionBean();
			bean.setEventTypeName(request.getParameter("eventTypeName"));
			String filter = request.getParameter("filter");
			if (filter != null)
				bean.setFilter(filter);
			Manager.getInstance().createPortalSubscription(bean);
			o.addProperty("success", true);
			o.add("data", new Gson().toJsonTree(bean));
			out.print(o.toString());
		} else if (type.equals("delete")) {
			int id;
			String idStr = request.getParameter("id");
			if (idStr == null) {
				o.addProperty("success", false);
				out.print(o.toString());
				return;
			}
			try {
				id = Integer.parseInt(idStr);
			} catch (NumberFormatException e) {
				o.addProperty("success", false);
				out.print(o.toString());
				return;
			}
			Manager.getInstance().deletePortalSubscription(id);
			o.addProperty("success", true);
			out.print(o.toString());
		} else if (type.equals("edit")) {
			PortalSubscriptionBean bean = new PortalSubscriptionBean();
			bean.setEventTypeName(request.getParameter("eventTypeName"));
			String filter = request.getParameter("filter");
			
			int id;
			if (filter != null)
				bean.setFilter(filter);
			String idStr = request.getParameter("id");
			if (idStr == null) {
				o.addProperty("success", false);
				out.print(o.toString());
				return;
			}
			try {
				id = Integer.parseInt(idStr);
			} catch (NumberFormatException e) {
				o.addProperty("success", false);
				out.print(o.toString());
				return;
			}
			bean.setId(id);
			
			Manager.getInstance().updatePortalSubscription(bean);
			o.addProperty("success", true);
			o.add("data", new Gson().toJsonTree(bean));
			out.print(o.toString());
		}
	}
}
