package net.happykoo.vcs.adapter.out.jpa.subscribe;

import net.happykoo.vcs.adapter.out.jpa.user.UserJpaEntity;
import net.happykoo.vcs.domain.channel.Channel;
import net.happykoo.vcs.domain.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SubscribeJpaRepository extends CrudRepository<SubscribeJpaEntity, String> {
    Optional<SubscribeJpaEntity> findByChannelIdAndUserId(String channelId, String userId);
    List<SubscribeJpaEntity> findAllByUserId(String userId);
    @Query("select u from subscribe s join s.user u where s.channel.id = :channelId")
    List<UserJpaEntity> findAllSubscriber(String channelId);
}
