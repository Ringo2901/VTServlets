<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page isELIgnored="false"%>
<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="messages"/>
<tags:master pageTitle="User authorisation">
  <div class="container">
    <h2><fmt:message key="authorisation_title" /></h2>
    <c:if test="${not empty messages.get('success')}">
        <div class="panel panel-success">
          <div class="panel-heading"><fmt:message key="success_title" /></div>
          <div class="panel-body">${messages.get('success')}</div>
        </div>
    </c:if>
    <c:if test="${not empty messages.get('error')}">
        <div class="panel panel-danger">
          <div class="panel-heading"><fmt:message key="error_title" /></div>
          <div class="panel-body">${messages.get('error')}</div>
      </div>
    </c:if>
    <form action="/?command=authorisation" method="post">
      <div class="form-group">
        <label for="login"><fmt:message key="button_login" />:</label>
        <input type="text" class="form-control" id="login" name="login" required>
      </div>
      <div class="form-group">
        <label for="password"><fmt:message key="button_password" />:</label>
        <input type="password" class="form-control" id="password" name="password" required>
      </div>
      <input type="hidden" name="operation" value="authorisation">
      <button type="submit" class="btn btn-primary"><fmt:message key="button_login" /></button>
    </form>
  </div>
</tags:master>
