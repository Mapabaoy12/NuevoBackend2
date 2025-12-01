package com.pruebas.unitarias.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
@Entity
@Table(name = "pedido_items")
@Data
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario;
    
    @Column(nullable = false)
    private BigDecimal subtotal;
    
    // Datos del producto al momento de la compra (por si cambian después)
    @Column(nullable = false)
    private String titulo;
    
    @Column
    private String imagen;
    
    @Column
    private String forma;
    
    @Column
    private String tamanio;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
}
