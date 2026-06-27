package bg.sofia.uni.event_management.model;

import bg.sofia.uni.event_management.model.enums.Role;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "users", schema = "events")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, length = 255)
    private String email;

    @Column(name = "hash_password", nullable = false, length = 255)
    private String hashPassword;

    @Column(name = "first_name", nullable = false, length = 255)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 255)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id)
                && Objects.equals(email, user.email)
                && Objects.equals(hashPassword, user.hashPassword)
                && Objects.equals(firstName, user.firstName)
                && Objects.equals(lastName, user.lastName)
                && role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, hashPassword, firstName, lastName, role);
    }

    public static class Builder {

        private String email;
        private String hashPassword;
        private String firstName;
        private String lastName;
        private Role role;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder hashPassword(String hashPassword) {
            this.hashPassword = hashPassword;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public User build() {
            return new User(email, hashPassword, firstName, lastName, role);
        }
    }

    public User() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public User(String email, String hashPassword, String firstName, String lastName, Role role) {
        this.email = email;
        this.hashPassword = hashPassword;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
