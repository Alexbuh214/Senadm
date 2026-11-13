package com.empresa.test.controlador;

import com.empresa.test.modelo.Catalogos;
import com.empresa.test.modelo.Riesgo;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Genera la matriz de riesgos en Excel con las mismas columnas, etapas y colores que la vista web.
 */
final class MatrizExcel {

    private MatrizExcel() {
    }

    /** Tipo de celda: texto corto, texto largo, número o nivel coloreado. */
    private enum Tipo { TEXTO, LARGO, ENTERO, DECIMAL, NIVEL, BRECHA }

    private static final class Columna {
        final String titulo;
        final int ancho;
        final Tipo tipo;
        final Function<Riesgo, Object> valor;
        final Function<Riesgo, Double> nivel;

        Columna(String titulo, int ancho, Tipo tipo, Function<Riesgo, Object> valor, Function<Riesgo, Double> nivel) {
            this.titulo = titulo;
            this.ancho = ancho;
            this.tipo = tipo;
            this.valor = valor;
            this.nivel = nivel;
        }
    }

    private static Columna c(String t, int ancho, Tipo tipo, Function<Riesgo, Object> v) {
        return new Columna(t, ancho, tipo, v, null);
    }

    private static Columna n(String t, int ancho, Function<Riesgo, Object> v, Function<Riesgo, Double> nivel) {
        return new Columna(t, ancho, Tipo.NIVEL, v, nivel);
    }

    /** Mismas columnas que matriz.jsp, en el mismo orden. */
    private static final Columna[] COLUMNAS = {
        // Etapa 1
        c("ID", 8, Tipo.TEXTO, Riesgo::getCodigo),
        c("Macroproceso / Área", 28, Tipo.TEXTO, Riesgo::getArea),
        c("Responsable", 20, Tipo.TEXTO, Riesgo::getResponsable),
        c("Estado actual (realidad)", 34, Tipo.LARGO, Riesgo::getEstadoActual),
        c("Causa raíz", 34, Tipo.LARGO, Riesgo::getCausaRaiz),
        c("Riesgo identificado", 34, Tipo.LARGO, Riesgo::getRiesgoIdentificado),
        c("Naturaleza", 26, Tipo.TEXTO, Riesgo::getNaturaleza),
        // Etapa 2
        c("Prob. (1-5)", 9, Tipo.ENTERO, Riesgo::getProbabilidad),
        c("Imp. (1-5)", 9, Tipo.ENTERO, Riesgo::getImpacto),
        n("Nivel de criticidad / Riesgo inherente", 16, Riesgo::getRiesgoInherente, Riesgo::getRiesgoInherente),
        c("Estado actual (realidad)", 34, Tipo.LARGO, Riesgo::getEstadoActual),
        c("Estado deseado", 34, Tipo.LARGO, Riesgo::getEstadoDeseado),
        c("Brecha detectada (GAP)", 34, Tipo.LARGO, Riesgo::getBrecha),
        c("Nivel de brecha", 12, Tipo.BRECHA, Riesgo::getNivelBrecha),
        // Etapa 3
        c("Riesgo inherente priorizado", 12, Tipo.ENTERO, Riesgo::getPrioridadInherente),
        c("Controles actuales", 34, Tipo.LARGO, Riesgo::getControlesActuales),
        c("Frecuencia del control", 22, Tipo.TEXTO, Riesgo::getFrecuenciaControl),
        c("Efectividad control", 16, Tipo.TEXTO, Riesgo::getEfectividadControl),
        c("Factor multiplicador", 12, Tipo.DECIMAL, Riesgo::getFactorControl),
        n("Nivel de criticidad (Riesgo residual)", 16, Riesgo::getRiesgoResidual, Riesgo::getRiesgoResidual),
        c("Riesgo residual priorizado", 12, Tipo.ENTERO, Riesgo::getPrioridadResidual),
        // Etapa 4
        c("Estrategia", 18, Tipo.TEXTO, Riesgo::getEstrategia),
        c("Nombre del plan de acción", 34, Tipo.LARGO, Riesgo::getPlanAccion),
        c("Tipo de control PLAN", 34, Tipo.LARGO, Riesgo::getTipoControlPlan),
        c("Tiempo para implementar acción", 16, Tipo.TEXTO, Riesgo::getTiempoImplementacion),
        c("Efectividad controles (PLAN)", 16, Tipo.TEXTO, Riesgo::getEfectividadPlan),
        c("Factor de efectividad", 12, Tipo.DECIMAL, Riesgo::getFactorPlan),
        n("Nuevo nivel de riesgo", 14, Riesgo::getNuevoNivelRiesgo, Riesgo::getNuevoNivelRiesgo),
        c("Frecuencia de ejecución / monitoreo del control", 24, Tipo.TEXTO, Riesgo::getFrecuenciaMonitoreo),
        n("R. inicial / criticidad", 12, Riesgo::getRiesgoInherente, Riesgo::getRiesgoInherente),
        n("R. residual", 12, Riesgo::getRiesgoResidual, Riesgo::getRiesgoResidual),
        n("R. actual", 12, Riesgo::getNuevoNivelRiesgo, Riesgo::getNuevoNivelRiesgo),
        c("Nuevos riesgos", 34, Tipo.LARGO, Riesgo::getNuevosRiesgos),
        c("Estado", 14, Tipo.TEXTO, Riesgo::getEstado),
    };

