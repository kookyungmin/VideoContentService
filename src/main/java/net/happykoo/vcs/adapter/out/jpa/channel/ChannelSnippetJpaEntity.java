package net.happykoo.vcs.adapter.out.jpa.channel;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.happykoo.vcs.domain.channel.ChannelSnippet;

import java.time.LocalDateTime;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChannelSnippetJpaEntity {
    private String title;
    private String description;
    private String thumbnailUrl;
    private LocalDateTime publishedAt;

    public static ChannelSnippetJpaEntity from(ChannelSnippet channelSnippet) {
        return new ChannelSnippetJpaEntity(channelSnippet.getTitle(), channelSnippet.getDescription(), channelSnippet.getThumbnailUrl(), channelSnippet.getPublishedAt());
    }

    public ChannelSnippet toDomain() {
        return ChannelSnippet.builder()
                .title(this.title)
                .description(this.description)
                .thumbnailUrl(this.thumbnailUrl)
                .publishedAt(this.publishedAt)
                .build();
    }
}
