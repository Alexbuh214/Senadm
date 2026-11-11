package com.empresa.test.controlador;

import com.empresa.test.dao.RiesgoDAO;
import com.empresa.test.modelo.Catalogos;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Al arrancar la aplicación: crea la tabla (y datos de ejemplo) y publica los catálogos para las vistas.
 */
@WebListener
public class InicioListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            new RiesgoDAO().inicializar();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo inicializar la base de datos", e);
        }
        sce.getServletContext().setAttribute("naturalezas", Catalogos.NATURALEZAS);
        sce.getServletContext().setAttribute("nivelesBrecha", Catalogos.NIVELES_BRECHA);
        sce.getServletContext().setAttribute("frecuencias", Catalogos.FRECUENCIAS);
        sce.getServletContext().setAttribute("efectividades", Catalogos.EFECTIVIDADES);
        sce.getServletContext().setAttribute("efectividadesDesc", Catalogos.EFECTIVIDADES_DESC);
        sce.getServletContext().setAttribute("estrategias", Catalogos.ESTRATEGIAS);
        sce.getServletContext().setAttribute("estados", Catalogos.ESTADOS);
        sce.getServletContext().setAttribute("probabilidades", Catalogos.PROBABILIDAD);
        sce.getServletContext().setAttribute("impactos", Catalogos.IMPACTO);
        sce.getServletContext().setAttribute("bandas", Catalogos.BANDAS);
        sce.getServletContext().setAttribute("catalogosJson", Catalogos.json());
        // Versión de los recursos estáticos: cambia en cada arranque para que el navegador no use un CSS viejo.
        sce.getServletContext().setAttribute("version", Long.toString(System.currentTimeMillis(), 36));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Libera el driver JDBC registrado por esta aplicación (evita fugas de memoria al redesplegar).
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver d = drivers.nextElement();
            if (d.getClass().getClassLoader() == cl) {
                try {
                    DriverManager.deregisterDriver(d);
                } catch (SQLException e) {
                    sce.getServletContext().log("No se pudo liberar el driver JDBC", e);
                }
            }
        }
    }
}
