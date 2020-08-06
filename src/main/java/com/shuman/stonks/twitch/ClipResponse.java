package com.shuman.stonks.twitch;

import java.net.URI;

public class ClipResponse {
    public final String title;
    public final String id;
    public final URI url;

    public ClipResponse(String title, String id, URI url) {
        this.title = title;
        this.id = id;
        this.url = url;
    }
}
