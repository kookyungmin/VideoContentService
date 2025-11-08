package net.happykoo.vcs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.happykoo.vcs.application.listener.RedisNewVideoMessageListener;
import net.happykoo.vcs.common.MessageTopics;
import net.happykoo.vcs.domain.message.NewVideoMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisMessageConfig {
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private RedisNewVideoMessageListener redisNewVideoMessageListener;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        final RedisTemplate<String,Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(NewVideoMessage.class));
        return redisTemplate;
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        //NEW_VIDEO topic listen
        container.addMessageListener(newVideoListener(), new ChannelTopic(MessageTopics.NEW_VIDEO));
        return container;
    }

    @Bean
    MessageListenerAdapter newVideoListener() {
        return new MessageListenerAdapter(redisNewVideoMessageListener);
    }
}
