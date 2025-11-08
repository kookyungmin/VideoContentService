package net.happykoo.vcs.application;

import net.happykoo.vcs.adapter.in.api.dto.CommentRequest;
import net.happykoo.vcs.application.port.out.LikeCommentPort;
import net.happykoo.vcs.application.port.out.LoadCommentPort;
import net.happykoo.vcs.application.port.out.LoadUserPort;
import net.happykoo.vcs.application.port.out.SaveCommentPort;
import net.happykoo.vcs.domain.comment.Comment;
import net.happykoo.vcs.domain.comment.CommentFixtures;
import net.happykoo.vcs.domain.user.User;
import net.happykoo.vcs.domain.user.UserFixtures;
import net.happykoo.vcs.exception.BadRequestException;
import net.happykoo.vcs.exception.DomainNotFoundException;
import net.happykoo.vcs.exception.ForbiddenRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.LongStream;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class CommentServiceTest {
    private CommentService commentService;

    private final LoadCommentPort loadCommentPort = mock(LoadCommentPort.class);
    private final SaveCommentPort saveCommentPort = mock(SaveCommentPort.class);
    private final LikeCommentPort likeCommentPort = mock(LikeCommentPort.class);
    private final LoadUserPort loadUserPort = mock(LoadUserPort.class);

    @BeforeEach
    void setUp() {
        commentService = new CommentService(loadCommentPort, saveCommentPort, likeCommentPort, loadUserPort);
    }

    @Test
    @DisplayName("댓글 생성")
    void test1() {
        // given
        var user = UserFixtures.stub();
        var commentRequest = new CommentRequest("videoId", null, "comment");
        // when
        commentService.createComment(user, commentRequest);
        // then
        var argumentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(saveCommentPort).saveComment(argumentCaptor.capture());
        then(argumentCaptor.getValue())
            .hasFieldOrPropertyWithValue("authorId", user.getId())
            .hasFieldOrPropertyWithValue("videoId", commentRequest.videoId())
            .hasFieldOrPropertyWithValue("text", commentRequest.text());
    }

    @Nested
    @DisplayName("댓글 수정")
    class UpdateComment {
        @Test
        @DisplayName("댓글 수정 후 수정된 댓글 반환")
        void test1() {
            var commentId = "commentId";
            var user = UserFixtures.stub();
            var comment = CommentFixtures.stub(commentId);
            var commentRequest = new CommentRequest("videoId", null, "new comment");
            given(loadCommentPort.loadComment(any())).willReturn(Optional.of(comment));
            given(saveCommentPort.saveComment(any())).willAnswer(arg -> arg.getArgument(0));

            var result = commentService.updateComment(commentId, user, commentRequest);

            then(result.getText()).isEqualTo("new comment");
        }

        @Test
        @DisplayName("댓글 channelId, videoId 가 다르면 BadRequestException throw")
        void test2() {
            var commentId = "commentId";
            var user = UserFixtures.stub();
            var commentRequest = new CommentRequest("otherVideoId", null, "new comment");
            given(loadCommentPort.loadComment(any())).willReturn(Optional.of(CommentFixtures.stub(commentId)));

            thenThrownBy(() -> commentService.updateComment(commentId, user, commentRequest))
                .isInstanceOf(BadRequestException.class);
        }

        @Test
        @DisplayName("댓글 수정 user와 댓글 author 가 다르면 ForbiddenRequestException throw")
        void test3() {
            var commentId = "commentId";
            var otherUser = UserFixtures.stub("otherUser");
            var commentRequest = new CommentRequest("videoId", null, "new comment");
            given(loadCommentPort.loadComment(any())).willReturn(Optional.of(CommentFixtures.stub(commentId)));

            thenThrownBy(() -> commentService.updateComment(commentId, otherUser, commentRequest))
                .isInstanceOf(ForbiddenRequestException.class);
        }

        @Test
        @DisplayName("존재하지 않는 댓글이면 DomainNotFoundException throw")
        void test4() {
            var commentId = "commentId";
            var user = UserFixtures.stub();
            var commentRequest = new CommentRequest("videoId", null, "new comment");
            given(loadCommentPort.loadComment(any())).willReturn(Optional.empty());

            thenThrownBy(() -> commentService.updateComment(commentId, user, commentRequest))
                    .isInstanceOf(DomainNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("댓글 삭제")
    class DeleteComment {
        @Test
        @DisplayName("댓글을 삭제")
        void test1() {
            var commentId = "commentId";
            var user = UserFixtures.stub();
            var comment = CommentFixtures.stub(commentId);
            given(loadCommentPort.loadComment(any())).willReturn(Optional.of(comment));

            commentService.deleteComment(commentId, user);

            verify(saveCommentPort).deleteComment(commentId);
        }

        @Test
        @DisplayName("댓글 삭제 user와 댓글 author 가 다르면 ForbiddenRequestException throw")
        void test2() {
            var commentId = "commentId";
            var otherUser = UserFixtures.stub("otherUser");
            given(loadCommentPort.loadComment(any())).willReturn(Optional.of(CommentFixtures.stub(commentId)));

            thenThrownBy(() -> commentService.deleteComment(commentId, otherUser))
                    .isInstanceOf(ForbiddenRequestException.class);
        }

        @Test
        @DisplayName("존재하지 않는 댓글이면 DomainNotFoundException throw")
        void test3() {
            var commentId = "commentId";
            var user = UserFixtures.stub();
            given(loadCommentPort.loadComment(any())).willReturn(Optional.empty());

            thenThrownBy(() -> commentService.deleteComment(commentId, user))
                    .isInstanceOf(DomainNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("댓글 한개 조회")
    class GetComment {
        @Test
        @DisplayName("댓글 ID로 조회")
        void test1() {
            var comment = CommentFixtures.stub("commentId");
            var author = User.builder()
                .id(comment.getAuthorId())
                .name("해피쿠")
                .profileImageUrl("https://happykoo.net/profile.jpg")
                .build();
            given(loadCommentPort.loadComment(any())).willReturn(Optional.of(comment));
            given(likeCommentPort.getCommentLikeCount(any())).willReturn(20L);
            given(loadUserPort.loadUser(any())).willReturn(Optional.of(author));

            var result = commentService.getComment("commentId");

            // Then
            then(result)
                .hasFieldOrPropertyWithValue("id", comment.getId())
                .hasFieldOrPropertyWithValue("videoId", comment.getVideoId())
                .hasFieldOrPropertyWithValue("text", comment.getText())
                .hasFieldOrPropertyWithValue("authorId", author.getId())
                .hasFieldOrPropertyWithValue("authorName", author.getName())
                .hasFieldOrPropertyWithValue("authorProfileImageUrl", author.getProfileImageUrl())
                .hasFieldOrPropertyWithValue("likeCount", 20L);
        }
    }


    @Nested
    @DisplayName("댓글 목록")
    class ListComment {
        @Test
        @DisplayName("User 없는 댓글 목록")
        void test1() {
            var videoId = "videoId";
            var userId = "happykoo";
            var comments = LongStream.range(1, 6)
                .mapToObj(l -> Comment.builder()
                    .id(UUID.randomUUID().toString())
                    .videoId(videoId)
                    .text("text " + l)
                    .authorId(userId)
                    .publishedAt(LocalDateTime.now())
                    .build()
                )
                .toList();
            var author = User.builder()
                    .id(userId)
                    .name("해피쿠")
                    .profileImageUrl("https://happykoo.net/profile.jpg")
                    .build();
            given(loadCommentPort.listComment(any(), any(), any(), any())).willReturn(comments);
            given(likeCommentPort.getCommentLikeCount(any())).willReturn(20L);
            given(loadUserPort.loadUser(any())).willReturn(Optional.of(author));

            var result = commentService.listComments(author, videoId, "time", LocalDateTime.now().toString(), 5);

            // Then
            then(result)
                .hasSize(5);
            verify(loadUserPort, times(5)).loadUser(any());
            verify(likeCommentPort, times(5)).getCommentLikeCount(any());
        }

//TODO: blocking
//        @Test
//        @DisplayName("User 있는 댓글 목록")
//        void test2() {
//            var videoId = "videoId";
//            var userId = "happykoo";
//            var user = UserFixtures.stub();
//            var comments = LongStream.range(1, 6)
//                    .mapToObj(l -> Comment.builder()
//                            .id(UUID.randomUUID().toString())
//                            .videoId(videoId)
//                            .text("text " + l)
//                            .authorId("user")
//                            .publishedAt(LocalDateTime.now())
//                            .build()
//                    )
//                    .toList();
//            var blockedComments = Set.of(comments.get(0).getId());
//            var author = User.builder()
//                    .id(userId)
//                    .name("해피쿠")
//                    .profileImageUrl("https://happykoo.net/profile.jpg")
//                    .build();
//            given(commentPort.listComment(any(), any(), any(), any())).willReturn(comments);
//            given(commentBlockPort.getUserCommentBlocks(any())).willReturn(blockedComments);
//            given(loadUserPort.loadUser(any())).willReturn(Optional.of(author));
//            given(commentLikePort.getCommentLikeCount(any())).willReturn(20L);
//
//            var result = sut.listComments(user, videoId, "time", LocalDateTime.now().toString(), 5);
//
//            // Then
//            then(result)
//                    .hasSize(4);
//            verify(loadUserPort, times(4)).loadUser(any());
//            verify(commentLikePort, times(4)).getCommentLikeCount(any());
//        }
    }

    @Nested
    @DisplayName("대댓글 목록")
    class ListReply {
        @Test
        @DisplayName("대댓글 목록 조회")
        void test1() {
            var parentId = "parentId";
            var userId = "happykoo";
            var comments = LongStream.range(1, 6)
                .mapToObj(l -> Comment.builder()
                        .id(UUID.randomUUID().toString())
                        .videoId("videoId")
                        .parentId(parentId)
                        .text("text " + l)
                        .authorId(userId)
                        .publishedAt(LocalDateTime.now())
                        .build()
                )
                .toList();
            var author = User.builder()
                    .id(userId)
                    .name("해피쿠")
                    .profileImageUrl("https://happykoo.net/profile.jpg")
                    .build();
            given(loadCommentPort.listReply(any(), any(), any())).willReturn(comments);
            given(loadUserPort.loadUser(any())).willReturn(Optional.of(author));
            given(likeCommentPort.getCommentLikeCount(any())).willReturn(20L);

            var result = commentService.listReplies(parentId, LocalDateTime.now().toString(), 5);

            // Then
            then(result)
                .hasSize(5);
            verify(loadUserPort, times(5)).loadUser(any());
            verify(likeCommentPort, times(5)).getCommentLikeCount(any());
        }
    }
}
