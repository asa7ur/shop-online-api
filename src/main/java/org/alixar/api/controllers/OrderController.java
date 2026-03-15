package org.alixar.api.controllers;

import org.alixar.api.dtos.OrderDTO;
import org.alixar.api.services.OrderService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @GetMapping("/")
    public ResponseEntity<@NotNull List<OrderDTO>> list() {
        logger.info("Solicitando la lista de todos los pedidos ...");
        try {
            List<OrderDTO> users = orderService.list();
            return ResponseEntity.ok().body(users);
        } catch (Exception e) {
            logger.error("Error al listar los pedidos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NotNull OrderDTO> getCategoryById(@PathVariable Long id) {
        logger.info("Solicitando el pedido con id: {}", id);
        try {
            Optional<OrderDTO> userDTO = orderService.getOrderById(id);
            return userDTO.map(dto -> ResponseEntity.ok().body(dto)).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error al obtener el pedido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<@NotNull OrderDTO> save(@RequestBody OrderDTO orderDTO) {
        logger.info("Insertando nuevo pedido para el usuario con id : {}", orderDTO.getUser().getId());
        try {
            return orderService.saveOrder(orderDTO);
        } catch (Exception e) {
            logger.error("Error al crear el pedido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("Eliminando el pedido con id: {}", id);
        return orderService.deleteOrder(id);
    }
}
