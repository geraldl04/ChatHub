package my.project.ChatHub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class Users {
//si fillim nje user ka emrin , emailin , passwordin , rolin qe me duhet me vone dhe nje creationTime

    @Id //trajtohen automatikisht si kolone , dhe nqs si percak emer kolone  , primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)//nga kolona identity eshte auto incremented
    private Long id;

    @NotBlank(message = " Duhet te vendosesh username")
    @Column(nullable = false, length = 255)
    private String username;


    @Email(message = "Email i pavlefshem")
    @Column(unique = true, nullable = false, length = 255)
    private String email;


    @NotBlank(message = "Password-i nuk duhet te jete bosh")
    @Column(nullable = false, length = 255)
    private String password;

    private LocalDateTime createdAt = LocalDateTime.now();


    @Enumerated(EnumType.STRING)  //specifikon tipin se si do behet store ne database (ordinal ose string)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;

    public enum Role {
        ADMIN, USER
    }

}