package com.pruebas.unitarias.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.pruebas.unitarias.entity.EstadoPedido;

import lombok.Data;

@Data
public class PedidoResponse {
    private Long id;
    private LocalDateTime fecha;
    private List<ItemResponse> items;
    private BigDecimal subtotal;
    private BigDecimal descuentoCodigo;
    private Integer descuentoUsuario;
    private BigDecimal total;
    private String codigoPromoAplicado;
    private EstadoPedido estado;
    
    @Data
    public static class ItemResponse {
        private Long id;
        private Long productoId;
        private String titulo;
        private String imagen;
        private String forma;
        private String tamanio;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }
    
}