    /** Etapas: título, columnas que abarca y color (los mismos de la vista web). */
    private static final Object[][] ETAPAS = {
        {"ETAPA 1 · IDENTIFICACIÓN DEL RIESGO", 7, "1D4ED8"},
        {"ETAPA 2 · ANÁLISIS DEL RIESGO (ANÁLISIS DE BRECHAS)", 7, "7C3AED"},
        {"ETAPA 3 · EVALUACIÓN DE RIESGOS", 7, "B45309"},
        {"ETAPA 4 · PLAN DE TRATAMIENTO DE RIESGOS Y MONITOREO", 13, "047857"},
    };

    /** Colores de niveles: fondo y texto (iguales a .nv-* en estilos.css). */
    private static final Map<String, String[]> NIVELES = Map.of(
            "nv-bajo", new String[]{"BBF7D0", "14532D"},
            "nv-medio", new String[]{"FEF08A", "713F12"},
            "nv-alto", new String[]{"FDBA74", "7C2D12"},
            "nv-critico", new String[]{"FCA5A5", "7F1D1D"});

    static void escribir(List<Riesgo> riesgos, String area, String estado, OutputStream salida) throws IOException {
        try (XSSFWorkbook libro = new XSSFWorkbook()) {
            Estilos e = new Estilos(libro);
            XSSFSheet hoja = libro.createSheet("Matriz de riesgos");
            int total = COLUMNAS.length;

            // Título y subtítulo
            Row titulo = hoja.createRow(0);
            titulo.setHeightInPoints(26);
            celda(titulo, 0, "SENADI · Matriz de gestión de riesgos integrada con análisis de brechas", e.titulo);
            hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, total - 1));

            String alcance = area.isEmpty() ? "Matriz general (todas las áreas)" : "Área: " + area;
            if (!estado.isEmpty()) {
                alcance += "  ·  Estado: " + estado;
            }
            alcance += "  ·  " + riesgos.size() + (riesgos.size() == 1 ? " riesgo" : " riesgos")
                    + "  ·  Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            Row sub = hoja.createRow(1);
            sub.setHeightInPoints(18);
            celda(sub, 0, alcance, e.subtitulo);
            hoja.addMergedRegion(new CellRangeAddress(1, 1, 0, total - 1));

