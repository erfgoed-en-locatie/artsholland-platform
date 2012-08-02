<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<ul>
<c:forEach var="user" items="${users}">
	<li>${user.name}</li>
</c:forEach>
</ul>