package net.happykoo.vcs.adapter.out;

import lombok.RequiredArgsConstructor;
import net.happykoo.vcs.adapter.out.jpa.channel.ChannelJpaEntity;
import net.happykoo.vcs.adapter.out.jpa.subscribe.SubscribeJpaEntity;
import net.happykoo.vcs.adapter.out.jpa.subscribe.SubscribeJpaRepository;
import net.happykoo.vcs.adapter.out.jpa.user.UserJpaEntity;
import net.happykoo.vcs.application.port.out.SubscribePort;
import net.happykoo.vcs.common.RedisKeyGenerator;
import net.happykoo.vcs.domain.channel.Channel;
import net.happykoo.vcs.domain.user.User;
import net.happykoo.vcs.exception.DomainNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static net.happykoo.vcs.common.CacheNames.SUBSCRIBE_CHANNEL_BY_USER;

@Component
@RequiredArgsConstructor
public class SubscribePersistenceAdapter implements SubscribePort {
    private final SubscribeJpaRepository subscribeJpaRepository;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @CacheEvict(cacheManager = "redisTtl10mCacheManager", cacheNames = SUBSCRIBE_CHANNEL_BY_USER, key = "#userId")
    public String insertSubscribeChannel(Channel channel, User user) {
        var entity = new SubscribeJpaEntity(
            UUID.randomUUID().toString(),
            ChannelJpaEntity.from(channel),
            UserJpaEntity.from(user)
        );
        subscribeJpaRepository.save(entity);

        var setOps = stringRedisTemplate.opsForSet();

        setOps.add(RedisKeyGenerator.getSubscribeChannelKey(channel.getId()), user.getId());
        setOps.add(RedisKeyGenerator.getSubscribeUserKey(user.getId()), channel.getId());


        return entity.getId();
    }

    @Override
    @CacheEvict(cacheManager = "redisTtl10mCacheManager", cacheNames = SUBSCRIBE_CHANNEL_BY_USER, key = "#userId")
    public void deleteSubscribeChannel(Channel channel, User user) {
        var subscribeEntity = subscribeJpaRepository.findByChannelIdAndUserId(channel.getId(), user.getId())
                .orElseThrow(DomainNotFoundException::new);

        var setOps = stringRedisTemplate.opsForSet();

        setOps.remove(RedisKeyGenerator.getSubscribeChannelKey(channel.getId()), user.getId());
        setOps.remove(RedisKeyGenerator.getSubscribeUserKey(user.getId()), channel.getId());

        subscribeJpaRepository.deleteById(subscribeEntity.getId());
    }

    @Override
    @Cacheable(cacheManager = "redisTtl10mCacheManager", cacheNames = SUBSCRIBE_CHANNEL_BY_USER, key = "#userId")
    public List<Channel> listSubscribeChannel(String userId) {
        return subscribeJpaRepository.findAllByUserId(userId)
                .stream()
                .map(SubscribeJpaEntity::getChannel)
                .map(ChannelJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<User> findAllSubscriber(String channelId) {
        return subscribeJpaRepository.findAllSubscriber(channelId)
                .stream()
                .map(UserJpaEntity::toDomain)
                .toList();
    }
}
