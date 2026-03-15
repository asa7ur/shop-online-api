package org.alixar.api.controllers;

import org.alixar.api.dtos.UserDTO;
import org.alixar.api.services.UserService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<@NotNull List<UserDTO>> list() {
        logger.info("Solicitando la lista de todos los usuarios ...");
        try {
            List<UserDTO> users = userService.list();
            return ResponseEntity.ok().body(users);
        } catch (Exception e) {
            logger.error("Error al listar los usuarios: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NotNull UserDTO> getCategoryById(@PathVariable Long id) {
        logger.info("Solicitando el producto con id: {}", id);
        try {
            Optional<UserDTO> userDTO = userService.getUserById(id);
            return userDTO.map(dto -> ResponseEntity.ok().body(dto)).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error al obtener el usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<@NotNull UserDTO> save(@RequestBody UserDTO userDTO) {
        logger.info("Insertando nuevo usuario con nombre : {}", userDTO.getEmail());
        try {
            return userService.saveUser(userDTO);
        } catch (Exception e) {
            logger.error("Error al crear el usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("Eliminando el usuario con id: {}", id);
        return userService.deleteUser(id);
    }

    @DeleteMapping("/{id}/orders")
    public ResponseEntity<?> getOrders(@PathVariable Long id) {
        logger.info("Solicitando la lista de todos los pedidos del usuario con id: {} ...", id);
        try {
            return userService.getOrders(id);
        } catch (Exception e) {
            logger.error("Error al obtener los pedidos del usuario con id:{}. Error: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
