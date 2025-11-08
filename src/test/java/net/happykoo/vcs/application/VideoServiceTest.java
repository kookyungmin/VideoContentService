package net.happykoo.vcs.application;

import net.happykoo.vcs.adapter.in.api.dto.VideoRequestFixtures;
import net.happykoo.vcs.application.port.out.*;
import net.happykoo.vcs.domain.channel.ChannelFixtures;
import net.happykoo.vcs.domain.video.VideoFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.stream.LongStream;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class VideoServiceTest {
    private VideoService videoService;

    private final LoadVideoPort loadVideoPort = mock(LoadVideoPort.class);
    private final SaveVideoPort saveVideoPort = mock(SaveVideoPort.class);
    private final LikeVideoPort likeVideoPort = mock(LikeVideoPort.class);
    private final LoadChannelPort loadChannelPort = mock(LoadChannelPort.class);
    private final SaveChannelPort saveChannelPort = mock(SaveChannelPort.class);
    private final MessagePort messagePort = mock(MessagePort.class);

    @BeforeEach
    void setUp() {
        videoService = new VideoService(loadVideoPort, saveVideoPort, likeVideoPort, loadChannelPort, saveChannelPort, messagePort);
    }

    @Test
    @DisplayName("videoId로 조회시 Video 반환")
    void test1() {
        // Given
        var videoId = "videoId";
        given(loadVideoPort.loadVideo(any())).willReturn(VideoFixtures.stub(videoId));
        given(loadVideoPort.getViewCount(any())).willReturn(150L);

        // When
        var result = videoService.getVideo(videoId);
        // Then
        then(result)
            .isNotNull()
            .hasFieldOrPropertyWithValue("id", videoId)
            .hasFieldOrPropertyWithValue("viewCount", 150L);
    }

    @Test
    @DisplayName("channelId로 조회시 Video 목록 반환")
    void test2() {
        // Given
        var channelId = "happykoo-channel";
        var list = LongStream.range(1L, 4L)
                .mapToObj(i -> VideoFixtures.stub("videoId" + i))
                .toList();
        given(loadVideoPort.loadVideoByChannel(any())).willReturn(list);
        given(loadVideoPort.getViewCount(any())).willReturn(100L, 150L, 200L);
        given(likeVideoPort.getVideoLikeCount(any())).willReturn(10L, 15L, 20L);
        // When
        var result = videoService.listVideos(channelId);
        // Then
        then(result)
            .hasSize(3)
            .extracting("channelId").containsOnly(channelId);
        then(result)
                .extracting("viewCount", "likeCount")
                .contains(tuple(100L, 10L), tuple(150L, 15L), tuple(200L, 20L));
    }

    @Test
    @DisplayName("createVideo 호출시 channel, video 저장")
    void test3() {
        var videoRequest = VideoRequestFixtures.stub();
        willDoNothing().given(saveVideoPort).saveVideo(any());
        given(loadChannelPort.loadChannel(any())).willReturn(Optional.of(ChannelFixtures.stub(videoRequest.channelId())));

        var result = videoService.createVideo(videoRequest);

        // Then
        then(result)
                .isNotNull()
                .hasFieldOrProperty("id");
        verify(saveVideoPort).saveVideo(any());
        verify(saveChannelPort).saveChannel(any());
    }

    @Test
    @DisplayName("incrementViewCount 테스트")
    void test4() {
        videoService.increaseViewCount("videoId");

        verify(saveVideoPort).incrementViewCount("videoId");
    }
}
