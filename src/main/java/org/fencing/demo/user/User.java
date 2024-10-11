package org.fencing.demo.user;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;


@Entity
@Getter
@Setter
@ToString(exclude = "password")
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Username is required")
    @Column(unique = true)
    // ! might need to implement unique logic in service as well
    private String username; 

    @NotNull(message = "Password is required")
    private String password; // Consider hashing passwords for security , Need Min length etc
    
    @Email(message = "Email should be valid")
    private String email;


    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
