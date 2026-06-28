package bg.sofia.uni.event_management.repository;

import bg.sofia.uni.event_management.model.PresentationMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PresentationMaterialRepository extends JpaRepository<PresentationMaterial, Long> {

    List<PresentationMaterial> findBySpeakerId(Long speakerId);
}