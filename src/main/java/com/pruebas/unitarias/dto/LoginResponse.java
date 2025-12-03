package com.pruebas.unitarias.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private boolean success;
    private String message;
    private UsuarioResponse usuario;
    private boolean isAdmin;

    public static LoginResponse exitoso(UsuarioResponse usuario, boolean isAdmin){
        LoginResponse response = new LoginResponse();
        response.setSuccess(true);
        response.setMessage("Login exitoso");
        response.setUsuario(usuario);
        response.setAdmin(isAdmin);
        return response;
    }

    public static LoginResponse fallido(String mensaje){
        LoginResponse response = new LoginResponse();
        response.setSuccess(false);
        response.setMessage(mensaje);
        response.setUsuario(null);
        response.setAdmin(false);
        return response;
    }


    
}
