package kitchenpos.order.application;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuGroup;
import kitchenpos.menu.domain.MenuValidator;
import kitchenpos.menu.dto.application.MenuProductDto;
import kitchenpos.menu.repository.MenuGroupRepository;
import kitchenpos.menu.repository.MenuRepository;
import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.OrderTable;
import kitchenpos.order.domain.OrderValidator;
import kitchenpos.order.dto.application.OrderLineItemDto;
import kitchenpos.order.dto.request.ChangeOrderTableEmptyRequest;
import kitchenpos.order.dto.request.ChangeOrderTableNumberOfGuestRequest;
import kitchenpos.order.dto.request.CreateOrderTableRequest;
import kitchenpos.order.dto.response.OrderTableResponse;
import kitchenpos.order.repository.OrderRepository;
import kitchenpos.order.repository.OrderTableRepository;
import kitchenpos.product.domain.Product;
import kitchenpos.product.repository.ProductRepository;

@SpringBootTest
class TableServiceTest {

    @Autowired
    private TableService tableService;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderValidator orderValidator;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private MenuValidator menuValidator;

    @Nested
    @DisplayName("create()")
    class CreateMethod {

        @Test
        @DisplayName("예외사항이 존재하지 않는 경우 새로운 테이블을 생성한다.")
        void create() {
            // given
            CreateOrderTableRequest request = new CreateOrderTableRequest(10, true);

            // when
            OrderTableResponse savedOrderTable = tableService.create(request);

            // then
            assertThat(savedOrderTable.getId()).isNotNull();
        }

    }

    @Nested
    @DisplayName("list()")
    class ListMethod {

        @Test
        @DisplayName("전체 테이블을 조회한다.")
        void list() {
            List<OrderTableResponse> tables = tableService.list();
            assertThat(tables).isNotNull();
        }

    }

    @Nested
    @DisplayName("changeEmpty()")
    class ChangeEmptyMethod {

        @Test
        @DisplayName("예외사항이 존재하지 않는 경우 상태를 변경한다.")
        void changeEmpty() {
            // given
            OrderTable savedOrderTable = createAndSaveOrderTable(true);
            ChangeOrderTableEmptyRequest request = new ChangeOrderTableEmptyRequest(false);

            // when
            OrderTableResponse changedOrderTable = tableService.changeEmpty(savedOrderTable.getId(), request);

            // then
            assertThat(changedOrderTable.getEmpty()).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 테이블 id인 경우 예외가 발생한다.")
        void invalidOrderTableId() {
            // given
            ChangeOrderTableEmptyRequest request = new ChangeOrderTableEmptyRequest(false);

            // when, then
            assertThatThrownBy(() -> tableService.changeEmpty(0L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않은 테이블입니다.");
        }

    }

    @Nested
    @DisplayName("changeNumberOfGuests()")
    class ChangeNumberOfGuestsMethod {

        @Test
        @DisplayName("예외사항이 존재하지 않는 경우 방문자 수를 변경한다.")
        void changeNumberOfGuests() {
            // given
            OrderTable savedOrderTable = createAndSaveOrderTable(false);
            ChangeOrderTableNumberOfGuestRequest request = new ChangeOrderTableNumberOfGuestRequest(20);

            // when
            OrderTableResponse changedOrderTable = tableService.changeNumberOfGuests(savedOrderTable.getId(), request);

            // then
            assertThat(changedOrderTable.getNumberOfGuests()).isEqualTo(20);
        }

        @Test
        @DisplayName("존재하지 않은 테이블 id의 경우 예외가 발생한다.")
        void invalidOrderTableId() {
            // given
            ChangeOrderTableNumberOfGuestRequest request = new ChangeOrderTableNumberOfGuestRequest(20);

            // when, then
            assertThatThrownBy(() -> tableService.changeNumberOfGuests(0L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않은 테이블입니다.");
        }

    }

    private OrderTable createAndSaveOrderTable(boolean empty) {
        OrderTable orderTable = new OrderTable(10, empty);
        return orderTableRepository.save(orderTable);
    }

}
