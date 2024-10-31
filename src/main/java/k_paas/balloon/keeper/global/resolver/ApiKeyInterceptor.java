package k_paas.balloon.keeper.global.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import k_paas.balloon.keeper.global.annotation.ValidAPIKey;
import k_paas.balloon.keeper.global.exception.InvalidAPIKeyException;
import k_paas.balloon.keeper.global.property.ApiKeyProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class ApiKeyInterceptor implements HandlerInterceptor {

    private final ApiKeyProperty apiKeyProperty;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            ValidAPIKey validAPIKey = handlerMethod.getMethodAnnotation(ValidAPIKey.class);

            if (validAPIKey != null) {
                String apiKey = request.getHeader("BK-API-KEY");
                if (apiKey == null || !apiKey.equals(apiKeyProperty.key())) {
                    throw new InvalidAPIKeyException();
                }
            }
        }
        return true;
    }
}
