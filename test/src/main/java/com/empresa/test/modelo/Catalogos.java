package com.empresa.test.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tablas de parametrización y data maestra (hoja "Parametrización" de la matriz FINAL
 * y hojas de referencia de la matriz v4).
 */
public final class Catalogos {

    private Catalogos() {
    }

    /** Macroprocesos / áreas de la empresa (lista desplegable del formulario). */
    public static final List<String> AREAS;
    /** Naturaleza del riesgo -> definición. */
    public static final Map<String, String> NATURALEZAS;
    /** Nivel de brecha -> significado operativo. */
    public static final Map<String, String> NIVELES_BRECHA;
    /** Frecuencia de ejecución / monitoreo del control. */
    public static final List<String> FRECUENCIAS;
    /** Efectividad del control -> factor multiplicador. */
    public static final Map<String, Double> EFECTIVIDADES;
    /** Efectividad del control -> explicación técnica. */
    public static final Map<String, String> EFECTIVIDADES_DESC;
    /** Estrategia -> definición. */
    public static final Map<String, String> ESTRATEGIAS;
    /** Estado del plan. */
    public static final List<String> ESTADOS;
    /** Escala de probabilidad (1-5). */
    public static final List<Escala> PROBABILIDAD;
    /** Escala de impacto (1-5). */
    public static final List<Escala> IMPACTO;
    /** Bandas por nivel de riesgo residual (ordenadas por rango mínimo ascendente). */
    public static final List<Banda> BANDAS;

