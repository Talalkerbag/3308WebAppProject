<%@page contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%> 

<%@page language="java" import="dbUtils.*" %>
<%@page language="java" import="model.webUser.*" %> 
<%@page language="java" import="view.*" %> 
<%@page language="java" import="com.google.gson.*" %>

<%
    
    StringDBSolution solution = new StringDBSolution();
    String searchId = request.getParameter("id");
    
    if (searchId == null) {
        solution.errorMsg = "Cannot search for problem - 'id' most be supplied as URL parameter";
    } else {

        DbConn dbc = new DbConn();
        solution.errorMsg = dbc.getErr(); // returns "" if connection is good, else db error msg.

        if (solution.errorMsg.length() == 0) { // if got good DB connection,

            System.out.println("*** Ready to call getProblemById");
            solution = Search.getSolutionById(dbc, searchId);
            
        }

        dbc.close(); // EVERY code path that opens a db connection, must also close it - no DB Conn leaks.
    }
    // This object (from the GSON library) can to convert between JSON <-> POJO (plain old java object) 
   System.out.println("Returned: " + solution);
    Gson gson = new Gson();
    out.print(gson.toJson(solution).trim());
    
%>