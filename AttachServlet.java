package example.nosql;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.cloudant.client.api.Database;
import com.google.gson.JsonObject;

@WebServlet("/attach")
@MultipartConfig()
public class AttachServlet extends HttpServlet {

	private static final int readBufferSize = 8192;
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		Part part = request.getPart("file");

		String id = request.getParameter("id");
		String name = request.getParameter("name");
		String value = request.getParameter("value");
		String app_cyc_date = request.getParameter("app_cyc_date");
		String app_cyc_compl_date = request.getParameter("app_cyc_compl_date");
	    String app_rf = request.getParameter("app_rf");
	    String app_sob = request.getParameter("app_sob");
	    String app_problem = request.getParameter("app_problem");
		String fileName = request.getParameter("filename");

		Database db = null;
		try {
			db = CloudantClientMgr.getDB();
		} catch (Exception re) {
			re.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		ResourceServlet servlet = new ResourceServlet();

		JsonObject resultObject = servlet.create(db, id, name, value, fileName, part, app_cyc_date, app_cyc_compl_date, app_rf, app_sob, app_problem);

		System.out.println("Upload completed.");

		response.getWriter().println(resultObject.toString());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = request.getParameter("id");
		String key = request.getParameter("key");
	//	String value = request.getParameter("value");
	//	String app_cyc_date = request.getParameter("app_cyc_date");
	//	String app_cyc_compl_date = request.getParameter("app_cyc_compl_date");
	//   String app_rf = request.getParameter("app_rf");
	//    String app_sob = request.getParameter("app_sob");
	//    String app_problem = request.getParameter("app_problem");
	//	String fileName = request.getParameter("filename");

		response.setHeader("Content-Disposition", "inline; filename=\"" + key + "\"");

		InputStream dbResponse = CloudantClientMgr.getDB().find(id + "/" + URLEncoder.encode(key,"UTF-8"));
		OutputStream output = response.getOutputStream();

		try {
			int readBytes = 0;
			byte[] buffer = new byte[readBufferSize];
			while ((readBytes = dbResponse.read(buffer)) >= 0) {
				output.write(buffer, 0, readBytes);
			}
		} finally {
			dbResponse.close();
		}

	}

}
