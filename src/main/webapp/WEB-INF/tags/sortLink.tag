<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag isELIgnored="false" %>
<%@ attribute name="sort" required="true" %>
<%@ attribute name="order" required="true" %>

<a href="?command=Product_List&sort=${sort}&order=${order}<c:if test="${not empty param.query}">&query=${param.query}</c:if>"
   style="${sort eq param.sort and order eq param.order ? 'font-weight: bold' :  ''}">${order eq 'asc' ? '/\\' : '\\/'}
</a>
