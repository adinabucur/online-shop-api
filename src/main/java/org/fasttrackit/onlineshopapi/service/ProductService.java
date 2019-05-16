package org.fasttrackit.onlineshopapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fasttrackit.onlineshopapi.domain.Product;
import org.fasttrackit.onlineshopapi.exception.ResourceNotFoundException;
import org.fasttrackit.onlineshopapi.persistence.ProductRepository;
import org.fasttrackit.onlineshopapi.transfer.product.CreateProductRequest;
import org.fasttrackit.onlineshopapi.transfer.product.GetProductsRequest;
import org.fasttrackit.onlineshopapi.transfer.product.ProductResponse;
import org.fasttrackit.onlineshopapi.transfer.product.UpdateProductRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
    }

    public Product createProduct(CreateProductRequest request) {
        LOGGER.info("Creating product {}", request);

        Product product = objectMapper.convertValue(request, Product.class);
        return productRepository.save(product);

    }

    public Product getProduct(long id) throws ResourceNotFoundException {
        LOGGER.info("Retrieving product {}", id);
        //optional and lambda expression
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product" + id + " not found"));

    }

    public Page<ProductResponse> getProducts(GetProductsRequest request, Pageable pageable) {
        LOGGER.info("Retrieving products >> {}", request);
        Page<Product> products;

        if (request.getPartialName() != null && request.getMinimumQuantity() != null &&
                request.getMinimumPrice() != null &&
                request.getMaximumPrice() != null) {
            products = productRepository.findByNameContainingAndQuantityGreaterThanEqualAndPriceBetween(
                    request.getPartialName(), request.getMinimumQuantity(), request.getMinimumPrice(),
                    request.getMaximumPrice(), pageable);

        } else if (request.getMinimumQuantity() != null &&
                request.getMinimumPrice() != null &&
                request.getMaximumPrice() != null) {
            products = productRepository.findByQuantityGreaterThanEqualAndPriceBetween(request.getMinimumQuantity(),
                    request.getMinimumPrice(), request.getMaximumPrice(),
                    pageable);

        } else if (request.getPartialName() != null &&
                request.getMinimumQuantity() != null) {
            products = productRepository.findByNameContainingAndQuantityGreaterThanEqual(
                    request.getPartialName(), request.getMinimumQuantity(), pageable);

        } else if (request.getPartialName() != null) {
            products = productRepository.findByNameContaining(request.getPartialName(), pageable);
        } else {
            products = productRepository.findAll(pageable);
        }
        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : products.getContent()) {
            ProductResponse productResponse = new ProductResponse();
            productResponse.setId(product.getId());
            productResponse.setName(product.getName());
            productResponse.setPrice(product.getPrice());
            productResponse.setQuantity(product.getQuantity());
            productResponse.setSku(product.getSku());

            productResponses.add(productResponse);

        }
        return new PageImpl<>(productResponses, pageable, products.getTotalElements());

    }


    public Product updateProduct(long id, UpdateProductRequest request) throws ResourceNotFoundException {
        LOGGER.info("Updating product {}, {}", id, request);
        Product product = getProduct(id);

        BeanUtils.copyProperties(request, product);

        return productRepository.save(product);
    }

    public void deleteProduct(long id) {
        LOGGER.info("Deleting product {}", id);
        productRepository.deleteById(id);

        LOGGER.info("Deleted product {}", id);


    }
}
