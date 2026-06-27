package bg.sofia.uni.event_management.dto;

import bg.sofia.uni.event_management.model.PresentationMaterial;

import java.time.LocalDateTime;

public record PresentationMaterialResponse(

    Long id,
    Long speakerId,
    Long sessionId,
    String fileUrl,
    String fileType,
    LocalDateTime uploadTime
) {

    public static PresentationMaterialResponse from(PresentationMaterial m) {
        return new PresentationMaterialResponse(
            m.getId(),
            m.getSpeaker().getId(),
            m.getSession().getId(),
            m.getFileUrl(),
            m.getFileType(),
            m.getUploadTime()
        );
    }
}
