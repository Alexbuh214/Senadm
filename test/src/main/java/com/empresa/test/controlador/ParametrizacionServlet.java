package com.empresa.test.controlador;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Hoja "Parametrización": tablas maestras (solo lectura).
 */
@WebServlet(name = "ParametrizacionServlet", urlPatterns = {"/app/parametrizacion"})
public class ParametrizacionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/vistas/parametrizacion.jsp").forward(request, response);
    }
}
