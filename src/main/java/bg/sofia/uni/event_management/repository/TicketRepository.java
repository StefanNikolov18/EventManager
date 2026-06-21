package bg.sofia.uni.event_management.repository;

import bg.sofia.uni.event_management.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    boolean existsByRegistrationId(Long registrationId);

    Optional<Ticket> findByRegistrationId(Long registrationId);
}
