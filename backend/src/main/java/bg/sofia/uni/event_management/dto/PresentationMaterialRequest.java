package bg.sofia.uni.event_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PresentationMaterialRequest(

    @NotNull(message = "Speaker is required")
    Long speakerId,

    @NotNull(message = "Session is required")
    Long sessionId,

    @NotBlank(message = "File URL is required")
    String fileUrl,

    String fileType
) {
}