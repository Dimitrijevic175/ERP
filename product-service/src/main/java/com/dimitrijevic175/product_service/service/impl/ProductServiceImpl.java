package com.dimitrijevic175.product_service.service.impl;

import com.dimitrijevic175.product_service.configuration.ProductSpecification;
import com.dimitrijevic175.product_service.domain.Category;
import com.dimitrijevic175.product_service.domain.Product;
import com.dimitrijevic175.product_service.dto.*;
import com.dimitrijevic175.product_service.exceptions.CategoryNotFoundException;
import com.dimitrijevic175.product_service.exceptions.ProductNotFoundException;
import com.dimitrijevic175.product_service.exceptions.SkuNotFoundException;
import com.dimitrijevic175.product_service.mapper.ProductMapper;
import com.dimitrijevic175.product_service.repository.*;
import com.dimitrijevic175.product_service.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {

        if (productRepository.existsBySku(request.getSku())) {
            throw new SkuNotFoundException(request.getSku());
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setBrand(request.getBrand());
        product.setPurchasePrice(request.getPurchasePrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setTaxRate(request.getTaxRate());
        product.setQuantity(request.getQuantity());
        product.setMinQuantity(request.getMinQuantity());
        product.setMaxQuantity(request.getMaxQuantity());
        product.setUnitOfMeasure(request.getUnitOfMeasure());
        product.setActive(true);

        Product saved = productRepository.save(product);

        return ProductMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));
        }

        ProductMapper.updateEntity(product, request, category);

        Product updated = productRepository.save(product);

        return ProductMapper.toResponse(updated);
    }
    @Override
    @Transactional
    public ProductResponse updateProductStatus(Long id, Boolean active) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setActive(active);
        return ProductMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductStockResponse getProductStock(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return new ProductStockResponse(product.getId(), product.getQuantity());
    }

    @Override
    public Page<ProductResponse> getProducts(ProductSearchRequest request, Pageable pageable) {

        Specification<Product> spec = Specification
                .where(ProductSpecification.hasName(request.getName()))
                .and(ProductSpecification.hasSku(request.getSku()))
                .and(ProductSpecification.inCategory(request.getCategoryId()))
                .and(ProductSpecification.hasBrand(request.getBrand()))
                .and(ProductSpecification.minPrice(request.getPriceMin()))
                .and(ProductSpecification.maxPrice(request.getPriceMax()))
                .and(ProductSpecification.isActive(request.getActive()));

        Page<Product> page = productRepository.findAll(spec, pageable);

        return page.map(ProductMapper::toResponse);
    }
}
