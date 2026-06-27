package bg.sofia.uni.event_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SpeakerRequest(

    @NotBlank(message = "Name cannot be empty")
    String name,

    @Size(max = 2000, message = "Biography is too long")
    String biography,

    String companyName,

    @Size(max = 500)
    String photoUrl,

    String websiteUrl
) {
}