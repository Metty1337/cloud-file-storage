package metty1337.cloudfilestorage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Size(max = 20)
    @NotNull
    @Column(nullable = false, length = 20)
    private String username;

    @Size(max = 70)
    @NotNull
    @Column(nullable = false, length = 70)
    @With
    private String password;
}