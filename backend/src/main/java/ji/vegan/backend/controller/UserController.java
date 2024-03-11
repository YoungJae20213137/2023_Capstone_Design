package ji.vegan.backend.controller;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import io.micrometer.common.util.StringUtils;
import ji.vegan.backend.bean.dto.UserCreate;
import ji.vegan.backend.bean.user.LoginUser;
import ji.vegan.backend.bean.user.QUser;
import ji.vegan.backend.bean.user.User;
import ji.vegan.backend.bean.user.UserInfo;
import ji.vegan.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<Page<User>> lists(@QuerydslPredicate(root = User.class) Predicate predicate,
                                            @PageableDefault(sort = "id") Pageable pageable) {
        Page<User> page = userService.findAll(predicate, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> detail(@PathVariable long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> insert(@LoginUser UserInfo userInfo,
                                       @RequestBody UserCreate userCreate) {

        if (StringUtils.isEmpty(userCreate.userId())) {
            log.warn("userId is empty error.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] userId is empty error. ");
        }

        final BooleanExpression userIdExistQuery = QUser.user.userId.eq(userCreate.userId());
        if (userService.exist(userIdExistQuery)) {
            log.warn("create user error. userId exist. : {}", userCreate.userId());
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] create user error");
        }

        User user = new User();
        user.setUserId(userCreate.userId());
        user.setRole(userCreate.role());
        user.setPassword(passwordEncoder.encode(userCreate.password()));
        user.setFavoriteShop(userCreate.favoriteShop());
        user.setCreatedAt(ZonedDateTime.now());

        User inserted = userService.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/user/{id}")
                .buildAndExpand(user.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(inserted);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> modify(@LoginUser UserInfo userInfo,
                                       @PathVariable long id,
                                       @RequestBody UserCreate userCreate) {
        User user = userService.findById(id);

        if (StringUtils.isNotBlank(userCreate.password())) {
            user.setPassword(passwordEncoder.encode(userCreate.password()));
        }

        user.setRole(userCreate.role());
        user.setFavoriteShop(userCreate.favoriteShop());

        User updated = userService.save(user);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/favoriteShop")
    public ResponseEntity<?> favoriteShop(@LoginUser UserInfo userInfo,
                                          @PathVariable long id,
                                          @RequestBody Map<String, String> map) {

        User user = userService.findById(id);
        user.setFavoriteShop(map);

        User updated = userService.save(user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{ids}")
    public ResponseEntity<Iterable<User>> delete(@LoginUser UserInfo userInfo,
                                                 @PathVariable List<Long> ids) {
        Predicate query = QUser.user.id.in(ids);

        final Iterable<User> deleteList = userService.findAll(query);
        userService.delete(deleteList);

        return ResponseEntity.ok(deleteList);
    }
}
