package net.happykoo.vcs.adapter.in.api;

import net.happykoo.vcs.adapter.in.api.attribute.HeaderAttribute;
import net.happykoo.vcs.application.port.in.VideoLikeUseCase;
import net.happykoo.vcs.application.port.out.LoadUserPort;
import net.happykoo.vcs.application.port.out.UserSessionPort;
import net.happykoo.vcs.domain.user.User;
import net.happykoo.vcs.domain.user.UserFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VideoRateApiController.class)
public class VideoRateApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideoLikeUseCase videoLikeUseCase;
    @MockBean
    private UserSessionPort userSessionPort;
    @MockBean
    private LoadUserPort loadUserPort;

    private String authKey;
    private User user;

    @BeforeEach
    void setUp() {
        //loginUser 셋팅
        authKey = UUID.randomUUID().toString();
        user = UserFixtures.stub();
        given(userSessionPort.getUserId(anyString())).willReturn(user.getId());
        given(loadUserPort.loadUser(anyString())).willReturn(Optional.of(user));
    }

    @Nested
    @DisplayName("POST /api/v1/videos/rate 테스트")
    class RateVideo {
        @Test
        @DisplayName("video 좋아요")
        void test1() throws Exception {
            var videoId = "videoId";
            mockMvc
                .perform(
                    post("/api/v1/videos/rate?videoId={videoId}&rating={rate}", videoId, "LIKE")
                        .header(HeaderAttribute.X_AUTH_KEY, authKey)
                )
                .andExpect(
                    status().isOk()
                );

            verify(videoLikeUseCase).likeVideo(videoId, user.getId());
        }

        @Test
        @DisplayName("video 좋아요 취소")
        void test2() throws Exception {
            var videoId = "videoId";
            mockMvc
                .perform(
                    post("/api/v1/videos/rate?videoId={videoId}&rating={rate}", videoId, "NONE")
                        .header(HeaderAttribute.X_AUTH_KEY, authKey)
                )
                .andExpect(
                    status().isOk()
                );

            verify(videoLikeUseCase).unlikeVideo(videoId, user.getId());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/rate 테스트")
    class GetVideoRate {
        @ParameterizedTest
        @CsvSource(value = {"true,LIKE", "false,NONE"})
        @DisplayName("좋아요한 비디오는 rate=like 를 반환")
        void testGetVideoLikeRate(Boolean likedVideo, String rate) throws Exception {
            given(videoLikeUseCase.isLikedVideo(any(), any())).willReturn(likedVideo);

            mockMvc
                .perform(
                    get("/api/v1/videos/rate?videoId={videoId}", "videoId")
                        .header(HeaderAttribute.X_AUTH_KEY, authKey)
                )
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.videoId").value("videoId"),
                    jsonPath("$.data.rate").value(rate)
                );
        }
    }
}
