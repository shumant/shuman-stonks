package com.shuman.stonks.repository;

import com.shuman.stonks.model.Clip;
import com.shuman.stonks.model.StreamerViewCount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Repository
public interface ClipsRepository extends CrudRepository<Clip, String> {
    @Query(value = "select sum(view_count) from clips where creator_id = :creator_id", nativeQuery = true)
    Long totalViewsForClipsCreatedBy(@Param("creator_id") UUID creatorId);

    @Query(value = "select streamer_login as login, sum(view_count) as total_view_count from clips where creator_id = :creator_id group by streamer_login", nativeQuery = true)
    List<Map<String, Object>> viewsPerStreamerForClipsCreatedByInternal(@Param("creator_id") UUID creatorId);

    default List<StreamerViewCount> viewsPerStreamerForClipsCreatedBy(UUID creatorId) {
        return viewsPerStreamerForClipsCreatedByInternal(creatorId).stream()
            .map(row -> new StreamerViewCount(row.get("login").toString(), Long.parseLong(row.get("total_view_count").toString())))
            .collect(toList());
    }

    @Query(value = "select * from clips where creator_id = :creator_id and streamer_login = :streamer_login", nativeQuery = true)
    List<Clip> clipsByCreatorAndStreamer(@Param("creator_id") UUID creatorId, @Param("streamer_login") String streamerLogin);
}
