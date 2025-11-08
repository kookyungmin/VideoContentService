package net.happykoo.vcs.adapter.out;

import net.happykoo.vcs.adapter.out.redis.channel.ChannelRedisHash;
import net.happykoo.vcs.adapter.out.redis.channel.ChannelRedisRepository;
import net.happykoo.vcs.adapter.out.redis.user.UserRedisHash;
import net.happykoo.vcs.adapter.out.redis.user.UserRedisRepository;
import net.happykoo.vcs.common.CacheNames;
import net.happykoo.vcs.common.RedisKeyGenerator;
import net.happykoo.vcs.config.TestRedisConfig;
import net.happykoo.vcs.domain.channel.ChannelFixtures;
import net.happykoo.vcs.domain.user.UserFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes = TestRedisConfig.class)
class CacheManagerPersistenceAdapterIntTest {
    @Autowired
    private CacheManagePersistenceAdapter cacheManagePersistenceAdapter;

    @Autowired
    private ChannelRedisRepository channelRedisRepository;
    @Autowired
    private UserRedisRepository userRedisRepository;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate<String, Long> longRedisTemplate;

    @BeforeEach
    void setUp() {
        channelRedisRepository.save(ChannelRedisHash.from(ChannelFixtures.stub("channelId")));
        userRedisRepository.save(UserRedisHash.from(UserFixtures.stub("userId")));
        stringRedisTemplate.opsForSet().add(RedisKeyGenerator.getVideoLikeKey("videoId"), "userId");
        longRedisTemplate.opsForValue().get(RedisKeyGenerator.getVideoViewCountKey("videoId"));
        stringRedisTemplate.opsForValue().set(RedisKeyGenerator.getUserSessionKey(UUID.randomUUID().toString()), "userId");
        longRedisTemplate.opsForValue().set(RedisKeyGenerator.getCommentLikeKey("commentId"), 20L);
        stringRedisTemplate.opsForValue().set(RedisKeyGenerator.getPinnedCommentKey("videoId"), "commentId");
    }

    @Test
    @DisplayName("key 조회")
    public void test1() {
        List<String> keys = cacheManagePersistenceAdapter.getAllCacheNames();

        then(CacheNames.getCacheNames())
            .allMatch(name -> keys.contains(name));
    }

    @Test
    @DisplayName("key 삭제 테스트")
    public void test2() {
        cacheManagePersistenceAdapter.deleteCache(RedisKeyGenerator.getCommentLikeKey("commentId"));

        then(longRedisTemplate.opsForValue().get(RedisKeyGenerator.getCommentLikeKey("commentId")));
    }
}
