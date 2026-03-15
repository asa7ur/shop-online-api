package org.alixar.api.services;

import org.alixar.api.dtos.OrderDTO;
import org.alixar.api.dtos.OrderProductDTO;
import org.alixar.api.entities.Order;
import org.alixar.api.mappers.OrderMapper;
import org.alixar.api.mappers.OrderProductMapper;
import org.alixar.api.repositories.OrderProductRepository;
import org.alixar.api.repositories.OrderRepository;
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
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderProductMapper orderProductMapper;

    public List<OrderDTO> list() {
        try {
            List<Order> orders = orderRepository.findAll();
            return orders.stream().map(orderMapper::toDTO).toList();
        } catch (Exception e) {
            logger.error("Error al obtener todos los pedidos: {}", e.getMessage());
            throw new RuntimeException("Error al obtener todos los pedidos", e);
        }
    }

    public Optional<OrderDTO> getOrderById(Long id) {
        Optional<OrderDTO> orderDTOOpt = Optional.empty();
        try {
            Optional<Order> user = orderRepository.findById(id);
            if (user.isPresent()) {
                orderDTOOpt = Optional.of(orderMapper.toDTO(user.get()));
            } else {
                logger.info("No se encontró el pedido con id: {}", id);
            }
            return orderDTOOpt;
        } catch (Exception e) {
            logger.error("Error al buscar el ppedido: {}", e.getMessage());
            throw new RuntimeException("Error al buscar el pedido", e);
        }
    }

    public ResponseEntity<@NotNull OrderDTO> saveOrder(OrderDTO orderDTO) {
        try {
            Order savedOrder = orderRepository.save(orderMapper.toEntity(orderDTO));
            for (OrderProductDTO orderProductDTO : orderDTO.getOrderProducts()) {
                orderProductRepository.save(orderProductMapper.toEntity(orderProductDTO, savedOrder));
            }
            logger.info("Pedido creado satisfactoriamente con id: {}", savedOrder.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(getOrderById(savedOrder.getId()).get());
        } catch (Exception e) {
            logger.error("Error al crear el pedido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<?> deleteOrder(Long id) {
        try {
            if (getOrderById(id).isPresent()) {
                orderRepository.deleteById(id);
                logger.info("Eliminado el pedido con id: {} satisfactoriamente", id);
                return ResponseEntity.ok().body(null);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error al eliminar el pedido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
