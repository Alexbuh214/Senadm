package com.empresa.test.filtro;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Protege todo lo que esté bajo /app/*: si no hay sesión, manda al login.
 */
@WebFilter("/app/*")
public class AutenticacionFilter implements Filter {

    // init/destroy vacíos: en Servlet 3.1 (p. ej. Jetty 9.4) no tienen implementación por defecto.
    @Override
    public void init(FilterConfig config) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession sesion = request.getSession(false);

        // Las páginas de la aplicación muestran datos que cambian (estados, ediciones, eliminaciones):
        // se prohíbe guardarlas en caché para que al usar "Atrás" el navegador vuelva a pedirlas
        // y nunca muestre una versión vieja. También evita ver páginas privadas tras cerrar sesión.
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, private");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        if (sesion == null || sesion.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        chain.doFilter(req, res);
    }
}
