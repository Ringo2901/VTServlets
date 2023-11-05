<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="cart" scope="session" type="com.bsuir.aleksandrov.phoneshop.model.entities.cart.Cart"/>
<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="messages"/>
<tags:master pageTitle="Cart">
    <p></p>
    <div class="container">
        <h2><fmt:message key= "cart_title"/></h2>
    </div>
    <c:choose>
        <c:when test="${not empty inputErrors}">
            <div class="container">
                <div class="panel panel-danger">
                    <div class="panel-heading"><fmt:message key="error_title" /></div>
                    <div class="panel-body">There were some problems updating the cart!</div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <c:if test="${not empty successMessage}">
                <div class="container">
                    <div class="panel panel-success">
                        <div class="panel-heading"><fmt:message key="success_title" /></div>
                        <div class="panel-body">${successMessage}</div>
                    </div>
                </div>
            </c:if>
        </c:otherwise>
    </c:choose>

    <div class="panel"></div>
    <div class="row">
        <div class="col-2"></div>
        <div class="col-8">
            <c:if test="${cart.items.size() > 0}">
                    <c:choose>
                        <c:when test="${not empty sessionScope.login}">
                            <form action="<c:url value="/"/>" method="get">
                                <input type="hidden" name="command" value="order">
                        </c:when>
                        <c:otherwise>
                            <form action="/" method="get">
                                <input type="hidden" name="command" value="authorisation">
                        </c:otherwise>
                    </c:choose>
                    <button class="btn btn-lg btn-outline-light text-dark border-dark float-right" type="submit"><fmt:message key="button_order" /></button>
                </form>
                    <c:choose>
                        <c:when test="${not empty sessionScope.login}">
                            <form action="/?command=cart_update" method="post" id="updateForm">
                        </c:when>
                        <c:otherwise>
                            <form action="/" method="get">
                                <input type="hidden" name="command" value="authorisation">
                        </c:otherwise>
                    </c:choose>
                    <input type="hidden" name="updateOperation" value="true">
                    <table class="table table-hover table-bordered text-center">
                        <thead>
                        <tr class="bg-light">
                            <th><fmt:message key="item_brand" /></th>
                            <th><fmt:message key="item_model" /></th>
                            <th><fmt:message key="item_Color" /></th>
                            <th><fmt:message key="item.price" /></th>
                            <th><fmt:message key="item_quantity" /></th>
                            <th><fmt:message key="table_action" /></th>
                        </tr>
                        </thead>
                        <c:forEach var="cartItem" items="${cart.items}">
                            <tr>
                                <td>${cartItem.phone.brand}</td>
                                <td>${cartItem.phone.model}</td>
                                <td>
                                    <ul>
                                        <c:forEach var="color" items="${cartItem.phone.colors}">
                                            <li>${color.code}</li>
                                        </c:forEach>
                                    </ul>
                                </td>
                                <td>$ ${cartItem.phone.price}</td>
                                <td>
                                    <input type="number" name="quantity" value="${cartItem.quantity}" min="1" required>
                                    <c:if test="${not empty inputErrors.get(cartItem.phone.id)}">
                                        <div class="error" style="color: red">${inputErrors.get(cartItem.phone.id)}</div>
                                    </c:if>
                                </td>
                                <td>
                                    <input type="hidden" name="id" value="${cartItem.phone.id}">
                                    <button class="btn btn-danger" type="button" onclick="deleteCartItem(${cartItem.phone.id})"><fmt:message key="button_delete" /></button>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                    <p><fmt:message key="order_total_price" />: $ ${cart.totalCost}</p>
                    <button class="btn btn-lg btn-outline-light text-dark border-dark float-right" type="button" onclick="updateCart()"><fmt:message key="button_update" /></button>
                </form>
            </c:if>
        </div>
        <div class="col-2"></div>
    </div>
    <script>
        function updateCart() {
            var form = document.getElementById("updateForm");
            var inputOperation = document.createElement("input");
            inputOperation.type = "hidden";
            inputOperation.name = "updateOperation";
            inputOperation.value = "true";
            form.submit();
        }

        function deleteCartItem(phoneId) {
                var form = document.createElement("form");
                form.action = "/?command=cart_delete";
                form.method = "post";

                var inputId = document.createElement("input");
                inputId.type = "hidden";
                inputId.name = "id";
                inputId.value = phoneId;

                var inputOperation = document.createElement("input");
                inputOperation.type = "hidden";
                inputOperation.name = "deleteOperation";
                inputOperation.value = "true";

                form.appendChild(inputId);
                form.appendChild(inputOperation);

                document.body.appendChild(form);
                form.submit();
        }
    </script>
</tags:master>
