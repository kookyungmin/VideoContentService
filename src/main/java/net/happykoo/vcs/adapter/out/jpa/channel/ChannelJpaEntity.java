package net.happykoo.vcs.adapter.out.jpa.channel;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.happykoo.vcs.domain.channel.Channel;

@Entity(name = "channel")
@Table(name = "vcs_channel")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChannelJpaEntity {
    @Id
    private String id;

    @Embedded
    private ChannelSnippetJpaEntity snippet;

    @Embedded
    private ChannelStatisticsJpaEntity statistics;

    private String contentOwnerId;

    public static ChannelJpaEntity from(Channel channel) {
        return new ChannelJpaEntity(channel.getId(),
                ChannelSnippetJpaEntity.from(channel.getSnippet()),
                ChannelStatisticsJpaEntity.from(channel.getStatistics()),
                channel.getContentOwnerId());
    }

    public Channel toDomain() {
        return Channel.builder()
                .id(this.id)
                .snippet(this.snippet.toDomain())
                .statistics(this.statistics.toDomain())
                .contentOwnerId(this.contentOwnerId)
                .build();
    }
}
