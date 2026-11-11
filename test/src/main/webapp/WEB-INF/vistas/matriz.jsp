<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="seccion" value="matriz" scope="request"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Matriz Operativa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=${applicationScope.version}">
</head>
<body>
<%@include file="_cabecera.jspf"%>
<main class="contenido ancho">
    <div class="titulo-acciones">
        <h1>Matriz de gestión de riesgos integrada con análisis de brechas</h1>
        <a class="boton" href="${ctx}/app/riesgos?accion=nuevo">+ Nuevo riesgo</a>
    </div>

    <form class="filtros" method="get" action="${ctx}/app/riesgos">
        <label>Ordenar por
            <select name="orden" onchange="this.form.submit()">
                <option value="id" ${orden == 'id' ? 'selected' : ''}>ID</option>
                <option value="inherente" ${orden == 'inherente' ? 'selected' : ''}>Riesgo inherente priorizado</option>
                <option value="residual" ${orden == 'residual' ? 'selected' : ''}>Riesgo residual priorizado</option>
            </select>
        </label>
        <label>Estado
            <select name="estado" onchange="this.form.submit()">
                <option value="">Todos</option>
                <c:forEach var="e" items="${estados}">
                    <option ${filtroEstado == e ? 'selected' : ''}><c:out value="${e}"/></option>
                </c:forEach>
            </select>
        </label>
        <noscript><button type="submit">Aplicar</button></noscript>
    </form>

    <div class="tabla-scroll matriz-scroll">
    <table class="tabla matriz">
        <thead>
        <tr class="etapas">
            <th colspan="7" class="e1">ETAPA 1 · IDENTIFICACIÓN DEL RIESGO</th>
            <th colspan="7" class="e2">ETAPA 2 · ANÁLISIS DEL RIESGO (ANÁLISIS DE BRECHAS)</th>
            <th colspan="7" class="e3">ETAPA 3 · EVALUACIÓN DE RIESGOS</th>
            <th colspan="13" class="e4">ETAPA 4 · PLAN DE TRATAMIENTO DE RIESGOS Y MONITOREO</th>
            <th rowspan="2" class="acciones-col">Acciones</th>
        </tr>
        <tr>
            <th>ID</th><th>Macroproceso / Área</th><th>Responsable</th><th>Estado actual (realidad)</th>
            <th>Causa raíz</th><th>Riesgo identificado</th><th>Naturaleza</th>
            <th>Prob. (1-5)</th><th>Imp. (1-5)</th><th>Nivel de criticidad / Riesgo inherente</th>
            <th>Estado actual (realidad)</th><th>Estado deseado</th><th>Brecha detectada (GAP)</th><th>Nivel de brecha</th>
            <th>Riesgo inherente priorizado</th><th>Controles actuales</th><th>Frecuencia del control</th>
            <th>Efectividad control</th><th>Factor multiplicador</th><th>Nivel de criticidad (Riesgo residual)</th>
            <th>Riesgo residual priorizado</th>
            <th>Estrategia</th><th>Nombre del plan de acción</th><th>Tipo de control PLAN</th>
            <th>Tiempo para implementar acción</th><th>Efectividad controles (PLAN)</th><th>Factor de efectividad</th>
            <th>Nuevo nivel de riesgo</th><th>Frecuencia de ejecución / monitoreo del control</th>
            <th>R. inicial / criticidad</th><th>R. residual</th><th>R. actual</th><th>Nuevos riesgos</th><th>Estado</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="r" items="${riesgos}">
            <tr>
                <td class="codigo"><c:out value="${r.codigo}"/></td>
                <td><c:out value="${r.area}"/></td>
                <td><c:out value="${r.responsable}"/></td>
                <td class="largo"><c:out value="${r.estadoActual}"/></td>
                <td class="largo"><c:out value="${r.causaRaiz}"/></td>
                <td class="largo"><c:out value="${r.riesgoIdentificado}"/></td>
                <td><c:out value="${r.naturaleza}"/></td>

                <td class="num">${r.probabilidad}</td>
                <td class="num">${r.impacto}</td>
                <td class="num ${r.cssInherente}"><fmt:formatNumber value="${r.riesgoInherente}" maxFractionDigits="2"/><small><c:out value="${r.clasifInherente}"/></small></td>
                <td class="largo"><c:out value="${r.estadoActual}"/></td>
                <td class="largo"><c:out value="${r.estadoDeseado}"/></td>
                <td class="largo"><c:out value="${r.brecha}"/></td>
                <td class="${r.cssBrecha}"><c:out value="${r.nivelBrecha}"/></td>

                <td class="num prioridad">${r.prioridadInherente}</td>
                <td class="largo"><c:out value="${r.controlesActuales}"/></td>
                <td><c:out value="${r.frecuenciaControl}"/></td>
                <td><c:out value="${r.efectividadControl}"/></td>
                <td class="num"><fmt:formatNumber value="${r.factorControl}" maxFractionDigits="2"/></td>
                <td class="num ${r.cssResidual}"><fmt:formatNumber value="${r.riesgoResidual}" maxFractionDigits="2"/><small><c:out value="${r.clasifResidual}"/></small></td>
                <td class="num prioridad">${r.prioridadResidual}</td>

                <td><c:out value="${r.estrategia}"/></td>
                <td class="largo"><c:out value="${r.planAccion}"/></td>
                <td class="largo"><c:out value="${r.tipoControlPlan}"/></td>
                <td><c:out value="${r.tiempoImplementacion}"/></td>
                <td><c:out value="${r.efectividadPlan}"/></td>
                <td class="num"><fmt:formatNumber value="${r.factorPlan}" maxFractionDigits="2"/></td>
                <td class="num ${r.cssNuevo}"><fmt:formatNumber value="${r.nuevoNivelRiesgo}" maxFractionDigits="2"/><small><c:out value="${r.clasifNuevo}"/></small></td>
                <td><c:out value="${r.frecuenciaMonitoreo}"/></td>
                <td class="num ${r.cssInherente}"><fmt:formatNumber value="${r.riesgoInherente}" maxFractionDigits="2"/></td>
                <td class="num ${r.cssResidual}"><fmt:formatNumber value="${r.riesgoResidual}" maxFractionDigits="2"/></td>
                <td class="num ${r.cssNuevo}"><fmt:formatNumber value="${r.nuevoNivelRiesgo}" maxFractionDigits="2"/></td>
                <td class="largo"><c:out value="${r.nuevosRiesgos}"/></td>
                <td>
                    <form method="post" action="${ctx}/app/riesgos" class="form-estado">
                        <input type="hidden" name="accion" value="estado">
                        <input type="hidden" name="id" value="${r.id}">
                        <input type="hidden" name="orden" value="<c:out value='${orden}'/>">
                        <input type="hidden" name="filtroEstado" value="<c:out value='${filtroEstado}'/>">
                        <select name="estado" aria-label="Estado de ${r.codigo}" onchange="this.form.submit()"
                                class="estado-${r.estado == 'Finalizado' ? 'fin' : (r.estado == 'En Proceso' ? 'proceso' : 'pend')}">
                            <c:forEach var="e" items="${estados}">
                                <option ${r.estado == e ? 'selected' : ''}><c:out value="${e}"/></option>
                            </c:forEach>
                        </select>
                        <noscript><button type="submit" class="boton chico">OK</button></noscript>
                    </form>
                </td>

                <td class="acciones">
                    <a class="boton chico" href="${ctx}/app/riesgos?accion=editar&id=${r.id}">Editar</a>
                    <form method="post" action="${ctx}/app/riesgos"
                          onsubmit="return confirm('¿Eliminar el riesgo ${r.codigo}? Esta acción no se puede deshacer.');">
                        <input type="hidden" name="accion" value="eliminar">
                        <input type="hidden" name="id" value="${r.id}">
                        <button type="submit" class="boton chico peligro">Eliminar</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty riesgos}">
            <tr><td colspan="35" class="vacio">No hay riesgos para mostrar.</td></tr>
        </c:if>
        </tbody>
    </table>
    </div>
</main>
</body>
</html>
