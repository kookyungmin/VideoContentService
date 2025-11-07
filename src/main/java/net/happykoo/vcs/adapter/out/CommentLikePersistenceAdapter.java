package net.happykoo.vcs.adapter.out;

import lombok.RequiredArgsConstructor;
import net.happykoo.vcs.application.port.out.LikeCommentPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommentLikePersistenceAdapter implements LikeCommentPort {
    private final RedisTemplate<String, Long> redisTemplate;

    @Override
    public Long getCommentLikeCount(String commentId) {
        var likeCount = redisTemplate.opsForValue().get(getCommentLikeCount(commentId));
        return Optional.ofNullable(likeCount)
                .orElse(0L);
    }
}
