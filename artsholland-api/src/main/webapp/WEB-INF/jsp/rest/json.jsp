<%@ page import="java.util.*" %>
<%@ page import="org.codehaus.jackson.map.ObjectMapper" %>
<%@ page import="org.waag.ah.jackson.AHObjectMapper" %>
<%@ page import="org.codehaus.jackson.map.ObjectWriter" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@page contentType="application/json	" %>
<%
	//Set<Room> rooms = (Set<Room>) request.getAttribute("rooms");
	Object object = request.getAttribute("result");
	//ArrayList<String> rooms = (ArrayList<String>) request.getAttribute("rooms");
	
	ObjectMapper mapper = new AHObjectMapper();		
	ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();	    

	String json = writer.writeValueAsString(object);
%>
<%= json %>

