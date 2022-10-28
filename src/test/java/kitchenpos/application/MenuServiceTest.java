package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Product;
import kitchenpos.dto.menu.CreateMenuProductRequest;
import kitchenpos.dto.menu.CreateMenuRequest;

class MenuServiceTest extends ServiceTest {

    @Nested
    @DisplayName("create()")
    class CreateMethod {

        @Test
        @DisplayName("예외사항이 존재하지 않는 경우 새로운 메뉴를 생성한다.")
        void create() {
            // given
            Product product = createAndSaveProduct();
            MenuGroup menuGroup = createAndSaveMenuGroup();

            CreateMenuRequest request = createMenuCreateRequest(menuGroup.getId(), product.getId());

            // when
            Menu savedMenu = menuService.create(request);

            // then
            assertThat(savedMenu.getId()).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 menu group id인 경우 예외가 발생한다.")
        void invalidMenuGroupId() {
            // given
            Product product = createAndSaveProduct();

            CreateMenuRequest request = createMenuCreateRequest(0L, product.getId());

            // when, then
            assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 메뉴그룹입니다.");
        }

        @Test
        @DisplayName("존재하지 않는 menu product id인 경우 예외가 발생한다.")
        void invalidMenuProductId() {
            // given
            MenuGroup menuGroup = createAndSaveMenuGroup();

            CreateMenuRequest request = createMenuCreateRequest(menuGroup.getId(), 0L);

            // when, then
            assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 제품입니다.");
        }

    }

    @Nested
    @DisplayName("list()")
    class ListMethod {

        @Test
        @DisplayName("전체 메뉴를 조회한다.")
        void list() {
            List<Menu> menus = menuService.list();
            assertThat(menus).isNotNull();
        }

    }

    private Product createAndSaveProduct() {
        Product product = new Product("product", new BigDecimal(1000));
        return productRepository.save(product);
    }

    private MenuGroup createAndSaveMenuGroup() {
        MenuGroup menuGroup = new MenuGroup("menuGroup");
        return menuGroupRepository.save(menuGroup);
    }

    private CreateMenuRequest createMenuCreateRequest(long menuGroupId, long productId) {
        return new CreateMenuRequest("name", new BigDecimal(1000),
            menuGroupId,
            new ArrayList<CreateMenuProductRequest>() {{
                add(new CreateMenuProductRequest(productId, 1L));
            }}
        );
    }
}
