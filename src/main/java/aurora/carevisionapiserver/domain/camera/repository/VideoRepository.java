package aurora.carevisionapiserver.domain.camera.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import aurora.carevisionapiserver.domain.camera.domain.Video;

public interface VideoRepository extends JpaRepository<Video, Long>, CustomVideoRepository {}
