package aurora.carevisionapiserver.domain.camera.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.repository.CameraRepository;
import aurora.carevisionapiserver.domain.camera.service.CameraService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CameraServiceImpl implements CameraService {
    private final CameraRepository cameraRepository;

    public List<Camera> getCameras() {
        return cameraRepository.SortByBedInfo();
    }
}
