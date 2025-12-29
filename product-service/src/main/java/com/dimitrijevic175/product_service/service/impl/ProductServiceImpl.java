package com.dimitrijevic175.product_service.service.impl;

import com.dimitrijevic175.product_service.configuration.ExcelUtils;
import com.dimitrijevic175.product_service.configuration.ProductExcelMapper;
import com.dimitrijevic175.product_service.configuration.ProductSpecification;
import com.dimitrijevic175.product_service.domain.Category;
import com.dimitrijevic175.product_service.domain.Product;
import com.dimitrijevic175.product_service.dto.*;
import com.dimitrijevic175.product_service.exceptions.CategoryNotFoundException;
import com.dimitrijevic175.product_service.exceptions.NotEnoughStockException;
import com.dimitrijevic175.product_service.exceptions.ProductNotFoundException;
import com.dimitrijevic175.product_service.exceptions.SkuNotFoundException;
import com.dimitrijevic175.product_service.mapper.ProductMapper;
import com.dimitrijevic175.product_service.repository.*;
import com.dimitrijevic175.product_service.service.ProductService;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Integer getMinQuantity(Long productId) {
        return productRepository.findById(productId)
                .map(Product::getMinQuantity)
                .orElse(null);
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

    @Override
    public ImportResult importProducts(MultipartFile file) {

        ImportResult.ImportResultBuilder resultBuilder = ImportResult.builder()
                .totalRows(0)
                .successfulImports(0)
                .failedImports(0)
                .errors(new ArrayList<>());

        if (file.isEmpty()) {
            resultBuilder.errors(List.of("File is empty"));
            return resultBuilder.build();
        }

        ExcelUtils excel = ExcelUtils.getInstance();
        ProductExcelMapper mapper = ProductExcelMapper.getInstance();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getPhysicalNumberOfRows() - 1;
            resultBuilder.totalRows(totalRows);

            List<String> skus = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String sku = excel.getCellString(row.getCell(1));
                if (sku != null) {
                    skus.add(sku);
                }
            }

            List<Product> existing = productRepository.findAllBySkuIn(skus);

            Map<String, Product> existingMap = existing.stream()
                    .collect(Collectors.toMap(Product::getSku, p -> p));

            List<Product> productsToSave = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String sku = excel.getCellString(row.getCell(1));

                    if (sku == null || sku.isBlank()) {
                        throw new IllegalArgumentException("SKU is required");
                    }

                    Product product;

                    if (existingMap.containsKey(sku)) {
                        // UPDATE
                        product = existingMap.get(sku);
                        mapper.updateProductFromRow(product, row, categoryRepository);

                    } else {
                        // NEW PRODUCT
                        product = mapper.mapRowToProduct(row, categoryRepository);
                    }

                    productsToSave.add(product);

                    // increment success count
                    resultBuilder.successfulImports(resultBuilder.build().getSuccessfulImports() + 1);

                } catch (Exception e) {

                    int failed = resultBuilder.build().getFailedImports() + 1;
                    resultBuilder.failedImports(failed);

                    List<String> errors = new ArrayList<>(resultBuilder.build().getErrors());
                    errors.add("Row " + (i + 1) + ": " + e.getMessage());
                    resultBuilder.errors(errors);
                }
            }

            productRepository.saveAll(productsToSave);

        } catch (Exception e) {
            resultBuilder.errors(
                    List.of("Failed to read Excel file: " + e.getMessage())
            );
        }

        return resultBuilder.build();
    }

}
