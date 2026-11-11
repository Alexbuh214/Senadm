package com.empresa.test.modelo;

/**
 * Una fila de la Matriz Operativa. Los campos que se ingresan se guardan en la base;
 * las columnas calculadas (J, S, T, X, Y, Z, AA, AB, AC, AD-AF) se calculan aquí
 * con las mismas fórmulas de la matriz FINAL.
 */
public class Riesgo {

    private int id;
    private String codigo;              // A  ID (R01, R02...)

    // ETAPA 1 - Identificación del riesgo
    private String area;                // B  Macroproceso / Área
    private String responsable;         // C
    private String estadoActual;        // D (y K) Estado actual (realidad)
    private String causaRaiz;           // E
    private String riesgoIdentificado;  // F
    private String naturaleza;          // G

    // ETAPA 2 - Análisis del riesgo (brechas)
    private int probabilidad;           // H (1-5)
    private int impacto;                // I (1-5)
    private String estadoDeseado;       // L
    private String brecha;              // M
    private String nivelBrecha;         // N

    // ETAPA 3 - Evaluación
    private String controlesActuales;   // P
    private String frecuenciaControl;   // Q
    private String efectividadControl;  // R

    // ETAPA 4 - Tratamiento y monitoreo
    private String estrategia;          // V
    private String planAccion;          // W
    private String nuevosRiesgos;       // AG
    private String estado;              // AH

    // Prioridades (O y U): posición en orden descendente, las asigna el servicio
    private int prioridadInherente;
    private int prioridadResidual;

    // ---------- Columnas calculadas ----------

    /** J = H * I */
    public double getRiesgoInherente() {
        return probabilidad * impacto;
    }

    /** S = VLOOKUP(R, efectividades) */
    public double getFactorControl() {
        return Catalogos.factor(efectividadControl);
    }

    /** T = ROUND(J * S, 2) */
    public double getRiesgoResidual() {
        return redondear(getRiesgoInherente() * getFactorControl());
    }

    private Catalogos.Banda banda() {
        return Catalogos.banda(getRiesgoResidual());
    }

    /** X */
    public String getTipoControlPlan() {
        return banda().getTipoControl();
    }

    /** Y */
    public String getTiempoImplementacion() {
        return banda().getTiempo();
    }

    /** Z */
    public String getEfectividadPlan() {
        return banda().getEfectividadPlan();
    }

    /** AA */
    public double getFactorPlan() {
        return Catalogos.factor(getEfectividadPlan());
    }

    /** AB = ROUND(T * AA, 2) */
    public double getNuevoNivelRiesgo() {
        return redondear(getRiesgoResidual() * getFactorPlan());
    }

    /** AC */
    public String getFrecuenciaMonitoreo() {
        return banda().getFrecuenciaMonitoreo();
    }

    public String getClasifInherente() {
        return Catalogos.clasificacion(getRiesgoInherente());
    }

    public String getClasifResidual() {
        return Catalogos.clasificacion(getRiesgoResidual());
    }

    public String getClasifNuevo() {
        return Catalogos.clasificacion(getNuevoNivelRiesgo());
    }

    public String getCssInherente() {
        return Catalogos.claseCriticidad(getRiesgoInherente());
    }

    public String getCssResidual() {
        return Catalogos.claseCriticidad(getRiesgoResidual());
    }

    public String getCssNuevo() {
        return Catalogos.claseCriticidad(getNuevoNivelRiesgo());
    }

    public String getCssBrecha() {
        return Catalogos.claseBrecha(nivelBrecha);
    }

    public boolean isCritico() {
        return getRiesgoInherente() >= 15.1;
    }

    private static double redondear(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    // ---------- Getters y setters ----------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    public String getCausaRaiz() {
        return causaRaiz;
    }

    public void setCausaRaiz(String causaRaiz) {
        this.causaRaiz = causaRaiz;
    }

    public String getRiesgoIdentificado() {
        return riesgoIdentificado;
    }

    public void setRiesgoIdentificado(String riesgoIdentificado) {
        this.riesgoIdentificado = riesgoIdentificado;
    }

    public String getNaturaleza() {
        return naturaleza;
    }

    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
    }

    public int getProbabilidad() {
        return probabilidad;
    }

    public void setProbabilidad(int probabilidad) {
        this.probabilidad = probabilidad;
    }

    public int getImpacto() {
        return impacto;
    }

    public void setImpacto(int impacto) {
        this.impacto = impacto;
    }

    public String getEstadoDeseado() {
        return estadoDeseado;
    }

    public void setEstadoDeseado(String estadoDeseado) {
        this.estadoDeseado = estadoDeseado;
    }

    public String getBrecha() {
        return brecha;
    }

    public void setBrecha(String brecha) {
        this.brecha = brecha;
    }

    public String getNivelBrecha() {
        return nivelBrecha;
    }

    public void setNivelBrecha(String nivelBrecha) {
        this.nivelBrecha = nivelBrecha;
    }

    public String getControlesActuales() {
        return controlesActuales;
    }

    public void setControlesActuales(String controlesActuales) {
        this.controlesActuales = controlesActuales;
    }

    public String getFrecuenciaControl() {
        return frecuenciaControl;
    }

    public void setFrecuenciaControl(String frecuenciaControl) {
        this.frecuenciaControl = frecuenciaControl;
    }

    public String getEfectividadControl() {
        return efectividadControl;
    }

    public void setEfectividadControl(String efectividadControl) {
        this.efectividadControl = efectividadControl;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public void setEstrategia(String estrategia) {
        this.estrategia = estrategia;
    }

    public String getPlanAccion() {
        return planAccion;
    }

    public void setPlanAccion(String planAccion) {
        this.planAccion = planAccion;
    }

    public String getNuevosRiesgos() {
        return nuevosRiesgos;
    }

    public void setNuevosRiesgos(String nuevosRiesgos) {
        this.nuevosRiesgos = nuevosRiesgos;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getPrioridadInherente() {
        return prioridadInherente;
    }

    public void setPrioridadInherente(int prioridadInherente) {
        this.prioridadInherente = prioridadInherente;
    }

    public int getPrioridadResidual() {
        return prioridadResidual;
    }

    public void setPrioridadResidual(int prioridadResidual) {
        this.prioridadResidual = prioridadResidual;
    }
}
