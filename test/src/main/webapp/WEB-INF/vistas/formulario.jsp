<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="seccion" value="${riesgo.id > 0 ? 'matriz' : 'nuevo'}" scope="request"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${riesgo.id > 0 ? 'Editar riesgo' : 'Nuevo riesgo'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<%@include file="_cabecera.jspf"%>
<main class="contenido">
    <h1>
        <c:choose>
            <c:when test="${riesgo.id > 0}">Editar riesgo <c:out value="${riesgo.codigo}"/></c:when>
            <c:otherwise>Nuevo riesgo</c:otherwise>
        </c:choose>
    </h1>

    <c:if test="${not empty errores}">
        <ul class="errores">
            <c:forEach var="e" items="${errores}"><li><c:out value="${e}"/></li></c:forEach>
        </ul>
    </c:if>

    <form method="post" action="${ctx}/app/riesgos" class="form-riesgo" id="formRiesgo">
        <input type="hidden" name="accion" value="guardar">
        <input type="hidden" name="id" value="${riesgo.id}">
        <input type="hidden" name="codigo" value="<c:out value='${riesgo.codigo}'/>">

        <fieldset class="e1">
            <legend>Etapa 1 · Identificación del riesgo</legend>
            <div class="grid">
                <label>Macroproceso / Área *
                    <input name="area" required maxlength="200" value="<c:out value='${riesgo.area}'/>">
                </label>
                <label>Responsable
                    <input name="responsable" maxlength="200" value="<c:out value='${riesgo.responsable}'/>">
                </label>
                <label class="ancho2">Estado actual (realidad)
                    <textarea name="estadoActual" maxlength="2000" rows="2"><c:out value="${riesgo.estadoActual}"/></textarea>
                </label>
                <label class="ancho2">Causa raíz (qué causa el estado actual)
                    <textarea name="causaRaiz" maxlength="2000" rows="2"><c:out value="${riesgo.causaRaiz}"/></textarea>
                </label>
                <label class="ancho2">Riesgo identificado (evento futuro) *
                    <textarea name="riesgoIdentificado" required maxlength="2000" rows="2"><c:out value="${riesgo.riesgoIdentificado}"/></textarea>
                </label>
                <label class="ancho2">Naturaleza *
                    <select name="naturaleza" required id="naturaleza">
                        <option value="">-- Seleccione --</option>
                        <c:forEach var="n" items="${naturalezas}">
                            <option value="<c:out value='${n.key}'/>" title="<c:out value='${n.value}'/>"
                                    ${riesgo.naturaleza == n.key ? 'selected' : ''}><c:out value="${n.key}"/></option>
                        </c:forEach>
                    </select>
                    <small class="ayuda" id="ayudaNaturaleza"></small>
                </label>
            </div>
        </fieldset>

        <fieldset class="e2">
            <legend>Etapa 2 · Análisis del riesgo (análisis de brechas)</legend>
            <div class="grid">
                <label>Probabilidad (1-5) *
                    <select name="probabilidad" required id="probabilidad">
                        <option value="">-- Seleccione --</option>
                        <c:forEach var="p" items="${probabilidades}">
                            <option value="${p.valor}" ${riesgo.probabilidad == p.valor ? 'selected' : ''}>${p.valor} - <c:out value="${p.nivel}"/> (<c:out value="${p.descripcion}"/>)</option>
                        </c:forEach>
                    </select>
                </label>
                <label>Impacto (1-5) *
                    <select name="impacto" required id="impacto">
                        <option value="">-- Seleccione --</option>
                        <c:forEach var="p" items="${impactos}">
                            <option value="${p.valor}" ${riesgo.impacto == p.valor ? 'selected' : ''}>${p.valor} - <c:out value="${p.nivel}"/> (<c:out value="${p.descripcion}"/>)</option>
                        </c:forEach>
                    </select>
                </label>
                <label class="ancho2">Estado deseado (estándar / meta)
                    <textarea name="estadoDeseado" maxlength="2000" rows="2"><c:out value="${riesgo.estadoDeseado}"/></textarea>
                </label>
                <label class="ancho2">Brecha detectada (GAP)
                    <textarea name="brecha" maxlength="2000" rows="2"><c:out value="${riesgo.brecha}"/></textarea>
                </label>
                <label>Nivel de brecha *
                    <select name="nivelBrecha" required id="nivelBrecha">
                        <option value="">-- Seleccione --</option>
                        <c:forEach var="n" items="${nivelesBrecha}">
                            <option value="<c:out value='${n.key}'/>" title="<c:out value='${n.value}'/>"
                                    ${riesgo.nivelBrecha == n.key ? 'selected' : ''}><c:out value="${n.key}"/></option>
                        </c:forEach>
                    </select>
                    <small class="ayuda" id="ayudaBrecha"></small>
                </label>
            </div>
        </fieldset>

        <fieldset class="e3">
            <legend>Etapa 3 · Evaluación de riesgos</legend>
            <div class="grid">
                <label class="ancho2">Controles actuales
                    <textarea name="controlesActuales" maxlength="2000" rows="2"><c:out value="${riesgo.controlesActuales}"/></textarea>
                </label>
                <label>Frecuencia del control *
                    <select name="frecuenciaControl" required>
                        <option value="">-- Seleccione --</option>
                        <c:forEach var="f" items="${frecuencias}">
                            <option ${riesgo.frecuenciaControl == f ? 'selected' : ''}><c:out value="${f}"/></option>
                        </c:forEach>
                    </select>
                </label>
                <label>Efectividad del control *
                    <select name="efectividadControl" required id="efectividadControl">
                        <option value="">-- Seleccione --</option>
                        <c:forEach var="e" items="${efectividades}">
                            <option value="<c:out value='${e.key}'/>" ${riesgo.efectividadControl == e.key ? 'selected' : ''}><c:out value="${e.key}"/> (factor ${e.value})</option>
                        </c:forEach>
                    </select>
                </label>
            </div>
        </fieldset>

        <fieldset class="e4">
            <legend>Etapa 4 · Plan de tratamiento y monitoreo</legend>
            <div class="grid">
                <label>Estrategia *
                    <select name="estrategia" required>
                        <option value="">-- Seleccione --</option>
                        <c:forEach var="e" items="${estrategias}">
                            <option value="<c:out value='${e.key}'/>" title="<c:out value='${e.value}'/>"
                                    ${riesgo.estrategia == e.key ? 'selected' : ''}><c:out value="${e.key}"/></option>
                        </c:forEach>
                    </select>
                </label>
                <label>Estado *
                    <select name="estado" required>
                        <c:forEach var="e" items="${estados}">
                            <option ${riesgo.estado == e ? 'selected' : ''}><c:out value="${e}"/></option>
                        </c:forEach>
                    </select>
                </label>
                <label class="ancho2">Nombre del plan de acción (cierre de brecha)
                    <textarea name="planAccion" maxlength="2000" rows="2"><c:out value="${riesgo.planAccion}"/></textarea>
                </label>
                <label class="ancho2">Nuevos riesgos
                    <textarea name="nuevosRiesgos" maxlength="2000" rows="2"><c:out value="${riesgo.nuevosRiesgos}"/></textarea>
                </label>
            </div>
        </fieldset>

        <section class="calculo" aria-live="polite">
            <h2>Cálculo automático</h2>
            <div class="calc-grid">
                <div><span>Riesgo inherente (Prob × Imp)</span><strong id="cInherente">-</strong></div>
                <div><span>Factor multiplicador</span><strong id="cFactor">-</strong></div>
                <div><span>Riesgo residual</span><strong id="cResidual">-</strong></div>
                <div><span>Efectividad controles (PLAN)</span><strong id="cEfPlan">-</strong></div>
                <div><span>Factor de efectividad</span><strong id="cFactorPlan">-</strong></div>
                <div><span>Nuevo nivel de riesgo</span><strong id="cNuevo">-</strong></div>
                <div class="ancho2"><span>Tipo de control PLAN</span><strong id="cTipo">-</strong></div>
                <div><span>Tiempo para implementar</span><strong id="cTiempo">-</strong></div>
                <div><span>Frecuencia de monitoreo</span><strong id="cFrecuencia">-</strong></div>
            </div>
        </section>

        <div class="botones">
            <a class="boton secundario" href="${ctx}/app/riesgos">Cancelar</a>
            <button type="submit">Guardar</button>
        </div>
    </form>
</main>

<script>
(function () {
    var CAT = ${catalogosJson};
    var $ = function (id) { return document.getElementById(id); };
    function r2(v) { return Math.round(v * 100) / 100; }
    function fmt(v) { return String(r2(v)).replace('.', ','); }
    function nivel(v) {
        if (v >= 15.1) return ['Crítico', 'nv-critico'];
        if (v >= 10.1) return ['Alto', 'nv-alto'];
        if (v >= 5.1) return ['Medio', 'nv-medio'];
        return ['Bajo', 'nv-bajo'];
    }
    function banda(v) {
        var b = CAT.bandas[0];
        CAT.bandas.forEach(function (x) { if (v >= x.min) b = x; });
        return b;
    }
    function pintar(el, v) {
        var n = nivel(v);
        el.textContent = fmt(v) + ' · ' + n[0];
        el.className = n[1];
    }
    function texto(el, t) { el.textContent = t; el.className = ''; }
    function brechaCss(n) {
        return {'Crítica': 'nv-critico', 'Alta': 'nv-alto', 'Media': 'nv-medio', 'Baja': 'nv-bajo'}[n] || '';
    }
    function ayuda(select, destino) {
        var o = select.options[select.selectedIndex];
        destino.textContent = o && o.value ? o.title : '';
    }
    function calcular() {
        ayuda($('naturaleza'), $('ayudaNaturaleza'));
        ayuda($('nivelBrecha'), $('ayudaBrecha'));
        $('nivelBrecha').className = brechaCss($('nivelBrecha').value);

        var p = parseInt($('probabilidad').value, 10), i = parseInt($('impacto').value, 10);
        if (!p || !i) {
            ['cInherente', 'cFactor', 'cResidual', 'cEfPlan', 'cFactorPlan', 'cNuevo', 'cTipo', 'cTiempo', 'cFrecuencia']
                .forEach(function (id) { texto($(id), '-'); });
            return;
        }
        var inh = p * i;
        var f = CAT.factores[$('efectividadControl').value];
        if (f === undefined) f = 1;
        var res = r2(inh * f);
        var b = banda(res);
        var fp = CAT.factores[b.efectividad];
        if (fp === undefined) fp = 1;
        var nuevo = r2(res * fp);

        pintar($('cInherente'), inh);
        texto($('cFactor'), fmt(f));
        pintar($('cResidual'), res);
        texto($('cEfPlan'), b.efectividad);
        texto($('cFactorPlan'), fmt(fp));
        pintar($('cNuevo'), nuevo);
        texto($('cTipo'), b.tipo);
        texto($('cTiempo'), b.tiempo);
        texto($('cFrecuencia'), b.frecuencia);
    }
    $('formRiesgo').addEventListener('change', calcular);
    calcular();
})();
</script>
</body>
</html>
