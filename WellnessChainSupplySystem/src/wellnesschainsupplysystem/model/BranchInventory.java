package wellnesschainsupplysystem.model;

public class BranchInventory {

    private int id;
    private int branchId;
    private int productId;
    private int quantityOnHand;
    private int reorderThreshold;

    public BranchInventory() {
    }

    public BranchInventory(int id, int branchId, int productId,
                           int quantityOnHand, int reorderThreshold) {
        this.id = id;
        this.branchId = branchId;
        this.productId = productId;
        this.quantityOnHand = quantityOnHand;
        this.reorderThreshold = reorderThreshold;
    }

    public BranchInventory(int branchId, int productId,
                           int quantityOnHand, int reorderThreshold) {
        this.branchId = branchId;
        this.productId = productId;
        this.quantityOnHand = quantityOnHand;
        this.reorderThreshold = reorderThreshold;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }    

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(int quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public int getReorderThreshold() {
        return reorderThreshold;
    }

    public void setReorderThreshold(int reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }
}
