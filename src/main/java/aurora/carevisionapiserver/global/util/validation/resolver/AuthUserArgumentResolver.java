package aurora.carevisionapiserver.global.util.validation.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.admin.service.AdminService;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.error.exception.GeneralException;
import aurora.carevisionapiserver.global.util.validation.annotation.AuthUser;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final AdminService adminService;
    private final NurseService nurseService;
    private static final String ADMIN_ROLE = "ADMIN";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.getParameterType().equals(Admin.class)
                        || parameter.getParameterType().equals(Nurse.class))
                && parameter.hasParameterAnnotation(AuthUser.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getName().equals("anonymousUser")) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }

        Object principal = authentication.getPrincipal();
        if (principal == null || principal.getClass() == String.class) {
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) authentication;
        String username = authenticationToken.getName();

        if (authenticationToken.getPrincipal() == ADMIN_ROLE)
            return adminService.getAdmin(username);

        return nurseService.getNurse(username);
    }
}
