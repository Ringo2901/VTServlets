<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@page isErrorPage="true" %>
<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="messages"/>
<tags:master pageTitle="Error 404: Product not found">
    <h1>
        <fmt:message key="error_product_not_found" />
    </h1>
    <p>
        <fmt:message key="error_page_subtitle" />
    </p>
</tags:master>