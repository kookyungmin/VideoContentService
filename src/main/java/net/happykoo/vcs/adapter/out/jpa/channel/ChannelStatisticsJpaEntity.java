package net.happykoo.vcs.adapter.out.jpa.channel;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.happykoo.vcs.domain.channel.ChannelStatistics;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChannelStatisticsJpaEntity {
    private long videoCount;
    private long subscriberCount;
    private long commentCount;

    public static ChannelStatisticsJpaEntity from(ChannelStatistics channelStatistics) {
        return new ChannelStatisticsJpaEntity(channelStatistics.getVideoCount(), channelStatistics.getSubscriberCount(), channelStatistics.getCommentCount());
    }

    public ChannelStatistics toDomain() {
        return ChannelStatistics.builder()
                .commentCount(this.commentCount)
                .subscriberCount(this.subscriberCount)
                .videoCount(this.videoCount)
                .build();
    }
}
