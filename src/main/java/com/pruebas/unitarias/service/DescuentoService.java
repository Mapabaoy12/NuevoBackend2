package com.pruebas.unitarias.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.pruebas.unitarias.entity.Usuario;

@Service
public class DescuentoService {
    public int calcularDescuentoTotal(Usuario usuario) {
        int descuento = 0;
        
        // Descuento Duoc UC
        if (usuario.getEsDuocUC()) {
            descuento += 5;
        }
        
        // Descuento mayores de 50
        if (usuario.getEsMayorDe50()) {
            descuento += 10;
        }
        
        // Descuento FELICES50
        if (usuario.getTieneDescuentoFelices50()) {
            descuento += 10;
        }
        
        return descuento;
    }
    
    public boolean esCumpleanosHoy(Usuario usuario) {
        LocalDate hoy = LocalDate.now();
        LocalDate nacimiento = usuario.getFechaNacimiento();
        
        return hoy.getMonth() == nacimiento.getMonth() && 
               hoy.getDayOfMonth() == nacimiento.getDayOfMonth();
    }
    
    public boolean puedeUsarTortaGratis(Usuario usuario) {
        if (!usuario.getTortaGratisCumpleanosDisponible() || 
            usuario.getTortaGratisCumpleanosUsada()) {
            return false;
        }
        
        // Verificar si ya la usó este año
        if (usuario.getAnioTortaGratisCumpleanos() != null) {
            int anioActual = LocalDate.now().getYear();
            return usuario.getAnioTortaGratisCumpleanos() < anioActual;
        }
        
        return true;
    }
}
