package aurora.carevisionapiserver.global.security.handler.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import aurora.carevisionapiserver.global.auth.exception.AuthException;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.security.handler.annotation.ExtractToken;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExtractTokenArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(String.class)
                && parameter.hasParameterAnnotation(ExtractToken.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {

        String authorizationHeader = webRequest.getHeader("Authorization");
        if (authorizationHeader == null) {
            throw new AuthException(ErrorStatus.UNAUTHORIZED_REQUEST);
        }

        return authorizationHeader.substring(7);
    }
}
