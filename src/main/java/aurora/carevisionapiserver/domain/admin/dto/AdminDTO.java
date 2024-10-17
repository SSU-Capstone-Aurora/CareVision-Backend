package aurora.carevisionapiserver.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

public class AdminDTO {

    @Getter
    @Builder
    public static class AdminJoinDTO {

        private String hospital;

        private String department;

        private String username;

        private String password;
    }

    @Getter
    @Builder
    public static class AdminInfoDTO {
        private Long id;

        private String hospital;

        private String department;
    }
}
