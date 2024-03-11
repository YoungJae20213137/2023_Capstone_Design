package ji.vegan.backend.config.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ji.vegan.backend.bean.user.UserInfo;
import ji.vegan.backend.service.UserService;
import ji.vegan.backend.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserRequestFilter extends OncePerRequestFilter {
    public static final String TOKEN_PREFIX = "Bearer ";
    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
    };
    private final JwtService jwtService;
    private final UserService userService;
    private final List<RequestMatcher> requestMatchers = List.of(
            new RequestMatcher("/favicon.ico", "GET").permitAll(),
            new RequestMatcher("/images/", "GET").permitAll(),
            new RequestMatcher("/assets/", "GET").permitAll(),
            new RequestMatcher("/vite.svg", "GET").permitAll(),
            new RequestMatcher("/actuator/", "GET").permitAll(),
            new RequestMatcher("/files/", "GET").permitAll(),
            new RequestMatcher("/login", "POST").permitAll(),
            new RequestMatcher("/signup", "POST").permitAll(),
            new RequestMatcher("/api/token").hasAnyRole("ADMIN", "USER"),
            new RequestMatcher("/api/").hasAnyRole("ADMIN", "USER")
    );

    private String getRemoteAddress(HttpServletRequest httpServletRequest) {
        if (null == httpServletRequest) {
            return "";
        }

        for (String header : IP_HEADER_CANDIDATES) {
            String ipList = httpServletRequest.getHeader(header);
            if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
                return ipList.split(",")[0];
            }
        }

        return httpServletRequest.getRemoteAddr();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String clientIp = getRemoteAddress(request);
        MDC.put("clientIp", clientIp);

        String path = request.getRequestURI();
        String method = request.getMethod();

        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");

        /* OPTIONS 예외처리 */
        if (StringUtils.equals(method, "OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        /* index 페이지 */
        if (StringUtils.equals(path, "/") && StringUtils.equals(method, "GET")) {
            filterChain.doFilter(request, response);
            return;
        }

        /* login 페이지 */
        if (StringUtils.equals(path, "/login") && StringUtils.equals(method, "GET")) {
            response.sendRedirect("/");
            return;
        }

        /* sign-up 페이지 */
        if (StringUtils.equals(path, "/signup") && StringUtils.equals(method, "GET")) {
            response.sendRedirect("/");
            return;
        }

        /* home 페이지 */
        if (StringUtils.equals(path, "/home") && StringUtils.equals(method, "GET")) {
            response.sendRedirect("/");
            return;
        }

        /* 내부 routing 주소 예외 */
        if (StringUtils.contains(path, "/routes/") && StringUtils.equals(method, "GET")) {
            String[] split = StringUtils.split(path, "/");
            if (split.length == 3) {
                response.sendRedirect("/");
                return;
            }
        }
        
        /* permitAll 예외처리 */
        if (getPermitAll(path, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getToken(request);

        if (StringUtils.isEmpty(token)) {
            errorResponseWrite(path, response, "Invalid token (token not found).");
            return;
        }

        final Claims claims = jwtService.extractAllClaims(token);

        /* getPermitRole 예외처리 */
        if (!getPermitRole(path, method, claims.get("role", String.class))) {
            errorResponseWrite(path, response, "Invalid role. " + path);
            return;
        }

        final String userId = claims.getSubject();
        UserInfo userInfo = userService.getUserInfo(userId);

        /* JWT 토큰 유효시간 체크하여서 유효하지 않으면 에러 */
        try {
            if (!jwtService.isTokenValid(token, userInfo)) {
                errorResponseWrite(path, response, "Access token expired.");
                return;
            }
        } catch (Exception e) {
            log.error("jwt token error.", e);
            errorResponseWrite(path, response, "Invalid token.");
            return;
        }

        request.setAttribute("claims", claims);
        filterChain.doFilter(request, response);
    }

    public void errorResponseWrite(String path, HttpServletResponse response, String message) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try {
            response.getWriter().write(message);
        } catch (IOException e) {
            log.error("response error.", e);
        }
    }

    /**
     * request 로 부터 token 을 얻어온다.
     *
     * @param request request
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getParameter("token");

        if (StringUtils.isEmpty(token)) {
            /* http 헤더에 authorization 필드가 존재하지 않으면 에러 */
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (null != authorization) {
                token = authorization.replace(TOKEN_PREFIX, "");
            }
        }

        return token;
    }

    private boolean getPermitAll(String path, String method) {
        for (RequestMatcher matcher : requestMatchers) {
            if (!matcher.isRole()) {
                if (StringUtils.startsWith(path, matcher.getPath())) {
                    if (StringUtils.startsWithAny(method, matcher.getMethods())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean getPermitRole(String path, String method, String role) {
        for (RequestMatcher matcher : requestMatchers) {
            if (matcher.isRole()) {
                if (StringUtils.startsWith(path, matcher.getPath())) {
                    if (StringUtils.startsWithAny(method, matcher.getMethods())) {
                        if (StringUtils.startsWithAny(role, matcher.getRoles())) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private String getRemoteAddress() {
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (null == requestAttributes) {
            return "";
        }

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        return getRemoteAddress(request);
    }
}
