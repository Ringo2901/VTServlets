<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<jsp:useBean id="order" scope="request" type="com.bsuir.aleksandrov.phoneshop.model.entities.order.Order"/>
<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="messages"/>
<tags:master pageTitle="Order">

    <p></p>
    <div class="container">
        <h2><fmt:message key="cart_title" /></h2>
    </div>
    <div id="statusMessage" class="container"><span></span></div
    <c:if test="${not empty errorsMap.get(Integer(0))}">
        <div class="container">
            <div class="panel panel-danger">
                <div class="panel-heading">Error</div>
                <div class="panel-body">${errorsMap.get(Integer(0))}</div>
            </div>
        </div>
    </c:if>

    <div class="panel"></div>
    <div class="row">
        <div class="col-2"></div>

        <div class="col-8">
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

            <c:choose>
                <c:when test="${not empty sessionScope.login}">
                    <form method="post" action="/">
                        <input type="hidden" name="command" value="order">
                </c:when>
                <c:otherwise>
                    <form action="/" method="get">
                        <input type="hidden" name="command" value="authorisation">
                </c:otherwise>
            </c:choose>
                <table class="table-borderless">
                    <tr>
                        <td class="align-top">
                            <fmt:message key="user_first_name" />*:
                        </td>
                        <td>
                            <input name="firstName" placeholder="First name" required/>
                            <c:if test="${not empty errorsMap.get(Integer(1))}">
                                <div class="error" style="color: red">${errorsMap.get(Integer(1))}</div>
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <td class="align-top">
                            <fmt:message key="user_last_name" />*:
                        </td>
                        <td>
                            <input name="lastName" placeholder="Last name" required/>
                            <c:if test="${not empty errorsMap.get(Integer(2))}">
                                <div class="error" style="color: red">${errorsMap.get(Integer(2))}</div>
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <td class="align-top">
                            <fmt:message key="user_delivery_address" />*:
                        </td>
                        <td>
                            <input name="deliveryAddress" placeholder="Address" required/>
                            <c:if test="${not empty errorsMap.get(Integer(3))}">
                                <div class="error" style="color: red">${errorsMap.get(Integer(3))}</div>
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <td class="align-top">
                            <fmt:message key="user_contact_phone" />*:
                        </td>
                        <td>
                            <input name="contactPhoneNo" placeholder="+375296789012" required/>
                            <c:if test="${not empty errorsMap.get(Integer(4))}">
                                <div class="error" style="color: red">${errorsMap.get(Integer(4))}</div>
                            </c:if>
                        </td>
                    </tr>
                </table>
                <textarea name="additionalInformation" placeholder=<fmt:message key="order_additional_information" />></textarea>
                <br>
                <button class="btn btn-light" type="submit"><fmt:message key="button_order" /></button>
            </form>
        </div>

        <div class="col-2"></div>
    </div>
</tags:master>