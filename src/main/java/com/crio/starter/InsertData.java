package com.crio.starter;

import com.crio.starter.connection.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebServlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Import Database Connection Class file
//import code.DatabaseConnection;

// Servlet Name
@Log4j2
@WebServlet(name="/InsertData", urlPatterns = {"/InsertData"})
public class InsertData extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
   public void doGet(HttpServletRequest req, HttpServletResponse resp) 
       throws ServletException, IOException {
         log.info("Hello!");
   }
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
      log.info("Hello");
        try {
          log.info(request.getParameter("username") + " " + request.getParameter("password"));
            Connection con = DatabaseConnection.initializeDatabase();

            // Create a SQL query to insert data into demo table
            // demo table consists of two columns, so two '?' is used
            log.info(request.getParameter("username") + " " + request.getParameter("password"));
            PreparedStatement st1 = con
                   .prepareStatement("insert into users(username,password) values(?, ?)");
            PreparedStatement st2 = con
                    .prepareStatement("insert into authorities(username,authority) values(?, ?)");
            // For the first parameter,
            // get the data using request object
            // sets the data to st pointer
            Random num = new Random();
            Integer count = num.nextInt()%100000;

            // Same for second parameter
            st1.setString(1, request.getParameter("username"));
            st1.setString(2, request.getParameter("password"));
            st2.setString(1, request.getParameter("username"));
            st2.setString(2,"ROLE_USER");

            // Execute the insert command using executeUpdate()
            // to make changes in database
            st1.executeUpdate();
            st2.executeUpdate();
            // Close all the connections
            st1.close();
            st2.close();
            con.close();

            // Get a writer pointer
            // to display the successful result
            PrintWriter out = response.getWriter();
            out.println("<html><body><b>Successfully Inserted"
                        + "</b></body></html>");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
