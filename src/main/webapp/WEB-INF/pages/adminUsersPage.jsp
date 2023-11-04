<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="users" scope="request" type="java.util.List"/>
<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="messages"/>
<tags:master pageTitle="Users">
  <p></p>
  <div id="statusMessage" class="container"><span></span></div>
  <c:if test="${not empty successMessage}">
    <div class="container">
      <div class="panel panel-success">
        <div class="panel-heading"><fmt:message key="success_title" /></div>
        <div class="panel-body">${successMessage}</div>
      </div>
    </div>
  </c:if>
  <c:if test="${not empty errorMessage}">
    <div class="container">
      <div class="panel panel-danger">
        <div class="panel-heading"><fmt:message key="error_title" /></div>
        <div class="panel-body">${errorMessage}</div>
      </div>
    </div>
  </c:if>
  <div class="container">
    <h2><fmt:message key="users_title" /></h2>
  </div>
  <div class="panel"></div>
  <div class="row">
    <div class="col-2"></div>

    <div class="col-8">
      <c:choose>
        <c:when test="${users.size() <= 0}">
          <h1 class="text-center">
            <fmt:message key="error_no_users" />
          </h1>
        </c:when>

        <c:when test="${users.size() > 0}">
          <table class="table table-hover table-bordered">
            <thead>
            <tr class="bg-light">
              <td><fmt:message key="user_id" /></td>
              <td><fmt:message key="user_role" /></td>
              <td><fmt:message key="user_login" /></td>
              <td><fmt:message key="table_action" /></td>
            </tr>
            </thead>
            <c:forEach var="user" items="${users}">
              <tr>
                <td class="align-middle">${user.id}</td>
                <td class="align-middle">${user.userRole}</td>
                <td class="align-middle">${user.login}</td>
                <td class="align-middle">
                    <c:choose>
                      <c:when test="${sessionScope.role eq 'Admin'}">
                        <form action="/" method="post">
                          <input type="hidden" name="command" value="ADMIN_USERS">
                      </c:when>
                      <c:otherwise>
                        <form action="/" method="get">
                          <input type="hidden" name="command" value="authorisation">
                      </c:otherwise>
                    </c:choose>
                    <input type="hidden" name="userId" value="${user.id}">
                    <button type="submit" class="btn btn-danger"><fmt:message key="button_delete" /></button>
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
