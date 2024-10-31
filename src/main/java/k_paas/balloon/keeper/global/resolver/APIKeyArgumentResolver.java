package k_paas.balloon.keeper.global.resolver;

import k_paas.balloon.keeper.global.annotation.ValidAPIKey;
import k_paas.balloon.keeper.global.exception.InvalidAPIKeyException;
import k_paas.balloon.keeper.global.property.ApiKeyProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class APIKeyArgumentResolver implements HandlerMethodArgumentResolver {

    private final ApiKeyProperty apiKeyProperty;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(ValidAPIKey.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String apiKey = webRequest.getHeader("BK-API-KEY");
        if (apiKey == null || !apiKey.equals(apiKeyProperty.key())) {
            throw new InvalidAPIKeyException();
        }
        return apiKey;
    }
}