package aurora.carevisionapiserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import aurora.carevisionapiserver.domain.admin.repository.AdminRepository;
import aurora.carevisionapiserver.domain.admin.service.AdminService;
import aurora.carevisionapiserver.domain.camera.api.AdminCameraController;
import aurora.carevisionapiserver.domain.camera.api.NurseCameraController;
import aurora.carevisionapiserver.domain.camera.service.CameraService;
import aurora.carevisionapiserver.domain.nurse.api.AdminNurseController;
import aurora.carevisionapiserver.domain.nurse.api.NurseController;
import aurora.carevisionapiserver.domain.nurse.repository.NurseRepository;
import aurora.carevisionapiserver.domain.nurse.service.NurseService;
import aurora.carevisionapiserver.domain.patient.api.AdminPatientController;
import aurora.carevisionapiserver.domain.patient.service.PatientService;
import aurora.carevisionapiserver.global.auth.util.JWTUtil;
import aurora.carevisionapiserver.global.common.service.ConnectService;
import aurora.carevisionapiserver.global.common.service.PageService;
import aurora.carevisionapiserver.global.fcm.service.FcmService;

@WebMvcTest(
        controllers = {
            NurseCameraController.class,
            NurseController.class,
            AdminNurseController.class,
            NurseCameraController.class,
            AdminPatientController.class,
            AdminCameraController.class,
        })
public abstract class ControllerTestSupport {
    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;
    @MockBean protected CameraService cameraService;
    @MockBean protected PatientService patientService;
    @MockBean protected AdminService adminService;
    @MockBean protected NurseService nurseService;
    @MockBean protected PageService pageService;
    @MockBean protected JWTUtil jwtUtil;
    @MockBean protected NurseRepository nurseRepository;
    @MockBean protected AdminRepository adminRepository;
    @MockBean protected FcmService fcmService;
    @MockBean protected ConnectService connectService;
}
