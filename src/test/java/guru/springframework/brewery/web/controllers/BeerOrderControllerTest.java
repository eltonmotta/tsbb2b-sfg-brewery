package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerOrderService;
import guru.springframework.brewery.web.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(BeerOrderController.class)
class BeerOrderControllerTest {

    @MockBean
    BeerOrderService beerOrderService;

    @Autowired
    MockMvc mockMvc;

    BeerDto validBeer;
    BeerOrderDto beerOrder;
    BeerOrderPagedList beerOrderPagedList;

    @BeforeEach
    void setUp() {
        validBeer = BeerDto.builder().id(UUID.randomUUID())
                .version(1)
                .beerName("Beer1")
                .beerStyle(BeerStyleEnum.PALE_ALE)
                .price(new BigDecimal("12.99"))
                .quantityOnHand(4)
                .upc(1234567546234L)
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .build();

        beerOrder = BeerOrderDto.builder()
                .id(UUID.randomUUID())
                .customerRef("1234")
                .beerOrderLines(List.of(BeerOrderLineDto.builder()
                    .beerId(validBeer.getId())
                    .build()))
                .build();

        beerOrderPagedList = new BeerOrderPagedList(List.of(beerOrder),
                PageRequest.of(1, 1), 1L);
    }

    @AfterEach
    void tearDown() {
        reset(beerOrderService);
    }

    @Test
    void testListOrders() throws Exception {
        given(beerOrderService.listOrders(any(), any())).willReturn(beerOrderPagedList);

        mockMvc.perform(get("/api/v1/customers/21b6a8aa-1805-4d6d-9e1c-1a63f3a4c3ee/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    void testGetOrder() throws Exception {
        given(beerOrderService.getOrderById(any(), any())).willReturn(beerOrder);

        mockMvc.perform(get("/api/v1/customers/21b6a8aa-1805-4d6d-9e1c-1a63f3a4c3ee/orders/ae73ff9d-03cc-4d65-b966-824e561d748f"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }
}
