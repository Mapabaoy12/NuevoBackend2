package com.pruebas.unitarias.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pruebas.unitarias.dto.CrearPedidoRequest;
import com.pruebas.unitarias.dto.PedidoResponse;
import com.pruebas.unitarias.entity.CodigoPromocional;
import com.pruebas.unitarias.entity.EstadoPedido;
import com.pruebas.unitarias.entity.Pedido;
import com.pruebas.unitarias.entity.PedidoItem;
import com.pruebas.unitarias.entity.Producto;
import com.pruebas.unitarias.entity.Usuario;
import com.pruebas.unitarias.repository.CodigoPromocionalRepository;
import com.pruebas.unitarias.repository.PedidoRepository;
import com.pruebas.unitarias.repository.ProductoRepository;
import com.pruebas.unitarias.repository.UsuarioRepository;
import com.pruebas.unitarias.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {
    
private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final CodigoPromocionalRepository codigoPromocionalRepository;
    private final DescuentoService descuentoService;
    
    @Transactional
    public PedidoResponse crearPedido(CrearPedidoRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getUsuarioEmail())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        
        // Crear items del pedido
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CrearPedidoRequest.ItemRequest itemReq : request.getItems()) {
            Producto producto = productoRepository.findById(itemReq.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
            
            // Verificar stock
            if (producto.getStock() < itemReq.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para " + producto.getTitulo());
            }
            
            PedidoItem item = new PedidoItem();
            item.setPedido(pedido);
            item.setProducto(producto);
            item.setCantidad(itemReq.getCantidad());
            item.setPrecioUnitario(producto.getPrecio());
            
            BigDecimal itemSubtotal = producto.getPrecio()
                .multiply(BigDecimal.valueOf(itemReq.getCantidad()));
            item.setSubtotal(itemSubtotal);
            
            // Guardar datos del producto
            item.setTitulo(producto.getTitulo());
            item.setImagen(producto.getImagen());
            item.setForma(producto.getForma());
            item.setTamanio(producto.getTamanio());
            item.setDescripcion(producto.getDescripcion());
            
            pedido.getItems().add(item);
            subtotal = subtotal.add(itemSubtotal);
            
            // Actualizar stock
            producto.setStock(producto.getStock() - itemReq.getCantidad());
            productoRepository.save(producto);
        }
        
        pedido.setSubtotal(subtotal);
        
        // Aplicar descuento por código promocional
        BigDecimal descuentoCodigo = BigDecimal.ZERO;
        if (request.getCodigoPromo() != null) {
            CodigoPromocional codigo = codigoPromocionalRepository
                .findByCodigo(request.getCodigoPromo())
                .orElse(null);
            
            if (codigo != null && codigo.getActivo()) {
                descuentoCodigo = subtotal
                    .multiply(BigDecimal.valueOf(codigo.getDescuento()))
                    .divide(BigDecimal.valueOf(100));
                pedido.setCodigoPromoAplicado(codigo.getCodigo());
                
                // Incrementar usos
                codigo.setUsosActuales(codigo.getUsosActuales() + 1);
                codigoPromocionalRepository.save(codigo);
            }
        }
        pedido.setDescuentoCodigo(descuentoCodigo);
        
        // Aplicar descuento de usuario
        pedido.setDescuentoUsuario(usuario.getDescuentoPorcentaje());
        BigDecimal descuentoUsuario = subtotal
            .multiply(BigDecimal.valueOf(usuario.getDescuentoPorcentaje()))
            .divide(BigDecimal.valueOf(100));
        
        // Calcular total
        BigDecimal total = subtotal
            .subtract(descuentoCodigo)
            .subtract(descuentoUsuario);
        pedido.setTotal(total.max(BigDecimal.ZERO));
        
        Pedido savedPedido = pedidoRepository.save(pedido);
        return convertirAPedidoResponse(savedPedido);
    }
    
    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerTodosPedidos() {
        return pedidoRepository.findAllByOrderByFechaDesc()
            .stream()
            .map(this::convertirAPedidoResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerPedidosPorUsuario(String email) {
        return pedidoRepository.findByUsuarioEmailOrderByFechaDesc(email)
            .stream()
            .map(this::convertirAPedidoResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PedidoResponse obtenerPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        return convertirAPedidoResponse(pedido);
    }
    
    @Transactional
    public PedidoResponse actualizarEstado(Long pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        
        // Si se cancela, devolver stock
        if (nuevoEstado == EstadoPedido.CANCELADO && 
            pedido.getEstado() != EstadoPedido.CANCELADO) {
            for (PedidoItem item : pedido.getItems()) {
                Producto producto = item.getProducto();
                producto.setStock(producto.getStock() + item.getCantidad());
                productoRepository.save(producto);
            }
        }
        
        pedido.setEstado(nuevoEstado);
        Pedido updatedPedido = pedidoRepository.save(pedido);
        return convertirAPedidoResponse(updatedPedido);
    }
    
    @Transactional
    public void eliminarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        
        // Devolver stock si no está cancelado
        if (pedido.getEstado() != EstadoPedido.CANCELADO) {
            for (PedidoItem item : pedido.getItems()) {
                Producto producto = item.getProducto();
                producto.setStock(producto.getStock() + item.getCantidad());
                productoRepository.save(producto);
            }
        }
        
        pedidoRepository.delete(pedido);
    }
    
    private PedidoResponse convertirAPedidoResponse(Pedido pedido) {
    PedidoResponse response = new PedidoResponse();
    response.setId(pedido.getId());
    response.setFecha(pedido.getFecha());
    response.setSubtotal(pedido.getSubtotal());
    response.setDescuentoCodigo(pedido.getDescuentoCodigo());
    response.setDescuentoUsuario(pedido.getDescuentoUsuario());
    response.setTotal(pedido.getTotal());
    response.setCodigoPromoAplicado(pedido.getCodigoPromoAplicado());
    response.setEstado(pedido.getEstado());
    
    // Convertir items
    List<PedidoResponse.ItemResponse> itemsResponse = pedido.getItems().stream()
        .map(item -> {
            PedidoResponse.ItemResponse itemResp = new PedidoResponse.ItemResponse();
            itemResp.setId(item.getId());
            itemResp.setProductoId(item.getProducto().getId());
            itemResp.setTitulo(item.getTitulo());
            itemResp.setImagen(item.getImagen());
            itemResp.setForma(item.getForma());
            itemResp.setTamanio(item.getTamanio());
            itemResp.setCantidad(item.getCantidad());
            itemResp.setPrecioUnitario(item.getPrecioUnitario());
            itemResp.setSubtotal(item.getSubtotal());
            return itemResp;
        })
        .collect(Collectors.toList());
    
    response.setItems(itemsResponse);
    return response;
    }
}
