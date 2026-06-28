package bg.sofia.uni.event_management.service;

import bg.sofia.uni.event_management.dto.PresentationMaterialRequest;
import bg.sofia.uni.event_management.dto.PresentationMaterialResponse;
import bg.sofia.uni.event_management.exceptions.AccessDeniedException;
import bg.sofia.uni.event_management.exceptions.NotFoundException;
import bg.sofia.uni.event_management.model.PresentationMaterial;
import bg.sofia.uni.event_management.model.Session;
import bg.sofia.uni.event_management.model.Speaker;
import bg.sofia.uni.event_management.repository.PresentationMaterialRepository;
import bg.sofia.uni.event_management.repository.SessionRepository;
import bg.sofia.uni.event_management.repository.SpeakerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PresentationMaterialService {

    private final PresentationMaterialRepository materialRepository;
    private final SpeakerRepository speakerRepository;
    private final SessionRepository sessionRepository;

    public PresentationMaterialService(PresentationMaterialRepository materialRepository,
                                       SpeakerRepository speakerRepository,
                                       SessionRepository sessionRepository) {
        this.materialRepository = materialRepository;
        this.speakerRepository = speakerRepository;
        this.sessionRepository = sessionRepository;
    }

    public PresentationMaterialResponse getById(Long id) {
        PresentationMaterial material = materialRepository.findById(id)
            .orElseThrow(() ->
                new NotFoundException("Presentation material not found with id: " + id));

        return PresentationMaterialResponse.from(material);
    }

    public List<PresentationMaterialResponse> getBySpeaker(Long speakerId) {
        Speaker speaker = speakerRepository.findById(speakerId)
            .orElseThrow(() ->
                new NotFoundException("Speaker not found with id: " + speakerId));

        return materialRepository.findBySpeakerId(speakerId)
            .stream()
            .map(PresentationMaterialResponse::from)
            .toList();
    }

    @Transactional
    public PresentationMaterialResponse create(Long currentUserId,
                                                Long speakerId,
                                                PresentationMaterialRequest request) {

        Speaker speaker = speakerRepository.findById(speakerId)
            .orElseThrow(() ->
                new NotFoundException("Speaker not found with id: " + speakerId));

        Session session = sessionRepository.findById(request.sessionId())
            .orElseThrow(() ->
                new NotFoundException("Session not found with id: " + request.sessionId()));

        if (!session.getEvent().getOrganizer().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not allowed to add materials to this session.");
        }

        if (!speaker.getCreator().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not allowed to add materials for this speaker.");
        }

        PresentationMaterial material = new PresentationMaterial();
        material.setSpeaker(speaker);
        material.setSession(session);
        material.setFileUrl(request.fileUrl());
        material.setFileType(request.fileType());
        material.setUploadTime(LocalDateTime.now());

        materialRepository.save(material);

        return PresentationMaterialResponse.from(material);
    }

    @Transactional
    public void delete(Long currentUserId, Long materialId) {
        PresentationMaterial material = materialRepository.findById(materialId)
            .orElseThrow(() ->
                new NotFoundException("Presentation material not found with id: " + materialId));

        Long organizerId = material.getSession().getEvent().getOrganizer().getId();
        Long speakerCreatorId = material.getSpeaker().getCreator().getId();

        if (!organizerId.equals(currentUserId) && !speakerCreatorId.equals(currentUserId)) {
            throw new AccessDeniedException("You are not allowed to delete this material.");
        }

        materialRepository.delete(material);
    }
}