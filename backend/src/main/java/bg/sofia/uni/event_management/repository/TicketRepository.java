package bg.sofia.uni.event_management.repository;

import bg.sofia.uni.event_management.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    boolean existsByRegistrationId(Long registrationId);

    Optional<Ticket> findByRegistrationId(Long registrationId);

    @Query("SELECT t FROM Ticket t JOIN FETCH t.registration r WHERE r.user.id = :userId")
    List<Ticket> findByUserId(@Param("userId") Long userId);
}
