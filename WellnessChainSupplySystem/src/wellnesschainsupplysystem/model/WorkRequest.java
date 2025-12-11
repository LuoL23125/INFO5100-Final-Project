package wellnesschainsupplysystem.model;

import java.time.LocalDateTime;

public class WorkRequest {

    private int id;
    private WorkRequestType type;
    private WorkRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int createdByUserId;
    private Integer relatedPurchaseOrderId;
    private String comments;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }    

    public WorkRequestType getType() {
        return type;
    }

    public void setType(WorkRequestType type) {
        this.type = type;
    }

    public WorkRequestStatus getStatus() {
        return status;
    }

    public void setStatus(WorkRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(int createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public Integer getRelatedPurchaseOrderId() {
        return relatedPurchaseOrderId;
    }

    public void setRelatedPurchaseOrderId(Integer relatedPurchaseOrderId) {
        this.relatedPurchaseOrderId = relatedPurchaseOrderId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
