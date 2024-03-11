package ji.vegan.backend.controller;

import io.jsonwebtoken.Claims;
import ji.vegan.backend.bean.dto.TokenRequest;
import ji.vegan.backend.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
@Slf4j
@RequiredArgsConstructor
public class TokenController {
    private final JwtService jwtService;
    
    @PostMapping(value = "/check")
    public ResponseEntity<Claims> check(@RequestBody TokenRequest authRequest) {

        /* 토큰이 유효하지 않을때 */
        if (!jwtService.isTokenValid(authRequest.getAccessToken())) {
            log.error("token is inValid");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] token is inValid");
        }

        Claims claims = jwtService.extractAllClaims(authRequest.getAccessToken());
        return ResponseEntity.ok(claims);
    }
}
