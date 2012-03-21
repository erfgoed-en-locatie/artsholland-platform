<%@ page import="java.util.*" %>
<%@ page import="org.codehaus.jackson.map.ObjectMapper" %>
<%@ page import="org.waag.ah.jackson.AHObjectMapper" %>
<%@ page import="org.codehaus.jackson.map.ObjectWriter" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@page contentType="application/json	" %>
<%
	Object object = request.getAttribute("result");
	
	ObjectMapper mapper = new AHObjectMapper();		
	ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();	    

	String json = writer.writeValueAsString(object);
%>
<%= json %>

