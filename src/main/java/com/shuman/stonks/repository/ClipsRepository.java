package com.shuman.stonks.repository;

import com.shuman.stonks.model.Clip;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface ClipsRepository extends CrudRepository<Clip, String> {
    @Query(value = "select sum(view_count) from clips where creator_id = :creator_id", nativeQuery = true)
    Long totalViewsForClipsCreatedBy(@Param("creator_id") UUID creatorId);

    @Query(value = "select streamer_id, sum(view_count) from clips where creator_id = :creator_id group by streamer_id", nativeQuery = true)
    Map<String,Long> viewsPerStreamerForClipsCreatedBy(@Param("creator_id") UUID creatorId);

    @Query(value = "select * from clips where creator_id = :creator_id and streamer_login = :streamer_login", nativeQuery = true)
    List<Clip> clipsByCreatorAndStreamer(@Param("creator_id") UUID creatorId, @Param("streamer_login") String streamerLogin);
}
