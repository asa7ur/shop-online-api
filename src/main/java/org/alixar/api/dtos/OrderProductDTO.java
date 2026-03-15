package org.alixar.api.dtos;

import lombok.Getter;
import lombok.Setter;
import org.alixar.api.entities.OrderProductId;

@Getter
@Setter
public class OrderProductDTO {
    private OrderProductId id;
    private ProductDTO product;
    private int quantity;
}
