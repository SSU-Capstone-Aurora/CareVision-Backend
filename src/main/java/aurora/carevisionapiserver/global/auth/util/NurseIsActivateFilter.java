package aurora.carevisionapiserver.global.auth.util;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.dto.request.NurseRequest.NurseLoginRequest;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.global.error.BaseResponse;
import aurora.carevisionapiserver.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NurseIsActivateFilter extends OncePerRequestFilter {

    private final NurseRepository nurseRepository;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        NurseLoginRequest loginRequest =
                objectMapper.readValue(request.getInputStream(), NurseLoginRequest.class);
        String username = loginRequest.getUsername();

        if (username != null) {
            Nurse nurse = nurseRepository.findByUsername(username).orElse(null);

            if (nurse == null || !nurse.isActivated()) {
                sendErrorResponse(response, ErrorStatus.USER_NOT_ACTIVATED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorStatus errorStatus)
            throws IOException {
        BaseResponse<Void> errorResponse =
                BaseResponse.onFailure(errorStatus.getCode(), errorStatus.getMessage(), null);

        response.setStatus(errorStatus.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        PrintWriter writer = response.getWriter();
        writer.print(jsonResponse);
        writer.flush();
    }
}
