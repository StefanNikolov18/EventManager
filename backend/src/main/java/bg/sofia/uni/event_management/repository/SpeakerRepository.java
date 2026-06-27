package bg.sofia.uni.event_management.repository;

import bg.sofia.uni.event_management.model.Speaker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeakerRepository extends JpaRepository<Speaker, Long> {
}
