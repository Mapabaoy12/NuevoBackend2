package com.pruebas.unitarias.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pruebas.unitarias.dto.CrearPedidoRequest;
import com.pruebas.unitarias.dto.PedidoResponse;
import com.pruebas.unitarias.entity.EstadoPedido;
import com.pruebas.unitarias.service.PedidoService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PedidoController {
    private final PedidoService pedidoService;
    
    @PostMapping
    public ResponseEntity<PedidoResponse> crearPedido(
            @RequestBody CrearPedidoRequest request) {
        PedidoResponse pedido = pedidoService.crearPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }
    
    @GetMapping
    public ResponseEntity<List<PedidoResponse>> obtenerTodosPedidos() {
        List<PedidoResponse> pedidos = pedidoService.obtenerTodosPedidos();
        return ResponseEntity.ok(pedidos);
    }
    
    @GetMapping("/usuario/{email}")
    public ResponseEntity<List<PedidoResponse>> obtenerPedidosPorUsuario(
            @PathVariable String email) {
        List<PedidoResponse> pedidos = pedidoService.obtenerPedidosPorUsuario(email);
        return ResponseEntity.ok(pedidos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerPedidoPorId(
            @PathVariable Long id) {
        PedidoResponse pedido = pedidoService.obtenerPedidoPorId(id);
        return ResponseEntity.ok(pedido);
    }
    
    @PutMapping("/{id}/estado")
    public ResponseEntity<PedidoResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestBody ActualizarEstadoRequest request) {
        PedidoResponse pedido = pedidoService.actualizarEstado(id, request.getEstado());
        return ResponseEntity.ok(pedido);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }
    
    @Data
    public static class ActualizarEstadoRequest {
        private EstadoPedido estado;
    }
}
