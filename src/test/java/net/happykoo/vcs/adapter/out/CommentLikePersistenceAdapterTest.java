package net.happykoo.vcs.adapter.out;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class CommentLikePersistenceAdapterTest {
    private CommentLikePersistenceAdapter commentLikePersistenceAdapter;

    private final RedisTemplate<String, Long> redisTemplate = mock(RedisTemplate.class, Mockito.RETURNS_DEEP_STUBS);
    private final ValueOperations<String, Long> valueOperations = mock(ValueOperations.class);

    @BeforeEach
    void setUp() {
        commentLikePersistenceAdapter = new CommentLikePersistenceAdapter(redisTemplate);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @Test
    @DisplayName("getCommentLikeCount 테스트")
    void test1() {
        var commentId = "commentId";
        given(redisTemplate.opsForValue().get(any())).willReturn(2L);

        var result = commentLikePersistenceAdapter.getCommentLikeCount(commentId);

        then(result).isEqualTo(2L);
    }

    @Test
    @DisplayName("getCommentLikeCount 테스트")
    void test2() {
        var commentId = "commentId";
        given(redisTemplate.opsForValue().get(any())).willReturn(null);

        var result = commentLikePersistenceAdapter.getCommentLikeCount(commentId);

        then(result).isEqualTo(0L);
    }
}
