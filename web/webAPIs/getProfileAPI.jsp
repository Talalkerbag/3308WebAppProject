<%@page contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%> 

<%@page language="java" import="dbUtils.*" %>
<%@page language="java" import="model.webUser.*" %> 
<%@page language="java" import="view.WebUserView" %> 
<%@page language="java" import="com.google.gson.*" %>

<%
    String msg = "No User is logged on";
    StringDataList obj = (StringDataList) session.getAttribute("user");
    Gson gson = new Gson();
    if (obj == null) {
        out.print(gson.toJson(msg).trim());
    }
    else{
        out.print(gson.toJson(obj).trim());
    }
%>

