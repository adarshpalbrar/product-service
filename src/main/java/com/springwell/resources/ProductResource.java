package com.springwell.resources;

import com.springwell.beans.Product;
import com.springwell.dto.Coupon;
import com.springwell.repos.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/product-api")
public class ProductResource {

    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;

    @Value("${coupon-service.url}")
    private String couponServiceUrl;

    @Autowired
    public ProductResource(ProductRepository productRepository, RestTemplate restTemplate) {
        this.productRepository = productRepository;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/products")
    public Product createProduct(@RequestBody Product product) {
        Coupon coupon = restTemplate.getForObject(couponServiceUrl + product.getCouponCode(), Coupon.class);
        if (coupon != null) {
            product.setPrice(product.getPrice().subtract(
                    coupon.getDiscount() == null ? BigDecimal.ZERO : coupon.getDiscount()
            ));
        }
        return productRepository.save(product);
    }

    @GetMapping("/products")
    public List<Product> findAll() {
        return productRepository.findAll();
    }

}
