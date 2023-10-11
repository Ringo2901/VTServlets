<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag isELIgnored="false" %>
<%@ attribute name="pageTitle" required="true" %>


<html>
<head>
    <title>${pageTitle}</title>
     <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Lobster&family=Lobster+Two:ital,wght@0,400;0,700;1,400;1,700&display=swap" rel="stylesheet">
</head>
<body>
<header>
    <div class="container bg-dark">
        <div class="row">
            <div class="col-6">
                <h1>
                    <a style="font-family: 'Lobster'" class="text-light" href="<c:url value="/"/>">
                        Phonify
                    </a>
                </h1>
            </div>
            <div class="col-6">
                <div class="float-right">
                    <form action="<c:url value="/cart"/>">
                        <button class="btn btn-light"> My Cart:
                            <span id="cartTotalCost"><c:out value="${cart.totalCost}"/></span>$
                        </button>
                    </form>
                </div>
            </div>
            <form action="<c:url value="/admin/orders"/>">
                <button class="btn btn-light"> Orders Page </button>
            </form>
            <form action="<c:url value="/admin/users"/>">
                <button class="btn btn-light"> Users Page </button>
            </form>
        </div>
    </div>

</header>
<main>
    <p></p>
    <jsp:doBody/>
</main>
</body>
</html>

