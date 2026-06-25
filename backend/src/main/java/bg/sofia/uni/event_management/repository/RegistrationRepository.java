package bg.sofia.uni.event_management.repository;

import bg.sofia.uni.event_management.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findByEventId(Long eventId);

    List<Registration> findByUserId(Long userId);

    // проверка дали user вече е записан за event
    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    // ако искаш да вземеш конкретна регистрация за user + event
    Optional<Registration> findByUserIdAndEventId(Long userId, Long eventId);
}
