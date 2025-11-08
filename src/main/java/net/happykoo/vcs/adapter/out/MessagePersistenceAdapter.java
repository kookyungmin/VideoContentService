package net.happykoo.vcs.adapter.out;

import lombok.RequiredArgsConstructor;
import net.happykoo.vcs.application.port.out.MessagePort;
import net.happykoo.vcs.common.MessageTopics;
import net.happykoo.vcs.domain.message.NewVideoMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessagePersistenceAdapter implements MessagePort {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void sendNewVideMessage(String channelId, String videoId) {
        redisTemplate.convertAndSend(MessageTopics.NEW_VIDEO, new NewVideoMessage(channelId, videoId));
    }
}
