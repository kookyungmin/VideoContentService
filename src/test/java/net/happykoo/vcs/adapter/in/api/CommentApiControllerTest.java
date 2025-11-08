package net.happykoo.vcs.adapter.in.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.happykoo.vcs.adapter.in.api.attribute.HeaderAttribute;
import net.happykoo.vcs.adapter.in.api.dto.CommentRequest;
import net.happykoo.vcs.adapter.in.api.dto.CommentResponse;
import net.happykoo.vcs.application.port.in.CommentUseCase;
import net.happykoo.vcs.domain.comment.CommentFixtures;
import net.happykoo.vcs.exception.BadRequestException;
import net.happykoo.vcs.exception.DomainNotFoundException;
import net.happykoo.vcs.exception.ForbiddenRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentApiController.class)
public class CommentApiControllerTest extends AuthBaseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentUseCase commentUseCase;

    @BeforeEach
    @DisplayName("로그인 유저 셋팅")
    void setUp() {
        prepareUser();
    }

    @Nested
    @DisplayName("POST /api/v1/comments")
    class CreateComment {
        @Test
        @DisplayName("200 OK, 생성된 id를 반환")
        void test1() throws Exception {
            // given
            var request = new CommentRequest("videoId", null, "comment");
            given(commentUseCase.createComment(any(), any())).willReturn(CommentFixtures.stub("commentId"));

            // when
            mockMvc
                .perform(
                    post("/api/v1/comments")
                        .header(HeaderAttribute.X_AUTH_KEY, authKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.data").value("commentId")
                );
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/comments")
    class UpdateComment {
        @Test
        @DisplayName("200 OK, 변경된 댓글의 id를 반환")
        void testUpdateCommentThenOk() throws Exception {
            // given
            var commentId = "commentId";
            var request = new CommentRequest("videoId", null, "new comment");
            given(commentUseCase.updateComment(any(), any(), any()))
                .willReturn(CommentFixtures.stub(commentId));

            // when
            mockMvc
                .perform(
                    put("/api/v1/comments/{commentId}", commentId)
                        .header(HeaderAttribute.X_AUTH_KEY, authKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.data").value(commentId)
                );
        }

        @Test
        @DisplayName("400 BadRequest, 댓글 meta 정보가 다를 경우 수정 실패")
        void test2() throws Exception {
            // given
            var commentId = "commentId";
            var request = new CommentRequest("otherVideoId", null, "new comment");
            given(commentUseCase.updateComment(any(), any(), any()))
                .willThrow(new BadRequestException("Request metadata is invalid."));

            // when
            mockMvc
                .perform(
                    put("/api/v1/comments/{commentId}", commentId)
                        .header(HeaderAttribute.X_AUTH_KEY, authKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.type").value("badRequest")
                );
        }

        @Test
        @DisplayName("403 Forbidden, 댓글 작성자와 API 호출자가 다름 수정 실패")
        void test3() throws Exception {
            // given
            var commentId = "commentId";
            var otherAuthKey = UUID.randomUUID().toString();
            var request = new CommentRequest("videoId", null, "new comment");
            given(commentUseCase.updateComment(any(), any(), any()))
                    .willThrow(new ForbiddenRequestException("Request might not be properly authorized."));

            // when
            mockMvc
                .perform(
                    put("/api/v1/comments/{commentId}", commentId)
                        .header(HeaderAttribute.X_AUTH_KEY, otherAuthKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpectAll(
                    status().isForbidden(),
                    jsonPath("$.type").value("forbidden")
                );
        }

        @Test
        @DisplayName("404 Not Found, 존재하지 않는 댓글 수정 실패")
        void test4() throws Exception {
            // given
            var commentId = "commentId";
            var request = new CommentRequest("videoId", null, "new comment");
            given(commentUseCase.updateComment(any(), any(), any()))
                .willThrow(new DomainNotFoundException("Comment Not Found."));

            // when
            mockMvc
                .perform(
                    put("/api/v1/comments/{commentId}", commentId)
                        .header(HeaderAttribute.X_AUTH_KEY, authKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.type").value("notFound")
                );
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/comments/{commentId}")
    class DeleteComment {
        @Test
        @DisplayName("200 Ok, 해당 댓글을 삭제")
        void test1() throws Exception {
            var commentId = "commentId";

            mockMvc
                .perform(
                    delete("/api/v1/comments/{commentId}", commentId)
                        .header(HeaderAttribute.X_AUTH_KEY, authKey)
                )
                .andExpect(
                        status().isOk()
                );

            verify(commentUseCase).deleteComment(commentId, user);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/comments?commentId={commentId}")
    class GetComment {
        @Test
        @DisplayName("200 Ok, 해당 댓글 반환")
        void test1() throws Exception {
            var commentId = "commentId";
            var commentResponse = CommentResponse.builder()
                    .id(commentId)
                    .videoId("videoId")
                    .text("comment")
                    .publishedAt(LocalDateTime.now())
                    .authorId("happykoo")
                    .authorName("해피쿠")
                    .authorProfileImageUrl("https://happykoo.net/profile.jpg")
                    .likeCount(100L)
                    .build();
            given(commentUseCase.getComment(any()))
                .willReturn(commentResponse);

            mockMvc
                .perform(
                    get("/api/v1/comments?commentId={commentId}", commentId)
                )
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.id").value(commentId),
                    jsonPath("$.data.videoId").value("videoId"),
                    jsonPath("$.data.text").value("comment"),
                    jsonPath("$.data.authorId").value("happykoo"),
                    jsonPath("$.data.authorName").value("해피쿠"),
                    jsonPath("$.data.authorProfileImageUrl").value("https://happykoo.net/profile.jpg"),
                    jsonPath("$.data.likeCount").value(100L)
                );
        }

        @Test
        @DisplayName("해당 댓글이 없으면 400 Not Found")
        void test2() throws Exception{
            var commentId = "commentId";
            given(commentUseCase.getComment(any())).willThrow(new DomainNotFoundException(""));

            mockMvc
                .perform(
                    get("/api/v1/comments?commentId={commentId}", commentId)
                )
                .andExpect(
                    status().isNotFound()
                );
        }
    }

    @Nested
    @DisplayName("GET /api/v1/comments/list")
    class ListComment {
        @Test
        @DisplayName("조회하는 회원 정보가 없는 시간 순 정렬")
        void test1() throws Exception {
            var videoId = "videoId";
            var order = "time";
            var offset = LocalDateTime.now();
            var maxSize = 10;
            given(commentUseCase.listComments(any(), any(), any(), any(), any())).willReturn(Collections.emptyList());
            mockMvc
                .perform(
                    get("/api/v1/comments/list?videoId={videoId}&order={order}&offset={offset}&maxSize={maxSize}", videoId, order, offset, maxSize)
                )
                .andExpect(
                    status().isOk()
                );


            verify(commentUseCase).listComments(null, videoId, order, offset.toString(), maxSize);
        }

        @Test
        @DisplayName("조회하는 회원 정보가 있는 시간 순 정렬")
        void testGivenUserListCommentsByPublishedAt() throws Exception {
            var videoId = "videoId";
            var order = "time";
            var offset = LocalDateTime.now();
            var maxSize = 10;
            given(commentUseCase.listComments(any(), any(), any(), any(), any())).willReturn(Collections.emptyList());
            mockMvc
                .perform(
                    get("/api/v1/comments/list?videoId={videoId}&order={order}&offset={offset}&maxSize={maxSize}", videoId, order, offset, maxSize)
                        .header(HeaderAttribute.X_AUTH_KEY, authKey)
                )
                .andExpect(
                    status().isOk()
                );

            verify(commentUseCase).listComments(user, videoId, order, offset.toString(), maxSize);
        }
    }
}
