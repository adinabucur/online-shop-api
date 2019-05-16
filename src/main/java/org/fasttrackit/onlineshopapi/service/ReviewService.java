package org.fasttrackit.onlineshopapi.service;

import org.fasttrackit.onlineshopapi.domain.Product;
import org.fasttrackit.onlineshopapi.domain.Review;
import org.fasttrackit.onlineshopapi.exception.ResourceNotFoundException;
import org.fasttrackit.onlineshopapi.persistence.ReviewRepository;
import org.fasttrackit.onlineshopapi.transfer.review.CreateReviewRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, ProductService productService) {
        this.reviewRepository = reviewRepository;
        this.productService = productService;
    }

    @Transactional
    public Review createReview(CreateReviewRequest request) throws ResourceNotFoundException {
        Product product = productService.getProduct(request.getProductId());

        Review review = new Review();
        review.setContent(request.getContent());
        review.setProduct(product);

        return reviewRepository.save(review);
    }
}
