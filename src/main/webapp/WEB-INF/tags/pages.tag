<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag isELIgnored="false" %>
<%@ attribute name="page" required="true" %>
<%@ attribute name="lastPage" required="true" %>

<div class="container">
    <form class="float-right">
        <div class="btn-group">
            <button class="btn btn-outline-secondary" type="submit" name="page" ${page eq 1 ? 'disabled="true"' : ''}
                    value="1"><<</button>
            <button class="btn btn-outline-secondary" type="submit" name="page" ${page eq 1 ? 'disabled="true"' : ''}
                    value="${(empty param.page ? 1 : param.page) - 1}"><-</button>

            <c:if test="${lastPage le 7}">
                <c:forEach var="p" begin="1" end="${lastPage}">
                    <button class="btn btn-outline-secondary ${page eq p ? 'active' : ''}"
                            type="submit" name="page" value="${p}">${p}</button>
                </c:forEach>
            </c:if>

            <c:if test="${lastPage gt 7}">
                <button class="btn btn-outline-secondary ${page eq 1 ? 'active' : ''}"
                        type="submit" name="page" value="1">1</button>
                <c:choose>
                    <c:when test="${page le 3}">
                        <c:forEach var="p" begin="2" end="4">
                            <button class="btn btn-outline-secondary ${page eq p ? 'active' : ''}"
                                    type="submit" name="page" value="${p}">${p}</button>
                        </c:forEach>
                        <button class="btn btn-outline-secondary" disabled="true">...</button>
                    </c:when>
                    <c:when test="${page gt 3 and page lt lastPage - 2}">
                        <button class="btn btn-outline-secondary" disabled="true">...</button>
                        <c:forEach var="p" begin="${page - 1}" end="${page + 1}">
                            <button class="btn btn-outline-secondary ${page eq p ? 'active' : ''}"
                                    type="submit" name="page" value="${p}">${p}</button>
                        </c:forEach>
                        <button class="btn btn-outline-secondary" disabled="true">...</button>
                    </c:when>
                    <c:when test="${page ge lastPage - 2}">
                        <button class="btn btn-outline-secondary" disabled="true">...</button>
                        <c:forEach var="p" begin="${lastPage - 3}" end="${lastPage -1}">
                            <button class="btn btn-outline-secondary ${page eq p ? 'active' : ''}"
                                    type="submit" name="page" value="${p}">${p}</button>
                        </c:forEach>
                    </c:when>
                </c:choose>
                <button class="btn btn-outline-secondary ${page eq lastPage ? 'active' : ''}"
                        type="submit" name="page" value="${lastPage}">${lastPage}</button>
            </c:if>

            <button class="btn btn-outline-secondary" type="submit" name="page" ${page eq lastPage ? 'disabled="true"' : ''}
                    value="${(empty param.page ? 1 : param.page) + 1}">-></button>
            <button class="btn btn-outline-secondary" type="submit" name="page" ${page eq lastPage ? 'disabled="true"' : ''}
                    value="${lastPage}">>></button>
        </div>
        <input type="hidden" name="command" value="Product_List">
        <c:if test="${not empty param.sort}"><input type="hidden" name="sort" value="${param.sort}"></c:if>
        <c:if test="${not empty param.order}"><input type="hidden" name="order" value="${param.order}"></c:if>
        <c:if test="${not empty param.query}"><input type="hidden" name="query" value="${param.query}"></c:if>
    </form>
</div>