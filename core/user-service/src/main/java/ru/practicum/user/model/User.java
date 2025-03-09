package ru.practicum.user.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "name", length = 250, nullable = false)
    String name;
    @Column(name = "email", length = 254, unique = true, nullable = false)
    String email;

    public User (Long id, String name) {
        this.id = id;
        this.name= name;
    }

}
