<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="phone" scope="request" type="com.bsuir.aleksandrov.phoneshop.model.entities.phone.Phone"/>
<tags:master pageTitle="Phohe Details">

    <c:choose>
        <c:when test="${not empty sessionScope.inputErrors}">
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
                        <div class="panel-heading">Success</div>
                        <div class="panel-body">${param.message}</div>
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
                    <form action="/cart" method="post">
                        <input type="hidden" name="addOperation" value="add">
                        <input type="hidden" name="id" value="${phone.id}">
                        <input type="number" name="quantity" id="quantity${phone.id}" min="1" required>

                        <button class="btn btn-lg btn-outline-light text-dark border-dark float-right" type="submit">Add to cart</button>
                    </form>
                    <c:if test="${not empty sessionScope.inputErrors.get(phone.id)}">
                        <div class="error" style="color: red">${sessionScope.inputErrors.get(phone.id)}</div>
                    </c:if>
                </div>

            </div>

            <div class="col-1"></div>

            <div class="col-4">
                <h3>Display</h3>
                <table class="table table-bordered table-light container-fluid">
                    <tr>
                        <td>Size</td>
                        <td>${phone.displaySizeInches}"</td>
                    </tr>
                    <tr>
                        <td>Resolution</td>
                        <td>${phone.displayResolution}</td>
                    </tr>
                    <tr>
                        <td>Technology</td>
                        <td>${phone.displayTechnology}</td>
                    </tr>
                    <tr>
                        <td>Pixel density</td>
                        <td>${phone.pixelDensity}</td>
                    </tr>
                </table>
                <h3>Dimensions & weight</h3>
                <table class="table table-bordered table-light container-fluid">
                    <tr>
                        <td>Length</td>
                        <td>${phone.lengthMm} mm</td>
                    </tr>
                    <tr>
                        <td>Width</td>
                        <td>${phone.widthMm} mm</td>
                    </tr>
                    <tr>
                        <td>Weight</td>
                        <td>${phone.weightGr} g</td>
                    </tr>
                </table>
                <h3>Camera</h3>
                <table class="table table-bordered table-light container-fluid">
                    <tr>
                        <td>Front</td>
                        <td>${phone.frontCameraMegapixels}</td>
                    </tr>
                    <tr>
                        <td>Back</td>
                        <td>${phone.backCameraMegapixels}</td>
                    </tr>
                </table>
                <h3>Battery</h3>
                <table class="table table-bordered table-light container-fluid">
                    <tr>
                        <td>Talk time</td>
                        <td>${phone.talkTimeHours} hours</td>
                    </tr>
                    <tr>
                        <td>Stand by time</td>
                        <td>${phone.standByTimeHours} hours</td>
                    </tr>
                    <tr>
                        <td>Battery capacity</td>
                        <td>${phone.batteryCapacityMah} mAh</td>
                    </tr>
                </table>
                <h3>Other</h3>
                <table class="table table-bordered table-light container-fluid">
                    <tr>
                        <td class="align-middle">Colors</td>
                        <td>
                            <ul>
                                <c:forEach var="color" items="${phone.colors}">
                                    <li>${color.code}</li>
                                </c:forEach>
                            </ul>
                        </td>
                    </tr>
                    <tr>
                        <td>Device type</td>
                        <td>${phone.deviceType}</td>
                    </tr>
                    <tr>
                        <td>Bluetooth</td>
                        <td>${phone.bluetooth}</td>
                    </tr>
                </table>
            </div>

            <div class="col-1"></div>
        </div>
    </div>
</tags:master>