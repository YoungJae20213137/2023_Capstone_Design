package ji.vegan.backend.controller;

import com.querydsl.core.types.dsl.BooleanExpression;
import io.micrometer.common.util.StringUtils;
import ji.vegan.backend.bean.dto.UserCreate;
import ji.vegan.backend.bean.user.QUser;
import ji.vegan.backend.bean.user.User;
import ji.vegan.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.ZonedDateTime;


@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
@Slf4j
public class SignupController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입을 처리한다.
     */
    @PostMapping
    public ResponseEntity<User> signup(@RequestBody UserCreate userCreate) {
        if (StringUtils.isEmpty(userCreate.userId())) {
            log.warn("userId is empty error.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] userId is empty error. ");
        }

        final BooleanExpression userIdExistQuery = QUser.user.userId.eq(userCreate.userId());
        if (userService.exist(userIdExistQuery)) {
            log.warn("create user error. userId exist. : {}", userCreate.userId());
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] create user error. userId exist " + userCreate.userId());
        }

        User user = new User();
        user.setUserId(userCreate.userId());
        user.setRole(userCreate.role());
        user.setPassword(passwordEncoder.encode(userCreate.password()));
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
}
