package bg.sofia.uni.event_management.repository;

import bg.sofia.uni.event_management.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = """
        SELECT DISTINCT e.* FROM events.events e
        LEFT JOIN events.event_categories ec ON e.id = ec.event_id
        WHERE (:title IS NULL OR e.title ILIKE CONCAT('%', CAST(:title AS text), '%'))
          AND (:venue IS NULL OR e.venue ILIKE CAST(:venue AS text))
          AND (:organizerId IS NULL OR e.organizer_id = :organizerId)
          AND (:categoryId IS NULL OR ec.category_id = :categoryId)
        """, nativeQuery = true)
    List<Event> findFiltered(
        @Param("title") String title,
        @Param("venue") String venue,
        @Param("organizerId") Long organizerId,
        @Param("categoryId") Long categoryId
    );

    @Query(value = """
        SELECT DISTINCT e.* FROM events.events e
        LEFT JOIN events.event_categories ec ON e.id = ec.event_id
        WHERE (:categoryId IS NULL OR ec.category_id = :categoryId)
        """,
        countQuery = """
        SELECT COUNT(DISTINCT e.id) FROM events.events e
        LEFT JOIN events.event_categories ec ON e.id = ec.event_id
        WHERE (:categoryId IS NULL OR ec.category_id = :categoryId)
        """,
        nativeQuery = true)
    Page<Event> findPaginated(
        @Param("categoryId") Long categoryId,
        Pageable pageable
    );
}