    static {
        // Para agregar o quitar áreas basta con editar esta lista y reiniciar el servidor.
        AREAS = List.of(
                "Tecnología de Información",
                "Recursos Humanos",
                "Logística y Suministros",
                "Finanzas y Contabilidad");

        Map<String, String> nat = new LinkedHashMap<>();
        nat.put("Financieros y presupuestarios", "Vinculados con ingresos, gastos, activos y utilización de recursos públicos.");
        nat.put("Legales y de cumplimiento", "Relacionados con el incumplimiento de disposiciones legales, regulatorias, contractuales o internas.");
        nat.put("Tecnológicos y de ciberseguridad y seguridad de la información", "Incluidos riesgos sobre confidencialidad, integridad, disponibilidad y protección de información.");
        nat.put("Fraude corrupción e integridad", "Incluidos conflictos de interés, abuso de funciones, manipulación de información y otros comportamientos que puedan afectar la probidad institucional.");
        nat.put("De desastres", "Naturales o antrópicos, que puedan afectar a las personas, infraestructura o continuidad de los servicios públicos.");
        nat.put("Ambientales", "Cuando puedan afectar el funcionamiento o los objetivos institucionales.");
        nat.put("Reputacionales", "Afectan directamente la misión, la visión, los objetivos a mediano y largo plazo y la viabilidad o supervivencia general de una organización.");
        nat.put("Infraestructura", "Amenazas naturales, tecnológicas, operativas y financieras que pueden afectar su funcionamiento, seguridad o continuidad.");
        NATURALEZAS = Collections.unmodifiableMap(nat);

        Map<String, String> br = new LinkedHashMap<>();
        br.put("Crítica", "Falta total de controles o incumplimiento normativo grave.");
        br.put("Alta", "Existen escasos controles.");
        br.put("Media", "El proceso funciona, pero tiene ineficiencias o debilidades operativas.");
        br.put("Baja", "Alineación casi total con el estándar deseado.");
        NIVELES_BRECHA = Collections.unmodifiableMap(br);

        FRECUENCIAS = List.of(
                "Automatizado / En tiempo real / Continuo",
                "Diario / Semanal",
                "Mensual / Quincenal",
                "Trimestral / Semestral",
                "Anual / Al cierre de ciclo");

        Map<String, Double> ef = new LinkedHashMap<>();
        ef.put("Muy eficiente", 0.2);
        ef.put("Eficiente", 0.4);
        ef.put("Moderado", 0.6);
        ef.put("Poco eficiente", 0.8);
        ef.put("Ineficiente", 1.0);
        EFECTIVIDADES = Collections.unmodifiableMap(ef);

        Map<String, String> efd = new LinkedHashMap<>();
        efd.put("Muy eficiente", "Elimina o mitiga la gran mayoría del riesgo de forma automatizada. Solo resta un 20% de exposición.");
        efd.put("Eficiente", "Documentado, se aplica periódicamente y absorbe más de la mitad del impacto/probabilidad.");
        efd.put("Moderado", "Mitigación parcial. Manual o intermitente, protege menos de la mitad de la exposición.");
        efd.put("Poco eficiente", "Débil o informal. Apenas reduce una quinta parte del riesgo y depende del factor humano.");
        efd.put("Ineficiente", "No hay ninguna medida de seguridad. El riesgo inherente pasa íntegro al residual (100%).");
        EFECTIVIDADES_DESC = Collections.unmodifiableMap(efd);

        Map<String, String> es = new LinkedHashMap<>();
        es.put("Evitar", "Elimina la causa del riesgo.");
        es.put("Mitigar", "Realiza acciones para reducirlo.");
        es.put("Compartir o transferir", "Traslado a otra unidad para que pueda solucionarlo.");
        es.put("Aceptar", "Se acepta el riesgo cuando el costo de mitigarlo es más alto que el beneficio.");
        ESTRATEGIAS = Collections.unmodifiableMap(es);

        ESTADOS = List.of("Pendiente", "En Proceso", "Finalizado");

        PROBABILIDAD = List.of(
                new Escala(1, "Muy baja", "Casi nunca ocurre"),
                new Escala(2, "Baja", "Ocurre raramente"),
                new Escala(3, "Media", "Ocurre ocasionalmente"),
                new Escala(4, "Alta", "Ocurre frecuentemente"),
                new Escala(5, "Muy alta", "Ocurre casi siempre"));

        IMPACTO = List.of(
                new Escala(1, "Muy bajo", "Afectación a una actividad de un componente del sistema"),
                new Escala(2, "Bajo", "Afecta a dos o más actividades de un componente del sistema"),
                new Escala(3, "Medio", "Afecta a un componente del sistema"),
                new Escala(4, "Alto", "Afecta dos o más componentes del sistema"),
                new Escala(5, "Muy alto", "Afecta a todo el sistema de mejora continua e innovación"));

        List<Banda> b = new ArrayList<>();
        b.add(new Banda(1.0, "Control Normal de Operación: Riesgo aceptable. Basta con una verificación anual.",
                "120 días / sin plan", "Ineficiente", "Anual / Al cierre de ciclo"));
        b.add(new Banda(3.1, "Supervisión Periódica: Riesgo tolerable. Se admiten revisiones manuales espaciadas.",
                "120 días / sin plan", "Poco eficiente", "Trimestral / Semestral"));
        b.add(new Banda(5.1, "Detectivo / Correctivo: Controles enfocados en revisiones periódicas, arqueos o cierres mensuales.",
                "90 días", "Moderado", "Mensual / Quincenal"));
        b.add(new Banda(10.1, "Preventivo y Detectivo: El control debe correr de forma permanente en cada jornada laboral.",
                "60 días", "Eficiente", "Diario / Semanal"));
        b.add(new Banda(15.1, "Preventivo Obligatorio: Requiere controles automáticos de bloqueo o supervisión continua diaria.",
                "Inmediata - 30 días", "Muy eficiente", "Automatizado / En tiempo real / Continuo"));
        BANDAS = Collections.unmodifiableList(b);
    }

    /** Factor multiplicador de una efectividad (1 si no existe, igual que el IFERROR del Excel). */
    public static double factor(String efectividad) {
        Double f = efectividad == null ? null : EFECTIVIDADES.get(efectividad);
        return f == null ? 1.0 : f;
    }

    /** Equivalente a VLOOKUP(valor, Parametrización!L4:P8, n, TRUE). Valores menores a 1 usan la primera banda. */
    public static Banda banda(double residual) {
        Banda elegida = BANDAS.get(0);
        for (Banda b : BANDAS) {
            if (residual >= b.getMinimo()) {
                elegida = b;
            }
        }
        return elegida;
    }

