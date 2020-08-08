package com.shuman.stonks.twitch;

import lombok.Getter;

import java.net.URI;

@Getter
public class ClipResponse {
    public final String title;
    public final String id;
    public final URI url;
    public final long viewCount;

    public ClipResponse(String title, String id, URI url, long viewCount) {
        this.title = title;
        this.id = id;
        this.url = url;
        this.viewCount = viewCount;
    }
}
