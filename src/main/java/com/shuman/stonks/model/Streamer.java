package com.shuman.stonks.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@Builder
@Data
@Table(name = "streamers")
@Entity
@AllArgsConstructor
public class Streamer {

    @Id
    private UUID id;
    private String name;
    private UUID creatorId;

    public Streamer() {
    }

    public Streamer withRandomId() {
        this.id = randomUUID();
        return this;
    }
}
