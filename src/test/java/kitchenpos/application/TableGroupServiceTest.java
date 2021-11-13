package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.domain.Order;
import kitchenpos.dto.TableGroupRequest;
import kitchenpos.dto.TableGroupResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static kitchenpos.fixture.OrderLineItemFixture.createOrderLineItem;
import static kitchenpos.fixture.TableGroupFixture.createTableGroupRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("test")
@SpringBootTest
class TableGroupServiceTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private OrderLineItemRepository orderLineItemRepository;

    @Autowired
    private TableGroupRepository tableGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private MenuProductRepository menuProductRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private TableGroupService tableGroupService;

    private OrderTable saveOrderTable(int numberOfGuests, boolean isEmpty) {
        return orderTableRepository.save(new OrderTable(numberOfGuests, isEmpty));
    }

    private OrderTable saveOrderTable(TableGroup tableGroup, int numberOfGuests, boolean isEmpty) {
        TableGroup save = tableGroupRepository.save(tableGroup);
        return orderTableRepository.save(new OrderTable(save, numberOfGuests, isEmpty));
    }

    @DisplayName("단체 지정 생성")
    @Nested
    class CreateTableGroup {

        @DisplayName("단체 지정을 생성한다.")
        @Test
        void create() {
            OrderTable orderTable1 = saveOrderTable(1, true);
            OrderTable orderTable2 = saveOrderTable(1, true);
            TableGroupResponse result = tableGroupService.create(createTableGroupRequest(orderTable1, orderTable2));
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isNotNull()
            );
        }

        @DisplayName("단체 지정 대상 테이블은 이미 지정된 단체가 없어야한다.")
        @Test
        void createWithInvalidOrderTable1() {
            TableGroup tableGroup = tableGroupRepository.save(new TableGroup());

            OrderTable orderTable1 = saveOrderTable(tableGroup, 1, true);
            OrderTable orderTable2 = saveOrderTable(tableGroup, 1, true);

            TableGroupRequest tableGroupRequest = createTableGroupRequest(orderTable1, orderTable2);
            assertThatThrownBy(() -> tableGroupService.create(tableGroupRequest)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("단체 지정 대상 테이블의 개수는 2개 이상이다.")
        @Test
        void createWithInvalidOrderTable2() {
            TableGroupRequest tableGroupRequest = createTableGroupRequest(saveOrderTable(1, true));
            assertThatThrownBy(() -> tableGroupService.create(tableGroupRequest)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("단체 지정 대상 테이블은 비어있어야한다.")
        @Test
        void createWithInvalidOrderTable3() {
            OrderTable orderTable1 = saveOrderTable(1, false);
            OrderTable orderTable2 = saveOrderTable(1, false);

            assertThatThrownBy(() -> tableGroupService.create(createTableGroupRequest(orderTable1, orderTable2)))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("단체 지정 해제")
    @Nested
    class UngroupTableGroup {

        private OrderTable orderTable1;
        private OrderTable orderTable2;
        private Long tableGroupId;

        @BeforeEach
        void setUp() {
            orderTable1 = saveOrderTable(1, true);
            orderTable2 = saveOrderTable(1, true);

            TableGroupRequest tableGroupRequest = createTableGroupRequest(orderTable1, orderTable2);
            tableGroupId = tableGroupService.create(tableGroupRequest).getId();
        }

        @DisplayName("단체 지정을 해제한다.")
        @Test
        void ungroup() {
            tableGroupService.ungroup(tableGroupId);
        }

        @DisplayName("COOKING, MEAL 상태의 테이블 그룹은 해제할 수 없다.")
        @Test
        void ungroupWithInvalidStatusOrderTables() {
            MenuGroup menuGroup = menuGroupRepository.save(new MenuGroup("NAME"));
            Product product = productRepository.save(new Product("NAME", BigDecimal.ONE));
            MenuProduct menuProduct = menuProductRepository.save(new MenuProduct(1L, product, 1L));
            Menu menu = menuRepository.save(new Menu("NAME", BigDecimal.ONE, menuGroup, Collections.singletonList(menuProduct)));
            Long menuId = menu.getId();

            TableGroup tableGroup = new TableGroup();
            orderTable1 = saveOrderTable(tableGroup, 1, false);

            OrderLineItem orderLineItem = orderLineItemRepository.save(createOrderLineItem(menuId));
            orderRepository.save(new Order(orderTable1, Collections.singletonList(orderLineItem), OrderStatus.MEAL, LocalDateTime.now()));

            assertThatThrownBy(() -> tableGroupService.ungroup(tableGroup.getId())).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @AfterEach
    void tearDown() {
        List<Menu> menus = menuRepository.findAll();
        for (Menu menu : menus) {
            menu.setMenuProducts(null);
        }
        menuRepository.saveAll(menus);

        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            order.setOrderLineItems(null);
        }
        orderRepository.saveAll(orders);

        orderLineItemRepository.deleteAll();
        orderRepository.deleteAll();
        orderTableRepository.deleteAll();
        menuProductRepository.deleteAll();
        menuRepository.deleteAll();
        productRepository.deleteAll();
        menuGroupRepository.deleteAll();
    }
}
