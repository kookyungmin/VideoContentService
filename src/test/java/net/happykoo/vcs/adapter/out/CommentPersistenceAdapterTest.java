package net.happykoo.vcs.adapter.out;

import net.happykoo.vcs.adapter.out.mongo.comment.CommentDocument;
import net.happykoo.vcs.adapter.out.mongo.comment.CommentMongoRepository;
import net.happykoo.vcs.domain.comment.Comment;
import net.happykoo.vcs.domain.comment.CommentFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.LongStream;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class CommentPersistenceAdapterTest {
    private CommentPersistenceAdapter commentPersistenceAdapter;

    private final CommentMongoRepository commentMongoRepository = mock(CommentMongoRepository.class);
    private final StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class, RETURNS_DEEP_STUBS);

    @BeforeEach
    void setUp() {
        commentPersistenceAdapter = new CommentPersistenceAdapter(commentMongoRepository, stringRedisTemplate);
    }

    @Test
    @DisplayName("saveComment 테스트")
    void test1() {
        var comment = CommentFixtures.stub("commentId");
        given(commentMongoRepository.save(any())).willAnswer(arg -> arg.getArgument(0));

        var result = commentPersistenceAdapter.saveComment(comment);

        then(result)
            .hasFieldOrPropertyWithValue("id", "commentId");
    }

    @Test
    @DisplayName("deleteComment 테스트")
    void test2() {
        var commentId = "commentId";

        commentPersistenceAdapter.deleteComment(commentId);

        verify(commentMongoRepository).deleteById(commentId);
    }

    @Test
    @DisplayName("loadComment 테스트")
    void testLoadComment() {
        var commentId = "commentId";
        var comment = CommentFixtures.stub(commentId);
        given(commentMongoRepository.findById(any()))
                .willReturn(Optional.of(CommentDocument.from(comment)));

        var result = commentPersistenceAdapter.loadComment(commentId);
        then(result)
            .isPresent()
            .hasValueSatisfying(c ->
                then(c)
                    .hasFieldOrPropertyWithValue("id", commentId)
                    .hasFieldOrPropertyWithValue("text", comment.getText())
            );
    }

    @Nested
    @DisplayName("댓글 목록")
    class ListComment {
        @Test
        @DisplayName("작성 시간 역순으로 maxSize 만큼 목록 반환")
        void test1() {
            var videoId = "videoId";
            var list = LongStream.range(1, 6)
                    .mapToObj(i -> commentBuilder(videoId, LocalDateTime.now()))
                    .toList();
            given(commentMongoRepository.findAllByVideoIdAndParentIdIsNullAndPublishedAtLessThanEqualOrderByPublishedAtDesc(any(), any(), any()))
                    .willReturn(list);
            var result = commentPersistenceAdapter.listComment(videoId, "time", "2025-11-08T12:34:56.789", 5);
            then(result)
                .hasSize(5);
        }
    }

    @Nested
    @DisplayName("대댓글 목록")
    class ListReply {
        @Test
        @DisplayName("작성 시간 역순으로 maxSize 만큼 목록 반환")
        void test1() {
            var parentId = "parentId";
            var list = LongStream.range(1, 6)
                    .mapToObj(i -> replyBuilder(parentId, LocalDateTime.now()))
                    .toList();
            given(commentMongoRepository.findAllByParentIdAndPublishedAtLessThanEqualOrderByPublishedAt(any(), any(), any()))
                    .willReturn(list);
            var result = commentPersistenceAdapter.listReply(parentId, "2025-11-08T12:34:56.789", 5);

            then(result)
                .hasSize(5);
        }
    }

    @Nested
    @DisplayName("고정 댓글")
    class PinnedComment {
        @Test
        @DisplayName("고정댓글이 있는 경우")
        void test1() {
            var videoId = "videoId";
            var commentId = "commentId";
            given(stringRedisTemplate.opsForValue().get(any())).willReturn(commentId);
            given(commentMongoRepository.findById(any())).willReturn(Optional.of(commentBuilder(videoId, LocalDateTime.now())));

            var result = commentPersistenceAdapter.getPinnedComment(videoId);

            then(result)
                    .isPresent();
        }

        @Test
        @DisplayName("고정댓글이 없는 경우")
        void test2() {
            given(stringRedisTemplate.opsForValue().get(any())).willReturn(null);

            var result = commentPersistenceAdapter.getPinnedComment("videoId");

            then(result)
                .isNotPresent();
        }
    }

    private CommentDocument commentBuilder(String videoId, LocalDateTime publishedAt) {
        var id = UUID.randomUUID().toString();
        return CommentDocument.from(
                Comment.builder()
                        .id(id)
                        .videoId(videoId)
                        .parentId(null)
                        .text("text " + id)
                        .authorId("happykoo")
                        .publishedAt(publishedAt)
                        .build()
        );
    }

    private CommentDocument replyBuilder(String parentId, LocalDateTime publishedAt) {
        var id = UUID.randomUUID().toString();
        return CommentDocument.from(
                Comment.builder()
                        .id(id)
                        .videoId("videoId")
                        .parentId(parentId)
                        .text("text " + id)
                        .authorId("happykoo")
                        .publishedAt(publishedAt)
                        .build()
        );
    }
}
