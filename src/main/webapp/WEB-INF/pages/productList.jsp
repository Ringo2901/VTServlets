<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ page isELIgnored="false"%>
<jsp:useBean id="phones" scope="request" type="java.util.List"/>
<jsp:useBean id="numberOfPages" scope="request" type="java.lang.Long"/>
<tags:master pageTitle="Phohe List">
  <p></p>
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

  <div class="row">
    <div class="container">
      <div class="container">
        <form class="float-right">
          <input name="query" value="${param.query}">
          <button class="btn btn-light">Search</button>
        </form>
      </div>
    </div>
  </div>
  <div class="panel"></div>
  <div class="row">
    <div class="col-2"></div>
    <div class="col-8">
      <table class="table table-hover table-bordered text-center">
        <thead>
        <tr class="bg-light">
          <td>Image</td>
          <td>
            Brand
            <tags:sortLink sort="brand" order="asc"/><tags:sortLink sort="brand" order="desc"/>
          </td>
          <td>
            Model
            <tags:sortLink sort="model" order="asc"/><tags:sortLink sort="model" order="desc"/>
          </td>
          <td>Color</td>
          <td>
            Display size
            <tags:sortLink sort="displaySizeInches" order="asc"/>
            <tags:sortLink sort="displaySizeInches" order="desc"/>
          </td>
          <td>
            Price
            <tags:sortLink sort="price" order="asc"/>
            <tags:sortLink sort="price" order="desc"/>
          </td>
          <td>Action</td>
        </tr>
        </thead>
        <c:forEach var="phone" items="${phones}">
          <tr>
            <td class="align-middle">
              <img class="rounded" src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${phone.imageUrl}">
            </td>
            <td class="align-middle">
              <a href="<c:url value="/productDetails/${phone.id}"/>">${phone.brand}</a>
            </td>
            <td class="align-middle">
              <a href="<c:url value="/productDetails/${phone.id}"/>">${phone.model}</a>
            </td>
            <td class="align-middle">
              <ul>
                <c:forEach var="color" items="${phone.colors}">
                  <li>${color.code}</li>
                </c:forEach>
              </ul>
            </td>
            <td class="align-middle">${phone.displaySizeInches}"</td>
            <td class="align-middle">$ ${phone.price}</td>
            <td class="align-middle">
                      <c:choose>
                        <c:when test="${not empty sessionScope.login}">
                            <form action="/cart" method="post">
                        </c:when>
                        <c:otherwise>
                            <form action="/user/authorisation" method="get">
                        </c:otherwise>
                      </c:choose>
                <input type="hidden" name="addOperation" value="add">
                <input type="hidden" name="id" value="${phone.id}">
                <input type="number" name="quantity" id="quantity${phone.id}" min="1" required>
                <button class="btn btn-lg btn-outline-light text-dark border-dark float-right" type="submit">Add to cart</button>
              </form>
              <c:if test="${not empty sessionScope.inputErrors.get(phone.id)}">
                <div class="error" style="color: red">${sessionScope.inputErrors[phone.id]}</div>
              </c:if>
            </td>
          </tr>
        </c:forEach>
      </table>
      <tags:pages page="${empty param.page or param.page lt 1 ? 1 : param.page}" lastPage="${numberOfPages}"/>
    </div>
  </div>
</tags:master>