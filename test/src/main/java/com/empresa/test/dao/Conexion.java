package com.empresa.test.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexión JDBC. Por defecto usa H2 embebido en ~/senadm-data/senadm.mv.db.
 * Para usar otra base basta con definir las propiedades del sistema
 * senadm.db.url, senadm.db.user y senadm.db.password (y agregar el driver al pom).
 */
public final class Conexion {

    private static final String URL = System.getProperty("senadm.db.url",
            "jdbc:h2:~/senadm-data/senadm;AUTO_SERVER=TRUE");
    private static final String USUARIO = System.getProperty("senadm.db.user", "sa");
    private static final String CLAVE = System.getProperty("senadm.db.password", "");

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            // Si se usa otra base de datos su driver se registra solo.
        }
    }

    private Conexion() {
    }

    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
