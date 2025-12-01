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

import com.pruebas.unitarias.dto.CrearUsuarioRequest;
import com.pruebas.unitarias.dto.UsuarioResponse;
import com.pruebas.unitarias.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    /**
     * Obtener todos los usuarios (Admin)
     * GET /api/usuarios
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> obtenerTodos() {
        List<UsuarioResponse> usuarios = usuarioService.obtenerTodos();
        return ResponseEntity.ok(usuarios);
    }
    
    /**
     * Obtener usuario por email
     * GET /api/usuarios/{email}
     */
    @GetMapping("/{email}")
    public ResponseEntity<UsuarioResponse> obtenerPorEmail(@PathVariable String email) {
        UsuarioResponse usuario = usuarioService.obtenerPorEmail(email);
        return ResponseEntity.ok(usuario);
    }
    
    /**
     * Crear nuevo usuario (Registro)
     * POST /api/usuarios
     */
    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody CrearUsuarioRequest request) {
        UsuarioResponse usuario = usuarioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }
    
    /**
     * Actualizar usuario
     * PUT /api/usuarios/{email}
     */
    @PutMapping("/{email}")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable String email,
            @Valid @RequestBody CrearUsuarioRequest request) {
        UsuarioResponse usuario = usuarioService.actualizar(email, request);
        return ResponseEntity.ok(usuario);
    }
    
    /**
     * Eliminar usuario
     * DELETE /api/usuarios/{email}
     */
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> eliminar(@PathVariable String email) {
        usuarioService.eliminar(email);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Verificar si existe un usuario por email
     * GET /api/usuarios/{email}/existe
     */
    @GetMapping("/{email}/existe")
    public ResponseEntity<Boolean> existe(@PathVariable String email) {
        boolean existe = usuarioService.existePorEmail(email);
        return ResponseEntity.ok(existe);
    }
}