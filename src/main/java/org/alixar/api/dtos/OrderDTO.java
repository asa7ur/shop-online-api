package org.alixar.api.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderDTO {
    private Long id;
    private UserDTO user;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private List<OrderProductDTO> orderProducts = new ArrayList<>();
}
