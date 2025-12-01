package com.pruebas.unitarias.dto;

import java.util.List;

import lombok.Data;

@Data
public class CrearPedidoRequest {
     private String usuarioEmail;
    private List<ItemRequest> items;
    private String codigoPromo;
    
    @Data
    public static class ItemRequest {
        private Long productoId;
        private Integer cantidad;
    }
    
}
