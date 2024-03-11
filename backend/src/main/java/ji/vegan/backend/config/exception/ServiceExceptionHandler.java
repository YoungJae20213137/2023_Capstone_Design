package ji.vegan.backend.config.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ServiceExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public Map<String, Object> defaultErrorHandler(Exception e, ServletWebRequest webRequest) {
        if (StringUtils.startsWithIgnoreCase(webRequest.getRequest().getRequestURI(), "/sse/connect")) {
            return null;
        }

        log.error("exception error. path=" + webRequest.getRequest().getRequestURI(), e);
        return getDefaultAttribute(e, webRequest);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public Map<String, Object> lockErrorHandler(OptimisticLockingFailureException e, ServletWebRequest webRequest) {
        final Map<String, Object> attribute = getDefaultAttribute(e, webRequest);

        attribute.put("error", "optimistic lock");
        attribute.put("message", e.getMessage());

        return attribute;
    }

    private Map<String, Object> getDefaultAttribute(Exception e, ServletWebRequest webRequest) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        errorAttributes.put("timestamp", LocalDateTime.now());
        errorAttributes.put("path", webRequest.getRequest().getRequestURI());
        errorAttributes.put("status", HttpStatus.NOT_FOUND.value());
        errorAttributes.put("result", false);

        errorAttributes.put("error", "Forbidden");
        errorAttributes.put("message", e.getMessage());

        if (e.getMessage().contains("[INTERNAL_SERVER_ERROR]")) {
            errorAttributes.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return errorAttributes;
    }
}
