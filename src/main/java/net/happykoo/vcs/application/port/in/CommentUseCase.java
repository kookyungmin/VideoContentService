package net.happykoo.vcs.application.port.in;

import net.happykoo.vcs.adapter.in.api.dto.CommentRequest;
import net.happykoo.vcs.adapter.in.api.dto.CommentResponse;
import net.happykoo.vcs.domain.comment.Comment;
import net.happykoo.vcs.domain.user.User;

import java.util.List;

public interface CommentUseCase {
    Comment createComment(User user, CommentRequest commentRequest);
    Comment updateComment(String commentId, User user, CommentRequest commentRequest);
    void deleteComment(String commentId, User user);
    CommentResponse getComment(String commentId);
    List<CommentResponse> listComments(User user, String videoId, String order, String offset, Integer maxSize);
    List<CommentResponse> listReplies(String parentId, String offset, Integer maxSize);


}
