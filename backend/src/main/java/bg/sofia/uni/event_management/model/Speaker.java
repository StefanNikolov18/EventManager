package bg.sofia.uni.event_management.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "speakers")
public class Speaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String biography;

    private String companyName;

    private String photoUrl;

    private String websiteUrl;

    public Speaker() {
    }

    public Long getId() {
        return id;
    }

    public User getCreator() {
        return creator;
    }

    public String getName() {
        return name;
    }

    public String getBiography() {
        return biography;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Speaker speaker = (Speaker) o;
        return Objects.equals(id, speaker.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
