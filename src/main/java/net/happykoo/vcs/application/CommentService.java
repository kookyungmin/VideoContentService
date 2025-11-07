package net.happykoo.vcs.application;

import lombok.RequiredArgsConstructor;
import net.happykoo.vcs.adapter.in.api.dto.CommentRequest;
import net.happykoo.vcs.adapter.in.api.dto.CommentResponse;
import net.happykoo.vcs.application.port.in.CommentUseCase;
import net.happykoo.vcs.application.port.out.LikeCommentPort;
import net.happykoo.vcs.application.port.out.LoadCommentPort;
import net.happykoo.vcs.application.port.out.LoadUserPort;
import net.happykoo.vcs.application.port.out.SaveCommentPort;
import net.happykoo.vcs.domain.comment.Comment;
import net.happykoo.vcs.domain.user.User;
import net.happykoo.vcs.exception.BadRequestException;
import net.happykoo.vcs.exception.DomainNotFoundException;
import net.happykoo.vcs.exception.ForbiddenRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService implements CommentUseCase {
    private final LoadCommentPort loadCommentPort;
    private final SaveCommentPort saveCommentPort;
    private final LoadUserPort loadUserPort;
    private final LikeCommentPort likeCommentPort;

    @Override
    public Comment createComment(User user, CommentRequest commentRequest) {
        var comment = Comment.builder()
                .id(UUID.randomUUID().toString())
                .videoId(commentRequest.videoId())
                .parentId(commentRequest.parentId())
                .text(commentRequest.text())
                .authorId(user.getId())
                .publishedAt(LocalDateTime.now())
                .build();
        return saveCommentPort.saveComment(comment);
    }

    @Override
    public Comment updateComment(String commentId, User user, CommentRequest commentRequest) {
        var comment = loadCommentPort.loadComment(commentId)
                .orElseThrow(DomainNotFoundException::new);
        //만약, 댓글 작성자가 아니면 403 에러
        if (!comment.getAuthorId().equals(user.getId())) {
            throw new ForbiddenRequestException("댓글 수정 권한이 없습니다.");
        }

        //메타 정보가 다르면, 400 에러
        if (!equalMetaData(comment, commentRequest)) {
            throw new BadRequestException("잘못된 요청입니다.");
        }

        comment.updateText(commentRequest.text());

        return saveCommentPort.saveComment(comment);
    }

    @Override
    public void deleteComment(String commentId, User user) {
        var comment = loadCommentPort.loadComment(commentId)
                .orElseThrow(DomainNotFoundException::new);
        //만약, 댓글 작성자가 아니면 403 에러
        if (!comment.getAuthorId().equals(user.getId())) {
            throw new ForbiddenRequestException("댓글 삭제 권한이 없습니다.");
        }

        saveCommentPort.deleteComment(commentId);
    }

    @Override
    public CommentResponse getComment(String commentId) {
        var comment = loadCommentPort.loadComment(commentId)
                .orElseThrow(DomainNotFoundException::new);
        return buildComment(comment);
    }

    @Override
    public List<CommentResponse> listComments(User user, String videoId, String order, String offset, Integer maxSize) {
        //TODO: comment block 로직 구현
        var list = loadCommentPort.listComment(videoId, order, offset, maxSize)
                .stream()
                .map(this::buildComment)
                .map(commentRes -> {
                    commentRes.addReplies(listReplies(commentRes.id(), offset, maxSize));
                    return commentRes;
                })
                .toList();

        //TODO: offset 이 처음일 때만 pinnedComment 추가해야 함
        loadCommentPort.getPinnedComment(videoId)
            .ifPresent(pinnedComment -> list.add(0, buildComment(pinnedComment)));

        return list;
    }

    @Override
    public List<CommentResponse> listReplies(String parentId, String offset, Integer maxSize) {
        return loadCommentPort.listReply(parentId, offset, 10)
                .stream()
                .map(this::buildComment)
                .toList();
    }

    private CommentResponse buildComment(Comment comment) {
        //user, likeCount는 레디스에서 조회, 댓글은 몽고디비에서 조회 결합
        var user = loadUserPort.loadUser(comment.getAuthorId())
                .orElse(User.defaultUser(comment.getId()));
        var likeCount = likeCommentPort.getCommentLikeCount(comment.getId());
        return CommentResponse.from(comment, user, likeCount);
    }

    private boolean equalMetaData(Comment comment, CommentRequest commentRequest) {
        //Objects.equals(null, null) -> true
        return Objects.equals(comment.getVideoId(), commentRequest.videoId())
                && Objects.equals(comment.getParentId(), commentRequest.parentId());
    }
}
