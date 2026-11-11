<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="seccion" value="dashboard" scope="request"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dashboard Ejecutivo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=${applicationScope.version}">
</head>
<body>
<%@include file="_cabecera.jspf"%>
<main class="contenido">
    <h1>Dashboard de control ejecutivo - Matriz GAP</h1>

    <section class="kpis">
        <div class="kpi"><span>Total riesgos</span><strong>${total}</strong></div>
        <div class="kpi critico"><span>Riesgos críticos (inherente &ge; 15,1)</span><strong>${criticos}</strong></div>
        <div class="kpi"><span>Críticos después de controles</span><strong>${criticosResiduales}</strong></div>
        <c:forEach var="e" items="${porEstado}">
            <div class="kpi"><span><c:out value="${e.key}"/></span><strong>${e.value}</strong></div>
        </c:forEach>
    </section>

    <section class="panel">
        <h2>Resumen de evolución del nivel de riesgo</h2>
        <div class="tabla-scroll">
        <table class="tabla">
            <thead>
            <tr>
                <th>Riesgo ID</th><th>Proceso</th><th>Riesgo inherente</th><th>Riesgo residual</th>
                <th>Nuevo riesgo</th><th>Comparativo</th><th>Estado</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="r" items="${riesgos}">
                <tr>
                    <td><a href="${ctx}/app/riesgos?accion=editar&id=${r.id}"><c:out value="${r.codigo}"/></a></td>
                    <td><c:out value="${r.area}"/></td>
                    <td class="num ${r.cssInherente}"><fmt:formatNumber value="${r.riesgoInherente}" maxFractionDigits="2"/></td>
                    <td class="num ${r.cssResidual}"><fmt:formatNumber value="${r.riesgoResidual}" maxFractionDigits="2"/></td>
                    <td class="num ${r.cssNuevo}"><fmt:formatNumber value="${r.nuevoNivelRiesgo}" maxFractionDigits="2"/></td>
                    <td class="barras">
                        <div class="barra-g ${r.cssInherente}" style="width:${r.riesgoInherente * 100 / escalaMax}%" title="Inicial"></div>
                        <div class="barra-g ${r.cssResidual}" style="width:${r.riesgoResidual * 100 / escalaMax}%" title="Residual"></div>
                        <div class="barra-g ${r.cssNuevo}" style="width:${r.nuevoNivelRiesgo * 100 / escalaMax}%" title="Actual"></div>
                    </td>
                    <td><c:out value="${r.estado}"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty riesgos}">
                <tr><td colspan="7" class="vacio">No hay riesgos registrados.</td></tr>
            </c:if>
            </tbody>
        </table>
        </div>
        <p class="leyenda">
            Barras: inicial, residual y actual (escala 0-25).
            <span class="chip nv-bajo">Bajo 1-5</span>
            <span class="chip nv-medio">Medio 5,1-10</span>
            <span class="chip nv-alto">Alto 10,1-15</span>
            <span class="chip nv-critico">Crítico 15,1-25</span>
        </p>
    </section>
</main>
</body>
</html>
