<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="orders" scope="request" type="java.util.List"/>
<tags:master pageTitle="Orders">
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
        <h2>Orders</h2>
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
                            <td>User Login</td>
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
                                        <c:when test="${sessionScope.role eq 'Admin'}">
                                            <a href="<c:url value="/admin/orders/${order.id}"/>">${order.id}</a>
                                        </c:when>
                                        <c:otherwise>
                                            <form action="/user/authorisation" method="get">
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="align-middle">${order.login}</td>
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
