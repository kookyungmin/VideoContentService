package net.happykoo.vcs.adapter.in.api.dto;

import net.happykoo.vcs.domain.comment.Comment;
import net.happykoo.vcs.domain.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public record CommentResponse (
    String id,
    String videoId,
    String parentId,
    String authorId,
    String authorName,
    String authorProfileImageUrl,
    String text,
    LocalDateTime publishedAt,
    long likeCount,
    List<CommentResponse> replies
) {
    public static CommentResponse from(Comment comment,
                                       User author,
                                       Long likeCount) {
        return from(comment, author, likeCount, Collections.emptyList());
    }

    public static CommentResponse from(Comment comment,
                                       User author,
                                       Long likeCount,
                                       List<CommentResponse> replies) {
        return new CommentResponse(
            comment.getId(),
            comment.getVideoId(),
            comment.getParentId(),
            author.getId(),
            author.getName(),
            author.getProfileImageUrl(),
            comment.getText(),
            comment.getPublishedAt(),
            likeCount,
            replies
        );
    }

    public void addReplies(List<CommentResponse> replies) {
        this.replies.addAll(replies);
    }
}
