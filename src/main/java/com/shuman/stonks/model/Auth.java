package com.shuman.stonks.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "auth")
@Data
public class Auth {

    public Auth() {
    }

    @Id
    private UUID id;
    private UUID userId;
    private String scopes;
    private String token;
}
