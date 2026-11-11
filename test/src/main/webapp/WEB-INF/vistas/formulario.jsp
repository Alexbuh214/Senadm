<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="seccion" value="${riesgo.id > 0 ? 'matriz' : 'nuevo'}" scope="request"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${riesgo.id > 0 ? 'Editar riesgo' : 'Nuevo riesgo'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css?v=${applicationScope.version}">
</head>
<body>
<%@include file="_cabecera.jspf"%>
<main class="contenido">
    <div class="titulo-acciones">
        <h1>
            <c:choose>
                <c:when test="${riesgo.id > 0}">Editar riesgo <c:out value="${riesgo.codigo}"/></c:when>
                <c:otherwise>Nuevo riesgo</c:otherwise>
            </c:choose>
        </h1>
        <button type="button" class="boton secundario chico oculto" id="btnModo">Ver todo el formulario</button>
    </div>

    <c:if test="${not empty errores}">
        <ul class="errores">
            <c:forEach var="e" items="${errores}"><li><c:out value="${e}"/></li></c:forEach>
        </ul>
    </c:if>

    <ol class="pasos oculto" id="pasos">
        <li data-paso="0"><span>1</span>Identificación</li>
        <li data-paso="1"><span>2</span>Análisis y brechas</li>
        <li data-paso="2"><span>3</span>Evaluación</li>
        <li data-paso="3"><span>4</span>Tratamiento</li>
        <li data-paso="4"><span>5</span>Revisión</li>
    </ol>

    <div class="asistente">
    <form method="post" action="${ctx}/app/riesgos" class="form-riesgo" id="formRiesgo">
        <input type="hidden" name="accion" value="guardar">
        <input type="hidden" name="id" value="${riesgo.id}">
        <input type="hidden" name="codigo" value="<c:out value='${riesgo.codigo}'/>">

        <fieldset class="e1 paso">
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

        <fieldset class="e2 paso">
            <legend>Etapa 2 · Análisis del riesgo (análisis de brechas)</legend>
            <div class="grid">
                <label>Probabilidad (1-5) *
                    <select name="probabilidad" required id="probabilidad">
                        <option value="">-- Seleccione --</option>
                        <c:forEach var="p" items="${probabilidades}">
                            <option value="${p.valor}" title="<c:out value='${p.descripcion}'/>" ${riesgo.probabilidad == p.valor ? 'selected' : ''}>${p.valor} - <c:out value="${p.nivel}"/> (<c:out value="${p.descripcion}"/>)</option>
                        </c:forEach>
                    </select>
                </label>
                <label>Impacto (1-5) *
                    <select name="impacto" required id="impacto">
                        <option value="">-- Seleccione --</option>
                        <c:forEach var="p" items="${impactos}">
                            <option value="${p.valor}" title="<c:out value='${p.descripcion}'/>" ${riesgo.impacto == p.valor ? 'selected' : ''}>${p.valor} - <c:out value="${p.nivel}"/> (<c:out value="${p.descripcion}"/>)</option>
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

        <fieldset class="e3 paso">
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
                            <option value="<c:out value='${e.key}'/>" title="<c:out value='${efectividadesDesc[e.key]}'/>"
                                    ${riesgo.efectividadControl == e.key ? 'selected' : ''}><c:out value="${e.key}"/> (factor <fmt:formatNumber value="${e.value}" maxFractionDigits="1"/>)</option>
                        </c:forEach>
                    </select>
                    <small class="ayuda" id="ayudaEfectividad"></small>
                </label>
            </div>
        </fieldset>

        <fieldset class="e4 paso">
            <legend>Etapa 4 · Plan de tratamiento y monitoreo</legend>
            <div class="grid">
                <label>Estrategia *
                    <select name="estrategia" required id="estrategia">
                        <option value="">-- Seleccione --</option>
                        <c:forEach var="e" items="${estrategias}">
                            <option value="<c:out value='${e.key}'/>" title="<c:out value='${e.value}'/>"
                                    ${riesgo.estrategia == e.key ? 'selected' : ''}><c:out value="${e.key}"/></option>
                        </c:forEach>
                    </select>
                    <small class="ayuda" id="ayudaEstrategia"></small>
                </label>
                <label>Estado del plan *
                    <select name="estado" required id="estado">
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

        <fieldset class="e5 paso">
            <legend>Revisión final</legend>
            <p class="intro-revision">Revise los datos antes de guardar. Puede volver a cualquier paso haciendo clic en él.</p>
            <dl class="resumen" id="resumen"></dl>
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
            <button type="button" class="boton secundario oculto" id="btnAnterior">&larr; Anterior</button>
            <button type="button" class="oculto" id="btnSiguiente">Siguiente &rarr;</button>
            <button type="submit" id="btnGuardar">Guardar</button>
        </div>
    </form>

    <aside class="guia oculto" id="guia" aria-live="polite">
        <div class="guia-paso">
            <span class="guia-etiqueta">Paso <b id="guiaNumero">1</b> de 5</span>
            <h2 id="guiaTitulo"></h2>
            <p id="guiaIntro"></p>
        </div>
        <div class="guia-campo" id="guiaCampo">
            <h3 id="guiaCampoTitulo">Consejo</h3>
            <p id="guiaCampoTexto">Haga clic en un campo para ver qué debe escribir y un ejemplo.</p>
            <p class="guia-ejemplo" id="guiaCampoEjemplo"></p>
        </div>
        <ul class="guia-alertas" id="guiaAlertas"></ul>
    </aside>
    </div>
</main>

<script>
(function () {
    var CAT = ${catalogosJson};
    var $ = function (id) { return document.getElementById(id); };
    var form = $('formRiesgo');
    var esNuevo = ${riesgo.id > 0 ? 'false' : 'true'};

    /* ------------------------------------------------------------------
       Cálculo automático (mismas fórmulas de la matriz FINAL)
       ------------------------------------------------------------------ */
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
    function ayudaOpcion(select, destino) {
        var o = select.options[select.selectedIndex];
        destino.textContent = o && o.value ? o.title : '';
    }

    var calc = null; // último resultado
    function calcular() {
        ayudaOpcion($('naturaleza'), $('ayudaNaturaleza'));
        ayudaOpcion($('nivelBrecha'), $('ayudaBrecha'));
        ayudaOpcion($('efectividadControl'), $('ayudaEfectividad'));
        ayudaOpcion($('estrategia'), $('ayudaEstrategia'));
        $('nivelBrecha').className = brechaCss($('nivelBrecha').value);

        var p = parseInt($('probabilidad').value, 10), i = parseInt($('impacto').value, 10);
        if (!p || !i) {
            ['cInherente', 'cFactor', 'cResidual', 'cEfPlan', 'cFactorPlan', 'cNuevo', 'cTipo', 'cTiempo', 'cFrecuencia']
                .forEach(function (id) { texto($(id), '-'); });
            calc = null;
        } else {
            var inh = p * i;
            var f = CAT.factores[$('efectividadControl').value];
            if (f === undefined) f = 1;
            var res = r2(inh * f);
            var b = banda(res);
            var fp = CAT.factores[b.efectividad];
            if (fp === undefined) fp = 1;
            var nuevo = r2(res * fp);
            calc = {inh: inh, res: res, nuevo: nuevo, banda: b};

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
        alertas();
        if (paso === 4) resumen();
    }

    /* ------------------------------------------------------------------
       Guía: textos de ayuda por paso y por campo
       ------------------------------------------------------------------ */
    var PASOS = [
        {t: 'Identificación del riesgo',
         i: 'Describa dónde está el riesgo, qué pasa hoy y qué podría llegar a pasar. Piense en un problema real del área.'},
        {t: 'Análisis del riesgo y brechas',
         i: 'Califique qué tan probable es y cuánto afectaría. Luego compare la realidad con el estándar que se debería cumplir.'},
        {t: 'Evaluación de controles',
         i: 'Indique qué se hace hoy para controlar el riesgo y qué tan bien funciona. El sistema calcula el riesgo residual.'},
        {t: 'Plan de tratamiento',
         i: 'Decida cómo tratar el riesgo y nombre el plan. El tipo de control, el tiempo y la frecuencia de monitoreo se calculan solos.'},
        {t: 'Revisión final',
         i: 'Verifique el resumen y los resultados calculados. Si todo está bien, pulse Guardar.'}
    ];

    var AYUDA = {
        area: ['Macroproceso / Área', 'Proceso o área de la empresa donde se encuentra el riesgo.',
            'Tecnología de Información'],
        responsable: ['Responsable', 'Persona o cargo encargado de gestionar este riesgo y su plan de acción.',
            'Jefe de Infraestructura TI'],
        estadoActual: ['Estado actual (realidad)', 'Describa cómo está la situación HOY, con hechos concretos. No escriba lo que debería ser, sino lo que es.',
            'Servidores locales sin cifrado de datos en reposo.'],
        causaRaiz: ['Causa raíz', 'Pregúntese "¿por qué ocurre el estado actual?" varias veces hasta llegar al origen del problema.',
            'No existe una política de cifrado ni presupuesto asignado para seguridad.'],
        riesgoIdentificado: ['Riesgo identificado', 'Evento FUTURO que podría ocurrir por el estado actual. Redáctelo como una consecuencia: "qué podría pasar".',
            'Fuga de información confidencial de clientes por accesos no autorizados.'],
        naturaleza: ['Naturaleza', 'Tipo de riesgo según su origen o lo que afecta. Al elegir una opción verá su definición debajo.',
            'Tecnológicos y de ciberseguridad y seguridad de la información'],
        probabilidad: ['Probabilidad (1-5)', '¿Qué tan seguido podría ocurrir? 1 = casi nunca, 5 = casi siempre. Multiplicada por el impacto da el riesgo inherente.',
            '4 - Alta (ocurre frecuentemente)'],
        impacto: ['Impacto (1-5)', '¿Cuánto afectaría si ocurre? 1 = una sola actividad, 5 = todo el sistema.',
            '5 - Muy alto (afecta a todo el sistema)'],
        estadoDeseado: ['Estado deseado', 'El estándar, norma o meta que se debería cumplir. Es el punto de comparación para medir la brecha.',
            'Implementación de cifrado AES-256 (Requisito ISO 27001).'],
        brecha: ['Brecha detectada (GAP)', 'La diferencia concreta entre el estado actual y el deseado. ¿Qué falta para llegar a la meta?',
            'Ausencia total de tecnologías y algoritmos de cifrado local.'],
        nivelBrecha: ['Nivel de brecha', 'Qué tan grande es la diferencia. Crítica = no hay controles o hay incumplimiento grave; Baja = casi alineado con el estándar.',
            'Crítica'],
        controlesActuales: ['Controles actuales', 'Lo que se hace HOY para prevenir o detectar el riesgo. Si no existe ninguno, escriba "Ninguno" y elija efectividad Ineficiente.',
            'Contraseña básica de red Windows.'],
        frecuenciaControl: ['Frecuencia del control', 'Cada cuánto se aplica el control actual.',
            'Automatizado / En tiempo real / Continuo'],
        efectividadControl: ['Efectividad del control', 'Qué tanto reduce el riesgo el control actual. Su factor multiplica el riesgo inherente: Muy eficiente (0,2) deja solo el 20 %, Ineficiente (1) no reduce nada.',
            'Eficiente (factor 0,4)'],
        estrategia: ['Estrategia', 'Cómo se va a tratar el riesgo: eliminar la causa (Evitar), reducirlo (Mitigar), trasladarlo (Compartir o transferir) o asumirlo (Aceptar).',
            'Mitigar'],
        estado: ['Estado del plan', 'Avance del plan de acción. Al crear un riesgo suele ser "Pendiente". Cámbielo a "Finalizado" cuando el plan se haya completado; también puede hacerlo desde la matriz.',
            'Pendiente'],
        planAccion: ['Plan de acción', 'Nombre corto y concreto de la acción que cerrará la brecha. Empiece con un verbo.',
            'Adquirir licencias e implementar software de cifrado corporativo.'],
        nuevosRiesgos: ['Nuevos riesgos', 'Riesgos que podrían aparecer al aplicar el plan de acción (opcional).',
            'Dependencia del proveedor de licencias de cifrado.']
    };

    function mostrarAyudaCampo(nombre) {
        var a = AYUDA[nombre];
        if (!a) return;
        $('guiaCampoTitulo').textContent = a[0];
        $('guiaCampoTexto').textContent = a[1];
        $('guiaCampoEjemplo').textContent = a[2] ? 'Ejemplo: ' + a[2] : '';
    }
    form.addEventListener('focusin', function (e) {
        if (e.target.name) mostrarAyudaCampo(e.target.name);
    });

    /* Alertas según lo que se va ingresando */
    function alertas() {
        var lista = [];
        var est = $('estrategia').value;
        var ef = $('efectividadControl').value;
        var controles = form.elements.controlesActuales.value.trim();
        if (calc) {
            var n = nivel(calc.res)[0];
            lista.push(['info', 'Riesgo residual ' + fmt(calc.res) + ' (' + n + '). La matriz exige: ' + calc.banda.tipo
                + ' Plazo: ' + calc.banda.tiempo + '. Monitoreo: ' + calc.banda.frecuencia + '.']);
            if (est === 'Aceptar' && calc.res >= 10.1) {
                lista.push(['alerta', 'Aceptar un riesgo residual ' + n.toLowerCase()
                    + ' no es recomendable: la estrategia Aceptar es para riesgos cuyo costo de mitigar supera el beneficio.']);
            }
            if (calc.inh >= 15.1 && $('nivelBrecha').value === 'Baja') {
                lista.push(['alerta', 'El riesgo inherente es crítico pero la brecha es Baja. Revise si el nivel de brecha es correcto.']);
            }
        }
        if ((ef === 'Muy eficiente' || ef === 'Eficiente') && !controles) {
            lista.push(['alerta', 'Eligió una efectividad "' + ef + '" pero no describió ningún control actual.']);
        }
        if (controles && /^ningun[oa]?\.?$/i.test(controles) && ef && ef !== 'Ineficiente') {
            lista.push(['alerta', 'Si no hay controles, la efectividad debería ser "Ineficiente".']);
        }
        if ($('estado').value === 'Finalizado' && !form.elements.planAccion.value.trim()) {
            lista.push(['alerta', 'El estado es Finalizado pero no hay un plan de acción registrado.']);
        }
        var ul = $('guiaAlertas');
        ul.innerHTML = '';
        lista.forEach(function (a) {
            var li = document.createElement('li');
            li.className = a[0];
            li.textContent = a[1];
            ul.appendChild(li);
        });
    }

    /* ------------------------------------------------------------------
       Asistente paso a paso
       ------------------------------------------------------------------ */
    var fieldsets = Array.prototype.slice.call(form.querySelectorAll('fieldset.paso'));
    var items = Array.prototype.slice.call($('pasos').querySelectorAll('li'));
    var guiado = esNuevo;
    var paso = 0;

    function pasoCompleto(fs) {
        var campos = fs.querySelectorAll('input, select, textarea');
        for (var k = 0; k < campos.length; k++) {
            if (!campos[k].checkValidity()) return false;
        }
        return true;
    }

    /* Muestra el paso (si está oculto) y señala el primer campo inválido. */
    function revisarPaso(k) {
        if (pasoCompleto(fieldsets[k])) return true;
        if (guiado && paso !== k) irA(k);
        campoValido(fieldsets[k]);
        return false;
    }

    function campoValido(fs) {
        var campos = fs.querySelectorAll('input, select, textarea');
        for (var k = 0; k < campos.length; k++) {
            if (!campos[k].checkValidity()) {
                campos[k].reportValidity();
                campos[k].focus();
                return false;
            }
        }
        return true;
    }

    function resumen() {
        var dl = $('resumen');
        dl.innerHTML = '';
        fieldsets.slice(0, 4).forEach(function (fs) {
            var h = document.createElement('dt');
            h.className = 'resumen-etapa';
            h.textContent = fs.querySelector('legend').textContent;
            dl.appendChild(h);
            fs.querySelectorAll('label').forEach(function (lab) {
                var c = lab.querySelector('input, select, textarea');
                if (!c) return;
                var v = c.tagName === 'SELECT'
                    ? (c.value ? c.options[c.selectedIndex].text : '')
                    : c.value.trim();
                var dt = document.createElement('dt');
                dt.textContent = lab.firstChild.textContent.replace('*', '').trim();
                var dd = document.createElement('dd');
                dd.textContent = v || '—';
                if (!v) dd.className = 'vacio';
                dl.appendChild(dt);
                dl.appendChild(dd);
            });
        });
    }

    function irA(n) {
        paso = Math.max(0, Math.min(fieldsets.length - 1, n));
        fieldsets.forEach(function (fs, k) { fs.classList.toggle('activo', k === paso); });
        items.forEach(function (li, k) {
            li.classList.toggle('actual', k === paso);
            li.classList.toggle('hecho', k < paso);
        });
        $('guiaNumero').textContent = paso + 1;
        $('guiaTitulo').textContent = PASOS[paso].t;
        $('guiaIntro').textContent = PASOS[paso].i;
        $('btnAnterior').classList.toggle('oculto', paso === 0);
        $('btnSiguiente').classList.toggle('oculto', paso === fieldsets.length - 1);
        $('btnGuardar').classList.toggle('oculto', paso !== fieldsets.length - 1);
        if (paso === 4) resumen();
        var primero = fieldsets[paso].querySelector('input:not([type=hidden]), select, textarea');
        if (primero) {
            mostrarAyudaCampo(primero.name);
        } else {
            $('guiaCampoTitulo').textContent = 'Todo listo';
            $('guiaCampoTexto').textContent = 'Si algo no está bien, vuelva al paso correspondiente y corríjalo.';
            $('guiaCampoEjemplo').textContent = '';
        }
        window.scrollTo({top: 0, behavior: 'smooth'});
    }

    function aplicarModo() {
        document.body.classList.toggle('modo-guiado', guiado);
        $('btnModo').textContent = guiado ? 'Ver todo el formulario' : 'Usar el asistente paso a paso';
        if (guiado) {
            irA(paso);
        } else {
            fieldsets.forEach(function (fs) { fs.classList.remove('activo'); });
            ['btnAnterior', 'btnSiguiente'].forEach(function (id) { $(id).classList.add('oculto'); });
            $('btnGuardar').classList.remove('oculto');
        }
    }

    $('btnSiguiente').addEventListener('click', function () {
        if (revisarPaso(paso)) irA(paso + 1);
    });
    $('btnAnterior').addEventListener('click', function () { irA(paso - 1); });
    items.forEach(function (li, k) {
        li.addEventListener('click', function () {
            if (!guiado) return;
            // Se puede retroceder libremente; para avanzar, los pasos intermedios deben estar completos.
            for (var j = Math.min(paso, k); j < k; j++) {
                if (!revisarPaso(j)) return;
            }
            irA(k);
        });
    });
    $('btnModo').addEventListener('click', function () { guiado = !guiado; aplicarModo(); });

    // La validación la hace el asistente para poder llevar al paso donde falta un dato
    form.noValidate = true;
    form.addEventListener('submit', function (e) {
        for (var k = 0; k < 4; k++) {
            if (!revisarPaso(k)) {
                e.preventDefault();
                return;
            }
        }
    });

    ['pasos', 'guia', 'btnModo'].forEach(function (id) { $(id).classList.remove('oculto'); });
    form.addEventListener('change', calcular);
    form.addEventListener('input', function (e) { if (e.target.tagName === 'TEXTAREA') alertas(); });
    calcular();
    aplicarModo();
})();
</script>
</body>
</html>
