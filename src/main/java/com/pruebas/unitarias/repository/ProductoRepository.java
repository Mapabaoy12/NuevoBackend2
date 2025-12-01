package com.pruebas.unitarias.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pruebas.unitarias.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrue();
    List<Producto> findByForma(String forma);
    List<Producto> findByTamanio(String tamanio);
    
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stock > 0")
    List<Producto> findProductosDisponibles();
    
}
