<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" scope="request" type="com.bsuir.aleksandrov.phoneshop.model.entities.order.Order"/>

<tags:master pageTitle="Order">

    <p></p>
    <div class="container">
        <h2>Cart</h2>
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

            <c:choose>
                <c:when test="${not empty sessionScope.login}">
                    <form method="post" action="${pageContext.servletContext.contextPath}/order">
                </c:when>
                <c:otherwise>
                    <form action="/user/authorisation" method="get">
                </c:otherwise>
            </c:choose>
                <table class="table-borderless">
                    <tr>
                        <td class="align-top">
                            First name*:
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
                            Last name*:
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
                            Delivery address*:
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
                            Contact phone*:
                        </td>
                        <td>
                            <input name="contactPhoneNo" placeholder="+375296789012" required/>
                            <c:if test="${not empty errorsMap.get(Integer(4))}">
                                <div class="error" style="color: red">${errorsMap.get(Integer(4))}</div>
                            </c:if>
                        </td>
                    </tr>
                </table>
                <textarea name="additionalInformation" placeholder="Additional information"></textarea>
                <br>
                <button class="btn btn-light" type="submit">Order</button>
            </form>
        </div>

        <div class="col-2"></div>
    </div>
</tags:master>