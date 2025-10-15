package com.wallet.marketplace_service.Service;
import org.springframework.stereotype.Service;

import com.wallet.marketplace_service.Repository.OrderItemRepository;
import com.wallet.marketplace_service.Repository.ReviewRepository;
import com.wallet.marketplace_service.dtos.ReviewDto;
import com.wallet.marketplace_service.model.OrderItem;
import com.wallet.marketplace_service.model.Review;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;

    public ReviewDto addReview(Long orderItemId, Long buyerId, int rating, String comment) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));

        if (!orderItem.getOrder().getStatus().equals("DELIVERED"))
            throw new RuntimeException("Cannot review undelivered item");

        Review review = Review.builder()
                .orderItem(orderItem)
                .buyerId(buyerId)
                .rating(rating)
                .comment(comment)
                .build();

        reviewRepository.save(review);

        return ReviewDto.builder()
                .orderItemId(orderItemId)
                .buyerId(buyerId)
                .rating(rating)
                .comment(comment)
                .build();
    }
}
