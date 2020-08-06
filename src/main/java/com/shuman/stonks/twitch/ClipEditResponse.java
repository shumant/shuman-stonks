package com.shuman.stonks.twitch;

import java.net.URI;

public class ClipEditResponse {
    public final String id;
    public final URI editUrl;

    public ClipEditResponse(String id, URI editUrl) {
        this.id = id;
        this.editUrl = editUrl;
    }
}
