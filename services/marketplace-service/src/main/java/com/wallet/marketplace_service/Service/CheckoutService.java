package com.wallet.marketplace_service.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.wallet.marketplace_service.client.WalletClient;
import com.wallet.marketplace_service.dtos.OrderDto;
import com.wallet.marketplace_service.dtos.OrderItemDto;
import com.wallet.marketplace_service.dtos.CheckoutRequestDto;

import com.wallet.marketplace_service.Repository.CartItemRepository;
import com.wallet.marketplace_service.Repository.CartRepository;
import com.wallet.marketplace_service.Repository.InventoryRepository;
import com.wallet.marketplace_service.Repository.OrderItemRepository;
import com.wallet.marketplace_service.Repository.OrderRepository;
import com.wallet.marketplace_service.model.Cart;
import com.wallet.marketplace_service.model.CartItem;
import com.wallet.marketplace_service.model.Inventory;
import com.wallet.marketplace_service.model.Order;
import com.wallet.marketplace_service.model.OrderItem;
import com.wallet.marketplace_service.model.Product;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CheckoutService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;
 

    // checkout cart
@Transactional
public OrderDto checkout(Long buyerId, CheckoutRequestDto request) {
    Cart cart = cartRepository.findByBuyerId(buyerId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
    if (cart.getItems().isEmpty()) throw new RuntimeException("Cart is empty");

    Map<Long, BigDecimal> sellerAmounts = new HashMap<>();

    // Create Order WITH delivery location
    Order order = Order.builder()
            .buyerId(buyerId)
            .buyerName(request.getBuyerName()) // from JWT
            .buyerEmail(request.getBuyerEmail())
            .buyerPhone(request.getBuyerPhone())
            .deliveryLatitude(request.getDeliveryLatitude())
            .deliveryLongitude(request.getDeliveryLongitude())
            .status("PENDING_PAYMENT")
            .build();
    order = orderRepository.save(order);

    List<OrderItem> orderItems = new ArrayList<>();

    for (CartItem item : cart.getItems()) {
        Product product = item.getProduct();
        Inventory inv = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("No inventory for " + product.getTitle()));

        if (inv.getAvailableQuantity() < item.getQuantity())
            throw new RuntimeException("Insufficient stock for " + product.getTitle());

        // reserve stock
        inv.setAvailableQuantity(inv.getAvailableQuantity() - item.getQuantity());
        inv.setReservedQuantity(inv.getReservedQuantity() + item.getQuantity());
        inventoryRepository.save(inv);

        // create OrderItem
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .sellerId(product.getSellerId())
                .quantity(item.getQuantity())
                .price(product.getPrice())
                .build();
        orderItemRepository.save(orderItem);
        orderItems.add(orderItem);

        // track amounts per seller
        sellerAmounts.put(product.getSellerId(),
                sellerAmounts.getOrDefault(product.getSellerId(), BigDecimal.ZERO)
                        .add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))));
    }

    // set items in order entity for DTO
    order.setItems(orderItems);

    // call Wallet
    sellerAmounts.forEach((sellerId, amount) -> walletClient.transfer(buyerId, sellerId, amount));

    // clear cart
    cartItemRepository.deleteByCartId(cart.getId());

    BigDecimal totalAmount = orderItems.stream()
            .map(oi -> oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    // optional: save totalAmount in order if your entity has it
    order.setTotalAmount(totalAmount);
    orderRepository.save(order);

    List<OrderItemDto> orderItemDtos = orderItems.stream().map(oi ->
            OrderItemDto.builder()
                    .productId(oi.getProduct().getId())
                    .sellerId(oi.getSellerId())
                    .quantity(oi.getQuantity())
                    .price(oi.getPrice())
                    .build()
    ).toList();

    return OrderDto.builder()
            .id(order.getId())
            .buyerId(order.getBuyerId())
            .buyerName(order.getBuyerName())
            .buyerEmail(order.getBuyerEmail())
            .buyerPhone(order.getBuyerPhone())
            .deliveryLatitude(order.getDeliveryLatitude())
            .deliveryLongitude(order.getDeliveryLongitude())
            .status(order.getStatus())
            .totalAmount(totalAmount)
            .items(orderItemDtos)
            .build();
}
