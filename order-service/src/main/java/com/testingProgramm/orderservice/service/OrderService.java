package com.testingProgramm.orderservice.service;

import com.testingProgramm.orderservice.dto.InventoryResponse;
import com.testingProgramm.orderservice.dto.OrderLineItemsDto;
import com.testingProgramm.orderservice.dto.OrderRequest;
import com.testingProgramm.orderservice.model.Order;
import com.testingProgramm.orderservice.model.OrderLineItems;
import com.testingProgramm.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsListDto()
                .stream()
                .map(this::mapToDTO)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = orderLineItems
                .stream()
                .map(OrderLineItems::getSkuCode)
                .toList();
        //call Inventory Service, and place order if product is in
        //stock
       InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
               .uri("http://inventory-service/api/inventory",
                       uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
               .retrieve()
               .bodyToMono(InventoryResponse[].class)
               .block();
//        assert inventoryResponseArray != null;
        assert inventoryResponseArray != null;
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::isInStock);
       if(allProductsInStock) {
           orderRepository.save(order);
           return "Order Placed";
       } else {
           throw new IllegalArgumentException("Product is not in stock, please try later");
       }

    }

    private OrderLineItems mapToDTO(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
