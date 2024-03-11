package ji.vegan.backend.config.resolver;

import io.jsonwebtoken.Claims;
import ji.vegan.backend.bean.user.LoginUser;
import ji.vegan.backend.bean.user.Role;
import ji.vegan.backend.bean.user.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
public class UserJwtArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        final LoginUser loginUser = methodParameter.getParameterAnnotation(LoginUser.class);
        final boolean flag = UserInfo.class.equals(methodParameter.getParameterType());

        return loginUser != null && flag;
    }

    @SuppressWarnings({"NullableProblems"})
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) {
        Claims claims = (Claims) nativeWebRequest.getAttribute("claims", RequestAttributes.SCOPE_REQUEST);
        if (claims == null) {
            log.warn("claims is null.");
            throw new RuntimeException("[INTERNAL_SERVER_ERROR] permit error");
        }

        Role role = Role.valueOf(claims.get("role", String.class));

        UserInfo userInfo = new UserInfo();
        userInfo.setId(claims.get("id", Long.class));
        userInfo.setUserId(claims.get("userId", String.class));
        userInfo.setRole(role);
        userInfo.setIp(MDC.get("clientIp"));

        return userInfo;
    }
}
