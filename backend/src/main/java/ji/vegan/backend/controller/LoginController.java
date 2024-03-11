package ji.vegan.backend.controller;

import com.querydsl.core.types.dsl.BooleanExpression;
import io.micrometer.common.util.StringUtils;
import ji.vegan.backend.bean.token.RequestToken;
import ji.vegan.backend.bean.token.ResponseToken;
import ji.vegan.backend.bean.user.QUser;
import ji.vegan.backend.bean.user.Role;
import ji.vegan.backend.bean.user.User;
import ji.vegan.backend.bean.user.UserInfo;
import ji.vegan.backend.config.CustomConfig;
import ji.vegan.backend.service.UserService;
import ji.vegan.backend.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final UserService userService;
    private final JwtService jwtService;
    private final CustomConfig customConfig;

    /**
     * 로그인을 처리한다.
     *
     * @param requestToken 요청 구조체
     * @return ResponseToken
     */
    @PostMapping
    public ResponseEntity<ResponseToken> login(@RequestBody RequestToken requestToken,
                                               @RequestParam(name = "role", required = false) Role role) {
        String userId = requestToken.getUserId();

        final QUser qUser = QUser.user;
        final BooleanExpression expression = qUser.userId.eq(userId);
        final Optional<User> optionalUser = userService.findOne(expression);

        if (StringUtils.isEmpty(requestToken.getUserId())) {
            log.warn("userId is empty error.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] userId is empty error. ");
        }

        if (StringUtils.isEmpty(requestToken.getPassword())) {
            log.warn("password is empty error.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] password is empty error. ");
        }


        if (optionalUser.isEmpty()) {
            log.warn("login error : {}", requestToken);
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] login user is not found. ");
        }

        User user = optionalUser.get();
        UserInfo userInfo = new UserInfo(user);
        userInfo.setIp(MDC.get("clientIp"));

        boolean matchPassword = userService.matchPassword(requestToken.getPassword(), user.getPassword());
        if (!matchPassword) {
            log.warn("login password match error : {}", requestToken);
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] login password match error");
        }

        /* role 요청이 있는 경우 체크하기 */
        if (role != null) {
            if (!role.equals(user.getRole())) {
                log.warn("login role match error : {}", role);
                throw new RuntimeException("[INTERNAL_SERVER_ERROR] login role match error");
            }
        }

        userService.loginSuccess(userInfo, user);

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", user.getId());
        extraClaims.put("userId", user.getUserId());
        extraClaims.put("role", userInfo.getRole().toString());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime expire = now.plusHours(customConfig.getTokenExpireHour());
        String accessToken = jwtService.generateToken(userInfo, now, expire, extraClaims);

        extraClaims.put("accessToken", accessToken);
        String refreshToken = jwtService.generateToken(userInfo, now, expire.plusDays(1L), extraClaims);

        ResponseToken responseToken = new ResponseToken(accessToken, refreshToken, now, expire, user.getRole());

        log.info("login success user. {}", userInfo);
        return ResponseEntity.ok(responseToken);
    }
}
