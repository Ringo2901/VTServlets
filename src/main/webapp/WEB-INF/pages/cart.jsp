<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="cart" scope="session" type="com.bsuir.aleksandrov.phoneshop.model.entities.cart.Cart"/>
<tags:master pageTitle="Cart">
    <p></p>
    <div class="container">
        <h2>Cart</h2>
    </div>
    <c:choose>
        <c:when test="${not empty inputErrors}">
            <div class="container">
                <div class="panel panel-danger">
                    <div class="panel-heading">Error</div>
                    <div class="panel-body">There were some problems updating the cart!</div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <c:if test="${not empty param.message}">
                <div class="container">
                    <div class="panel panel-success">
                        <div class="panel-heading">Error</div>
                        <div class="panel-body">${param.message}</div>
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
                <form action="<c:url value="/order"/>" method="get">
                    <button class="btn btn-lg btn-outline-light text-dark border-dark float-right" type="submit">Order</button>
                </form>
                <form action="/cart" method="post" id="updateForm">
                    <input type="hidden" name="updateOperation" value="true">
                    <table class="table table-hover table-bordered text-center">
                        <thead>
                        <tr class="bg-light">
                            <th>Brand</th>
                            <th>Model</th>
                            <th>Color</th>
                            <th>Price</th>
                            <th>Quantity</th>
                            <th>Action</th>
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
                                    <button class="btn btn-danger" type="button" onclick="deleteCartItem(${cartItem.phone.id})">Delete</button>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                    <p>Total Price: $ ${cart.totalCost}</p>
                    <button class="btn btn-lg btn-outline-light text-dark border-dark float-right" type="button" onclick="updateCart()">Update</button>
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
                form.action = "/cart";
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
