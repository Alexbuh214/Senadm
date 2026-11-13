package com.empresa.test.controlador;

import com.empresa.test.dao.RiesgoDAO;
import com.empresa.test.modelo.Catalogos;
import com.empresa.test.modelo.Riesgo;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Hoja "Dashboard Ejecutivo": totales y evolución del nivel de riesgo.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/app/inicio"})
public class DashboardServlet extends HttpServlet {

    private final RiesgoDAO dao = new RiesgoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Riesgo> riesgos;
        try {
            riesgos = dao.listar();
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        long criticos = riesgos.stream().filter(Riesgo::isCritico).count();
        long criticosResiduales = riesgos.stream().filter(r -> r.getRiesgoResidual() >= 15.1).count();

        Map<String, Long> porEstado = new LinkedHashMap<>();
        for (String e : Catalogos.ESTADOS) {
            porEstado.put(e, riesgos.stream().filter(r -> e.equals(r.getEstado())).count());
        }

        double max = 25;
        request.setAttribute("riesgos", riesgos);
        request.setAttribute("total", riesgos.size());
        request.setAttribute("criticos", criticos);
        request.setAttribute("criticosResiduales", criticosResiduales);
        request.setAttribute("porEstado", porEstado);
        request.setAttribute("escalaMax", max);
        request.getRequestDispatcher("/WEB-INF/vistas/dashboard.jsp").forward(request, response);
    }
}
