package net.happykoo.vcs.application.port.out;

import net.happykoo.vcs.domain.comment.Comment;

public interface SaveCommentPort {
    Comment saveComment(Comment comment);
    void deleteComment(String commentId);
}
