package net.happykoo.vcs.application.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.happykoo.vcs.application.port.out.MessagePort;
import net.happykoo.vcs.application.port.out.SubscribePort;
import net.happykoo.vcs.domain.message.NewVideoMessage;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisNewVideoMessageListener implements MessageListener {
    private final SubscribePort subscribePort;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            NewVideoMessage newVideoMessage = objectMapper.readValue(message.getBody(), NewVideoMessage.class);
            var channelId = newVideoMessage.getChannelId();

            subscribePort.findAllSubscriber(channelId)
                    .forEach(u -> log.info("유저({}) : 채널 {} 에 {} 비디오가 생성되었습니다.", u.getId(), channelId, newVideoMessage.getVideoId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
