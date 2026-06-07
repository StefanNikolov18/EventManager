package bg.sofia.uni.event_management.repository;

import bg.sofia.uni.event_management.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    /*
    // всички събития на даден организатор
    List<Event> findByOrganizerId(Long organizerId);

    // търсене по заглавие (частично)
    List<Event> findByTitleContainingIgnoreCase(String title);

    // търсене по venue (частично)
    List<Event> findByVenueContainingIgnoreCase(String venue);

    // комбиниран филтър (ако искаш basic search)
    List<Event> findByTitleContainingIgnoreCaseAndVenueContainingIgnoreCase(
            String title,
            String venue
    );
     */
}
