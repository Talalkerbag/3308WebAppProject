<%@page contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%> 

<%@page language="java" import="dbUtils.*" %>
<%@page language="java" import="model.webUser.*" %> 
<%@page language="java" import="view.WebUserView" %> 
<%@page language="java" import="com.google.gson.*" %>

<%
    String msg;
    StringDataList obj = (StringDataList) session.getAttribute("user");
    session.invalidate();
    if (obj != null) {
        msg = "User has logged off";
    } else {
        msg = "No User is logged on";
    }

    Gson gson = new Gson();
    out.print(gson.toJson(msg).trim());
%>

