<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="messages"/>
<tags:master pageTitle="User Registration">
  <div class="container">
    <h2><fmt:message key="registration_title" /></h2>
    <c:if test="${not empty message.get('success')}">
        <div class="panel panel-success">
          <div class="panel-heading"><fmt:message key="success_title" /></div>
          <div class="panel-body">${message.get('success')}</div>
        </div>
    </c:if>
    <c:if test="${not empty message.get('error')}">
        <div class="panel panel-danger">
          <div class="panel-heading"><fmt:message key="error_title" /></div>
          <div class="panel-body">${message.get('error')}</div>
        </div>
    </c:if>
    <form action="/" method="post">
      <input type="hidden" name="command" value="registration">
      <div class="form-group">
        <label for="login"><fmt:message key="button_login" />:</label>
        <input type="text" class="form-control" id="login" name="login" required>
      </div>
      <div class="form-group">
        <label for="password"><fmt:message key="button_password" />:</label>
        <input type="password" class="form-control" id="password" name="password" required>
      </div>
      <input type="hidden" name="operation" value="registration">
      <button type="submit" class="btn btn-primary"><fmt:message key="button_register" /></button>
    </form>
  </div>
</tags:master>
