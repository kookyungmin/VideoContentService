package net.happykoo.vcs.application.port.out;

import net.happykoo.vcs.domain.comment.Comment;

import java.util.List;
import java.util.Optional;

public interface LoadCommentPort {
    Optional<Comment> loadComment(String commentId);

    List<Comment> listComment(String videoId, String order, String offset, Integer maxSize);

    List<Comment> listReply(String parentId, String offset, Integer maxSize);

    Optional<Comment> getPinnedComment(String videoId);
}
