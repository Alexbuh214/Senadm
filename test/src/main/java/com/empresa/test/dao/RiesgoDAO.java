package com.empresa.test.dao;

import com.empresa.test.modelo.Riesgo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * CRUD de la tabla RIESGO.
 */
public class RiesgoDAO {

    private static final String COLUMNAS = "AREA, RESPONSABLE, ESTADO_ACTUAL, CAUSA_RAIZ, RIESGO_IDENTIFICADO, "
            + "NATURALEZA, PROBABILIDAD, IMPACTO, ESTADO_DESEADO, BRECHA, NIVEL_BRECHA, CONTROLES_ACTUALES, "
            + "FRECUENCIA_CONTROL, EFECTIVIDAD_CONTROL, ESTRATEGIA, PLAN_ACCION, NUEVOS_RIESGOS, ESTADO";

    /** Crea la tabla si no existe y carga los riesgos de ejemplo de la matriz la primera vez. */
    public void inicializar() throws SQLException {
        try (Connection cn = Conexion.obtener(); Statement st = cn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS RIESGO ("
                    + "ID INT AUTO_INCREMENT PRIMARY KEY,"
                    + "CODIGO VARCHAR(10) UNIQUE,"
                    + "AREA VARCHAR(200) NOT NULL,"
                    + "RESPONSABLE VARCHAR(200),"
                    + "ESTADO_ACTUAL VARCHAR(2000),"
                    + "CAUSA_RAIZ VARCHAR(2000),"
                    + "RIESGO_IDENTIFICADO VARCHAR(2000) NOT NULL,"
                    + "NATURALEZA VARCHAR(200) NOT NULL,"
                    + "PROBABILIDAD INT NOT NULL CHECK (PROBABILIDAD BETWEEN 1 AND 5),"
                    + "IMPACTO INT NOT NULL CHECK (IMPACTO BETWEEN 1 AND 5),"
                    + "ESTADO_DESEADO VARCHAR(2000),"
                    + "BRECHA VARCHAR(2000),"
                    + "NIVEL_BRECHA VARCHAR(20) NOT NULL,"
                    + "CONTROLES_ACTUALES VARCHAR(2000),"
                    + "FRECUENCIA_CONTROL VARCHAR(100) NOT NULL,"
                    + "EFECTIVIDAD_CONTROL VARCHAR(50) NOT NULL,"
                    + "ESTRATEGIA VARCHAR(50) NOT NULL,"
                    + "PLAN_ACCION VARCHAR(2000),"
                    + "NUEVOS_RIESGOS VARCHAR(2000),"
                    + "ESTADO VARCHAR(20) NOT NULL DEFAULT 'Pendiente')");
            migrarAreas(cn);
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM RIESGO")) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    return;
                }
            }
        }
        for (Riesgo r : datosIniciales()) {
            insertar(r);
        }
    }

    /** Áreas genéricas antiguas -> dirección oficial que las reemplaza. */
    private static final String[][] AREAS_ANTIGUAS = {
        {"Tecnología de Información", "Dirección de Tecnologías de la Información y Comunicación"},
        {"Recursos Humanos", "Dirección de Administración de Talento Humano"},
        {"Logística y Suministros", "Dirección Administrativa"},
        {"Finanzas y Contabilidad", "Dirección Financiera"},
    };

    /** Pasa los riesgos guardados con un área antigua a su dirección oficial (se ejecuta al iniciar). */
    private static void migrarAreas(Connection cn) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement("UPDATE RIESGO SET AREA=? WHERE AREA=?")) {
            for (String[] a : AREAS_ANTIGUAS) {
                ps.setString(1, a[1]);
                ps.setString(2, a[0]);
                ps.executeUpdate();
            }
        }
    }

    public List<Riesgo> listar() throws SQLException {
        List<Riesgo> lista = new ArrayList<>();
        try (Connection cn = Conexion.obtener();
                PreparedStatement ps = cn.prepareStatement("SELECT * FROM RIESGO ORDER BY ID");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        asignarPrioridades(lista);
        return lista;
    }

    public Riesgo buscar(int id) throws SQLException {
        for (Riesgo r : listar()) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }

    public void insertar(Riesgo r) throws SQLException {
        String sql = "INSERT INTO RIESGO (" + COLUMNAS + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection cn = Conexion.obtener();
                PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarParametros(ps, r);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                r.setId(rs.getInt(1));
            }
            r.setCodigo(String.format("R%02d", r.getId()));
            try (PreparedStatement ps2 = cn.prepareStatement("UPDATE RIESGO SET CODIGO=? WHERE ID=?")) {
                ps2.setString(1, r.getCodigo());
                ps2.setInt(2, r.getId());
                ps2.executeUpdate();
            }
        }
    }

    public boolean actualizar(Riesgo r) throws SQLException {
        String sql = "UPDATE RIESGO SET AREA=?, RESPONSABLE=?, ESTADO_ACTUAL=?, CAUSA_RAIZ=?, RIESGO_IDENTIFICADO=?, "
                + "NATURALEZA=?, PROBABILIDAD=?, IMPACTO=?, ESTADO_DESEADO=?, BRECHA=?, NIVEL_BRECHA=?, "
                + "CONTROLES_ACTUALES=?, FRECUENCIA_CONTROL=?, EFECTIVIDAD_CONTROL=?, ESTRATEGIA=?, PLAN_ACCION=?, "
                + "NUEVOS_RIESGOS=?, ESTADO=? WHERE ID=?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            asignarParametros(ps, r);
            ps.setInt(19, r.getId());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean cambiarEstado(int id, String estado) throws SQLException {
        try (Connection cn = Conexion.obtener();
                PreparedStatement ps = cn.prepareStatement("UPDATE RIESGO SET ESTADO=? WHERE ID=?")) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        try (Connection cn = Conexion.obtener();
                PreparedStatement ps = cn.prepareStatement("DELETE FROM RIESGO WHERE ID=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    /** Columnas O y U: posición del riesgo ordenando de mayor a menor criticidad. */
    private static void asignarPrioridades(List<Riesgo> lista) {
        List<Riesgo> copia = new ArrayList<>(lista);
        copia.sort(Comparator.comparingDouble(Riesgo::getRiesgoInherente).reversed()
                .thenComparingInt(Riesgo::getId));
        for (int i = 0; i < copia.size(); i++) {
            copia.get(i).setPrioridadInherente(i + 1);
        }
        copia.sort(Comparator.comparingDouble(Riesgo::getRiesgoResidual).reversed()
                .thenComparingInt(Riesgo::getId));
        for (int i = 0; i < copia.size(); i++) {
            copia.get(i).setPrioridadResidual(i + 1);
        }
    }

    private static void asignarParametros(PreparedStatement ps, Riesgo r) throws SQLException {
        ps.setString(1, r.getArea());
        ps.setString(2, r.getResponsable());
        ps.setString(3, r.getEstadoActual());
        ps.setString(4, r.getCausaRaiz());
        ps.setString(5, r.getRiesgoIdentificado());
        ps.setString(6, r.getNaturaleza());
        ps.setInt(7, r.getProbabilidad());
        ps.setInt(8, r.getImpacto());
        ps.setString(9, r.getEstadoDeseado());
        ps.setString(10, r.getBrecha());
        ps.setString(11, r.getNivelBrecha());
        ps.setString(12, r.getControlesActuales());
        ps.setString(13, r.getFrecuenciaControl());
        ps.setString(14, r.getEfectividadControl());
        ps.setString(15, r.getEstrategia());
        ps.setString(16, r.getPlanAccion());
        ps.setString(17, r.getNuevosRiesgos());
        ps.setString(18, r.getEstado());
    }

    private static Riesgo mapear(ResultSet rs) throws SQLException {
        Riesgo r = new Riesgo();
        r.setId(rs.getInt("ID"));
        r.setCodigo(rs.getString("CODIGO"));
        r.setArea(rs.getString("AREA"));
        r.setResponsable(rs.getString("RESPONSABLE"));
        r.setEstadoActual(rs.getString("ESTADO_ACTUAL"));
        r.setCausaRaiz(rs.getString("CAUSA_RAIZ"));
        r.setRiesgoIdentificado(rs.getString("RIESGO_IDENTIFICADO"));
        r.setNaturaleza(rs.getString("NATURALEZA"));
        r.setProbabilidad(rs.getInt("PROBABILIDAD"));
        r.setImpacto(rs.getInt("IMPACTO"));
        r.setEstadoDeseado(rs.getString("ESTADO_DESEADO"));
        r.setBrecha(rs.getString("BRECHA"));
        r.setNivelBrecha(rs.getString("NIVEL_BRECHA"));
        r.setControlesActuales(rs.getString("CONTROLES_ACTUALES"));
        r.setFrecuenciaControl(rs.getString("FRECUENCIA_CONTROL"));
        r.setEfectividadControl(rs.getString("EFECTIVIDAD_CONTROL"));
        r.setEstrategia(rs.getString("ESTRATEGIA"));
        r.setPlanAccion(rs.getString("PLAN_ACCION"));
        r.setNuevosRiesgos(rs.getString("NUEVOS_RIESGOS"));
        r.setEstado(rs.getString("ESTADO"));
        return r;
    }

    /** Los 4 riesgos de ejemplo de la matriz FINAL (R01-R04). */
    private static List<Riesgo> datosIniciales() {
        List<Riesgo> l = new ArrayList<>();
        l.add(nuevo("Dirección de Tecnologías de la Información y Comunicación",
                "Servidores locales sin cifrado de datos en reposo.",
                "Fuga de información confidencial de clientes por accesos no autorizados.",
                "Tecnológicos y de ciberseguridad y seguridad de la información", 4, 5,
                "Implementación de cifrado AES-256 (Requisito ISO 27001).",
                "Ausencia total de tecnologías y algoritmos de cifrado local.", "Crítica",
                "Contraseña básica de red Windows.", "Automatizado / En tiempo real / Continuo", "Muy eficiente",
                "Evitar", "Adquirir licencias e implementar software de cifrado corporativo."));
        l.add(nuevo("Dirección de Administración de Talento Humano",
                "No existen planes de sucesión ni revisión de bandas salariales.",
                "Fuga de talento clave y pérdida de continuidad del negocio.",
                "Legales y de cumplimiento", 4, 4,
                "Planes de carrera activos y salarios alineados al mercado.",
                "Desfase del 40% en compensaciones versus la competencia directa.", "Media",
                "Entrevistas de salida informales.", "Diario / Semanal", "Eficiente",
                "Mitigar", "Diseñar un programa de fidelización y actualizar la estructura salarial."));
        l.add(nuevo("Dirección Administrativa",
                "Dependencia absoluta de un único proveedor de empaques.",
                "Paralización de la cadena de distribución por falta de stock crítico.",
                "Tecnológicos y de ciberseguridad y seguridad de la información", 2, 5,
                "Políticas de abastecimiento con multi-proveedores y stock de reserva.",
                "Cero convenios alternativos vigentes para materias primas críticas.", "Crítica",
                "Monitoreo mensual de entregas.", "Mensual / Quincenal", "Moderado",
                "Compartir o transferir", "Homologar y contratar dos proveedores alternativos de respaldo."));
        l.add(nuevo("Dirección Financiera",
                "Cálculo y llenado manual de plantillas impositivas.",
                "Sanciones impositivas y multas por errores en declaraciones fiscales.",
                "De desastres", 3, 3,
                "Automatización mediante módulo ERP actualizado según normativa vigente.",
                "Carencia de herramientas tecnológicas de validación automática.", "Baja",
                "Revisión cruzada por un segundo contador.", "Trimestral / Semestral", "Poco eficiente",
                "Aceptar", "Adquirir e integrar el módulo de actualización tributaria en el ERP."));
        return l;
    }

    private static Riesgo nuevo(String area, String estadoActual, String riesgo, String naturaleza, int prob,
            int imp, String deseado, String brecha, String nivelBrecha, String controles, String frecuencia,
            String efectividad, String estrategia, String plan) {
        Riesgo r = new Riesgo();
        r.setArea(area);
        r.setEstadoActual(estadoActual);
        r.setRiesgoIdentificado(riesgo);
        r.setNaturaleza(naturaleza);
        r.setProbabilidad(prob);
        r.setImpacto(imp);
        r.setEstadoDeseado(deseado);
        r.setBrecha(brecha);
        r.setNivelBrecha(nivelBrecha);
        r.setControlesActuales(controles);
        r.setFrecuenciaControl(frecuencia);
        r.setEfectividadControl(efectividad);
        r.setEstrategia(estrategia);
        r.setPlanAccion(plan);
        r.setEstado("En Proceso");
        return r;
    }
}
