<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Inicio</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body class="centrado">
    <div class="tarjeta">
        <h1>Bienvenido, <c:out value="${sessionScope.usuario}"/></h1>
        <p>Login correcto. Aquí irá el CRUD.</p>
        <a class="boton" href="${pageContext.request.contextPath}/logout">Cerrar sesión</a>
    </div>
</body>
</html>
