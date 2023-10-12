<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@page isErrorPage="true" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="messages"/>
<tags:master pageTitle="Error 404: Order not found">
    <h1>
        <fmt:message key="order_not_found" />
    </h1>
    <p>
        <fmt:message key="error_page_subtitle" />
    </p>
</tags:master>
