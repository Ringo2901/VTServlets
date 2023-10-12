<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="orders" scope="request" type="java.util.List"/>
<tags:master pageTitle="My Orders">
  <p></p>
  <div class="container">
    <h2>My Orders</h2>
  </div>
  <div class="panel"></div>
  <div class="row">
    <div class="col-2"></div>
    <div class="col-8">
      <c:choose>
        <c:when test="${orders.size() <= 0}">
          <h1 class="text-center">
            There is no orders right now
          </h1>
        </c:when>

        <c:when test="${orders.size() > 0}">
          <table class="table table-hover table-bordered">
            <thead>
            <tr class="bg-light">
              <td>Order ID</td>
              <td>Customer</td>
              <td>Phone</td>
              <td>Address</td>
              <td>Date</td>
              <td>Total price</td>
              <td>Status</td>
            </tr>
            </thead>
            <c:forEach var="order" items="${orders}">
              <tr>
                <td class="align-middle">
                  <c:choose>
                  <c:when test="${sessionScope.login eq order.login}">
                    <a href="<c:url value="/orderOverview/${order.secureID}"/>">${order.id}</a>
                  </c:when>
                  <c:otherwise>
                  <form action="/user/authorisation" method="get">
                    </c:otherwise>
                    </c:choose>
                </td>
                <td class="align-middle">${order.firstName} ${order.lastName}</td>
                <td class="align-middle">${order.contactPhoneNo}</td>
                <td class="align-middle">${order.deliveryAddress}</td>
                <td class="align-middle">${order.time} ${order.date}</td>
                <td class="align-middle">${order.totalPrice}</td>
                <td class="align-middle">${order.status.toString()}</td>
              </tr>
            </c:forEach>
          </table>
        </c:when>
      </c:choose>
    </div>
    <div class="col-2"></div>
  </div>
</tags:master>
