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
    public void run(String... args) {

        if (categoryRepository.count() > 0) return;

        // =========================
        // GLAVNA KATEGORIJA
        // =========================
        Category construction = new Category();
        construction.setName("Građevinski materijal");
        construction.setDescription("Svi građevinski materijali za gradnju i renoviranje");
        categoryRepository.save(construction);

        // =========================
        // PODKATEGORIJE
        // =========================

        Category cement = createCategory("Cement",
                "Različite vrste cementa", construction);

        Category sand = createCategory("Pesak",
                "Pesak za beton i malter", construction);

        Category bricks = createCategory("Cigla",
                "Različite vrste cigli", construction);

        Category rebar = createCategory("Armatura",
                "Čelična armatura za beton", construction);

        Category insulation = createCategory("Izolacija",
                "Materijali za termo i hidro izolaciju", construction);

        // =========================
        // PROIZVODI (UKUPNO 15)
        // =========================

        // CEMENT (3)
        productRepository.save(createProduct("Portland cement 25kg", "CEM-PORT-25",
                "Standardni Portland cement", cement, "Cemex", 4.5, 6.5, 10, 500, "kom"));

        productRepository.save(createProduct("Rapid cement 25kg", "CEM-RAP-25",
                "Brzovezujući cement", cement, "Heidelberg", 5.0, 7.0, 5, 300, "kom"));

        productRepository.save(createProduct("Beli cement 25kg", "CEM-BEL-25",
                "Dekorativni beli cement", cement, "Holcim", 6.0, 8.5, 5, 200, "kom"));

        // PESAK (3)
        productRepository.save(createProduct("Betonski pesak 1m3", "SAND-BET-1",
                "Pesak za beton", sand, "GradnjaPlus", 20, 30, 1, 50, "m3"));

        productRepository.save(createProduct("Rečni pesak 1m3", "SAND-REC-1",
                "Prirodni rečni pesak", sand, "PesakTrans", 18, 28, 1, 50, "m3"));

        productRepository.save(createProduct("Kvarcni pesak 1m3", "SAND-KVA-1",
                "Fini kvarcni pesak", sand, "GradnjaPlus", 25, 35, 1, 40, "m3"));

        // CIGLA (3)
        productRepository.save(createProduct("Puna cigla", "BRICK-PUN-01",
                "Standardna puna cigla", bricks, "Tondach", 0.30, 0.50, 500, 10000, "kom"));

        productRepository.save(createProduct("Šuplja cigla", "BRICK-SUP-01",
                "Šuplja cigla za zidanje", bricks, "Wienerberger", 0.40, 0.65, 500, 10000, "kom"));

        productRepository.save(createProduct("Blok cigla 25cm", "BRICK-BLO-25",
                "Blok za noseće zidove", bricks, "YTONG", 1.20, 1.80, 200, 5000, "kom"));

        // ARMATURA (3)
        productRepository.save(createProduct("Armatura fi8", "REB-08",
                "Čelična šipka fi8", rebar, "Metalac", 0.60, 0.90, 100, 5000, "m"));

        productRepository.save(createProduct("Armatura fi12", "REB-12",
                "Čelična šipka fi12", rebar, "Metalac", 1.10, 1.60, 100, 5000, "m"));

        productRepository.save(createProduct("Armaturna mreža 6mm", "REB-MR-6",
                "Zavarena armaturna mreža", rebar, "MetalPro", 15, 22, 10, 500, "kom"));

        // IZOLACIJA (3)
        productRepository.save(createProduct("Stiropor 5cm", "INS-STI-5",
                "EPS termoizolacija 5cm", insulation, "Austrotherm", 3.5, 5.5, 50, 2000, "m2"));

        productRepository.save(createProduct("Kamena vuna 10cm", "INS-VUN-10",
                "Toplotna izolacija", insulation, "Knauf", 6, 9, 30, 1500, "m2"));

        productRepository.save(createProduct("Hidroizolaciona folija", "INS-HYD-01",
                "PVC hidro folija", insulation, "Sika", 2, 3.5, 50, 3000, "m2"));

        System.out.println("Initial data loaded successfully with 15 products and 5 categories!");
    }

    // =========================
    // HELPER METODE
    // =========================

    private Category createCategory(String name, String description, Category parent) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setParentCategory(parent);
        parent.getSubCategories().add(category);
        return categoryRepository.save(category);
    }

    private Product createProduct(String name, String sku, String description,
                                  Category category, String brand,
                                  double purchasePrice, double sellingPrice,
                                  int minQty, int maxQty, String unit) {

        Product product = new Product();
        product.setName(name);
        product.setSku(sku);
        product.setDescription(description);
        product.setCategory(category);
        product.setBrand(brand);
        product.setPurchasePrice(BigDecimal.valueOf(purchasePrice));
        product.setSellingPrice(BigDecimal.valueOf(sellingPrice));
        product.setTaxRate(BigDecimal.valueOf(0.20));
        product.setMinQuantity(minQty);
        product.setMaxQuantity(maxQty);
        product.setUnitOfMeasure(unit);

        return product;
    }
}