    /** Clasificación del nivel de criticidad (hoja "Nv. criticidad R inheren y resi"). */
    public static String clasificacion(double valor) {
        if (valor >= 15.1) {
            return "Crítico";
        } else if (valor >= 10.1) {
            return "Alto";
        } else if (valor >= 5.1) {
            return "Medio";
        }
        return "Bajo";
    }

    /** Clase CSS para colorear un nivel de criticidad. */
    public static String claseCriticidad(double valor) {
        switch (clasificacion(valor)) {
            case "Crítico":
                return "nv-critico";
            case "Alto":
                return "nv-alto";
            case "Medio":
                return "nv-medio";
            default:
                return "nv-bajo";
        }
    }

    /** Clase CSS para colorear un nivel de brecha. */
    public static String claseBrecha(String nivel) {
        if (nivel == null) {
            return "";
        }
        switch (nivel) {
            case "Crítica":
                return "nv-critico";
            case "Alta":
                return "nv-alto";
            case "Media":
                return "nv-medio";
            case "Baja":
                return "nv-bajo";
            default:
                return "";
        }
    }

    /** Catálogos para el cálculo en vivo del formulario (JSON). */
    public static String json() {
        StringBuilder sb = new StringBuilder("{\"factores\":{");
        boolean primero = true;
        for (Map.Entry<String, Double> e : EFECTIVIDADES.entrySet()) {
            if (!primero) {
                sb.append(',');
            }
            sb.append(texto(e.getKey())).append(':').append(e.getValue());
            primero = false;
        }
        sb.append("},\"bandas\":[");
        for (int i = 0; i < BANDAS.size(); i++) {
            Banda b = BANDAS.get(i);
            if (i > 0) {
                sb.append(',');
            }
            sb.append("{\"min\":").append(b.getMinimo())
                    .append(",\"tipo\":").append(texto(b.getTipoControl()))
                    .append(",\"tiempo\":").append(texto(b.getTiempo()))
                    .append(",\"efectividad\":").append(texto(b.getEfectividadPlan()))
                    .append(",\"frecuencia\":").append(texto(b.getFrecuenciaMonitoreo()))
                    .append('}');
        }
        return sb.append("]}").toString();
    }

    private static String texto(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            if (c == '"' || c == '\\') {
                sb.append('\\').append(c);
            } else if (c == '<') {
                sb.append("\\u003c");
            } else {
                sb.append(c);
            }
        }
        return sb.append('"').toString();
    }

    /** Fila de las escalas de probabilidad e impacto. */
    public static final class Escala {
        private final int valor;
        private final String nivel;
        private final String descripcion;

        Escala(int valor, String nivel, String descripcion) {
            this.valor = valor;
            this.nivel = nivel;
            this.descripcion = descripcion;
        }

        public int getValor() {
            return valor;
        }

        public String getNivel() {
            return nivel;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    /** Fila de la tabla de tipo y frecuencia del control según el riesgo residual. */
    public static final class Banda {
        private final double minimo;
        private final String tipoControl;
        private final String tiempo;
        private final String efectividadPlan;
        private final String frecuenciaMonitoreo;

        Banda(double minimo, String tipoControl, String tiempo, String efectividadPlan, String frecuenciaMonitoreo) {
            this.minimo = minimo;
            this.tipoControl = tipoControl;
            this.tiempo = tiempo;
            this.efectividadPlan = efectividadPlan;
            this.frecuenciaMonitoreo = frecuenciaMonitoreo;
        }

        public double getMinimo() {
            return minimo;
        }

        public String getTipoControl() {
            return tipoControl;
        }

        public String getTiempo() {
            return tiempo;
        }

        public String getEfectividadPlan() {
            return efectividadPlan;
        }

        public String getFrecuenciaMonitoreo() {
            return frecuenciaMonitoreo;
        }
    }
}
