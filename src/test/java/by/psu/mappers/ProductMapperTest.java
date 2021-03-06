package by.psu.mappers;

import by.psu.BaseTest;
import by.psu.model.postgres.*;
import by.psu.service.dto.ProductDTO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProductMapperTest extends BaseTest {

    @Autowired
    private ProductMapper productMapper;

    private Translate translate;

    @Before
    public void init() {
        translate = new Translate();

        StringValue stringValueRu = new StringValue(Language.RU.getUuid(), "RU", translate);
        StringValue stringValueEn = new StringValue(Language.EN.getUuid(), "EN", translate);

        translate.setValue(stringValueEn);
        translate.setValue(stringValueRu);
    }

    @Test
    public void testMapperProductToProductDTO() {
        Product product = new Product();

        TypeService typeService = new TypeService();
        typeService.setId(UUID.randomUUID());
        typeService.setTitle(translate);


        product.setPrice(new BigDecimal(1234));
        product.setId(UUID.randomUUID());
        product.setService(typeService);
        ProductDTO productDTO = productMapper.map(product);

        assertNotNull(productDTO);
        assertEquals(productDTO.getPrice(), product.getPrice());
    }


    @Test
    public void testMapperProductFromProductDTO() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setPrice(new BigDecimal(1234));

        Product product = productMapper.map( productDTO );

        assertNotNull(product);
        assertEquals(product.getPrice(), productDTO.getPrice());
        assertNotNull(product.getService());
    }

}
