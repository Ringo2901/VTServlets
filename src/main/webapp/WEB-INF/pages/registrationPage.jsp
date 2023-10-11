<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ page isELIgnored="false"%>
<tags:master pageTitle="User Registration">
  <div class="container">
    <h2>User Registration</h2>
    <c:if test="${not empty messages.get('success')}">
        <div class="panel panel-success">
          <div class="panel-heading">Success</div>
          <div class="panel-body">${messages.get('success')}</div>
        </div>
    </c:if>
    <c:if test="${not empty messages.get('error')}">
        <div class="panel panel-danger">
          <div class="panel-heading">Error</div>
          <div class="panel-body">${messages.get('error')}</div>
        </div>
    </c:if>
    <form action="/user/registration" method="post">
      <div class="form-group">
        <label for="login">Login:</label>
        <input type="text" class="form-control" id="login" name="login" required>
      </div>
      <div class="form-group">
        <label for="password">Password:</label>
        <input type="password" class="form-control" id="password" name="password" required>
      </div>
      <input type="hidden" name="operation" value="registration">
      <button type="submit" class="btn btn-primary">Register</button>
    </form>
  </div>
</tags:master>
