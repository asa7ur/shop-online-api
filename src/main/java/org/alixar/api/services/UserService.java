package org.alixar.api.services;

import org.alixar.api.dtos.UserDTO;
import org.alixar.api.entities.Order;
import org.alixar.api.entities.User;
import org.alixar.api.mappers.OrderMapper;
import org.alixar.api.mappers.UserMapper;
import org.alixar.api.repositories.OrderRepository;
import org.alixar.api.repositories.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    public List<UserDTO> list() {
        try {
            List<User> users = userRepository.findAll();
            return users.stream().map(userMapper::toDTO).toList();
        } catch (Exception e) {
            logger.error("Error al obtener todos los usuarios: {}", e.getMessage());
            throw new RuntimeException("Error al obtener todos los usuarios", e);
        }
    }

    public Optional<UserDTO> getUserById(Long id) {
        Optional<UserDTO> userDTOOpt = Optional.empty();
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                userDTOOpt = Optional.of(userMapper.toDTO(user.get()));
            } else {
                logger.info("No se encontró el usuario con id: {}", id);
            }
            return userDTOOpt;
        } catch (Exception e) {
            logger.error("Error al buscar el usuario: {}", e.getMessage());
            throw new RuntimeException("Error al buscar el usuario", e);
        }
    }

    public ResponseEntity<@NotNull UserDTO> saveUser(UserDTO userDTO) {
        try {
            if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
                logger.warn("Error al insertar un usuario");
                return ResponseEntity.badRequest().body(null);
            }
            User savedUsuario = userRepository.save(userMapper.toEntity(userDTO));
            logger.info("Usuario creado satisfactoriamente con id: {}", savedUsuario.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDTO(savedUsuario));
        } catch (Exception e) {
            logger.error("Error al crear el usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<?> deleteUser(Long id) {
        try {
            if (getUserById(id).isPresent()) {
                userRepository.deleteById(id);
                logger.info("Eliminada el usuario con id: {} satisfactoriamente", id);
                return ResponseEntity.ok().body(null);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error al eliminar el usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<?> getOrders(Long id) {
        try {
            if (getUserById(id).isPresent()) {
                List<Order> orders = orderRepository.findByUserId(id);
                return ResponseEntity.ok().body(orders.stream().map(orderMapper::toDTO).toList());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
