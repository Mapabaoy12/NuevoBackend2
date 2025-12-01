package com.pruebas.unitarias.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pruebas.unitarias.entity.CodigoPromocional;

@Repository
public interface CodigoPromocionalRepository extends JpaRepository<CodigoPromocional, Long>{
        Optional<CodigoPromocional> findByCodigo(String codigo);

    
}
