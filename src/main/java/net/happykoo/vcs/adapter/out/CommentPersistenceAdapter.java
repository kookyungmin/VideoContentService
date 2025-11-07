package net.happykoo.vcs.adapter.out;

import lombok.RequiredArgsConstructor;
import net.happykoo.vcs.adapter.out.mongo.comment.CommentDocument;
import net.happykoo.vcs.adapter.out.mongo.comment.CommentMongoRepository;
import net.happykoo.vcs.application.port.out.LoadCommentPort;
import net.happykoo.vcs.application.port.out.SaveCommentPort;
import net.happykoo.vcs.common.RedisKeyGenerator;
import net.happykoo.vcs.domain.comment.Comment;
import org.springframework.data.domain.Limit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommentPersistenceAdapter implements LoadCommentPort, SaveCommentPort {
    private final CommentMongoRepository commentMongoRepository;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Comment saveComment(Comment comment) {
        var document = CommentDocument.from(comment);
        return commentMongoRepository.save(document).toDomain();
    }

    @Override
    public void deleteComment(String commentId) {
        commentMongoRepository.deleteById(commentId);
    }

    @Override
    public Optional<Comment> loadComment(String commentId) {
        return commentMongoRepository.findById(commentId)
                .map(CommentDocument::toDomain);
    }

    @Override
    public List<Comment> listComment(String videoId, String order, String offset, Integer maxSize) {
        //TODO: order 에 따라 분기 처리 해야함(default: time)
        return commentMongoRepository.findAllByVideoIdAndParentIdIsNullAndPublishedAtLessThanEqualOrderByPublishedAtDesc(
                    videoId,
                    LocalDateTime.parse(offset),
                    Limit.of(maxSize))
                .stream()
                .map(CommentDocument::toDomain)
                .toList();
    }

    @Override
    public List<Comment> listReply(String parentId, String offset, Integer maxSize) {
        return commentMongoRepository.findAllByParentIdAndPublishedAtLessThanEqualOrderByPublishedAt(
                    parentId,
                    LocalDateTime.parse(offset),
                    Limit.of(maxSize))
                .stream()
                .map(CommentDocument::toDomain)
                .toList();
    }

    @Override
    public Optional<Comment> getPinnedComment(String videoId) {
        //redis 에서 pinned 댓글 ID 조회
        var commentId = stringRedisTemplate.opsForValue()
                .get(RedisKeyGenerator.getPinnedCommentKey(videoId));

        if (commentId == null) {
            return Optional.empty();
        }

        return commentMongoRepository.findById(videoId)
                .map(CommentDocument::toDomain);
    }
}
