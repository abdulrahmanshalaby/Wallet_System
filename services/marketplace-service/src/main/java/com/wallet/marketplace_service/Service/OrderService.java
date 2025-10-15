package com.wallet.marketplace_service.Service;
import java.util.List;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import com.wallet.marketplace_service.Repository.OrderRepository;
import com.wallet.marketplace_service.dtos.OrderDto;
import com.wallet.marketplace_service.dtos.OrderItemDto;
import com.wallet.marketplace_service.model.Order;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    // mark order as delivered
    public void markAsDelivered(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getStatus().equals("PAID") && !order.getStatus().equals("SHIPPED"))
            throw new RuntimeException("Order cannot be marked delivered in current status");

        order.setStatus("DELIVERED");
        orderRepository.save(order);
    }

    public List<OrderDto> getOrdersByBuyer(Long buyerId) {
        return orderRepository.findByBuyerId(buyerId).stream().map(order -> {
            List<OrderItemDto> items = order.getItems().stream().map(oi ->
                    OrderItemDto.builder()
                            .productId(oi.getProduct().getId())
                            .sellerId(oi.getSellerId())
                            .quantity(oi.getQuantity())
                            .price(oi.getPrice())
                            .build()
            ).toList();

            BigDecimal totalAmount = items.stream()
                    .map(oi -> oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return OrderDto.builder()
                    .id(order.getId())
                    .buyerId(order.getBuyerId())
                    .status(order.getStatus())
                    .items(items)
                    .totalAmount(totalAmount)
                    .build();
        }).toList();
    }
}
