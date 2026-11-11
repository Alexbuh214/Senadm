<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Iniciar sesión</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=${applicationScope.version}">
</head>
<body class="centrado">
    <form class="tarjeta" method="post" action="${pageContext.request.contextPath}/login">
        <h1>Iniciar sesión</h1>

        <c:if test="${not empty error}">
            <p class="error"><c:out value="${error}"/></p>
        </c:if>

        <label for="usuario">Usuario</label>
        <input id="usuario" name="usuario" type="text" required autofocus
               value="<c:out value='${param.usuario}'/>">

        <label for="clave">Contraseña</label>
        <input id="clave" name="clave" type="password" required>

        <button type="submit">Entrar</button>
    </form>
</body>
</html>
