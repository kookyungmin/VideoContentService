package net.happykoo.vcs.adapter.out.jpa.video;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.happykoo.vcs.domain.video.Video;

import java.time.LocalDateTime;

@Entity(name = "video")
@Table(name = "vcs_video")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class VideoJpaEntity {
    @Id
    private String id;

    private String title;

    private String description;

    private String thumbnailUrl;

    private String fileUrl;

    private String channelId;

    private long viewCount;

    private long likeCount;

    private LocalDateTime publishedAt;

    public static VideoJpaEntity from(Video video) {
        return new VideoJpaEntity(video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getThumbnailUrl(),
                video.getFileUrl(),
                video.getChannelId(),
                video.getViewCount(),
                video.getLikeCount(),
                video.getPublishedAt());
    }

    public Video toDomain() {
        return Video.builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .thumbnailUrl(this.thumbnailUrl)
                .fileUrl(this.fileUrl)
                .channelId(this.channelId)
                .viewCount(this.viewCount)
                .likeCount(this.likeCount)
                .publishedAt(this.publishedAt)
                .build();
    }
}
