<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="seccion" value="param" scope="request"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Parametrización</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<%@include file="_cabecera.jspf"%>
<main class="contenido">
    <h1>Tablas de parametrización y data maestra</h1>

    <div class="param-grid">
        <section class="panel">
            <h2>Naturaleza del riesgo</h2>
            <table class="tabla">
                <thead><tr><th>Naturaleza</th><th>Relación / definición</th></tr></thead>
                <tbody>
                <c:forEach var="n" items="${naturalezas}">
                    <tr><td><c:out value="${n.key}"/></td><td><c:out value="${n.value}"/></td></tr>
                </c:forEach>
                </tbody>
            </table>
        </section>

        <section class="panel">
            <h2>Probabilidad</h2>
            <table class="tabla">
                <thead><tr><th>Valor</th><th>Nivel</th><th>Descripción</th></tr></thead>
                <tbody>
                <c:forEach var="p" items="${probabilidades}">
                    <tr><td class="num">${p.valor}</td><td><c:out value="${p.nivel}"/></td><td><c:out value="${p.descripcion}"/></td></tr>
                </c:forEach>
                </tbody>
            </table>
            <h2>Impacto</h2>
            <table class="tabla">
                <thead><tr><th>Valor</th><th>Nivel</th><th>Descripción</th></tr></thead>
                <tbody>
                <c:forEach var="p" items="${impactos}">
                    <tr><td class="num">${p.valor}</td><td><c:out value="${p.nivel}"/></td><td><c:out value="${p.descripcion}"/></td></tr>
                </c:forEach>
                </tbody>
            </table>
        </section>

        <section class="panel">
            <h2>Nivel de criticidad (riesgo inherente / residual)</h2>
            <table class="tabla">
                <thead><tr><th>Puntaje</th><th>Clasificación</th></tr></thead>
                <tbody>
                <tr><td>1 a 5</td><td class="nv-bajo">Bajo</td></tr>
                <tr><td>5,1 a 10</td><td class="nv-medio">Medio</td></tr>
                <tr><td>10,1 a 15</td><td class="nv-alto">Alto</td></tr>
                <tr><td>15,1 a 25</td><td class="nv-critico">Crítico</td></tr>
                </tbody>
            </table>
            <h2>Nivel de brecha</h2>
            <table class="tabla">
                <thead><tr><th>Nivel</th><th>Significado operativo</th></tr></thead>
                <tbody>
                <c:forEach var="n" items="${nivelesBrecha}">
                    <tr>
                        <td class="${n.key == 'Crítica' ? 'nv-critico' : (n.key == 'Alta' ? 'nv-alto' : (n.key == 'Media' ? 'nv-medio' : 'nv-bajo'))}"><c:out value="${n.key}"/></td>
                        <td><c:out value="${n.value}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </section>

        <section class="panel">
            <h2>Efectividad del control</h2>
            <table class="tabla">
                <thead><tr><th>Calificación</th><th>Factor</th><th>Explicación técnica</th></tr></thead>
                <tbody>
                <c:forEach var="e" items="${efectividades}">
                    <tr><td><c:out value="${e.key}"/></td><td class="num">${e.value}</td><td><c:out value="${efectividadesDesc[e.key]}"/></td></tr>
                </c:forEach>
                </tbody>
            </table>
            <h2>Estrategia</h2>
            <table class="tabla">
                <thead><tr><th>Estrategia</th><th>Definición</th></tr></thead>
                <tbody>
                <c:forEach var="e" items="${estrategias}">
                    <tr><td><c:out value="${e.key}"/></td><td><c:out value="${e.value}"/></td></tr>
                </c:forEach>
                </tbody>
            </table>
            <h2>Frecuencia de ejecución / monitoreo</h2>
            <table class="tabla">
                <tbody>
                <c:forEach var="f" items="${frecuencias}"><tr><td><c:out value="${f}"/></td></tr></c:forEach>
                </tbody>
            </table>
        </section>
    </div>

    <section class="panel">
        <h2>Tipo y frecuencia del control según el riesgo residual</h2>
        <div class="tabla-scroll">
        <table class="tabla">
            <thead><tr><th>Rango mín.</th><th>Tipo de control PLAN</th><th>Tiempo</th><th>Efectividad PLAN</th><th>Frecuencia monitoreo</th></tr></thead>
            <tbody>
            <c:forEach var="b" items="${bandas}">
                <tr>
                    <td class="num">${b.minimo}</td>
                    <td><c:out value="${b.tipoControl}"/></td>
                    <td><c:out value="${b.tiempo}"/></td>
                    <td><c:out value="${b.efectividadPlan}"/></td>
                    <td><c:out value="${b.frecuenciaMonitoreo}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        </div>
    </section>
</main>
</body>
</html>
