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
@Table(name = "clips")
@Entity
@AllArgsConstructor
public class Clip {

    @Id
    private UUID id;
    private UUID creatorId;
    private UUID streamerId;
    private long viewCount;

    public Clip() {
    }

    public Clip withRandomId() {
        this.id = randomUUID();
        return this;
    }
}
