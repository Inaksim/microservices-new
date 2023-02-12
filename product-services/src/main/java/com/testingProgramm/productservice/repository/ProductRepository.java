package com.testingProgramm.productservice.repository;

import com.testingProgramm.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