            // Fila de etapas
            Row etapas = hoja.createRow(3);
            etapas.setHeightInPoints(22);
            int col = 0;
            for (Object[] et : ETAPAS) {
                int span = (Integer) et[1];
                CellStyle s = e.etapa((String) et[2]);
                for (int i = 0; i < span; i++) {
                    celda(etapas, col + i, i == 0 ? (String) et[0] : "", s);
                }
                hoja.addMergedRegion(new CellRangeAddress(3, 3, col, col + span - 1));
                col += span;
            }

            // Encabezados de columna
            Row cab = hoja.createRow(4);
            cab.setHeightInPoints(48);
            for (int i = 0; i < total; i++) {
                celda(cab, i, COLUMNAS[i].titulo, e.encabezado);
                hoja.setColumnWidth(i, COLUMNAS[i].ancho * 256);
            }

            // Datos
            int fila = 5;
            for (Riesgo r : riesgos) {
                Row row = hoja.createRow(fila);
                boolean par = (fila - 5) % 2 == 1;
                for (int i = 0; i < total; i++) {
                    Columna cl = COLUMNAS[i];
                    Object v = cl.valor.apply(r);
                    Cell cell = row.createCell(i);
                    switch (cl.tipo) {
                        case ENTERO:
                            cell.setCellValue(((Number) v).doubleValue());
                            cell.setCellStyle(e.numero(par, false));
                            break;
                        case DECIMAL:
                            cell.setCellValue(((Number) v).doubleValue());
                            cell.setCellStyle(e.numero(par, true));
                            break;
                        case NIVEL:
                            cell.setCellValue(((Number) v).doubleValue());
                            cell.setCellStyle(e.nivel(Catalogos.claseCriticidad(cl.nivel.apply(r))));
                            break;
                        case BRECHA:
                            cell.setCellValue(v == null ? "" : v.toString());
                            String css = Catalogos.claseBrecha((String) v);
                            cell.setCellStyle(css.isEmpty() ? e.texto(par, false) : e.nivelTexto(css));
                            break;
                        default:
                            cell.setCellValue(v == null ? "" : v.toString());
                            cell.setCellStyle(e.texto(par, cl.tipo == Tipo.LARGO));
                    }
                }
                fila++;
            }
            if (riesgos.isEmpty()) {
                Row row = hoja.createRow(fila);
                celda(row, 0, "No hay riesgos para mostrar con los filtros seleccionados.", e.subtitulo);
                hoja.addMergedRegion(new CellRangeAddress(fila, fila, 0, total - 1));
            } else {
                hoja.setAutoFilter(new CellRangeAddress(4, fila - 1, 0, total - 1));
            }

            // Leyenda de niveles
            Row ley = hoja.createRow(fila + 1);
            celda(ley, 0, "Niveles:", e.subtitulo);
            String[][] leyenda = {{"nv-bajo", "Bajo (1 - 5)"}, {"nv-medio", "Medio (5.1 - 10)"},
                {"nv-alto", "Alto (10.1 - 15)"}, {"nv-critico", "Crítico (15.1 - 25)"}};
            for (int i = 0; i < leyenda.length; i++) {
                celda(ley, 1 + i, leyenda[i][1], e.nivelTexto(leyenda[i][0]));
            }

            hoja.createFreezePane(2, 5);
            hoja.getPrintSetup().setLandscape(true);
            hoja.setFitToPage(true);
            hoja.getPrintSetup().setFitWidth((short) 1);
            hoja.getPrintSetup().setFitHeight((short) 0);
            hoja.setRepeatingRows(new CellRangeAddress(3, 4, -1, -1));

