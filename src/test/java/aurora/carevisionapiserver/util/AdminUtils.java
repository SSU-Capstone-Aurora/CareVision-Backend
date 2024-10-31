package aurora.carevisionapiserver.util;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;

public class AdminUtils {
    public static Admin createAdmin(Hospital hospital) {
        return Admin.builder().id(1L).username("admin1").hospital(hospital).build();
    }
}
