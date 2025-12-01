package com.pruebas.unitarias.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "codigos_promocionales")
@Data
public class CodigoPromocional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String codigo;
    
    @Column(nullable = false)
    private Integer descuento; // Porcentaje
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;
    
    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;
    
    @Column(name = "usos_maximos")
    private Integer usosMaximos;
    
    @Column(name = "usos_actuales")
    private Integer usosActuales = 0;
}