            libro.write(salida);
        }
    }

    private static void celda(Row row, int col, String valor, CellStyle estilo) {
        Cell c = row.createCell(col);
        c.setCellValue(valor);
        c.setCellStyle(estilo);
    }

    /** Estilos reutilizados (Excel limita la cantidad de estilos por libro). */
    private static final class Estilos {
        private final XSSFWorkbook libro;
        final CellStyle titulo;
        final CellStyle subtitulo;
        final CellStyle encabezado;
        private final Map<String, CellStyle> cache = new HashMap<>();

        Estilos(XSSFWorkbook libro) {
            this.libro = libro;
            XSSFCellStyle t = libro.createCellStyle();
            t.setFont(fuente(16, true, "1E293B"));
            t.setVerticalAlignment(VerticalAlignment.CENTER);
            titulo = t;

            XSSFCellStyle s = libro.createCellStyle();
            s.setFont(fuente(10, false, "475569"));
            s.setVerticalAlignment(VerticalAlignment.CENTER);
            subtitulo = s;

            XSSFCellStyle h = base(true);
            h.setFillForegroundColor(color("E2E8F0"));
            h.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            h.setFont(fuente(9, true, "1E293B"));
            h.setAlignment(HorizontalAlignment.CENTER);
            encabezado = h;
        }

        CellStyle etapa(String hex) {
            return cache.computeIfAbsent("etapa" + hex, k -> {
                XSSFCellStyle st = base(false);
                st.setFillForegroundColor(color(hex));
                st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                st.setFont(fuente(10, true, "FFFFFF"));
                st.setAlignment(HorizontalAlignment.CENTER);
                return st;
            });
        }

        CellStyle texto(boolean par, boolean largo) {
            return cache.computeIfAbsent("txt" + par + largo, k -> {
                XSSFCellStyle st = base(true);
                cebra(st, par);
                st.setFont(fuente(9, false, "222222"));
                return st;
            });
        }

        CellStyle numero(boolean par, boolean decimal) {
            return cache.computeIfAbsent("num" + par + decimal, k -> {
                XSSFCellStyle st = base(false);
                cebra(st, par);
                st.setFont(fuente(9, false, "222222"));
                st.setAlignment(HorizontalAlignment.CENTER);
                st.setDataFormat(libro.createDataFormat().getFormat(decimal ? "0.00" : "0"));
                return st;
            });
        }

        CellStyle nivel(String css) {
            return cache.computeIfAbsent("nv" + css, k -> {
                XSSFCellStyle st = coloreado(css);
                st.setDataFormat(libro.createDataFormat().getFormat("0.##"));
                return st;
            });
        }

        CellStyle nivelTexto(String css) {
            return cache.computeIfAbsent("nvt" + css, k -> coloreado(css));
        }

        private XSSFCellStyle coloreado(String css) {
            String[] c = NIVELES.get(css);
            XSSFCellStyle st = base(false);
            st.setFillForegroundColor(color(c[0]));
            st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            st.setFont(fuente(9, true, c[1]));
            st.setAlignment(HorizontalAlignment.CENTER);
            return st;
        }

        private void cebra(XSSFCellStyle st, boolean par) {
            if (par) {
                st.setFillForegroundColor(color("F8FAFC"));
                st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
        }

        private XSSFCellStyle base(boolean ajustar) {
            XSSFCellStyle st = libro.createCellStyle();
            st.setVerticalAlignment(VerticalAlignment.CENTER);
            st.setWrapText(true);
            st.setBorderTop(BorderStyle.THIN);
            st.setBorderBottom(BorderStyle.THIN);
            st.setBorderLeft(BorderStyle.THIN);
            st.setBorderRight(BorderStyle.THIN);
            XSSFColor borde = color("CBD5E1");
            st.setTopBorderColor(borde);
            st.setBottomBorderColor(borde);
            st.setLeftBorderColor(borde);
            st.setRightBorderColor(borde);
            if (ajustar) {
                st.setVerticalAlignment(VerticalAlignment.TOP);
            }
            return st;
        }

        private XSSFFont fuente(int puntos, boolean negrita, String hex) {
            XSSFFont f = libro.createFont();
            f.setFontName("Calibri");
            f.setFontHeightInPoints((short) puntos);
            f.setBold(negrita);
            f.setColor(color(hex));
            return f;
        }

        private static XSSFColor color(String hex) {
            int v = Integer.parseInt(hex, 16);
            return new XSSFColor(new byte[]{(byte) (v >> 16), (byte) (v >> 8), (byte) v}, null);
        }
    }
}
