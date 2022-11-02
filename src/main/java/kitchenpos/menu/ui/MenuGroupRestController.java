package kitchenpos.menu.ui;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kitchenpos.menu.application.MenuGroupService;
import kitchenpos.menu.dto.request.CreateMenuGroupRequest;
import kitchenpos.menu.dto.response.MenuGroupResponse;

@RestController
public class MenuGroupRestController {
    private final MenuGroupService menuGroupService;

    public MenuGroupRestController(final MenuGroupService menuGroupService) {
        this.menuGroupService = menuGroupService;
    }

    @PostMapping("/api/menu-groups")
    public ResponseEntity<MenuGroupResponse> create(@RequestBody final CreateMenuGroupRequest request) {
        final MenuGroupResponse created = menuGroupService.create(request);
        final URI uri = URI.create("/api/menu-groups/" + created.getId());

        return ResponseEntity.created(uri)
            .body(created)
            ;
    }

    @GetMapping("/api/menu-groups")
    public ResponseEntity<List<MenuGroupResponse>> list() {
        final List<MenuGroupResponse> menuGroups = menuGroupService.list();

        return ResponseEntity.ok()
            .body(menuGroups)
            ;
    }
}
