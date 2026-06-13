package bg.sofia.uni.event_management.repository;

import bg.sofia.uni.event_management.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    boolean existsByRegistrationId(Long registrationId);
}
