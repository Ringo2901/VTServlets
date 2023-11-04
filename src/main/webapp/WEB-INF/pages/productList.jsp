<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false"%>

<jsp:useBean id="phones" scope="request" type="java.util.List"/>
<jsp:useBean id="numberOfPages" scope="request" type="java.lang.Long"/>

<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="messages"/>
<tags:master pageTitle="Phohe List">
  <fmt:setLocale value="${sessionScope.lang}"/>
  <fmt:setBundle basename="messages"/>

  <c:choose>
    <c:when test="${not empty param.inputErrors}">
      <div class="container">
        <div class="panel panel-danger">
          <div class="panel-heading"><fmt:message key="error_title" /></div>
          <div class="panel-body">
            <fmt:message key="error_updating_cart" />
            ${param.inputErrors}
          </div>
        </div>
      </div>
    </c:when>
    <c:otherwise>
      <c:if test="${not empty param.successMessage}">
        <div class="container">
          <div class="panel panel-success">
            <div class="panel-heading"><fmt:message key="success_title" /></div>
            <div class="panel-body">${param.successMessage}</div>
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
          <input type="hidden" name="command" value="Product_List">
          <button class="btn btn-light"><fmt:message key="button_search" /></button>
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
          <td><fmt:message key="phone_image" /></td>
          <td>
            <fmt:message key="item_brand" />
            <tags:sortLink sort="brand" order="asc"/><tags:sortLink sort="brand" order="desc"/>
          </td>
          <td>
            <fmt:message key="item_model" />
            <tags:sortLink sort="model" order="asc"/><tags:sortLink sort="model" order="desc"/>
          </td>
          <td><fmt:message key="item_Color" /></td>
          <td>
            <fmt:message key="item_Display_Size" />
            <tags:sortLink sort="displaySizeInches" order="asc"/>
            <tags:sortLink sort="displaySizeInches" order="desc"/>
          </td>
          <td>
            <fmt:message key="item.price" />
            <tags:sortLink sort="price" order="asc"/>
            <tags:sortLink sort="price" order="desc"/>
          </td>
          <td><fmt:message key="table_action" /></td>
        </tr>
        </thead>
        <c:forEach var="phone" items="${phones}">
          <tr>
            <td class="align-middle">
              <img class="rounded" src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${phone.imageUrl}">
            </td>
            <td class="align-middle">
              <a href="<c:url value="/?command=product_details&phone_id=${phone.id}"/>">${phone.brand}</a>
            </td>
            <td class="align-middle">
              <a href="<c:url value="/?command=product_details&phone_id=${phone.id}"/>">${phone.model}</a>
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
                            <form action="/" method="post">
                              <input type="hidden" name="command" value="cart_add">
                        </c:when>
                        <c:otherwise>
                            <form action="/" method="get">
                              <input type="hidden" name="command" value="authorisation">
                        </c:otherwise>
                      </c:choose>
                <input type="hidden" name="id" value="${phone.id}">
                <input type="number" name="quantity" id="quantity${phone.id}" min="1" required>
                <button class="btn btn-lg btn-outline-light text-dark border-dark float-right" type="submit" style="font-size: 14px"><fmt:message key="button_add" /></button>
              </form>
            </td>
          </tr>
        </c:forEach>
      </table>
      <tags:pages page="${empty param.page or param.page lt 1 ? 1 : param.page}" lastPage="${numberOfPages}"/>
    </div>
  </div>
</tags:master>