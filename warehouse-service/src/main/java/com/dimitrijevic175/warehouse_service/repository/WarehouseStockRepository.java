package com.dimitrijevic175.warehouse_service.repository;

import com.dimitrijevic175.warehouse_service.domain.WarehouseStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, Long> {

    List<WarehouseStock> findByWarehouseId(Long warehouseId);

    List<WarehouseStock> findByProductId(Long productId);

//    WarehouseStock findByWarehouseIdAndProductId(Long warehouseId, Long productId);
    Optional<WarehouseStock> findByWarehouseIdAndProductId(Long warehouseId, Long productId);
}

