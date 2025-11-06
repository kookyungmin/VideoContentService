package net.happykoo.vcs.adapter.out.redis;

import net.happykoo.vcs.adapter.out.redis.channel.ChannelRedisHash;
import net.happykoo.vcs.adapter.out.redis.channel.ChannelRedisRepository;
import net.happykoo.vcs.config.TestRedisConfig;
import net.happykoo.vcs.domain.channel.ChannelFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = TestRedisConfig.class)
class ChannelRedisRepositoryIntTest {
    @Autowired
    private ChannelRedisRepository channelRedisRepository;

    @BeforeEach
    void setup() {
        channelRedisRepository.deleteAll();
    }

    @Test
    @DisplayName("@Indexed 로 지정한 property 조회 테스트")
    void test1() {
        //given
        String channelId = "happykoo";
        var channel = ChannelFixtures.stub(channelId);
        var redisHash = ChannelRedisHash.from(channel);
        channelRedisRepository.save(redisHash);

        var result1 = channelRedisRepository.findById(channelId);
        var result2 = channelRedisRepository.findByContentOwnerId(channel.getContentOwnerId());

        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(result1.get().getId(), result2.get().getId());
    }

}
