package com.shuman.stonks.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@Builder
@Data
@Table(name = "users")
@Entity
@AllArgsConstructor
public class User {

    @Id
    private UUID id;
    private String oauthId;
    private String name;
    private String email;
    private String thumbnailUrl;

    public User() {
    }

    public User update(User user) {
        this.name = user.name;
        this.email = user.email;
        this.thumbnailUrl = user.thumbnailUrl;
        return this;
    }

    public User withRandomId() {
        this.id = randomUUID();
        return this;
    }
}
