package net.happykoo.vcs.adapter.out;

import lombok.RequiredArgsConstructor;
import net.happykoo.vcs.application.port.out.LikeVideoPort;
import net.happykoo.vcs.common.RedisKeyGenerator;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoLikePersistenceAdapter implements LikeVideoPort {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Long addVideoLike(String videoId, String userId) {
        //SADD
        return stringRedisTemplate.opsForSet().add(RedisKeyGenerator.getVideoLikeKey(videoId), userId);
    }

    @Override
    public Long removeVideoLike(String videoId, String userId) {
        //SREM
        return stringRedisTemplate.opsForSet().remove(RedisKeyGenerator.getVideoLikeKey(videoId), userId);
    }

    @Override
    public Boolean isVideoLikeMember(String videoId, String userId) {
        //SISMEMBER
        return stringRedisTemplate.opsForSet().isMember(RedisKeyGenerator.getVideoLikeKey(videoId), userId);
    }

    @Override
    public Long getVideoLikeCount(String videoId) {
        //SCARD
        return stringRedisTemplate.opsForSet().size(RedisKeyGenerator.getVideoLikeKey(videoId));
    }
}
