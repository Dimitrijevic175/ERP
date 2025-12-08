package com.dimitrijevic175.product_service.configuration;


import com.dimitrijevic175.product_service.domain.Category;
import com.dimitrijevic175.product_service.domain.Product;
import com.dimitrijevic175.product_service.repository.CategoryRepository;
import org.apache.poi.ss.usermodel.Row;
import java.math.BigDecimal;

public class ProductExcelMapper {

    private static final ProductExcelMapper INSTANCE = new ProductExcelMapper();
    private ProductExcelMapper() {}

    public static ProductExcelMapper getInstance() {
        return INSTANCE;
    }

    private final ExcelUtils excel = ExcelUtils.getInstance();

    public Product mapRowToProduct(Row row, CategoryRepository categoryRepository) {
        Product p = new Product();
        updateProductFromRow(p, row, categoryRepository);
        return p;
    }

    public void updateProductFromRow(Product product, Row row, CategoryRepository categoryRepository) {

        product.setName(excel.getCellString(row.getCell(0)));
        product.setSku(excel.getCellString(row.getCell(1)));
        product.setDescription(excel.getCellString(row.getCell(2)));
        product.setQuantity(excel.getCellInteger(row.getCell(3)));
        product.setMinQuantity(excel.getCellInteger(row.getCell(4)));
        product.setMaxQuantity(excel.getCellInteger(row.getCell(5)));
        product.setBrand(excel.getCellString(row.getCell(6)));
        product.setUnitOfMeasure(excel.getCellString(row.getCell(7)));

        BigDecimal purchasePrice = excel.getCellBigDecimal(row.getCell(8));
        BigDecimal sellingPrice = excel.getCellBigDecimal(row.getCell(9));
        BigDecimal taxRate = excel.getCellBigDecimal(row.getCell(10));

        product.setPurchasePrice(purchasePrice);
        product.setSellingPrice(sellingPrice);
        product.setTaxRate(taxRate);

        String categoryName = excel.getCellString(row.getCell(11));
        if (categoryName != null) {
            Category category = categoryRepository.findByName(categoryName)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryName));
            product.setCategory(category);
        }

        if (product.getActive() == null) {
            product.setActive(true);
        }

        if (product.getName() == null || product.getSku() == null || product.getQuantity() == null) {
            throw new IllegalArgumentException("Required fields missing (name, sku, quantity)");
        }
    }
}
