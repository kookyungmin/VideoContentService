package net.happykoo.vcs.application;

import lombok.RequiredArgsConstructor;
import net.happykoo.vcs.adapter.in.api.dto.VideoRequest;
import net.happykoo.vcs.application.port.in.VideoUseCase;
import net.happykoo.vcs.application.port.out.*;
import net.happykoo.vcs.domain.video.Video;
import net.happykoo.vcs.exception.DomainNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService implements VideoUseCase {
    private final LoadVideoPort loadVideoPort;
    private final SaveVideoPort saveVideoPort;
    private final LikeVideoPort likeVideoPort;
    private final LoadChannelPort loadChannelPort;
    private final SaveChannelPort saveChannelPort;

    @Override
    public Video getVideo(String videoId) {
        var video = loadVideoPort.loadVideo(videoId);
        var viewCount = loadVideoPort.getViewCount(videoId);
        var likeCount = likeVideoPort.getVideoLikeCount(videoId);

        video.bindCount(viewCount, likeCount);

        return video;
    }

    @Override
    public List<Video> listVideos(String channelId) {
        return loadVideoPort.loadVideoByChannel(channelId)
                .stream()
                .map(video -> {
                    var viewCount = loadVideoPort.getViewCount(video.getId());
                    var likeCount = likeVideoPort.getVideoLikeCount(video.getId());
                    video.bindCount(viewCount, likeCount);
                    return video;
                })
                .toList();
    }

    @Override
    public Video createVideo(VideoRequest videoRequest) {
        var video = Video.builder()
                .id(UUID.randomUUID().toString())
                .channelId(videoRequest.channelId())
                .title(videoRequest.title())
                .description(videoRequest.description())
                .thumbnailUrl(videoRequest.thumbnailUrl())
                .publishedAt(LocalDateTime.now())
                .build();
        saveVideoPort.saveVideo(video);

        var channel = loadChannelPort.loadChannel(video.getChannelId()).orElseThrow(DomainNotFoundException::new);
        channel.getStatistics().updateVideoCount(channel.getStatistics().getVideoCount());
        saveChannelPort.saveChannel(channel);

        return video;
    }

    @Override
    public void increaseViewCount(String videoId) {
        saveVideoPort.incrementViewCount(videoId);
    }
}
