<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="order" scope="request" type="com.bsuir.aleksandrov.phoneshop.model.entities.order.Order"/>
<tags:master pageTitle="Order overview">
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
        <h2>Order overview</h2>
    </div>
    <div class="panel"></div>
    <div class="row">
        <div class="col-2"></div>

        <div class="col-8">
            <c:choose>

                <c:when test="${empty order}">
                    <h1 class="text-center">Order not found</h1>
                </c:when>
                <c:when test="${not empty order}">
                    <div class="row">
                        <div class="col-6"><h1>Order ID: ${order.id}</h1></div>
                        <div class="col-6"><h1 class="float-right">Status: ${order.status.toString()}</h1></div>
                    </div>
                    <table class="table table-bordered text-center">
                        <thead>
                        <tr class="bg-light">
                            <td>
                                Brand
                            </td>
                            <td>
                                Model
                            </td>
                            <td>
                                Color
                            </td>
                            <td>
                                Display size
                            </td>
                            <td>
                                Quantity
                            </td>
                            <td>
                                Price
                            </td>
                        </tr>
                        </thead>
                        <c:forEach var="item" items="${order.orderItems}">
                            <tr>
                                <td class="align-middle">
                                        ${item.phone.brand}
                                </td>
                                <td class="align-middle">
                                        ${item.phone.model}
                                </td>
                                <td class="align-middle">
                                    <ul>
                                        <c:forEach var="color" items="${item.phone.colors}">
                                            <li>${color.code}</li>
                                        </c:forEach>
                                    </ul>
                                </td>
                                <td class="align-middle">
                                        ${item.phone.displaySizeInches}"
                                </td>
                                <td class="align-middle">
                                        ${item.quantity}
                                </td>
                                <td class="align-middle">
                                        ${item.phone.price}
                                </td>
                            </tr>
                        </c:forEach>
                        <tr>
                            <td class="border-white"></td><td class="border-white"></td><td class="border-white"></td><td class="border-white"></td>
                            <td>
                                Subtotal
                            </td>
                            <td>
                                    ${order.subtotal}
                            </td>
                        </tr>
                        <tr>
                            <td class="border-white"></td><td class="border-white"></td><td class="border-white"></td><td class="border-white"></td>
                            <td>
                                Delivery
                            </td>
                            <td>
                                    ${order.deliveryPrice}
                            </td>
                        </tr>
                        <tr>
                            <td class="border-white"></td><td class="border-white"></td><td class="border-white"></td><td class="border-white"></td>
                            <td>
                                TOTAL
                            </td>
                            <td>
                                    ${order.totalPrice}
                            </td>
                        </tr>
                    </table>
                    <table class="table-borderless">
                        <tr>
                            <td class="align-top">
                                First name:
                            </td>
                            <td class="align-top">
                                    ${order.firstName}
                            </td>
                        </tr>
                        <tr>
                            <td class="align-top">
                                Last name:
                            </td>
                            <td class="align-top">
                                    ${order.lastName}
                            </td>
                        </tr>
                        <tr>
                            <td class="align-top">
                                Delivery address:
                            </td>
                            <td class="align-top">
                                    ${order.deliveryAddress}
                            </td>
                        </tr>
                        <tr>
                            <td class="align-top">
                                Contact phone:
                            </td>
                            <td class="align-top">
                                    ${order.contactPhoneNo}
                            </td>
                        </tr>
                    </table>
                    <p>${order.additionalInformation}</p>
                    <c:choose>
                        <c:when test="${sessionScope.role eq 'Admin'}">
                            <form>
                        </c:when>
                        <c:otherwise>
                            <form action="/user/authorisation" method="get">
                        </c:otherwise>
                    </c:choose>
                        <button class="btn btn-lg btn-secondary" formmethod="get" formaction="<c:url value="/admin/orders"/>">
                            Back
                        </button>
                        <button class="btn btn-lg btn-secondary"
                                formmethod="post" formaction="<c:url value="/admin/orders/${order.id}"/>"
                                name="status" value="delivered">
                            Delivered
                        </button>
                        <button class="btn btn-lg btn-secondary"
                                formmethod="post" formaction="<c:url value="/admin/orders/${order.id}"/>"
                                name="status" value="rejected">
                            Rejected
                        </button>
                    </form>
                </c:when>
            </c:choose>
        </div>

        <div class="col-2"></div>
    </div>
</tags:master>