package bg.sofia.uni.event_management.service;

import bg.sofia.uni.event_management.dto.SpeakerRequest;
import bg.sofia.uni.event_management.dto.SpeakerResponse;
import bg.sofia.uni.event_management.exceptions.AccessDeniedException;
import bg.sofia.uni.event_management.exceptions.NotFoundException;
import bg.sofia.uni.event_management.model.Event;
import bg.sofia.uni.event_management.model.Session;
import bg.sofia.uni.event_management.model.Speaker;
import bg.sofia.uni.event_management.model.User;
import bg.sofia.uni.event_management.repository.SessionRepository;
import bg.sofia.uni.event_management.repository.SpeakerRepository;
import bg.sofia.uni.event_management.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpeakerService {

    private final SpeakerRepository speakerRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public SpeakerService(SpeakerRepository speakerRepository,
                          SessionRepository sessionRepository,
                          UserRepository userRepository) {
        this.speakerRepository = speakerRepository;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    public SpeakerResponse getById(Long id) {

        Speaker speaker = speakerRepository.findById(id)
            .orElseThrow(() ->
                new NotFoundException("Speaker is missing with id: " + id));

        return SpeakerResponse.from(speaker);
    }

    @Transactional
    public void update(Long currentUserId,
                       Long speakerId,
                       SpeakerRequest request) {

        Speaker speaker = speakerRepository.findById(speakerId)
            .orElseThrow(() ->
                new NotFoundException("Speaker is missing with id: " + speakerId));

        if (!speaker.getCreator().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not allowed to update this speaker.");
        }

        speaker.setName(request.name());
        speaker.setBiography(request.biography());
        speaker.setCompanyName(request.companyName());
        speaker.setPhotoUrl(request.photoUrl());
        speaker.setWebsiteUrl(request.websiteUrl());
    }

    @Transactional
    public void delete(Long currentUserId,
                       Long speakerId) {

        Speaker speaker = speakerRepository.findById(speakerId)
            .orElseThrow(() ->
                new NotFoundException("Speaker is missing with id: " + speakerId));

        if (!speaker.getCreator().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not allowed to delete this speaker.");
        }

        speakerRepository.delete(speaker);
    }

    public List<SpeakerResponse> getBySession(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() ->
                new NotFoundException("Session not found with id: " + sessionId));

        return session.getSpeakers()
            .stream()
            .map(SpeakerResponse::from)
            .toList();
    }

    @Transactional
    public SpeakerResponse create(Long currentUserId,
                                  Long sessionId,
                                  SpeakerRequest request) {

        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() ->
                new NotFoundException("Session not found with id: " + sessionId));

        Event event = session.getEvent();

        if (!event.getOrganizer().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not allowed to add speakers.");
        }

        User creator = userRepository.findById(currentUserId)
            .orElseThrow(() ->
                new NotFoundException("User not found with id: " + currentUserId));

        Speaker speaker = new Speaker();

        speaker.setCreator(creator);
        speaker.setName(request.name());
        speaker.setBiography(request.biography());
        speaker.setCompanyName(request.companyName());
        speaker.setPhotoUrl(request.photoUrl());
        speaker.setWebsiteUrl(request.websiteUrl());

        speakerRepository.save(speaker);

        session.getSpeakers().add(speaker);

        return SpeakerResponse.from(speaker);
    }
}