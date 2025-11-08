package net.happykoo.vcs.adapter.in.api;

import net.happykoo.vcs.adapter.in.api.attribute.HeaderAttribute;
import net.happykoo.vcs.application.port.in.SubscribeUseCase;
import net.happykoo.vcs.application.port.in.UserUseCase;
import net.happykoo.vcs.domain.channel.ChannelFixtures;
import net.happykoo.vcs.domain.user.UserFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChannelSubscribeApiController.class)
public class ChannelSubscribeControllerTest extends AuthBaseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscribeUseCase subscribeUseCase;
    @MockBean
    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        prepareUser();
    }

    @Test
    @DisplayName("/api/v1/subscribe POST 테스트")
    void test1() throws Exception {
        var subscribeId = "subscribeId";
        given(subscribeUseCase.subscribeChannel(anyString(), anyString())).willReturn(subscribeId);

        mockMvc
            .perform(
                post("/api/v1/subscribe?channelId={channelId}", "channelId")
                    .header(HeaderAttribute.X_AUTH_KEY, authKey)
            )
            .andExpectAll(
                    status().isOk(),
                    jsonPath("$.data").value(subscribeId)
            );
    }

    @Test
    @DisplayName("/api/v1/subscribe/mine GET 테스트")
    void test2() throws Exception {
        var list = IntStream.range(1, 4)
                .mapToObj(i -> ChannelFixtures.stub("channelId" + i))
                .toList();
        given(subscribeUseCase.listSubscribeChannel(any())).willReturn(list);
        given(userUseCase.getUser(any())).willReturn(UserFixtures.stub());

        mockMvc
            .perform(
                get("/api/v1/subscribe/mine")
                    .header(HeaderAttribute.X_AUTH_KEY, authKey)
            )
            .andExpectAll(
                status().isOk(),
                jsonPath("$.data.size()").value(3)
            );
    }
}
