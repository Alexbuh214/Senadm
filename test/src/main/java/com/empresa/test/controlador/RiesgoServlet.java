package com.empresa.test.controlador;

import com.empresa.test.dao.RiesgoDAO;
import com.empresa.test.modelo.Catalogos;
import com.empresa.test.modelo.Riesgo;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CRUD de la Matriz Operativa.
 * GET  /app/riesgos                 -> matriz (orden=id|inherente|residual, estado=..., area=...)
 * GET  /app/riesgos?accion=excel    -> descarga .xlsx con los mismos filtros
 * GET  /app/riesgos?accion=nuevo    -> formulario vacío
 * GET  /app/riesgos?accion=editar&id=N
 * POST /app/riesgos accion=guardar  -> crea o actualiza
 * POST /app/riesgos accion=eliminar -> elimina
 */
@WebServlet(name = "RiesgoServlet", urlPatterns = {"/app/riesgos"})
public class RiesgoServlet extends HttpServlet {

    private final RiesgoDAO dao = new RiesgoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = valor(request.getParameter("accion"));
        try {
            if ("nuevo".equals(accion)) {
                Riesgo r = new Riesgo();
                r.setEstado("Pendiente");
                mostrarFormulario(request, response, r, null);
            } else if ("editar".equals(accion)) {
                Riesgo r = dao.buscar(entero(request.getParameter("id"), -1));
                if (r == null) {
                    mensaje(request, "El riesgo no existe.");
                    response.sendRedirect(request.getContextPath() + "/app/riesgos");
                    return;
                }
                mostrarFormulario(request, response, r, null);
            } else if ("excel".equals(accion)) {
                exportar(request, response);
            } else {
                listar(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String accion = valor(request.getParameter("accion"));
        try {
            if ("eliminar".equals(accion)) {
                int id = entero(request.getParameter("id"), -1);
                Riesgo r = dao.buscar(id);
                if (r != null && dao.eliminar(id)) {
                    mensaje(request, "Riesgo " + r.getCodigo() + " eliminado.");
                }
                response.sendRedirect(request.getContextPath() + "/app/riesgos");
            } else if ("estado".equals(accion)) {
                int id = entero(request.getParameter("id"), -1);
                String estado = valor(request.getParameter("estado"));
                Riesgo r = dao.buscar(id);
                if (r == null) {
                    mensaje(request, "El riesgo ya no existe.");
                } else if (!Catalogos.ESTADOS.contains(estado)) {
                    mensaje(request, "Estado no válido.");
                } else if (dao.cambiarEstado(id, estado)) {
                    mensaje(request, "Riesgo " + r.getCodigo() + " marcado como " + estado + ".");
                }
                response.sendRedirect(request.getContextPath() + "/app/riesgos" + volverA(request));
            } else if ("guardar".equals(accion)) {
                Riesgo r = leerFormulario(request);
                // Un riesgo antiguo puede tener un área que ya no está en la lista: se le permite conservarla.
                Riesgo anterior = r.getId() > 0 ? dao.buscar(r.getId()) : null;
                List<String> errores = validar(r, anterior == null ? null : anterior.getArea());
                if (!errores.isEmpty()) {
                    mostrarFormulario(request, response, r, errores);
                    return;
                }
                if (r.getId() > 0) {
                    Riesgo actual = dao.buscar(r.getId());
                    if (actual == null || !dao.actualizar(r)) {
                        mensaje(request, "El riesgo ya no existe.");
                    } else {
                        mensaje(request, "Riesgo " + actual.getCodigo() + " actualizado.");
                    }
                } else {
                    dao.insertar(r);
                    mensaje(request, "Riesgo " + r.getCodigo() + " creado.");
                }
                response.sendRedirect(request.getContextPath() + "/app/riesgos");
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Riesgo> todos = dao.listar();
        String orden = ordenValido(request.getParameter("orden"));
        String estado = valor(request.getParameter("estado"));
        String area = valor(request.getParameter("area"));
        request.setAttribute("riesgos", filtrar(todos, orden, estado, area));
        request.setAttribute("orden", orden);
        request.setAttribute("filtroEstado", estado);
        request.setAttribute("filtroArea", area);
        request.setAttribute("areasFiltro", areasFiltro(todos));
        request.getRequestDispatcher("/WEB-INF/vistas/matriz.jsp").forward(request, response);
    }

    /** Descarga la matriz en Excel respetando los filtros activos (área, estado y orden). */
    private void exportar(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String orden = ordenValido(request.getParameter("orden"));
        String estado = valor(request.getParameter("estado"));
        String area = valor(request.getParameter("area"));
        List<Riesgo> lista = filtrar(dao.listar(), orden, estado, area);

        String nombre = "Matriz_Riesgos_" + (area.isEmpty() ? "General" : slug(area))
                + (estado.isEmpty() ? "" : "_" + slug(estado)) + "_" + java.time.LocalDate.now() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + "\"; filename*=UTF-8''"
                + java.net.URLEncoder.encode(nombre, "UTF-8").replace("+", "%20"));
        MatrizExcel.escribir(lista, area, estado, response.getOutputStream());
    }

    private static List<Riesgo> filtrar(List<Riesgo> lista, String orden, String estado, String area) {
        List<Riesgo> r = lista.stream()
                .filter(x -> estado.isEmpty() || estado.equals(x.getEstado()))
                .filter(x -> area.isEmpty() || area.equals(x.getArea()))
                .collect(Collectors.toList());
        if ("inherente".equals(orden)) {
            r.sort(Comparator.comparingInt(Riesgo::getPrioridadInherente));
        } else if ("residual".equals(orden)) {
            r.sort(Comparator.comparingInt(Riesgo::getPrioridadResidual));
        }
        return r;
    }

    /** Áreas del catálogo más las que solo existan en riesgos antiguos, para no perder ningún filtro. */
    private static List<String> areasFiltro(List<Riesgo> todos) {
        Set<String> areas = new LinkedHashSet<>(Catalogos.AREAS);
        todos.stream().map(Riesgo::getArea).filter(a -> a != null && !a.isEmpty()).sorted().forEach(areas::add);
        return new ArrayList<>(areas);
    }

    private static String ordenValido(String orden) {
        String o = valor(orden);
        return o.matches("inherente|residual") ? o : "id";
    }

    /** Texto apto para nombre de archivo: sin tildes ni espacios. */
    private static String slug(String s) {
        String sinTildes = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.replaceAll("[^A-Za-z0-9]+", "_").replaceAll("^_|_$", "");
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response, Riesgo r,
            List<String> errores) throws ServletException, IOException {
        request.setAttribute("riesgo", r);
        request.setAttribute("errores", errores);
        request.getRequestDispatcher("/WEB-INF/vistas/formulario.jsp").forward(request, response);
    }

    private static Riesgo leerFormulario(HttpServletRequest req) {
        Riesgo r = new Riesgo();
        r.setId(entero(req.getParameter("id"), 0));
        r.setCodigo(texto(req, "codigo"));
        r.setArea(texto(req, "area"));
        r.setResponsable(texto(req, "responsable"));
        r.setEstadoActual(texto(req, "estadoActual"));
        r.setCausaRaiz(texto(req, "causaRaiz"));
        r.setRiesgoIdentificado(texto(req, "riesgoIdentificado"));
        r.setNaturaleza(texto(req, "naturaleza"));
        r.setProbabilidad(entero(req.getParameter("probabilidad"), 0));
        r.setImpacto(entero(req.getParameter("impacto"), 0));
        r.setEstadoDeseado(texto(req, "estadoDeseado"));
        r.setBrecha(texto(req, "brecha"));
        r.setNivelBrecha(texto(req, "nivelBrecha"));
        r.setControlesActuales(texto(req, "controlesActuales"));
        r.setFrecuenciaControl(texto(req, "frecuenciaControl"));
        r.setEfectividadControl(texto(req, "efectividadControl"));
        r.setEstrategia(texto(req, "estrategia"));
        r.setPlanAccion(texto(req, "planAccion"));
        r.setNuevosRiesgos(texto(req, "nuevosRiesgos"));
        r.setEstado(texto(req, "estado"));
        return r;
    }

    private static List<String> validar(Riesgo r, String areaAnterior) {
        List<String> e = new ArrayList<>();
        if (r.getArea().isEmpty()) {
            e.add("Seleccione el macroproceso / área.");
        } else if (!Catalogos.AREAS.contains(r.getArea()) && !r.getArea().equals(areaAnterior)) {
            e.add("Seleccione un macroproceso / área de la lista.");
        }
        if (r.getRiesgoIdentificado().isEmpty()) {
            e.add("El riesgo identificado es obligatorio.");
        }
        if (!Catalogos.NATURALEZAS.containsKey(r.getNaturaleza())) {
            e.add("Seleccione la naturaleza del riesgo.");
        }
        if (r.getProbabilidad() < 1 || r.getProbabilidad() > 5) {
            e.add("La probabilidad debe estar entre 1 y 5.");
        }
        if (r.getImpacto() < 1 || r.getImpacto() > 5) {
            e.add("El impacto debe estar entre 1 y 5.");
        }
        if (!Catalogos.NIVELES_BRECHA.containsKey(r.getNivelBrecha())) {
            e.add("Seleccione el nivel de brecha.");
        }
        if (!Catalogos.FRECUENCIAS.contains(r.getFrecuenciaControl())) {
            e.add("Seleccione la frecuencia del control.");
        }
        if (!Catalogos.EFECTIVIDADES.containsKey(r.getEfectividadControl())) {
            e.add("Seleccione la efectividad del control.");
        }
        if (!Catalogos.ESTRATEGIAS.containsKey(r.getEstrategia())) {
            e.add("Seleccione la estrategia.");
        }
        if (!Catalogos.ESTADOS.contains(r.getEstado())) {
            e.add("Seleccione el estado.");
        }
        int[] limites = {200, 200, 2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000};
        String[] campos = {r.getArea(), r.getResponsable(), r.getEstadoActual(), r.getCausaRaiz(),
            r.getRiesgoIdentificado(), r.getEstadoDeseado(), r.getBrecha(), r.getControlesActuales(),
            r.getPlanAccion(), r.getNuevosRiesgos()};
        for (int i = 0; i < campos.length; i++) {
            if (campos[i].length() > limites[i]) {
                e.add("Un campo de texto supera los " + limites[i] + " caracteres.");
                break;
            }
        }
        return e;
    }

    /** Conserva el orden y el filtro de la matriz al volver a ella. */
    private static String volverA(HttpServletRequest request) throws IOException {
        String orden = valor(request.getParameter("orden"));
        String filtro = valor(request.getParameter("filtroEstado"));
        String area = valor(request.getParameter("filtroArea"));
        StringBuilder q = new StringBuilder();
        if (orden.matches("id|inherente|residual")) {
            q.append("orden=").append(orden);
        }
        if (Catalogos.ESTADOS.contains(filtro)) {
            q.append(q.length() > 0 ? "&" : "").append("estado=")
                    .append(java.net.URLEncoder.encode(filtro, "UTF-8"));
        }
        if (!area.isEmpty() && area.length() <= 200) {
            q.append(q.length() > 0 ? "&" : "").append("area=")
                    .append(java.net.URLEncoder.encode(area, "UTF-8"));
        }
        return q.length() > 0 ? "?" + q : "";
    }

    private static void mensaje(HttpServletRequest request, String texto) {
        request.getSession().setAttribute("mensaje", texto);
    }

    private static String texto(HttpServletRequest req, String nombre) {
        return valor(req.getParameter(nombre));
    }

    private static String valor(String s) {
        return s == null ? "" : s.trim();
    }

    private static int entero(String s, int porDefecto) {
        try {
            return Integer.parseInt(valor(s));
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }
}
