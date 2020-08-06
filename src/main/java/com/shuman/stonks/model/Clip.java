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
@Table(name = "clips")
@Entity
@AllArgsConstructor
public class Clip {

    @Id
    private String id;
    private UUID creatorId;
    private String streamerLogin;
    private long viewCount;

    public Clip() {
    }
}