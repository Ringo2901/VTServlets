<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="order" scope="request" type="com.bsuir.aleksandrov.phoneshop.model.entities.order.Order"/>
<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="messages"/>
<tags:master pageTitle="Order overview">
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
        <h2><fmt:message key="order_overview_title" /></h2>
    </div>
    <div class="panel"></div>
    <div class="row">
        <div class="col-2"></div>

        <div class="col-8">
            <c:choose>

                <c:when test="${empty order}">
                    <h1 class="text-center"><fmt:message key="order_not_found" /></h1>
                </c:when>
                <c:when test="${not empty order}">
                    <div class="row">
                        <div class="col-6"><h1><fmt:message key="order_id" />: ${order.id}</h1></div>
                        <div class="col-6"><h1 class="float-right"><fmt:message key="order_status" />: ${order.status.toString()}</h1></div>
                    </div>
                    <table class="table table-bordered text-center">
                        <thead>
                        <tr class="bg-light">
                            <td>
                                <fmt:message key="item_brand" />
                            </td>
                            <td>
                                <fmt:message key="item_model" />
                            </td>
                            <td>
                                <fmt:message key="item_Color" />
                            </td>
                            <td>
                                <fmt:message key="item_Display_Size" />
                            </td>
                            <td>
                                <fmt:message key="item_quantity" />
                            </td>
                            <td>
                                <fmt:message key="item.price" />
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
                                <fmt:message key="order_subtotal" />
                            </td>
                            <td>
                                    ${order.subtotal}
                            </td>
                        </tr>
                        <tr>
                            <td class="border-white"></td><td class="border-white"></td><td class="border-white"></td><td class="border-white"></td>
                            <td>
                                <fmt:message key="order_delivery" />
                            </td>
                            <td>
                                    ${order.deliveryPrice}
                            </td>
                        </tr>
                        <tr>
                            <td class="border-white"></td><td class="border-white"></td><td class="border-white"></td><td class="border-white"></td>
                            <td>
                                <fmt:message key="order_total_price" />
                            </td>
                            <td>
                                    ${order.totalPrice}
                            </td>
                        </tr>
                    </table>
                    <table class="table-borderless">
                        <tr>
                            <td class="align-top">
                                <fmt:message key="user_first_name" />:
                            </td>
                            <td class="align-top">
                                    ${order.firstName}
                            </td>
                        </tr>
                        <tr>
                            <td class="align-top">
                                <fmt:message key="user_last_name" />:
                            </td>
                            <td class="align-top">
                                    ${order.lastName}
                            </td>
                        </tr>
                        <tr>
                            <td class="align-top">
                                <fmt:message key="user_delivery_address" />:
                            </td>
                            <td class="align-top">
                                    ${order.deliveryAddress}
                            </td>
                        </tr>
                        <tr>
                            <td class="align-top">
                                <fmt:message key="user_contact_phone" />:
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
                                <input type="hidden" name="command" value="admin_order_manage">
                        </c:when>
                        <c:otherwise>
                            <form action="/" method="get">
                                <input type="hidden" name="command" value="authorisation">
                        </c:otherwise>
                    </c:choose>
                        <button class="btn btn-lg btn-secondary"
                                formmethod="post" formaction="<c:url value="/?command=admin_order_manage&orderId=${order.id}"/>"
                                name="status" value="delivered">
                            <fmt:message key="button_delivered" />
                        </button>
                        <button class="btn btn-lg btn-secondary"
                                formmethod="post" formaction="<c:url value="/?command=admin_order_manage&orderId=${order.id}"/>"
                                name="status" value="rejected">
                            <fmt:message key="button_rejected" />
                        </button>
                    </form>
                </c:when>
            </c:choose>
        </div>

        <div class="col-2"></div>
    </div>
</tags:master>