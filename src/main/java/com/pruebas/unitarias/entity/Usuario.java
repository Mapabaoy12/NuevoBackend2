package com.pruebas.unitarias.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String password;
    
    @Column(nullable = false)
    private String telefono;
    
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;
    
    @Column(nullable = false)
    private String direccion;
    
    @Column(name = "codigo_promocional")
    private String codigoPromocional;
    
    @Column(name = "es_duoc_uc", nullable = false)
    private Boolean esDuocUC = false;
    
    @Column(name = "es_mayor_de_50", nullable = false)
    private Boolean esMayorDe50 = false;
    
    @Column(name = "tiene_descuento_felices50", nullable = false)
    private Boolean tieneDescuentoFelices50 = false;
    
    @Column(name = "descuento_porcentaje", nullable = false)
    private Integer descuentoPorcentaje = 0;
    
    @Column(name = "torta_gratis_cumpleanos_disponible", nullable = false)
    private Boolean tortaGratisCumpleanosDisponible = false;
    
    @Column(name = "torta_gratis_cumpleanos_usada", nullable = false)
    private Boolean tortaGratisCumpleanosUsada = false;
    
    @Column(name = "anio_torta_gratis_cumpleanos")
    private Integer anioTortaGratisCumpleanos;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
}
