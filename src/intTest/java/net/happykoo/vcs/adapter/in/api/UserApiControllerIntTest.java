package net.happykoo.vcs.adapter.in.api;

import net.happykoo.vcs.adapter.in.api.attribute.HeaderAttribute;
import net.happykoo.vcs.adapter.out.jpa.user.UserJpaEntity;
import net.happykoo.vcs.adapter.out.jpa.user.UserJpaRepository;
import net.happykoo.vcs.common.RedisKeyGenerator;
import net.happykoo.vcs.config.TestRedisConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestRedisConfig.class)
@AutoConfigureMockMvc
public class UserApiControllerIntTest {
    private static final String authKey = UUID.randomUUID().toString();
    private static final String userId = "happykoo";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    void setUp() {
        userJpaRepository.save(new UserJpaEntity(userId, "해피쿠", "https://happykoo.net/profile.jpg"));
        stringRedisTemplate.opsForValue().set(RedisKeyGenerator.getUserSessionKey(authKey), userId);
    }

    @Test
    @DisplayName("헤더에 auth key 있어서 로그인 유저 정상적으로 조회되는 경우")
    void test1() throws Exception {
        mockMvc
            .perform(
                get("/api/v1/users/login-user")
                    .header(HeaderAttribute.X_AUTH_KEY, authKey)
            )
            .andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.id").value(userId),
                    jsonPath("$.data.name").value("해피쿠")
            );
    }

    @Test
    @DisplayName("헤더에 auth key가 없는 경우")
    void test2() throws Exception{
        mockMvc
            .perform(
                get("/api/v1/users/login-user")
            )
            .andExpectAll(
                status().isOk(),
                jsonPath("$.id").doesNotExist()
            );
    }

    @Test
    @DisplayName("헤더 키가 잘못된 경우")
    void test3() throws Exception{
        mockMvc
            .perform(
                get("/api/v1/users/login-user")
                    .header(HeaderAttribute.X_AUTH_KEY, "noauth")
            )
            .andExpectAll(
                    status().isUnauthorized()
            );
    }
}
