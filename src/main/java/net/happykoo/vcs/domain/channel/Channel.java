package net.happykoo.vcs.domain.channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.happykoo.vcs.adapter.in.api.dto.ChannelSnippetRequest;

@AllArgsConstructor
@Getter
@ToString
@Builder
public class Channel {
    private String id;
    private ChannelSnippet snippet;
    private ChannelStatistics statistics;
    private String contentOwnerId;

    public void updateSnippet(ChannelSnippetRequest snippetRequest) {
        snippet = ChannelSnippet.builder()
                .title(snippetRequest.title())
                .thumbnailUrl(snippetRequest.thumbnailUrl())
                .description(snippetRequest.description())
                .build();
    }
}
