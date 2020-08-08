package com.shuman.stonks.cron;

import com.shuman.stonks.model.Clip;
import com.shuman.stonks.repository.ClipsRepository;
import com.shuman.stonks.twitch.TwitchApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

import static com.google.common.collect.Lists.partition;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;

public class ViewCountUpdater {
    private final int TWITCH_CLIP_LIMIT = 100;

    private final ClipsRepository clipsRepository;
    private final TwitchApi twitchApi;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public ViewCountUpdater(ClipsRepository clipsRepository, TwitchApi twitchApi) {
        this.clipsRepository = clipsRepository;
        this.twitchApi = twitchApi;
    }

    //    @Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(fixedDelay = 1000)
    public void updateViewCount() {
        logger.info("Updating clips view count");
        final var clipIds = stream(clipsRepository.findAll().spliterator(), false)
            .map(Clip::getId)
            .collect(toList());

        partition(clipIds, TWITCH_CLIP_LIMIT).forEach(this::updateCLips);
    }

    private void updateCLips(List<String> clipIds) {
        final var clips = twitchApi.clips(clipIds).stream()
            .collect(toMap(clip -> clip.id, identity()));
        final var updatedClips = stream(clipsRepository.findAllById(clipIds).spliterator(), false)
            .map(clip -> {
                if (clips.containsKey(clip.getId())) {
                    return clip.withViewCount(clips.get(clip.getId()).viewCount);
                } else return clip;
            }).collect(toList());

        clipsRepository.saveAll(updatedClips);
    }
}
