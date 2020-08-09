package com.shuman.stonks.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class StreamerViewCount {
    private String login;
    private long totalViewCount;

    public StreamerViewCount() {
    }
}
