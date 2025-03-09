package ru.practicum.subscription.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    Long id;
    String name;
    String email;

    public User (Long id, String name) {
        this.id = id;
        this.name = name;
    }

}