package kitchenpos.dto;

public class OrderStatusChangeRequest {

    private String status;

    public OrderStatusChangeRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
