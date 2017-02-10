package example.nosql;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.servlet.RequestDispatcher;

import java.io.PrintWriter;
 



import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class LoginServlet
 */
//@WebServlet("/loginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
    }

    //private static final long serialVersionUID = 1L;
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
		    String username = request.getParameter("username");
	        String password = request.getParameter("password");
	         
	        System.out.println("username: " + username);
	        System.out.println("password: " + password);
	        
	        response.setContentType("text/html");
	        java.io.PrintWriter out = response.getWriter();

	        // do some processing here...
	        if ((isEmpty(username) || ( isEmpty(password)))) {
	        	out.println("<html>");
	            out.println("<title>Invalid User</title>");
	            out.println("<body><center><h2>" + "Invalid User!</h2><br>");
	            out.println("Press the 'Back' button to try again");
	            out.println("</center></body></html>");
	            out.flush();
	            return;

	         }
	        
	        if(username=="aishwarya" && password =="user1"){
	        	RequestDispatcher requestDispatcher = request.getRequestDispatcher("/index.html");
	            requestDispatcher.forward(request, response);

	        }
	        
	        // get response writer
	        //PrintWriter writer = response.getWriter();
	         
	        // build HTML code
	        //String htmlRespone = "<html>";
	        //htmlRespone += "<h2>Your username is: " + username + "<br/>";       
	        //htmlRespone += "Your password is: " + password + "</h2>";     
	        //htmlRespone += "</html>";
	         
	        // return response
	        //writer.println("/index.html");

	        response.sendRedirect("index.html");
	        
	}
	
	    public static boolean isEmpty(String string) {
	    return (string == null || string.isEmpty());
	    }
	    
	   
}


