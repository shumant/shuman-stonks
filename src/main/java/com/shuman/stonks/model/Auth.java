package com.shuman.stonks.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Builder
@Data
@Table(name = "auth")
@Entity
@AllArgsConstructor
public class Auth {

    public Auth() {
    }

    @Id
    private UUID id;
    private UUID userId;
    private String scopes;
    private String accessToken;
    private String refreshToken;

    public Auth update(Auth auth) {
        this.scopes = auth.scopes;
        this.accessToken = auth.accessToken;
        this.refreshToken = auth.refreshToken;
        return this;
    }

    public Auth withRandomId() {
        this.id = UUID.randomUUID();
        return this;
    }
}
