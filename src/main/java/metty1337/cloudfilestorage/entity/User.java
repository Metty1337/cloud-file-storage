package metty1337.cloudfilestorage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

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
    private String password;
}