<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="phone" scope="request" type="com.bsuir.aleksandrov.phoneshop.model.entities.phone.Phone"/>
<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="messages"/>
<tags:master pageTitle="Phohe Details">

    <c:choose>
        <c:when test="${not empty inputErrors}">
            <div class="container">
                <div class="panel panel-danger">
                    <div class="panel-heading"><fmt:message key="error_title" /></div>
                    <div class="panel-body">
                        <fmt:message key="error_updating_cart" />
                        <br>
                        ${inputErrors.get(phone.id)}
                    </div>
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
    <div class="container">
        <h2>${phone.model}</h2>
        <div class="row">
            <div class="col-6">
                <img class="rounded" src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${phone.imageUrl}">
                <p class="text-justify">${phone.description}</p>
                <div class="float-right">
                    <p class="text">Price: $${phone.price}</p>
                    <c:choose>
                        <c:when test="${not empty sessionScope.login}">
                            <form action="/" method="post">
                                <input type="hidden" name="command" value="cart_add">
                                <input type="hidden" name="page_type" value="product_details">
                        </c:when>
                        <c:otherwise>
                            <form action="/" method="get">
                                <input type="hidden" name="command" value="authorisation">
                        </c:otherwise>
                    </c:choose>
                        <input type="hidden" name="id" value="${phone.id}">
                        <input type="number" name="quantity" id="quantity${phone.id}" min="1" required>
                        <button class="btn btn-lg btn-outline-light text-dark border-dark float-right" type="submit"><fmt:message key="button_add" /></button>
                    </form>
                </div>
            </div>

            <div class="col-1"></div>

            <div class="col-4">
                <h3><fmt:message key="phone_display" /></h3>
                <table class="table table-bordered table-light container-fluid">
                    <tr>
                        <td><fmt:message key="phone_display_size" /></td>
                        <td>${phone.displaySizeInches}"</td>
                    </tr>
                    <tr>
                        <td><fmt:message key="phone_display_resolution" /></td>
                        <td>${phone.displayResolution}</td>
                    </tr>
                    <tr>
                        <td><fmt:message key="phone_display_technology" /></td>
                        <td>${phone.displayTechnology}</td>
                    </tr>
                    <tr>
                        <td><fmt:message key="phone_display_pixel" /></td>
                        <td>${phone.pixelDensity}</td>
                    </tr>
                </table>
                <h3><fmt:message key="phone_dimensions" /></h3>
                <table class="table table-bordered table-light container-fluid">
                    <tr>
                        <td><fmt:message key="phone_dimensions_length" /></td>
                        <td>${phone.lengthMm} mm</td>
                    </tr>
                    <tr>
                        <td><fmt:message key="phone_dimensions_width" /></td>
                        <td>${phone.widthMm} mm</td>
                    </tr>
                    <tr>
                        <td><fmt:message key="phone_dimensions_weight" /></td>
                        <td>${phone.weightGr} g</td>
                    </tr>
                </table>
                <h3><fmt:message key="phone_camera" /></h3>
                <table class="table table-bordered table-light container-fluid">
                    <tr>
                        <td><fmt:message key="phone_camera_front" /></td>
                        <td>${phone.frontCameraMegapixels}</td>
                    </tr>
                    <tr>
                        <td><fmt:message key="phone_camera_back" /></td>
                        <td>${phone.backCameraMegapixels}</td>
                    </tr>
                </table>
                <h3><fmt:message key="phone_battery" /></h3>
                <table class="table table-bordered table-light container-fluid">
                    <tr>
                        <td><fmt:message key="phone_battery_talk" /></td>
                        <td>${phone.talkTimeHours} hours</td>
                    </tr>
                    <tr>
                        <td><fmt:message key="phone_battery_stand" /></td>
                        <td>${phone.standByTimeHours} hours</td>
                    </tr>
                    <tr>
                        <td><fmt:message key="phone_battery_capacity" /></td>
                        <td>${phone.batteryCapacityMah} mAh</td>
                    </tr>
                </table>
                <h3><fmt:message key="phone_other" /></h3>
                <table class="table table-bordered table-light container-fluid">
                    <tr>
                        <td class="align-middle"><fmt:message key="phone_other_colors" /></td>
                        <td>
                            <ul>
                                <c:forEach var="color" items="${phone.colors}">
                                    <li>${color.code}</li>
                                </c:forEach>
                            </ul>
                        </td>
                    </tr>
                    <tr>
                        <td><fmt:message key="phone_other_type" /></td>
                        <td>${phone.deviceType}</td>
                    </tr>
                    <tr>
                        <td><fmt:message key="phone_other_bluetooth" /></td>
                        <td>${phone.bluetooth}</td>
                    </tr>
                </table>
            </div>

            <div class="col-1"></div>
        </div>
    </div>
</tags:master>