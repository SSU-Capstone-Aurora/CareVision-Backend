package aurora.carevisionapiserver.global.security.handler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
    @ApiResponse(responseCode = "AUTH401", description = "BAD_REQUEST, refresh token이 null입니다."),
    @ApiResponse(responseCode = "AUTH402", description = "BAD_REQUEST, refresh token이 인식되지 않았습니다."),
    @ApiResponse(responseCode = "AUTH403", description = "UNAUTHORIZED, refresh token이 만료되었습니다.")
})
public @interface RefreshTokenApiResponse {}
