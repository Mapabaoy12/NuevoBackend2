package com.pruebas.unitarias.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pruebas.unitarias.dto.CrearUsuarioRequest;
import com.pruebas.unitarias.dto.LoginRequest;
import com.pruebas.unitarias.dto.LoginResponse;
import com.pruebas.unitarias.dto.UsuarioResponse;
import com.pruebas.unitarias.entity.Usuario;
import com.pruebas.unitarias.repository.UsuarioRepository;
import com.pruebas.unitarias.exception.ResourceNotFoundException;
import com.pruebas.unitarias.exception.DuplicateResourceException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    public static final String admin_email = "matias@administrador.cl";
    public static final String admin_password = "admin1234";
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> obtenerTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
        return convertirAResponse(usuario);
    }
    @Transactional(readOnly = true)
    public LoginResponse Login(LoginRequest request) {
        if (admin_email.equalsIgnoreCase((request.getEmail()))){
            if (admin_password.equals(request.getPassword())) {
                UsuarioResponse adminResponse = new UsuarioResponse();
                adminResponse.setNombre("Administrador");
                adminResponse.setEmail(admin_email);
                return LoginResponse.exitoso(adminResponse, true);
                
            }else{
                return LoginResponse.fallido("password incorrecta");
                
            }
        }
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElse(null);
        if (usuario == null){
            return LoginResponse.fallido("Usuario no encontrado");
        }
        if (!request.getPassword().equals(usuario.getPassword())) {
            return LoginResponse.fallido("Password incorrecta");
            
        }
        return LoginResponse.exitoso(convertirAResponse(usuario), false);
    }
    
    @Transactional
    public UsuarioResponse crear(CrearUsuarioRequest request) {
        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Ya existe un usuario con el email: " + request.getEmail());
        }
        
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        usuario.setFechaNacimiento(LocalDate.parse(request.getFechaNacimiento()));
        usuario.setDireccion(request.getDireccion());
        usuario.setCodigoPromocional(request.getCodigoPromocional());
        
        // Calcular atributos automáticos
        calcularAtributosUsuario(usuario);
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return convertirAResponse(usuarioGuardado);
    }
    
    @Transactional
    public UsuarioResponse actualizar(String email, CrearUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
        
        // Actualizar campos
        usuario.setNombre(request.getNombre());
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());
        
        // Si cambió la fecha de nacimiento, recalcular edad
        if (request.getFechaNacimiento() != null && 
            !request.getFechaNacimiento().equals(usuario.getFechaNacimiento().toString())) {
            usuario.setFechaNacimiento(LocalDate.parse(request.getFechaNacimiento()));
            calcularEdad(usuario);
        }
        
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return convertirAResponse(usuarioActualizado);
    }
    
    @Transactional
    public void eliminar(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
        usuarioRepository.delete(usuario);
    }
    
    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    
    // Métodos auxiliares
    
    private void calcularAtributosUsuario(Usuario usuario) {
        // Verificar si es correo Duoc UC
        String emailLower = usuario.getEmail().toLowerCase();
        usuario.setEsDuocUC(emailLower.endsWith("@duoc.cl") || emailLower.endsWith("@duocuc.cl"));
        
        // Calcular edad y verificar si es mayor de 50
        calcularEdad(usuario);
        
        // Verificar código FELICES50
        if ("FELICES50".equalsIgnoreCase(usuario.getCodigoPromocional())) {
            usuario.setTieneDescuentoFelices50(true);
        }
        
        // Calcular descuento total
        calcularDescuentoTotal(usuario);
        
        // Verificar torta gratis de cumpleaños
        if (esCumpleanosHoy(usuario.getFechaNacimiento())) {
            usuario.setTortaGratisCumpleanosDisponible(true);
        }
    }
    
    private void calcularEdad(Usuario usuario) {
        LocalDate hoy = LocalDate.now();
        int edad = Period.between(usuario.getFechaNacimiento(), hoy).getYears();
        usuario.setEsMayorDe50(edad >= 50);
    }
    
    private void calcularDescuentoTotal(Usuario usuario) {
        int descuento = 0;
        
        // Descuento Duoc UC: 5%
        if (usuario.getEsDuocUC()) {
            descuento += 5;
        }
        
        // Descuento mayores de 50: 10%
        if (usuario.getEsMayorDe50()) {
            descuento += 10;
        }
        
        // Descuento código FELICES50: 10%
        if (usuario.getTieneDescuentoFelices50()) {
            descuento += 10;
        }
        
        usuario.setDescuentoPorcentaje(descuento);
    }
    
    private boolean esCumpleanosHoy(LocalDate fechaNacimiento) {
        LocalDate hoy = LocalDate.now();
        return hoy.getMonth() == fechaNacimiento.getMonth() && 
               hoy.getDayOfMonth() == fechaNacimiento.getDayOfMonth();
    }
    
        private UsuarioResponse convertirAResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setEmail(usuario.getEmail());
        response.setPassword(usuario.getPassword());
        response.setTelefono(usuario.getTelefono());
        response.setFechaNacimiento(usuario.getFechaNacimiento().toString());
        response.setDireccion(usuario.getDireccion());
        response.setCodigoPromocional(usuario.getCodigoPromocional());
        response.setEsDuocUC(usuario.getEsDuocUC());
        response.setEsMayorDe50(usuario.getEsMayorDe50());
        response.setTieneDescuentoFelices50(usuario.getTieneDescuentoFelices50());
        response.setDescuentoPorcentaje(usuario.getDescuentoPorcentaje());
        response.setTortaGratisCumpleanosDisponible(usuario.getTortaGratisCumpleanosDisponible());
        response.setTortaGratisCumpleanosUsada(usuario.getTortaGratisCumpleanosUsada());
        response.setAnioTortaGratisCumpleanos(usuario.getAnioTortaGratisCumpleanos());
        return response;
    }
}
