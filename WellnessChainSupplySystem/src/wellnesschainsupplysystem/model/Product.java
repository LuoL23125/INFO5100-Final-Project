package wellnesschainsupplysystem.model;

import java.math.BigDecimal;

public class Product {

    private int id;
    private String name;
    private String category;
    private String unit;
    private BigDecimal unitPrice;

    public Product() {
    }

    public Product(int id, String name, String category, String unit, BigDecimal unitPrice) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.unitPrice = unitPrice;
    }

    public Product(String name, String category, String unit, BigDecimal unitPrice) {
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.unitPrice = unitPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", unit='" + unit + '\'' +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
