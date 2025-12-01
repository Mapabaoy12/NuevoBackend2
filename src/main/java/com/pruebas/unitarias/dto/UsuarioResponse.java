package com.pruebas.unitarias.dto;

import lombok.Data;

@Data
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String fechaNacimiento;
    private String direccion;
    private String codigoPromocional;
    private Boolean esDuocUC;
    private Boolean esMayorDe50;
    private Boolean tieneDescuentoFelices50;
    private Integer descuentoPorcentaje;
    private Boolean tortaGratisCumpleanosDisponible;
    private Boolean tortaGratisCumpleanosUsada;
    private Integer anioTortaGratisCumpleanos;
}
