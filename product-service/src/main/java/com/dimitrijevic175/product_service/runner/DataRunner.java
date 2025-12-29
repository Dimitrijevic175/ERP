package com.dimitrijevic175.product_service.runner;

import com.dimitrijevic175.product_service.domain.Category;
import com.dimitrijevic175.product_service.domain.Product;
import com.dimitrijevic175.product_service.repository.CategoryRepository;
import com.dimitrijevic175.product_service.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataRunner implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataRunner(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Proveri da li već postoji data
        if (categoryRepository.count() > 0) return;

        // Glavna kategorija: Građevinski materijal
        Category constructionMaterials = new Category();
        constructionMaterials.setName("Građevinski materijal");
        constructionMaterials.setDescription("Svi građevinski materijali za gradnju i renoviranje");
        categoryRepository.save(constructionMaterials);

        // Podkategorija: Cement
        Category cement = new Category();
        cement.setName("Cement");
        cement.setDescription("Različite vrste cementa za gradnju");
        cement.setParentCategory(constructionMaterials);
        constructionMaterials.getSubCategories().add(cement);
        categoryRepository.save(cement);

        // Podkategorija: Pesak
        Category sand = new Category();
        sand.setName("Pesak");
        sand.setDescription("Pesak za beton, malter i druge namene");
        sand.setParentCategory(constructionMaterials);
        constructionMaterials.getSubCategories().add(sand);
        categoryRepository.save(sand);

        // Proizvodi u Cement kategoriji
        Product portlandCement = new Product();
        portlandCement.setName("Portland cement 25kg");
        portlandCement.setSku("CEM-PORT-25");
        portlandCement.setDescription("Standardni Portland cement za sve vrste betona");
        portlandCement.setCategory(cement);
        portlandCement.setBrand("Cemex");
        portlandCement.setPurchasePrice(BigDecimal.valueOf(4.50));
        portlandCement.setSellingPrice(BigDecimal.valueOf(6.50));
        portlandCement.setTaxRate(BigDecimal.valueOf(0.20));
        portlandCement.setMinQuantity(10);
        portlandCement.setMaxQuantity(500);
        portlandCement.setUnitOfMeasure("kom");
        productRepository.save(portlandCement);

        Product rapidCement = new Product();
        rapidCement.setName("Rapid cement 25kg");
        rapidCement.setSku("CEM-RAP-25");
        rapidCement.setDescription("Brzovezujući cement za hitne popravke");
        rapidCement.setCategory(cement);
        rapidCement.setBrand("Heidelberg");
        rapidCement.setPurchasePrice(BigDecimal.valueOf(5.00));
        rapidCement.setSellingPrice(BigDecimal.valueOf(7.00));
        rapidCement.setTaxRate(BigDecimal.valueOf(0.20));
        rapidCement.setMinQuantity(5);
        rapidCement.setMaxQuantity(300);
        rapidCement.setUnitOfMeasure("kom");
        productRepository.save(rapidCement);

        // Proizvodi u Pesak kategoriji
        Product concreteSand = new Product();
        concreteSand.setName("Betonski pesak 1m3");
        concreteSand.setSku("SAND-BET-1");
        concreteSand.setDescription("Pesak za beton i malter");
        concreteSand.setCategory(sand);
        concreteSand.setBrand("GradnjaPlus");
        concreteSand.setPurchasePrice(BigDecimal.valueOf(20.0));
        concreteSand.setSellingPrice(BigDecimal.valueOf(30.0));
        concreteSand.setTaxRate(BigDecimal.valueOf(0.20));
        concreteSand.setMinQuantity(1);
        concreteSand.setMaxQuantity(50);
        concreteSand.setUnitOfMeasure("m3");
        productRepository.save(concreteSand);

        System.out.println("Initial data loaded successfully!");
    }
}
