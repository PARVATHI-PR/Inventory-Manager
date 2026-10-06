package edu.inventory.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class InventoryData {
    private InventoryData() {}
    public record Category(Long id, String name, String description, boolean active) { @Override public String toString(){return name;} }
    public record Supplier(Long id, String name, String contactName, String email, String phone, String address, boolean active) { @Override public String toString(){return name;} }
    public record Product(Long id, String sku, String name, String description, long categoryId, long supplierId, String unit, BigDecimal reorderLevel, boolean active) { @Override public String toString(){return sku+" — "+name;} }
    public record StockRow(long id, String sku, String product, String category, String supplier, String unit, BigDecimal quantity, BigDecimal reorderLevel, boolean lowStock, boolean active) {}
    public record TransactionRow(long id, String sku, String product, BigDecimal quantity, String type, LocalDateTime occurredAt, String username, String reference, String note) {}
    public record UserRow(long id, String username, Role role, boolean active, Long supplierId) { @Override public String toString(){return username;} }
    public record SupplierProfile(String username, String supplierName, String email, String phone, String address, boolean active) {}
}
