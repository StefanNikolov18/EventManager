package bg.sofia.uni.event_management.dto;

import bg.sofia.uni.event_management.model.Speaker;

public record SpeakerResponse(

    Long id,
    Long creatorId,
    String name,
    String biography,
    String companyName,
    String photoUrl,
    String websiteUrl
) {

    public static SpeakerResponse from(Speaker speaker) {
        return new SpeakerResponse(
            speaker.getId(),
            speaker.getCreator().getId(),
            speaker.getName(),
            speaker.getBiography(),
            speaker.getCompanyName(),
            speaker.getPhotoUrl(),
            speaker.getWebsiteUrl()
        );
    }
}