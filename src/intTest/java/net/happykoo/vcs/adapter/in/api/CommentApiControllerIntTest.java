package net.happykoo.vcs.adapter.in.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.happykoo.vcs.adapter.in.api.attribute.HeaderAttribute;
import net.happykoo.vcs.adapter.in.api.dto.CommentRequest;
import net.happykoo.vcs.adapter.out.jpa.user.UserJpaEntity;
import net.happykoo.vcs.adapter.out.jpa.user.UserJpaRepository;
import net.happykoo.vcs.adapter.out.mongo.comment.CommentMongoRepository;
import net.happykoo.vcs.common.RedisKeyGenerator;
import net.happykoo.vcs.config.TestRedisConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestRedisConfig.class)
@AutoConfigureMockMvc
class CommentApiControllerIntTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CommentMongoRepository commentMongoRepository;

    private String authKey = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        String userId = "happykoo";
        userJpaRepository.save(new UserJpaEntity(userId, "해피쿠", "https://happykoo.net/profile.jpg"));
        stringRedisTemplate.opsForValue().set(RedisKeyGenerator.getUserSessionKey(authKey), userId);
    }

    @Test
    @DisplayName("/api/v1/comments POST 테스트")
    void test1() throws Exception{
        mockMvc
            .perform(
                post("/api/v1/comments")
                    .header(HeaderAttribute.X_AUTH_KEY, authKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                        new CommentRequest("videoId", null, "댓글")
                    ))
            )
            .andExpectAll(
                    status().isOk()
            );

        then(commentMongoRepository.count()).isEqualTo(1);
    }
}
