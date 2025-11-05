package net.happykoo.vcs.domain.channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class ChannelSnippet {
    private String title;
    private String description;
    private String thumbnailUrl;
    private LocalDateTime publishedAt;
}
