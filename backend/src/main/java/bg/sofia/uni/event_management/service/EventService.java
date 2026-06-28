package bg.sofia.uni.event_management.service;

import bg.sofia.uni.event_management.dto.EventRequest;
import bg.sofia.uni.event_management.dto.EventResponse;
import bg.sofia.uni.event_management.exceptions.AccessDeniedException;
import bg.sofia.uni.event_management.exceptions.BadRequestException;
import bg.sofia.uni.event_management.exceptions.NotFoundException;
import bg.sofia.uni.event_management.model.Category;
import bg.sofia.uni.event_management.model.Event;
import bg.sofia.uni.event_management.model.User;
import bg.sofia.uni.event_management.model.enums.Currency;
import bg.sofia.uni.event_management.model.enums.Role;
import bg.sofia.uni.event_management.repository.CategoryRepository;
import bg.sofia.uni.event_management.repository.EventRepository;
import bg.sofia.uni.event_management.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.math.BigDecimal;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public EventService(EventRepository eventRepository,
                        UserRepository userRepository,
                        CategoryRepository categoryRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<EventResponse> getEvents(
        String title,
        String venue,
        Long organizerId,
        Long categoryId
    ) {
        return eventRepository.findFiltered(title, venue, organizerId, categoryId)
            .stream()
            .map(EventResponse::from)
            .toList();
    }

    public Page<EventResponse> getEventsPaginated(int page, int size, Long categoryId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("start_time").descending());
        return eventRepository.findPaginated(categoryId, pageable)
            .map(EventResponse::from);
    }

    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() ->
                new NotFoundException("Event is missing with id: " + id));

        return EventResponse.from(event);
    }

    @Transactional
    public EventResponse createEvent(Long organizerId, EventRequest request) {
        User organizer = userRepository.findById(organizerId)
            .orElseThrow(() -> new NotFoundException("User is missing with id: " + organizerId));

        Event event = new Event();

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setVenue(request.venue());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());
        event.setCapacity(request.capacity());
        event.setAvailableTickets(request.capacity());
        event.setOrganizer(organizer);

        validateTicket(request.ticketPrice(), request.currency());
        event.setTicketPrice(request.ticketPrice());
        event.setCurrency(request.currency());

        // resolve categories from IDs
        if (request.categoryIds() != null && !request.categoryIds().isEmpty()) {
            event.setCategories(resolveCategories(request.categoryIds()));
        }

        Event saved = eventRepository.save(event);

        return EventResponse.from(saved);
    }

    @Transactional
    public void updateEvent(Long currentUserId, Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() ->
                new NotFoundException("Event is missing with id: " + id));

        if (!event.getOrganizer().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not allowed to update this event.");
        }

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setVenue(request.venue());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());
        int bookedTickets = event.getCapacity() - event.getAvailableTickets();
        if (bookedTickets > request.capacity()) {
            throw new BadRequestException("Capacity too low");
        }
        event.setCapacity(request.capacity());
        event.setAvailableTickets(request.capacity() - bookedTickets);
        if (request.categoryIds() != null) {
            event.setCategories(resolveCategories(request.categoryIds()));
        }

        validateTicket(request.ticketPrice(), request.currency());
        event.setTicketPrice(request.ticketPrice());
        event.setCurrency(request.currency());
    }

    public void deleteEvent(Long currentUserId, Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() ->
                new NotFoundException("Event is missing with id: " + eventId));

        User currentUser = userRepository.findById(currentUserId)
            .orElseThrow(() -> new NotFoundException("User is missing with id: " + currentUserId));

        boolean isOrganizer = event.getOrganizer().getId().equals(currentUserId);
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOrganizer && !isAdmin) {
            throw new AccessDeniedException("You are not allowed to delete this event.");
        }

        eventRepository.delete(event);
    }

    private Set<Category> resolveCategories(Set<Long> categoryIds) {

        Set<Category> categories = new HashSet<>(
            categoryRepository.findAllById(categoryIds)
        );

        if (categories.size() != categoryIds.size()) {
            throw new NotFoundException("Some categories do not exist");
        }

        return categories;
    }

    private void validateTicket(BigDecimal price, Currency currency) {
        if (price == null) {
            throw new BadRequestException("Ticket price is required.");
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Ticket price cannot be negative.");
        }

        if (price.compareTo(BigDecimal.ZERO) > 0 && currency == null) {
            throw new BadRequestException("Currency is required when price > 0.");
        }
    }
}