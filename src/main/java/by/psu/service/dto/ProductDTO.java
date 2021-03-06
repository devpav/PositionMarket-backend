package by.psu.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ProductDTO extends AbstractDTO {

    private BigDecimal price;
    private Long order;
    private UUID service;
    private UUID positionId;

}
