package kitchenpos.dto;

import java.util.List;

public class OrderCreateRequest {

    private Long orderTableId;
    private List<OrderLineItemCreateRequest> orderLineItems;

    public OrderCreateRequest(Long orderTableId, List<OrderLineItemCreateRequest> orderLineItems) {
        this.orderTableId = orderTableId;
        this.orderLineItems = orderLineItems;
    }

    public Long getOrderTableId() {
        return orderTableId;
    }

    public List<OrderLineItemCreateRequest> getOrderLineItems() {
        return orderLineItems;
    }
}
