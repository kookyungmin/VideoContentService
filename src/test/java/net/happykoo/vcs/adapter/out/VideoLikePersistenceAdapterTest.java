package net.happykoo.vcs.adapter.out;

import net.happykoo.vcs.common.RedisKeyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class VideoLikePersistenceAdapterTest {
    private VideoLikePersistenceAdapter videoLikePersistenceAdapter;

    private final StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class, Mockito.RETURNS_DEEP_STUBS);
    private final SetOperations<String, String> setOperations = mock(SetOperations.class);

    @BeforeEach
    void setUp() {
        videoLikePersistenceAdapter = new VideoLikePersistenceAdapter(stringRedisTemplate);

        given(stringRedisTemplate.opsForSet()).willReturn(setOperations);
    }

    @Test
    @DisplayName("addVideoLike :: redis set add 호출")
    void test1() {
        var videoId = "videoId";
        var userId = "userId";
        given(setOperations.add(any(), any())).willReturn(5L);

        var result = videoLikePersistenceAdapter.addVideoLike(videoId, userId);

        then(result).isEqualTo(5L);
        verify(setOperations).add(RedisKeyGenerator.getVideoLikeKey(videoId), userId);
    }

    @Test
    @DisplayName("removeVideoLike :: redis set remove 호출")
    void test2() {
        var videoId = "videoId";
        var userId = "userId";
        given(setOperations.remove(any(), any())).willReturn(4L);

        var result = videoLikePersistenceAdapter.removeVideoLike(videoId, userId);

        then(result).isEqualTo(4L);
        verify(setOperations).remove(RedisKeyGenerator.getVideoLikeKey(videoId), userId);
    }

    @Test
    @DisplayName("isVideoLikeMember :: redis set isMember 호출")
    void test3() {
        var videoId = "videoId";
        var userId = "userId";
        given(setOperations.isMember(any(), anyString())).willReturn(true);

        var result = videoLikePersistenceAdapter.isVideoLikeMember(videoId, userId);

        then(result).isTrue();
        verify(setOperations).isMember(RedisKeyGenerator.getVideoLikeKey(videoId), userId);
    }

    @Test
    @DisplayName("getVideoLikeCount :: redis set size 호출")
    void test4() {
        given(setOperations.size(any())).willReturn(10L);

        var result = videoLikePersistenceAdapter.getVideoLikeCount("videoId");

        then(result).isEqualTo(10L);
        verify(setOperations).size(RedisKeyGenerator.getVideoLikeKey("videoId"));
    }
}
