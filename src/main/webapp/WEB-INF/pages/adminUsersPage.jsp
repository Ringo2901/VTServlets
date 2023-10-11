<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="users" scope="request" type="java.util.List"/>
<tags:master pageTitle="Users">
  <p></p>
  <div id="statusMessage" class="container"><span></span></div>
  <c:if test="${not empty successMessage}">
    <div class="container">
      <div class="panel panel-success">
        <div class="panel-heading">Success</div>
        <div class="panel-body">${successMessage}</div>
      </div>
    </div>
  </c:if>
  <c:if test="${not empty errorMessage}">
    <div class="container">
      <div class="panel panel-danger">
        <div class="panel-heading">Error</div>
        <div class="panel-body">${errorMessage}</div>
      </div>
    </div>
  </c:if>
  <div class="container">
    <h2>Users</h2>
  </div>
  <div class="panel"></div>
  <div class="row">
    <div class="col-2"></div>

    <div class="col-8">
      <c:choose>
        <c:when test="${users.size() <= 0}">
          <h1 class="text-center">
            There is no users right now
          </h1>
        </c:when>

        <c:when test="${users.size() > 0}">
          <table class="table table-hover table-bordered">
            <thead>
            <tr class="bg-light">
              <td>User ID</td>
              <td>Role</td>
              <td>Login</td>
              <td>Action</td>
            </tr>
            </thead>
            <c:forEach var="user" items="${users}">
              <tr>
                <td class="align-middle">${user.id}</td>
                <td class="align-middle">${user.userRole}</td>
                <td class="align-middle">${user.login}</td>
                <td class="align-middle">
                  <form action="/admin/users" method="post">
                    <input type="hidden" name="userId" value="${user.id}">
                    <button type="submit" class="btn btn-danger">Delete</button>
                  </form>
                </td>
              </tr>
            </c:forEach>
          </table>
        </c:when>
      </c:choose>
    </div>

    <div class="col-2"></div>
  </div>
</tags:master>
