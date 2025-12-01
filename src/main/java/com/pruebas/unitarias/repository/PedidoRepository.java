package com.pruebas.unitarias.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pruebas.unitarias.entity.EstadoPedido;
import com.pruebas.unitarias.entity.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>{
    List<Pedido> findByUsuarioEmailOrderByFechaDesc(String email);
    List<Pedido> findByEstadoOrderByFechaDesc(EstadoPedido estado);
    List<Pedido> findAllByOrderByFechaDesc();
    
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = :estado")
    Long countByEstado(EstadoPedido estado);
    
    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.estado = 'COMPLETADO'")
    BigDecimal calcularVentasTotales();
    
}
